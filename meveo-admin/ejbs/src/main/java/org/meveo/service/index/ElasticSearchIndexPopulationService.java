package org.meveo.service.index;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class ElasticSearchIndexPopulationService implements Serializable {

    private static final long serialVersionUID = 6177817839276664632L;

    @Inject
    @MeveoJpa
    private EntityManager em;

    @Inject
    @MeveoJpaForJobs
    private EntityManager emfForJobs;

    @Inject
    private Conversation conversation;

    @Inject
    private ElasticSearchConfiguration esConfiguration;

    @EJB
    private CustomFieldInstanceService customFieldInstanceService;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;
    @Inject
    private Logger log;

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int populateIndex(String classname, int from, ReindexingStatistics statistics, TransportClient client) {

        Set<String> cftIndexable = new HashSet<>();
        Set<String> cftNotIndexable = new HashSet<>();

        Query query = getEntityManager().createQuery("select e from " + classname + " e");
        query.setFirstResult(from);
        query.setMaxResults(ElasticClient.INDEX_POPULATE_PAGE_SIZE);

        List<? extends BusinessEntity> entities = query.getResultList();
        int found = entities.size();

        log.trace("Repopulating Elastic Search with records {}-{} of {} entity", from, from + found, classname);

        if (entities.isEmpty()) {
            return 0;
        }

        String index = esConfiguration.getIndex(entities.get(0));
        String type = null;
        String id = null;

        // Process results

        // Retrieve custom fields for a given batch if applicable
        List<String> uuids = null;
        Map<String, Map<String, Object>> uuidCFMap = new HashMap<>();
        if (entities.get(0) instanceof ICustomFieldEntity) {
            uuids = new ArrayList<>();
            for (BusinessEntity entity : entities) {
                uuids.add(((ICustomFieldEntity) entity).getUuid());
            }

            List<CustomFieldInstance> customFields = customFieldInstanceService.getCustomFieldInstances(uuids);

            for (CustomFieldInstance customFieldInstance : customFields) {

                if (!uuidCFMap.containsKey(customFieldInstance.getAppliesToEntity())) {
                    uuidCFMap.put(customFieldInstance.getAppliesToEntity(), new HashMap<String, Object>());
                }

                // Maps are stored as Json encoded strings
                Object value = customFieldInstance.getValue();
                if (value instanceof Map || value instanceof EntityReferenceWrapper) {
                    value = JsonUtils.toJson(value, false);
                }

                uuidCFMap.get(customFieldInstance.getAppliesToEntity()).put(customFieldInstance.getCode(), value);
            }
        }

        // Prepare bulk request
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        // Convert entities to map of values and supplement it with custom field values if applicable and add to a bulk request
        for (BusinessEntity entity : entities) {

            type = esConfiguration.getType(entity);
            id = ElasticClient.cleanUpCode(entity.getCode());

            Map<String, Object> valueMap = convertEntityToJson(entity);

            // Set custom field values if applicable
            if (entity instanceof ICustomFieldEntity) {
                if (uuidCFMap.containsKey(((ICustomFieldEntity) entity).getUuid())) {

                    for (Entry<String, Object> cfValueInfo : uuidCFMap.get(((ICustomFieldEntity) entity).getUuid()).entrySet()) {

                        if (cftIndexable.contains(entity.getClass().getName() + "_" + cfValueInfo.getKey())) {
                            valueMap.put(cfValueInfo.getKey(), cfValueInfo.getValue());

                        } else if (cftNotIndexable.contains(entity.getClass().getName() + "_" + cfValueInfo.getKey())) {
                            continue;

                        } else {
                            CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(cfValueInfo.getKey(), (ICustomFieldEntity) entity);
                            if (cft != null && cft.getIndexType() != null) {
                                cftIndexable.add(entity.getClass().getName() + "_" + cfValueInfo.getKey());
                                valueMap.put(cfValueInfo.getKey(), cfValueInfo.getValue());
                            } else {
                                cftNotIndexable.add(entity.getClass().getName() + "_" + cfValueInfo.getKey());
                            }
                        }
                    }
                }
            }

            bulkRequest.add(new IndexRequest(index, type, id).source(valueMap));
        }

        // Execute bulk request

        int failedRequests = 0;
        BulkResponse bulkResponse = bulkRequest.get();
        for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
            if (bulkItemResponse.getFailureMessage() != null) {
                log.error("Failed to add document to Elastic Search for {}/{}/{} reason: {}", bulkItemResponse.getIndex(), bulkItemResponse.getType(), bulkItemResponse.getId(),
                    bulkItemResponse.getFailureMessage(), bulkItemResponse.getFailure().getCause());
                failedRequests++;
            }
        }

        statistics.updateStatstics(classname, found, failedRequests);
        return found;
    }

    private EntityManager getEntityManager() {
        EntityManager result = emfForJobs;
        if (conversation != null) {
            try {
                conversation.isTransient();
                result = em;
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     * Convert entity to a map of values that is accepted by Elastic Search as document to be stored and indexed
     * 
     * @param entity Entity to store in Elastic Search
     * @return A map of values
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertEntityToJson(BusinessEntity entity) {

        Map<String, Object> jsonValueMap = new HashMap<String, Object>();

        // Maps fields between entity and json.
        Map<String, String> fields = esConfiguration.getFields(entity);
        String fieldNameTo = null;
        String fieldNameFrom = null;

        for (Entry<String, String> fieldInfo : fields.entrySet()) {

            fieldNameTo = fieldInfo.getKey();
            fieldNameFrom = fieldInfo.getValue();

            Object value = null;
            try {
                // Obtain field value from entity
                if (!fieldNameFrom.contains(".")) {
                    if (fieldNameFrom.endsWith("()")) {
                        value = MethodUtils.invokeMethod(value, fieldNameFrom.substring(0, fieldNameFrom.length() - 2));
                    } else {
                        value = FieldUtils.readField(entity, fieldNameFrom, true);
                    }

                } else {
                    String fieldNames[] = fieldNameFrom.split("\\.");

                    Object fieldValue = entity;
                    for (String fieldName : fieldNames) {
                        if (fieldName.endsWith("()")) {
                            fieldValue = MethodUtils.invokeMethod(fieldValue, fieldName.substring(0, fieldName.length() - 2));
                        } else {
                            fieldValue = FieldUtils.readField(fieldValue, fieldName, true);
                        }
                        if (fieldValue == null) {
                            break;
                        }
                    }
                    value = fieldValue;
                }

                if (value != null && (value instanceof IEntity || value.getClass().isAnnotationPresent(Embeddable.class))) {
                    value = convertObjectToFieldMap(value);
                }

                // Set value to json
                if (!fieldNameTo.contains(".")) {
                    jsonValueMap.put(fieldNameTo, value);

                } else {
                    String fieldNames[] = fieldNameTo.split("\\.");
                    String fieldName = null;
                    Map<String, Object> mapEntry = jsonValueMap;
                    int length = fieldNames.length;
                    for (int i = 0; i < length; i++) {
                        fieldName = fieldNames[i];
                        if (i < length - 1) {
                            if (!mapEntry.containsKey(fieldName)) {
                                mapEntry.put(fieldName, new HashMap<String, Object>());
                            }
                            mapEntry = (Map<String, Object>) mapEntry.get(fieldName);
                        } else {
                            mapEntry.put(fieldName, value);
                        }
                    }
                }

            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log.error("Failed to access field {} of {}", fieldInfo.getValue(), ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()));
            }
        }
        return jsonValueMap;
    }

    /**
     * Convert object to a map of fields (recursively)
     * 
     * @param valueToConvert Object to convert
     * @return A map of fieldnames and values
     * @throws IllegalAccessException
     */
    private Map<String, Object> convertObjectToFieldMap(Object valueToConvert) throws IllegalAccessException {
        Map<String, Object> fieldValueMap = new HashMap<>();

        List<Field> fields = new ArrayList<Field>();
        ReflectionUtils.getAllFields(fields, valueToConvert.getClass());

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Object value = FieldUtils.readField(field, valueToConvert, true);

            if (value != null && (value instanceof IEntity || value.getClass().isAnnotationPresent(Embeddable.class))) {
                fieldValueMap.put(field.getName(), convertObjectToFieldMap(value));
            } else {
                fieldValueMap.put(field.getName(), value);
            }
        }
        return fieldValueMap;
    }
}