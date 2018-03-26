package org.meveo.service.index;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.ISearchable;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.EntityCustomizationUtils;
import org.meveo.util.MeveoJpaForMultiTenancy;
import org.meveo.util.MeveoJpaForMultiTenancyForJobs;
import org.slf4j.Logger;

/**
 * Takes care of managing and populating Elastic search indexes
 *
 * @author Andrius Karpavicius
 * @author Tony Alejandro
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class ElasticSearchIndexPopulationService implements Serializable {

    private static final long serialVersionUID = 6177817839276664632L;

    private static String INDEX_PROVIDER_PREFIX = "<provider>";

    @Inject
    private ElasticSearchConfiguration esConfiguration;

    @EJB
    private CustomFieldInstanceService customFieldInstanceService;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private ElasticClientConnection esConnection;

    @Inject
    private Logger log;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    @MeveoJpaForMultiTenancy
    private EntityManager em;

    @Inject
    @MeveoJpaForMultiTenancyForJobs
    private EntityManager emfForJobs;

    @Inject
    private Conversation conversation;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    /**
     * Populate index with data of a given entity class
     *
     * @param classname Entity classname
     * @param from Populate starting record number
     * @param statistics Statistics to add progress info to
     * @return Number of items added
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int populateIndex(String classname, int from, ReindexingStatistics statistics) {

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

        // Prepare bulk request
        BulkRequestBuilder bulkRequest = esConnection.getClient().prepareBulk();

        // Convert entities to map of values and supplement it with custom field values if applicable and add to a bulk request
        for (ISearchable entity : entities) {

            type = esConfiguration.getType(entity);
            if(entity instanceof BusinessEntity) {
                id = ElasticClient.cleanUpCode(entity.getCode());
            } else {
                id = ElasticClient.cleanUpCode(ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()) + entity.getId());
            }

            Map<String, Object> valueMap = convertEntityToJson(entity, cftIndexable, cftNotIndexable);

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

    public EntityManager getEntityManager() {
        if (conversation != null) {
            try {
                conversation.isTransient();
                return em;
            } catch (Exception e) {
                return emfForJobs;
            }
        }

        return emfForJobs;
    }

    /**
     * Convert entity to a map of values that is accepted by Elastic Search as document to be stored and indexed.
     * 
     * @param entity Entity to store in Elastic Search
     * @param cftIndexable Sets to track CFTs that are indexable. Used in massive initial ES population.
     * @param cftNotIndexable Sets to track CFTs that are not indexable. Used in massive initial ES population.
     * @return A map of values
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertEntityToJson(ISearchable entity, Set<String> cftIndexable, Set<String> cftNotIndexable) {

        Map<String, Object> jsonValueMap = new HashMap<String, Object>();

        // Maps fields between entity and json.
        Map<String, String> fields = esConfiguration.getFields(entity);
        String fieldNameTo = null;
        String fieldNameFrom = null;

//        log.trace("Processing entity: {}", entity);

        for (Entry<String, String> fieldInfo : fields.entrySet()) {

            fieldNameTo = fieldInfo.getKey();
            fieldNameFrom = fieldInfo.getValue();

//            log.trace("Mapping {} to {}", fieldNameFrom, fieldNameTo);

            Object value = null;
            try {
                // Obtain field value from entity
                if (!fieldNameFrom.contains(".")) {
//                    log.trace("Fetching value of property {}", fieldNameFrom);
                    if (fieldNameFrom.endsWith("()")) {
                        value = MethodUtils.invokeMethod(entity, fieldNameFrom.substring(0, fieldNameFrom.length() - 2));
                    } else {
                        value = FieldUtils.readField(entity, fieldNameFrom, true);
                    }

                    if (value != null && value instanceof HibernateProxy) {
                        value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
                    }

//                    log.trace("Value retrieved: {}", value);
                } else {
                    String[] fieldNames = fieldNameFrom.split("\\.");

                    Object fieldValue = entity;
                    for (String fieldName : fieldNames) {
//                        log.trace("Fetching value of property {}", fieldName);
                        if (fieldName.endsWith("()")) {
                            fieldValue = MethodUtils.invokeMethod(fieldValue, fieldName.substring(0, fieldName.length() - 2));
                        } else {
                            fieldValue = FieldUtils.readField(fieldValue, fieldName, true);
                        }
//                        log.trace("Value retrieved: {}", fieldValue);
                        if (fieldValue == null) {
                            break;
                        }
                        if (fieldValue instanceof HibernateProxy) {
                            fieldValue = ((HibernateProxy) fieldValue).getHibernateLazyInitializer().getImplementation();
                        }
                    }
                    value = fieldValue;
//                    log.trace("Final value retrieved, {}: {}", fieldNameFrom, value);
                }

                if (value != null && (value instanceof IEntity || value.getClass().isAnnotationPresent(Embeddable.class))) {
                    value = convertObjectToFieldMap(value);
                }

                // Set value to json
                if (!fieldNameTo.contains(".")) {
                    jsonValueMap.put(fieldNameTo, value);

                } else {
                    String[] fieldNames = fieldNameTo.split("\\.");
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

        // Set custom field values if applicable
        if (entity instanceof ICustomFieldEntity && ((ICustomFieldEntity) entity).getCfValues() != null) {

            ICustomFieldEntity cfEntity = (ICustomFieldEntity) entity;

            for (Entry<String, List<CustomFieldValue>> cfValueInfo : cfEntity.getCfValues().getValuesByCode().entrySet()) {

                if (cfValueInfo.getValue().isEmpty()) {
                    continue;
                }

                // At the moment does not handle versioned values - just take the first value
                Object value = cfValueInfo.getValue().get(0).getValue();
                if (value instanceof Map || value instanceof EntityReferenceWrapper) {
                    value = JsonUtils.toJson(value, false);
                }

                if (cftIndexable != null && cftIndexable.contains(entity.getClass().getName() + "_" + cfValueInfo.getKey())) {
                    jsonValueMap.put(cfValueInfo.getKey(), value);

                } else if (cftNotIndexable != null && cftNotIndexable.contains(entity.getClass().getName() + "_" + cfValueInfo.getKey())) {
                    continue;

                } else {
                    CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(cfValueInfo.getKey(), (ICustomFieldEntity) entity);
                    if (cft != null && cft.getIndexType() != null) {
                        if (cftIndexable != null) {
                            cftIndexable.add(entity.getClass().getName() + "_" + cfValueInfo.getKey());
                        }
                        jsonValueMap.put(cfValueInfo.getKey(), value);
                    } else if (cftNotIndexable != null) {
                        cftNotIndexable.add(entity.getClass().getName() + "_" + cfValueInfo.getKey());
                    }
                }
            }
        }
        return jsonValueMap;
    }

    /**
     * Convert object to a map of fields (recursively).
     * 
     * @param valueToConvert Object to convert
     * @return A map of fieldnames and values
     * @throws IllegalAccessException illegal access exception.
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

            if (value != null && value instanceof HibernateProxy) {
                value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
            }

            if (value != null && (value instanceof IEntity || value.getClass().isAnnotationPresent(Embeddable.class))) {
                fieldValueMap.put(field.getName(), convertObjectToFieldMap(value));
            } else {
                fieldValueMap.put(field.getName(), value);
            }
        }
        return fieldValueMap;
    }

    /**
     * Make a REST call to drop all indexes (all providers).
     * 
     * @throws BusinessException business exception
     */
    public void dropAllIndexes() throws BusinessException {

        log.debug("Dropping all Elastic Search indexes");
        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(uri + "/*/");

        Response response = target.request().delete();
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {

            String deleteIndexResponse = response.readEntity(String.class);
            response.close();
            log.error("Failed to delete all indexes in URL {}. Response {}", target.getUri(), deleteIndexResponse);

            throw new BusinessException(
                "Failed to communicate or process data in Elastic Search. Http status " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
        }
    }

    /**
     * Make a REST call to drop all indexes of a current provider. Index names are prefixed by provider code (removed spaces and lowercase).
     *
     * @throws BusinessException business exception
     */
    public void dropIndexes() throws BusinessException {

        String indexPrefix = ElasticClient.cleanUpAndLowercaseCode(appProvider.getCode());

        log.debug("Dropping all Elastic Search indexes with prefix {}", indexPrefix);
        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();

        // Create indexes
        for (Entry<String, String> model : esConfiguration.getDataModel().entrySet()) {
            String indexName = model.getKey().replace(INDEX_PROVIDER_PREFIX, indexPrefix);

            ResteasyWebTarget target = client.target(uri + "/" + indexName + "/");

            Response response = target.request().delete();
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                String deleteIndexResponse = response.readEntity(String.class);
                // Index might not exist yet
                if (deleteIndexResponse == null || !deleteIndexResponse.contains("index_not_found_exception")) {

                    log.error("Failed to delete an index in URL {}. Response {}", target.getUri(), deleteIndexResponse);

                    response.close();
                    throw new BusinessException("Failed to communicate or process data in Elastic Search. Url: " + target.getUri() + " Http status " + response.getStatus() + " "
                            + response.getStatusInfo().getReasonPhrase());
                }
            }
            response.close();
        }
    }

    /**
     * Recreate indexes for a current provider. Index names are prefixed by provider code (removed spaces and lowercase).
     * 
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createIndexes() throws BusinessException {

        String indexPrefix = ElasticClient.cleanUpAndLowercaseCode(appProvider.getCode());

        log.debug("Creating Elastic Search indexes with prefix {}", indexPrefix);
        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();

        // Create indexes
        for (Entry<String, String> model : esConfiguration.getDataModel().entrySet()) {
            String indexName = model.getKey().replace(INDEX_PROVIDER_PREFIX, indexPrefix);
            String modelJson = model.getValue().replace(INDEX_PROVIDER_PREFIX, indexPrefix);

            ResteasyWebTarget target = client.target(uri + "/" + indexName);

            log.debug("Creating index for entity: {}", indexName);
            log.debug("Index settings: {}", modelJson);

            Response response = target.request().put(javax.ws.rs.client.Entity.entity(modelJson, MediaType.APPLICATION_JSON_TYPE));
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {

                String createIndexResponse = response.readEntity(String.class);
                response.close();
                log.error("Failed to create an index in URL {}. Response {}", target.getUri(), createIndexResponse);

                throw new BusinessException(
                    "Failed to create index " + indexName + " in Elastic Search. Http status " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
            } else {
                response.close();
            }
        }

        log.trace("Creating Elastic Search mappings for CETs with prefix {}", indexPrefix);

        // Create mappings for custom entity templates
        List<CustomEntityTemplate> cets = customEntityTemplateService.listNoCache();
        for (CustomEntityTemplate cet : cets) {
            createCETMapping(cet);
        }

        log.trace("Updating Elastic Search mappings for CFTs with prefix {}", indexPrefix);

        // Update model mapping with custom fields
        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForIndex();
        for (CustomFieldTemplate cft : cfts) {
            updateCFMapping(cft);
        }

    }

    /**
     * Update Elastic Search model with custom entity template definition.
     * 
     * @param cet Custom entity template
     * @throws BusinessException business exception
     */
    public void createCETMapping(CustomEntityTemplate cet) throws BusinessException {

        String index = esConfiguration.getIndex(CustomEntityInstance.class);
        // Not interested in storing and indexing this entity in Elastic Search
        if (index == null) {
            log.warn("No matching index found for CET {}", cet);
            return;
        }

        String type = esConfiguration.getType(CustomEntityInstance.class, cet.getCode());

        String fieldMappingJson = esConfiguration.getCetMapping(cet);
        if (fieldMappingJson == null) {
            log.warn("No matching field mapping found for CET {}", cet);
            return;
        }

        log.debug("fieldMappingJson: {}", fieldMappingJson);

        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(uri + "/" + index + "/_mapping/" + type);

        Response response = target.request().put(javax.ws.rs.client.Entity.entity(fieldMappingJson, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            String updateIndexResponse = response.readEntity(String.class);
            response.close();
            log.error("Failed to update an index in URL {}. Response {}", target.getUri(), updateIndexResponse);

            log.error("Failed to update {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
            throw new BusinessException(
                "Failed to update " + index + "/_mapping/" + type + " in Elastic Search. Http status " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
        } else {
            response.close();
            log.info("Updated {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
        }

    }

    /**
     * Update Elastic Search model with custom field definition.
     * 
     * @param cft Custom field template
     * @throws BusinessException business exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void updateCFMapping(CustomFieldTemplate cft) throws BusinessException {

        // Not interested in indexing
        if (cft.getIndexType() == null) {
            return;
        }

        String fieldMappingJson = esConfiguration.getCustomFieldMapping(cft);
        if (fieldMappingJson == null) {
            log.warn("No matching field mapping found for CFT {}", cft);
            return;
        }

        Set<Class<?>> cfClasses = ReflectionUtils.getClassesAnnotatedWith(CustomFieldEntity.class);
        Class entityClass = null;
        String entityCode = null;
        for (Class<?> clazz : cfClasses) {
            if (cft.getAppliesTo().startsWith(clazz.getAnnotation(CustomFieldEntity.class).cftCodePrefix())) {
                entityClass = clazz;
                entityCode = EntityCustomizationUtils.getEntityCode(cft.getAppliesTo());
            }
        }

        if (entityClass == null) {
            log.error("Could not find a matching entity class for {}", cft);
            return;

        } else if (!BusinessEntity.class.isAssignableFrom(entityClass)) {
            log.trace("Entity class {} matched for {} is not BusinessEntity and is not tracked by Elastic Search", entityClass, cft);
            return;
        }

        String index = esConfiguration.getIndex(entityClass);
        // Not interested in storing and indexing this entity in Elastic Search
        if (index == null) {
            return;
        }

        String type = esConfiguration.getType(entityClass, entityCode);

        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(uri + "/" + index + "/_mapping/" + type);

        Response response = target.request().put(javax.ws.rs.client.Entity.entity(fieldMappingJson, MediaType.APPLICATION_JSON_TYPE));
        response.close();
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {

            String updateIndexResponse = response.readEntity(String.class);
            response.close();

            log.error("Failed to update {}/{} mapping in Elastic Search with field mapping {}. Response {}", index, type, fieldMappingJson, updateIndexResponse);
            throw new BusinessException(
                "Failed to update " + index + "/_mapping/" + type + " in Elastic Search. Http status " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
        } else {
            log.error("Updated {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
        }
    }
}