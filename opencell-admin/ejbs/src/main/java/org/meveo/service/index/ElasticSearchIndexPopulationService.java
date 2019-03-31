package org.meveo.service.index;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.infinispan.Cache;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.customEntities.CustomTableRecord;
import org.meveo.model.transformer.AliasToEntityOrderedMapResultTransformer;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.util.EntityCustomizationUtils;
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

    private static String INDEX_PROVIDER_PLACEHOLDER = "<provider>";

    private static String INDEX_INDEX_NAME_PLACEHOLDER = "<indexName>";

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
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    /**
     * A mapping between providerCode, classname, custom entity code (if applicable) and index name and type (if applicable)
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-es-index-cache")
    private Cache<ESIndexKey, String[]> indices;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    /**
     * A cache key: providerCode, classname, custom entity code (if applicable)
     */
    private class ESIndexKey implements Serializable {

        private static final long serialVersionUID = -7102146427175802501L;
        public String providerCode;
        public String classname;
        public String cetCode;

        /**
         * Constructor
         */
        public ESIndexKey() {
        }

        /**
         * Constructor
         * 
         * @param providerCode Provider code
         * @param classname Full class name
         * @param cetCode Custom entity template code
         */
        public ESIndexKey(String providerCode, String classname, String cetCode) {
            this.providerCode = providerCode;
            this.classname = classname;

            if (classname.equals(CustomTableRecord.class.getName())) {
                cetCode = BaseEntity.cleanUpAndLowercaseCodeOrId(cetCode);
            }
            this.cetCode = cetCode;
        }

        @Override
        public String toString() {
            return providerCode + ", " + classname + ", " + cetCode;
        }

        @Override
        public boolean equals(Object obj) {
            ESIndexKey other = (ESIndexKey) obj;

            if (other == null) {
                return false;
            }

            return (providerCode + "_" + classname + "_" + cetCode).equals(other.providerCode + "_" + other.classname + "_" + other.cetCode);
        }

        @Override
        public int hashCode() {
            return (providerCode + "_" + classname + "_" + cetCode).hashCode();
        }

        public boolean isMatchProvider(String codeToMatch) {
            return (providerCode == null && codeToMatch == null) || (providerCode != null && providerCode.equals(codeToMatch));
        }

    }

    /**
     * Populate index with data of a given entity class
     *
     * @param classname Entity full classname
     * @param fromId Populate starting record id
     * @param pageSize Number of records to retrieve
     * @param statistics Statistics to add progress info to
     * @return An array consisting of: Number of items added and last identifier processed
     * @throws BusinessException Communication with ES/bulk request execution exception
     */
    @SuppressWarnings("unchecked")
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] populateIndex(String classname, Object fromId, int pageSize, ReindexingStatistics statistics) throws BusinessException {

        Set<String> cftIndexable = new HashSet<>();
        Set<String> cftNotIndexable = new HashSet<>();

        Query query = getEntityManager().createQuery("select e from " + classname + " e where e.id>" + fromId + " order by e.id");
        query.setMaxResults(pageSize);

        List<? extends ISearchable> entities = query.getResultList();
        int found = entities.size();

        log.trace("Repopulating Elastic Search with records {}/+{} of {} entity", fromId, found, classname);

        if (entities.isEmpty()) {
            return new Object[] { 0, null };
        }

        boolean isCEI = classname.equals(CustomEntityInstance.class.getName());

        String[] indexAndType = null;
        String indexName = null;

        // For CEI index has to be looked for every entity as index and type might depend in custom entity template code
        if (!isCEI) {
            indexAndType = getIndexAndType(entities.get(0));
            indexName = indexAndType[0];
        }
        Object lastId = null;
        String idForES = null;

        // Process results

        // Prepare bulk request
        BulkRequest bulkRequest = new BulkRequest();

        // Convert entities to map of values and supplement it with custom field values if applicable and add to a bulk request
        for (ISearchable entity : entities) {

            if (isCEI) {
                indexAndType = getIndexAndType(entity);
                indexName = indexAndType[0];
            }
            lastId = entity.getId();
            idForES = ElasticSearchIndexPopulationService.buildId(entity);
            if (indexAndType[1] != null) {
                idForES = indexAndType[1] + "_" + idForES;
            }

            Map<String, Object> valueMap = convertEntityToJson(entity, cftIndexable, cftNotIndexable, indexAndType[1]);

            try {
                bulkRequest.add(new IndexRequest(indexName, ElasticSearchConfiguration.MAPPING_DOC_TYPE, idForES).source(valueMap));
            } catch (Exception e) {
                log.error("Failed to prepare data for index operation {}/{} values:{}", indexName, idForES, valueMap);
                throw new BusinessException(e);
            }
        }

        // Execute bulk request

        int failedRequests = 0;
        BulkResponse bulkResponse;
        try {
            bulkResponse = esConnection.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("Failed to execute ES bulk request to populate index " + indexName, e);
        }

        for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
            if (bulkItemResponse.getFailureMessage() != null) {
                log.error("Failed to add document to Elastic Search for {}/{} reason: {}", bulkItemResponse.getIndex(), bulkItemResponse.getId(),
                    bulkItemResponse.getFailureMessage(), bulkItemResponse.getFailure().getCause());
                failedRequests++;
            }
        }

        statistics.updateStatistics(classname, found, failedRequests);
        return new Object[] { found, lastId };
    }

    private EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    /**
     * Convert entity to a map of values that is accepted by Elastic Search as document to be stored and indexed.
     * 
     * @param entity Entity to store in Elastic Search
     * @param cftIndexable Sets to track CFTs that are indexable. Used in massive initial ES population.
     * @param cftNotIndexable Sets to track CFTs that are not indexable. Used in massive initial ES population.
     * @param type Index type used to distinguish one entity type from another inside the same index. Optional, 'entityType' value wont be set if not provided.
     * @return A map of values
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> convertEntityToJson(ISearchable entity, Set<String> cftIndexable, Set<String> cftNotIndexable, String type) {

        Map<String, Object> jsonValueMap = new HashMap<String, Object>();

        if (type != null) {
            jsonValueMap.put(ElasticSearchConfiguration.MAPPING_FIELD_TYPE, type);
        }

        // A special case where values are already present as a map
        if (entity instanceof CustomTableRecord) {
            jsonValueMap.putAll(((CustomTableRecord) entity).getValues());
            return jsonValueMap;
        }

        // Maps fields between entity and json.
        Map<String, String> fields = esConfiguration.getFields(entity);
        String fieldNameTo = null;
        String fieldNameFrom = null;

        // log.trace("Processing entity: {}", entity);

        for (Entry<String, String> fieldInfo : fields.entrySet()) {

            fieldNameTo = fieldInfo.getKey();
            fieldNameFrom = fieldInfo.getValue();

            // log.trace("Mapping {} to {}", fieldNameFrom, fieldNameTo);

            Object value = null;
            try {
                // Obtain field value from entity
                if (!fieldNameFrom.contains(".")) {
                    // log.trace("Fetching value of property {}", fieldNameFrom);
                    if (fieldNameFrom.endsWith("()")) {
                        value = MethodUtils.invokeMethod(entity, fieldNameFrom.substring(0, fieldNameFrom.length() - 2));
                    } else {
                        value = FieldUtils.readField(entity, fieldNameFrom, true);
                    }

                    if (value != null && value instanceof HibernateProxy) {
                        value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
                    }

                    // log.trace("Value retrieved: {}", value);
                } else {
                    String[] fieldNames = fieldNameFrom.split("\\.");

                    Object fieldValue = entity;
                    for (String fieldName : fieldNames) {
                        // log.trace("Fetching value of property {}", fieldName);
                        if (fieldValue == null) {
                            break;
                        }
                        if (fieldName.endsWith("()")) {
                            // log.trace("Invoking method {}.{}", fieldValue.getClass().getSimpleName(), fieldName);
                            fieldValue = MethodUtils.invokeMethod(fieldValue, true, fieldName.substring(0, fieldName.length() - 2), ArrayUtils.EMPTY_OBJECT_ARRAY, null);
                        } else {
                            // log.trace("Reading property {}.{}", fieldValue.getClass().getSimpleName(), fieldName);
                            fieldValue = FieldUtils.readField(fieldValue, fieldName, true);
                        }

                        if (fieldValue == null) {
                            break;
                        }

                        if (fieldValue instanceof HibernateProxy) {
                            // log.trace("Fetching value through HibernateProxy {}.{}", fieldValue.getClass().getSimpleName(), fieldName);
                            fieldValue = ((HibernateProxy) fieldValue).getHibernateLazyInitializer().getImplementation();
                        }
                        // log.trace("Value retrieved: {}", fieldValue);
                    }
                    value = fieldValue;
                    // log.trace("Final value retrieved, {}: {}", fieldNameFrom, value);
                }

                // Process value further in case of certain data types - date, list or entity
                if (value != null) {
                    if (value instanceof Timestamp) {
                        value = ((Timestamp) value).getTime();
                    } else if (value instanceof java.sql.Date) {
                        value = ((java.sql.Date) value).getTime();
                    } else if (value instanceof Collection && !((Collection) value).isEmpty()) {
                        List values = new ArrayList<>();
                        for (Object val : ((Collection) value)) {
                            if (val instanceof Timestamp) {
                                values.add(((Timestamp) val).getTime());
                            } else if (val instanceof java.sql.Date) {
                                values.add(((java.sql.Date) val).getTime());
                            } else if (val.getClass().isAnnotationPresent(Embeddable.class)) {
                                values.add(convertObjectToFieldMap(val));
                            } else if (val instanceof IEntity) {
                                values.add(val.toString());
                            } else {
                                values.add(val);
                            }
                        }
                        value = values;
                    } else if (value.getClass().isAnnotationPresent(Embeddable.class)) {
                        value = convertObjectToFieldMap(value);
                    } else if (value instanceof IEntity) {
                        value = value.toString();
                    }
                }

                // Set value to json preserving the field hierarchy
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
        if (entity instanceof ICustomFieldEntity && ((ICustomFieldEntity) entity).getCfValues() != null && ((ICustomFieldEntity) entity).getCfValuesAsValues() != null) {

            ICustomFieldEntity cfEntity = (ICustomFieldEntity) entity;

            // At the moment does not handle versioned values - just take the today's value
            for (Entry<String, Object> cfValueInfo : cfEntity.getCfValuesAsValues().entrySet()) {

                String cfCode = cfValueInfo.getKey();
                Object value = cfValueInfo.getValue();
                if (value instanceof Map || value instanceof EntityReferenceWrapper) {
                    value = JsonUtils.toJson(value, false);
                }

                if (cftIndexable != null && cftIndexable.contains(entity.getClass().getName() + "_" + cfCode)) {
                    jsonValueMap.put(cfCode, value);

                } else if (cftNotIndexable != null && cftNotIndexable.contains(entity.getClass().getName() + "_" + cfCode)) {
                    continue;

                } else {
                    CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(cfCode, (ICustomFieldEntity) entity);
                    if (cft != null && cft.getIndexType() != null) {
                        if (cftIndexable != null) {
                            cftIndexable.add(entity.getClass().getName() + "_" + cfCode);
                        }
                        jsonValueMap.put(cfCode, value);

                    } else if (cftNotIndexable != null) {
                        cftNotIndexable.add(entity.getClass().getName() + "_" + cfCode);
                    }
                }
            }
        }

        // log.trace("Returning jsonValueMap: {}", jsonValueMap);
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

        // log.trace("valueToConvert: {}", valueToConvert);
        List<Field> fields = new ArrayList<Field>();
        ReflectionUtils.getAllFields(fields, valueToConvert.getClass());

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Auditable.class.isAssignableFrom(field.getType())) {
                continue;
            }

            Object value = FieldUtils.readField(field, valueToConvert, true);

            if (value != null && value instanceof HibernateProxy) {
                value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
            }

            if (value != null && value instanceof Timestamp) {
                fieldValueMap.put(field.getName(), ((Timestamp) value).getTime());
            } else if (value != null && value instanceof java.sql.Date) {
                fieldValueMap.put(field.getName(), ((java.sql.Date) value).getTime());
            } else if (value != null && (value instanceof IEntity || value.getClass().isAnnotationPresent(Embeddable.class))) {
                fieldValueMap.put(field.getName(), convertObjectToFieldMap(value));
            } else {
                fieldValueMap.put(field.getName(), value);
            }
        }
        // log.trace("fieldValueMap: {}", fieldValueMap);
        return fieldValueMap;
    }

    /**
     * Make a REST call to drop absolutely <b>ALL</b> indexes (all providers).
     * 
     * @throws BusinessException business exception
     */
    public void dropAllIndexes() throws BusinessException {

        log.debug("Dropping all Elastic Search indexes");
        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200").split(";")[0];

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
     * Make a REST call to drop indexes of a <b>current provider</b>. Index names are prefixed by provider code (removed spaces and lowercase).
     *
     * @throws BusinessException Failed to delete an index in ES exception
     */
    public void dropIndexes() throws BusinessException {

        String indexPrefix = currentUser.getProviderCode() == null ? "null" : BaseEntity.cleanUpAndLowercaseCodeOrId(currentUser.getProviderCode());

        log.debug("Dropping all Elastic Search indexes with prefix {}", indexPrefix);

        List<String> indexNames = getIndicesFromES();

        // Delete indices
        RestHighLevelClient client = esConnection.getClient();

        for (String indexName : indexNames) {

            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);

            try {
                @SuppressWarnings("unused")
                AcknowledgedResponse responseResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new BusinessException("Failed to delete index " + indexName + " in Elastic Search.", e);
            } catch (ElasticsearchStatusException e) {
                if (!(e.status() == RestStatus.NOT_FOUND && e.getMessage().contains("type=index_not_found_exception"))) {
                    throw new BusinessException("Failed to delete index " + indexName + " in Elastic Search.", e);
                }
                // index was not found, so continue on
            }
        }
    }

    /**
     * Recreate indexes for a <b>current provider</b>. Index names are prefixed by provider code (removed spaces and lowercase).
     * 
     * @throws BusinessException Failure to create index in ES exception.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createIndexes() throws BusinessException {

        String indexPrefix = currentUser.getProviderCode() == null ? "null" : BaseEntity.cleanUpAndLowercaseCodeOrId(currentUser.getProviderCode());

        log.debug("Creating Elastic Search indexes with prefix {}", indexPrefix);

        repopulateIndexAndTypeCache(false);

        RestHighLevelClient client = esConnection.getClient();

        // Create indexes
        CreateIndexRequest createIndexRequest = null;
        for (Entry<String, String> model : esConfiguration.getDataModel().entrySet()) {
            String indexName = model.getKey().replace(INDEX_PROVIDER_PLACEHOLDER, indexPrefix);
            String modelJson = model.getValue().replace(INDEX_PROVIDER_PLACEHOLDER, indexPrefix);

            log.debug("Creating index for entity: {} with model {}", indexName, modelJson);

            createIndexRequest = new CreateIndexRequest(indexName);
            createIndexRequest.source(modelJson, XContentType.JSON);

            try {
                @SuppressWarnings("unused")
                CreateIndexResponse createResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new BusinessException("Failed to create index " + indexName + " in Elastic Search.", e);
            }
        }

        log.trace("Creating Elastic Search mappings for CETs with prefix {}", indexPrefix);

        // Create mappings for custom entity templates
        List<CustomEntityTemplate> cets = customEntityTemplateService.listNoCache();
        for (CustomEntityTemplate cet : cets) {
            createCETIndex(cet);
        }

        log.trace("Updating Elastic Search mappings for CFTs with prefix {}", indexPrefix);

        // Update model mapping with custom fields
        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForIndex();
        for (CustomFieldTemplate cft : cfts) {
            updateCFMapping(cft);
        }

    }

    /**
     * Repopulate cache for a <b>current provider</b>, that stores index and type mapping to class and custom entity template code.
     * 
     * @param populateCets Should custom entity templates be included
     */
    public void repopulateIndexAndTypeCache(boolean populateCets) {

        // Clean up cache for current provider
        Set<ESIndexKey> cacheKeys = new HashSet<>();

        indices.forEach((key, value) -> {
            if (key.isMatchProvider(currentUser.getProviderCode())) {
                cacheKeys.add(key);
            }
        });

        for (ESIndexKey key : cacheKeys) {
            indices.remove(key);
        }

        // Recreate mapping again for regular classes
        for (String classname : esConfiguration.getEntityClassesManaged()) {

            if (!classname.equals(CustomTableRecord.class.getName()) && !classname.equals(CustomEntityInstance.class.getName())) {
                addToIndexAndTypeCache(classname, null);
            }
        }

        // Recreate mapping for custom entity templates - either custom tables, or custom entity instances
        List<CustomEntityTemplate> cets = customEntityTemplateService.listNoCache();
        for (CustomEntityTemplate cet : cets) {
            String classname = cet.isStoreAsTable() ? CustomTableRecord.class.getName() : CustomEntityInstance.class.getName();
            addToIndexAndTypeCache(classname, cet.getCode());
        }
    }

    /**
     * Update Elastic Search model with custom entity template definitions - depending on the configuration might create new index for each CET
     * 
     * @param cet Custom entity template
     * @throws BusinessException business exception
     */
    public void createCETIndex(CustomEntityTemplate cet) throws BusinessException {

        Class<? extends ISearchable> instanceClass = cet.isStoreAsTable() ? CustomTableRecord.class : CustomEntityInstance.class;
        String[] indexAndType = addToIndexAndTypeCache(instanceClass, cet.getCode());

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            log.warn("No matching index found for CET {}", cet);
            return;
        }

        String indexName = indexAndType[0];

        String modelJson = esConfiguration.getCetIndexConfiguration(cet);
        if (modelJson == null) {
            log.warn("No matching index mapping found for CET {}", cet);
            return;
        }

        // Check if index is not defined yet, and define it if thats the case
        boolean indexExists = false;

        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        try {
            GetIndexResponse response = esConnection.getClient().indices().get(getIndexRequest, RequestOptions.DEFAULT);
            indexExists = response.indices().length != 0;
        } catch (IOException e) {
            throw new BusinessException("Failed to get index " + indexName + " information in Elastic Search.", e);
        } catch (ElasticsearchStatusException e) {
            if (!(e.status() == RestStatus.NOT_FOUND && e.getMessage().contains("type=index_not_found_exception"))) {
                throw new BusinessException("Failed to find index " + indexName + " in Elastic Search.", e);
            }
            // index was not found, so continue on
        }

        if (!indexExists) {

            // Replace index name (already contains provider prefix) in index configuration json - this already contain provider prefix
            modelJson = modelJson.replaceAll(INDEX_INDEX_NAME_PLACEHOLDER, indexName);

            // Strip the actual index configuration by removing index name and use that index name in index creation (might contain non-alias name e.g. demo_custom_v1 vs
            // demo_custom as alias)
            int firstPos = modelJson.indexOf('"');
            String realIndexName = modelJson.substring(firstPos + 1, modelJson.indexOf('"', firstPos + 1));
            modelJson = modelJson.substring(modelJson.indexOf("{", realIndexName.length()), modelJson.lastIndexOf('}'));

            log.debug("Creating index {}/{} for Custom entity template: {} with model {}", realIndexName, indexName, cet.getCode(), modelJson);

            CreateIndexRequest createIndexRequest = new CreateIndexRequest(realIndexName);
            createIndexRequest.source(modelJson, XContentType.JSON);

            try {
                @SuppressWarnings("unused")
                CreateIndexResponse createResponse = esConnection.getClient().indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new BusinessException("Failed to create index " + realIndexName + "/" + indexName + " in Elastic Search.", e);
            }
        } else {
            log.debug("Index {} creation for Custom entity template: {} will be skipped as such index already exists", indexName, cet.getCode());
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

        } else if (!ISearchable.class.isAssignableFrom(entityClass)) {
            log.trace("Entity class {} matched for {} is not ISearchable and is not tracked by Elastic Search", entityClass, cft);
            return;
        }

        // For Custom tables (CFT is linked to CustomEntityInstance, but corresponding CustomEntityTemplate.storeAsTable=true)
        // CFT fieldname should be cleanedup and lowercased.
        // Entity class should be changed to CustomTableRecord
        boolean cleanupCFTFieldname = false;
        if (entityClass.isAssignableFrom(CustomEntityInstance.class)) {
            CustomEntityTemplate cet = customEntityTemplateService.findByCode(entityCode);
            if (cet == null) {
                log.trace("Custom entity template {} was not found", entityCode);
                return;
            }
            if (cet.isStoreAsTable()) {
                entityClass = CustomTableRecord.class;
                cleanupCFTFieldname = true;
            }
        }

        String fieldMappingJson = esConfiguration.getCustomFieldMapping(cft, cleanupCFTFieldname);
        if (fieldMappingJson == null) {
            log.warn("No matching field mapping found for CFT {}", cft);
            return;
        }

        String[] indexAndType = getIndexAndType(entityClass, entityCode);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        String indexName = indexAndType[0];

        log.debug("Updating index {} mapping with custom field {} mapping {}", indexName, cleanupCFTFieldname, fieldMappingJson);

        PutMappingRequest putMappingRequest = new PutMappingRequest(indexName);
        putMappingRequest.type("_doc");
        putMappingRequest.source(fieldMappingJson, XContentType.JSON);

        try {
            @SuppressWarnings("unused")
            AcknowledgedResponse response = esConnection.getClient().indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("Failed to update index " + indexName + " mapping in Elastic Search.", e);
        }
    }

    /**
     * Get a number of records in a given db table
     * 
     * @param tableName Native table name
     * @return Number of records
     */
    public int getRecordCountInNativeTable(String tableName) {

        Object count = getEntityManager().createNativeQuery("select count(*) from " + tableName).getSingleResult();

        if (count instanceof BigInteger) {
            return ((BigInteger) count).intValue();
        } else if (count instanceof Long) {
            return ((Long) count).intValue();
        } else {
            return (Integer) count;
        }
    }

    /**
     * Populate index with data of a given db table
     *
     * @param tableName Native table name
     * @param fromId Populate starting record id
     * @param pageSize Number of records to retrieve. Value of -1 will retrieve all remaining records
     * @param statistics Statistics to add progress info to
     * @return An array consisting of: Number of items added and last identifier processed
     * @throws BusinessException Communication with ES/bulk request execution exception
     */
    @SuppressWarnings("unchecked")
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] populateIndexFromNativeTable(String tableName, Object fromId, int pageSize, ReindexingStatistics statistics) throws BusinessException {

        Session session = getEntityManager().unwrap(Session.class);
        SQLQuery query = session.createSQLQuery("select * from " + tableName + " e where e.id>" + fromId + " order by e.id");
        query.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);
        if (pageSize > -1) {
            query.setMaxResults(pageSize);
        }
        List<Map<String, Object>> entities = query.list();

        int found = entities.size();

        log.trace("Populating Elastic Search with records {}/+{} of {} table", fromId, found, tableName);

        if (entities.isEmpty()) {
            return new Object[] { 0, null };
        }

        String[] indexAndType = getIndexAndType(CustomTableRecord.class, tableName);
        String indexName = indexAndType[0];

        // Process results

        // Prepare bulk request
        BulkRequest bulkRequest = new BulkRequest();

        Object lastId = null;
        String idForES = null;

        // Add map of values
        for (Map<String, Object> values : entities) {

            Map<String, Object> convertedValues = new HashMap<>(values);

            // Replace sql.Timestamp and sql.Date with a numeric value
            for (Entry<String, Object> valueInfo : values.entrySet()) {

                Object val = valueInfo.getValue();

                if (val != null && val instanceof Timestamp) {
                    convertedValues.put(valueInfo.getKey(), ((Timestamp) val).getTime());
                } else if (val != null && val instanceof java.sql.Date) {
                    convertedValues.put(valueInfo.getKey(), ((java.sql.Date) val).getTime());
                }
            }

            lastId = values.get(NativePersistenceService.FIELD_ID);

            idForES = lastId.toString();
            if (indexAndType[1] != null) {
                idForES = indexAndType[1] + "_" + idForES;
                values.put(ElasticSearchConfiguration.MAPPING_FIELD_TYPE, indexAndType[1]);
            }

            bulkRequest.add(new IndexRequest(indexName, ElasticSearchConfiguration.MAPPING_DOC_TYPE, idForES).source(convertedValues));
        }

        // Execute bulk request

        int failedRequests = 0;
        BulkResponse bulkResponse;
        try {
            bulkResponse = esConnection.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("Failed to execute ES bulk request to populate index " + indexName, e);
        }

        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                if (bulkItemResponse.getFailureMessage() != null) {
                    log.error("Failed to add document to Elastic Search for {}/{} reason: {}", bulkItemResponse.getIndex(), bulkItemResponse.getId(),
                        bulkItemResponse.getFailureMessage(), bulkItemResponse.getFailure().getCause());
                    failedRequests++;
                }
            }
        }

        statistics.updateStatistics(tableName, found, failedRequests);
        return new Object[] { found, lastId };
    }

    /**
     * Remove custom entity template definition from Elastic Search model. Applies only for cases where each custom entity template has its own index - that is no type is used.
     * 
     * @param cet Custom entity template
     * @throws BusinessException business exception
     */
    public void removeCETIndex(CustomEntityTemplate cet) throws BusinessException {

        Class<? extends ISearchable> instanceClass = cet.isStoreAsTable() ? CustomTableRecord.class : CustomEntityInstance.class;
        String[] indexAndType = getIndexAndType(instanceClass, cet.getCode());

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            log.warn("No matching index found for CET {}", cet.getCode());
            return;

        } else if (indexAndType[1] != null) {
            log.warn("CET {} shares an index with other entity types and therefore index won't be removed", cet.getCode());
            return;
        }

        String indexName = indexAndType[0];

        // Find what is a real index name, as we know only the alias
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        try {
            GetIndexResponse response = esConnection.getClient().indices().get(getIndexRequest, RequestOptions.DEFAULT);
            indexName = response.indices()[0];
        } catch (IOException e) {
            throw new BusinessException("Failed to get index " + indexName + " information in Elastic Search.", e);
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND && e.getMessage().contains("type=index_not_found_exception")) {
                // Index was not found, so there is nothing to remove
                return;
            }
            throw new BusinessException("Failed to find index " + indexName + " in Elastic Search.", e);
        }

        // Delete index in Elastic search and also remove it from cached index names
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            esConnection.getClient().indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            removeFromIndexAndTypeCache(instanceClass, cet.getCode());

        } catch (IOException e) {
            throw new BusinessException("Failed to delete index " + indexName + " information in Elastic Search.", e);
        } catch (ElasticsearchStatusException e) {
            if (!(e.status() == RestStatus.NOT_FOUND && e.getMessage().contains("type=index_not_found_exception"))) {
                throw new BusinessException("Failed to delete index " + indexName + " in Elastic Search.", e);
            }
            // index was not found, so ignore
        }

        log.debug("Deleted index {} for Custom entity template: {}", indexName, cet.getCode());
    }

    /**
     * Get all full index names as defined in Elastic search for a <b>current provider</b>, that is those indexes which name starts with a provider code (removed spaces and ). For
     * a mian provider a string value of 'null' is used.
     * 
     * @return A list of full index names
     * @throws BusinessException Elastic search mapping can not be accessed
     */
    private List<String> getIndicesFromES() throws BusinessException {

        // Get a list of indexes, and then filter with provider prefix
        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200").split(";")[0];

        ResteasyClient rsClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = rsClient.target(uri + "/_mapping");

        List<String> indexNames = new ArrayList<>();

        String indexPrefix = currentUser.getProviderCode() == null ? "null" : BaseEntity.cleanUpAndLowercaseCodeOrId(currentUser.getProviderCode());

        Response response = target.request().get();
        if (response.getStatus() == HttpURLConnection.HTTP_OK) {

            @SuppressWarnings("rawtypes")
            Map indexMappings = response.readEntity(Map.class);
            for (Object indexName : indexMappings.keySet()) {
                if (((String) indexName).startsWith(indexPrefix)) {
                    indexNames.add((String) indexName);
                }
            }
        } else {
            log.error("Failed to obtain current Elastic search mapping information. Response obtained: {}", response.readEntity(String.class));
            throw new BusinessException("Failed to obtain current Elastic Search mapping information");
        }

        return indexNames;
    }

    /**
     * Get a unique list of indexes and type for given entity classes. All indexes of a <b>current provider</b> will be returned if no class information is provided.
     * 
     * @param classesInfo A list of entity class information
     * @return A set of arrays with full index name and type names in that order. Index names are prefixed by provider code (removed spaces and lowercase).
     */
    public Set<String[]> getIndexAndTypes(List<ElasticSearchClassInfo> classesInfo) {

        Set<String[]> indexes = new HashSet<>();

        if (classesInfo == null || classesInfo.isEmpty()) {

            indices.forEach((key, value) -> {
                if (key.isMatchProvider(currentUser.getProviderCode())) {
                    indexes.add(value);
                }
            });

        } else {
            for (ElasticSearchClassInfo classInfo : classesInfo) {
                String[] indexAndType = getIndexAndType(classInfo.getClazz(), classInfo.getCetCode());
                if (indexAndType != null) {
                    indexes.add(indexAndType);
                }
            }
        }
        return indexes;
    }

    /**
     * Determine index and type value for Elastic Search for a given class. Index names are prefixed by provider code (removed spaces and lowercase).
     * 
     * @param clazzToConvert Entity class that extends ISearchable interface
     * @param cetCode Custom entity template/custom table code
     * @return An array with full index name and type in that order. Or null if no match was found e.g. not interested in storing in ES. Index names are prefixed by provider code
     *         (removed spaces and lowercase).
     */
    public String[] getIndexAndType(Class<? extends ISearchable> clazzToConvert, String cetCode) {

        String classname = ReflectionUtils.getCleanClassName(clazzToConvert.getName());

        return indices.get(new ESIndexKey(currentUser.getProviderCode(), classname, cetCode));
    }

    /**
     * Determine index and type value for Elastic Search for a given entity. Index names are prefixed by provider code (removed spaces and lowercase).
     * 
     * @param entity ISearchable entity to be stored/indexed in Elastic Search
     * @return An array with full index name and type in that order. Index names are prefixed by provider code (removed spaces and lowercase).
     */
    public String[] getIndexAndType(ISearchable entity) {
        String cetCode = null;
        if (entity instanceof CustomEntityInstance) {
            cetCode = ((CustomEntityInstance) entity).getCetCode();
        } else if (entity instanceof CustomTableRecord) {
            cetCode = ((CustomTableRecord) entity).getCetCode();
        }
        return getIndexAndType(entity.getClass(), cetCode);
    }

    /**
     * Determine index and type value for Elastic Search for a given class and store it in indices cache
     * 
     * @param clazz Entity class that extends ISearchable interface
     * @param cetCode Custom entity template/custom table code
     * @return An array with a full index name and type in that order
     */
    private String[] addToIndexAndTypeCache(Class<? extends ISearchable> clazz, String cetCode) {

        String classname = ReflectionUtils.getCleanClassName(clazz.getName());
        return addToIndexAndTypeCache(classname, cetCode);
    }

    /**
     * Determine index and type value for Elastic Search for a given class and store it in indices cache
     * 
     * @param clazzToConvert Entity class that extends ISearchable interface
     * @param cetCode Custom entity template/custom table code
     * @return An array with a full index name and type in that order
     */
    private String[] addToIndexAndTypeCache(String classname, String cetCode) {

        String indexName = esConfiguration.getIndexName(classname);

        // No index, no interest in ES
        if (indexName == null) {
            return null;
        }

        String type = esConfiguration.getType(classname);

        if (cetCode != null) {
            if (indexName.startsWith("#")) {
                indexName = ValueExpressionWrapper.evaluateToStringIgnoreErrors(indexName, "cetCode", cetCode);
            }
            if (type != null && type.startsWith("#")) {
                type = ValueExpressionWrapper.evaluateToStringIgnoreErrors(type, "cetCode", cetCode);
            }
        }

        indexName = currentUser.getProviderCode() + "_" + indexName;

        String[] indexAndType = new String[] { BaseEntity.cleanUpAndLowercaseCodeOrId(indexName), type };
        indices.put(new ESIndexKey(currentUser.getProviderCode(), classname, cetCode), indexAndType);

        return indexAndType;
    }

    /**
     * Remove index and type value mapping for a given class from indices cache
     * 
     * @param clazzToConvert Entity class that extends ISearchable interface
     * @param cetCode Custom entity template/custom table code
     */
    private void removeFromIndexAndTypeCache(Class<? extends ISearchable> clazzToConvert, String cetCode) {

        String classname = ReflectionUtils.getCleanClassName(clazzToConvert.getName());
        indices.remove(new ESIndexKey(currentUser.getProviderCode(), classname, cetCode));
    }

    /**
     * Determine a classname and a Custom entity template code from index name and entity type
     * 
     * @param fullIndexName Full index name
     * @param type Entity type value as stored in Elastic search datatable value
     * @param Entity type as stored in Elastic search index
     * @return An array with a full classname and a Custom entity code (when applicable). OR null if no match was found.
     */
    public String[] getClassnameAndCETCodeFromIndex(String fullIndexName, String type) {

        for (Entry<ESIndexKey, String[]> keyValue : indices.entrySet()) {
            ESIndexKey key = keyValue.getKey();
            String[] value = keyValue.getValue();

            if ((value[0].equals(fullIndexName) || fullIndexName.startsWith(value[0])) && ((value[1] == null && type == null) || (value[1] != null && value[1].equals(type)))
                    && (key.isMatchProvider(currentUser.getProviderCode()))) {
                return new String[] { key.classname, key.cetCode };
            }
        }

        return null;
    }

    /**
     * Construct identifier for Elastic search for a given entity.
     * 
     * @param entity Entity to construct ID for
     * @return Identifier value. Its either code, id or <code>_<id> of an entity
     */
    protected static String buildId(ISearchable entity) {
        if (entity instanceof BusinessEntity) {
            return BaseEntity.cleanUpCodeOrId(entity.getCode());
        } else if (entity instanceof CustomTableRecord) {
            return ((CustomTableRecord) entity).getId().toString();
        } else {
            return BaseEntity.cleanUpCodeOrId(entity.getCode() + "__" + entity.getId());
        }
    }

    /**
     * Get a summary of cached information.
     * 
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(indices.getName(), indices);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name. Removes <b>current provider's</b> data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(indices.getName()) || cacheName.contains(indices.getName())) {
            repopulateIndexAndTypeCache(true);
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(indices.getName())) {
            repopulateIndexAndTypeCache(true);
        }
    }
}