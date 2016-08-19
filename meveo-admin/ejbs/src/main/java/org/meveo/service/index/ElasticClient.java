package org.meveo.service.index;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.index.ElasticSearchChangeset.ElasticSearchAction;
import org.meveo.util.EntityCustomizationUtils;
import org.slf4j.Logger;

/**
 * Connect to an Elastic Search cluster
 * 
 * Provider code superseeds index names
 * 
 * @author smichea
 * 
 */
@Startup
@Singleton
public class ElasticClient {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static int DEFAULT_SEARCH_PAGE_SIZE = 10;
    public static int INDEX_POPULATE_PAGE_SIZE = 100;
    private static String INDEX_PROVIDER_PREFIX = "<provider>";

    @Inject
    private Logger log;

    @Inject
    private ElasticClientQueuedChanges queuedChanges;

    private ParamBean paramBean = ParamBean.getInstance();

    private TransportClient client = null;

    @Inject
    private ElasticSearchConfiguration esConfiguration;

    @EJB
    private ProviderService providerService;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private ElasticSearchIndexPopulationService elasticSearchIndexPopulationService;

    /**
     * Initialize Elastic Search client
     */
    @PostConstruct
    private void initES() {

        String clusterName = null;
        String[] hosts = null;
        String portStr = null;

        try {
            clusterName = paramBean.getProperty("elasticsearch.cluster.name", "");
            hosts = paramBean.getProperty("elasticsearch.hosts", "localhost").split(";");
            portStr = paramBean.getProperty("elasticsearch.port", "9300");
            String sniffingStr = paramBean.getProperty("elasticsearch.client.transport.sniff", "false").toLowerCase();
            if (!StringUtils.isBlank(portStr) && StringUtils.isNumeric(portStr) && (sniffingStr.equals("true") || sniffingStr.equals("false")) && !StringUtils.isBlank(clusterName)
                    && hosts.length > 0) {
                log.debug("Connecting to elasticSearch cluster {} and hosts {}, port {}", clusterName, StringUtils.join(hosts, ";"), portStr);
                boolean sniffing = Boolean.parseBoolean(sniffingStr);
                Settings settings = Settings.settingsBuilder().put("client.transport.sniff", sniffing).put("cluster.name", clusterName).build();
                client = TransportClient.builder().settings(settings).build();
                int port = Integer.parseInt(portStr);
                for (String host : hosts) {
                    client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
                }
                List<DiscoveryNode> nodes = client.connectedNodes();
                if (nodes.isEmpty()) {
                    log.error("No nodes available. Verify ES is running!. Current settings: clusterName={}, hosts={}, port={}", clusterName, hosts, portStr);
                    shutdownES();
                } else {
                    log.debug("connected elasticsearch to {} nodes. Current settings: clusterName={}, hosts={}, port={}", nodes.size(), clusterName, hosts, portStr);
                }
            } else {
                log.warn("Elastic search is not enabled. Current settings: clusterName={}, hosts={}, port={}", clusterName, hosts, portStr);
            }

        } catch (Exception e) {
            log.error("Error while initializing elastic search. Current settings: clusterName={}, hosts={}, port={}", clusterName, hosts, portStr, e);
            shutdownES();
        }

        try {
            esConfiguration.loadConfiguration();
        } catch (Exception e) {
            log.error("Error while loading elastic search mapping configuration. Elastic search client will be shutdown", e);
            shutdownES();
        }
    }

    /**
     * Store and index entity in Elastic Search
     * 
     * @param entity Entity to store in Elastic Search
     */
    public void createOrFullUpdate(BusinessEntity entity) {

        createOrUpdate(entity, false);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     * 
     * @param entity Entity to store in Elastic Search via partial update
     */
    public void partialUpdate(BusinessEntity entity) {
        createOrUpdate(entity, true);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     * 
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only
     * @param valuesToUpdate A map of fieldname and values to update in entity
     */
    public void partialUpdate(BusinessEntity entity, String fieldName, Object fieldValue) {

        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put(fieldName, fieldValue);
        partialUpdate(entity, fieldsToUpdate);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     * 
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only
     * @param fieldsToUpdate A map of fieldname and values to update in entity
     */
    public void partialUpdate(BusinessEntity entity, Map<String, Object> fieldsToUpdate) {

        if (client == null) {
            return;
        }

        String index = null;
        String type = null;
        String id = null;
        try {

            index = esConfiguration.getIndex(entity);
            // Not interested in storing and indexing this entity in Elastic Search
            if (index == null) {
                return;
            }

            type = esConfiguration.getType(entity);
            id = ElasticClient.cleanUpCode(entity.getCode());

            ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.UPDATE, index, type, id, entity.getClass(), fieldsToUpdate);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);

        } catch (Exception e) {
            log.error("Failed to queue document in Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Store and index entity in Elastic Search
     * 
     * @param entity Entity to store in Elastic Search
     * @param partialUpdate Should it be treated as partial update instead of replace if document exists
     */
    private void createOrUpdate(BusinessEntity entity, boolean partialUpdate) {

        if (client == null) {
            return;
        }

        String index = null;
        String type = null;
        String id = null;
        try {

            index = esConfiguration.getIndex(entity);
            // Not interested in storing and indexing this entity in Elastic Search
            if (index == null) {
                return;
            }

            type = esConfiguration.getType(entity);
            id = ElasticClient.cleanUpCode(entity.getCode());
            boolean upsert = esConfiguration.isDoUpsert(entity);

            ElasticSearchAction action = upsert ? ElasticSearchAction.UPSERT : partialUpdate ? ElasticSearchAction.UPDATE : ElasticSearchAction.ADD_REPLACE;

            Map<String, Object> jsonValueMap = elasticSearchIndexPopulationService.convertEntityToJson(entity);

            ElasticSearchChangeset change = new ElasticSearchChangeset(action, index, type, id, entity.getClass(), jsonValueMap);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);

        } catch (Exception e) {
            log.error("Failed to queue document store to Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Remove entity from Elastic Search
     * 
     * @param entity Entity to remove from Elastic Search
     */
    public void remove(BusinessEntity entity) {

        if (client == null) {
            return;
        }

        String index = null;
        String type = null;
        String id = null;
        try {

            index = esConfiguration.getIndex(entity);
            // Not interested in storing and indexing this entity in Elastic Search
            if (index == null) {
                return;
            }

            type = esConfiguration.getType(entity);
            id = ElasticClient.cleanUpCode(entity.getCode());

            ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, index, type, id, entity.getClass(), null);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);

        } catch (Exception e) {
            log.error("Failed to queue document delete from Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Process pending changes to Elastic Search
     */
    public void flushChanges() {

        if (queuedChanges.isNoChange()) {
            log.trace("Nothing to flush to ES");
            return;
        }

        // Prepare bulk request
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        try {
            for (ElasticSearchChangeset change : queuedChanges.getQueuedChanges().values()) {

                if (change.getAction() == ElasticSearchAction.ADD_REPLACE) {
                    bulkRequest.add(client.prepareIndex(change.getIndex(), change.getType(), change.getId()).setSource(change.getSource()));

                } else if (change.getAction() == ElasticSearchAction.UPDATE) {
                    bulkRequest.add(client.prepareUpdate(change.getIndex(), change.getType(), change.getId()).setDoc(change.getSource()));

                } else if (change.getAction() == ElasticSearchAction.UPSERT) {
                    bulkRequest.add(client.prepareUpdate(change.getIndex(), change.getType(), change.getId()).setDoc(change.getSource()).setUpsert(change.getSource()));

                } else if (change.getAction() == ElasticSearchAction.DELETE) {
                    bulkRequest.add(client.prepareDelete(change.getIndex(), change.getType(), change.getId()));
                }
            }

            log.debug("Bulk processing {} action Elastic Search requests", bulkRequest.numberOfActions());

            // Execute bulk request
            BulkResponse bulkResponse = bulkRequest.get();
            for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                if (bulkItemResponse.getFailureMessage() != null) {
                    log.error("Failed to process {} in Elastic Search for {}/{}/{} reason: {}", bulkItemResponse.getOpType(), bulkItemResponse.getIndex(),
                        bulkItemResponse.getType(), bulkItemResponse.getId(), bulkItemResponse.getFailureMessage(), bulkItemResponse.getFailure().getCause());
                } else {
                    log.debug("Processed {} in Elastic Search for {}/{}/{} version: {}", bulkItemResponse.getOpType(), bulkItemResponse.getIndex(), bulkItemResponse.getType(),
                        bulkItemResponse.getId(), bulkItemResponse.getVersion());
                }
            }

            queuedChanges.clear();

        } catch (Exception e) {
            log.error("Failed to process bulk request in Elastic Search. Pending changes {}", queuedChanges.getQueuedChanges(), e);
        }
    }

    /**
     * Execute a search on all fields (_all field)
     * 
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param currentUser Current user
     * @param classInfo Entity classes to match
     * @return Json result
     */
    public String search(String query, Integer from, Integer size, User currentUser, List<ElasticSearchClassInfo> classInfo) {

        // Not clear where to look, return empty json
        if (classInfo.isEmpty()) {
            return "{}";
        }
        Set<String> indexes = esConfiguration.getIndexes(currentUser.getProvider(), classInfo);

        // None of the classes are stored in Elastic Search, return empty json
        if (indexes.isEmpty()) {
            return "{}";
        }
        Set<String> types = esConfiguration.getTypes(classInfo);

        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = DEFAULT_SEARCH_PAGE_SIZE;
        }

        log.debug("Execute Elastic Search search for {} records {}-{} on {}, {}", query, from, from + size, indexes, types);

        SearchRequestBuilder reqBuilder = client.prepareSearch(indexes.toArray(new String[0]));
        reqBuilder.setTypes(types.toArray(new String[0]));
        reqBuilder.setFrom(from);
        reqBuilder.setSize(size);

        if (StringUtils.isBlank(query)) {
            reqBuilder.setQuery(QueryBuilders.matchAllQuery());
        } else {
            reqBuilder.setQuery(QueryBuilders.simpleQueryStringQuery(query).defaultOperator(Operator.AND));
        }
        SearchResponse response = reqBuilder.execute().actionGet();

        String result = response.toString();
        return result;
    }

    /**
     * Execute a search on given fields for given values
     * 
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param currentUser Current user
     * @param classInfo Entity classes to match
     * @return Json result
     */
    public String search(Map<String, String> queryValues, Integer from, Integer size, User currentUser, List<ElasticSearchClassInfo> classInfo) {

        // Not clear where to look, return empty json
        if (classInfo.isEmpty()) {
            return "{}";
        }
        Set<String> indexes = esConfiguration.getIndexes(currentUser.getProvider(), classInfo);
        // None of the classes are stored in Elastic Search, return empty json
        if (indexes.isEmpty()) {
            return "{}";
        }
        Set<String> types = esConfiguration.getTypes(classInfo);

        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = DEFAULT_SEARCH_PAGE_SIZE;
        }

        log.debug("Execute Elastic Search search for {} records {}-{} on {}, {}", queryValues, from, from + size, indexes, types);

        SearchRequestBuilder reqBuilder = client.prepareSearch(indexes.toArray(new String[0]));
        reqBuilder.setTypes(types.toArray(new String[0]));

        reqBuilder.setFrom(from);
        reqBuilder.setSize(size);

        if (queryValues.isEmpty()) {
            reqBuilder.setQuery(QueryBuilders.matchAllQuery());

        } else if (queryValues.size() == 1) {
            Entry<String, String> fieldValue = queryValues.entrySet().iterator().next();
            if (fieldValue.getKey().contains(",")) {
                reqBuilder.setQuery(QueryBuilders.multiMatchQuery(fieldValue.getValue(), StringUtils.stripAll(fieldValue.getKey().split(","))));
            } else {
                reqBuilder.setQuery(QueryBuilders.matchQuery(fieldValue.getKey(), fieldValue.getValue()));
            }
        } else {

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            for (Entry<String, String> fieldValue : queryValues.entrySet()) {
                if (fieldValue.getKey().contains(",")) {
                    boolQuery.must(QueryBuilders.multiMatchQuery(fieldValue.getValue(), StringUtils.stripAll(fieldValue.getKey().split(","))));
                } else {
                    boolQuery.must(QueryBuilders.matchQuery(fieldValue.getKey(), fieldValue.getValue()));
                }
            }
            reqBuilder.setQuery(boolQuery);
        }
        SearchResponse response = reqBuilder.execute().actionGet();

        String result = response.toString();
        return result;
    }

    /**
     * Shutdown Elastic Search client
     */
    @PreDestroy
    private void shutdownES() {
        if (client != null) {
            try {
                client.close();
                client = null;
            } catch (Exception e) {
                log.error("Failed to close ES client", e);
            }
        }
    }

    /**
     * Get a list of entity classes that is managed by Elastic Search
     * 
     * @return A list of entity simple classnames
     */
    public Set<String> getEntityClassesManaged() {
        return esConfiguration.getEntityClassesManaged();
    }

    public boolean isEnabled() {
        return client != null;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> cleanAndReindex() throws BusinessException {

        log.info("Start to repopulate Elastic Search");

        ReindexingStatistics statistics = new ReindexingStatistics();

        try {
            // Drop all indexes
            dropIndexes();

            // Recreate all indexes
            createIndexes();

            // Repopulate index from DB

            // Process each class
            for (String classname : esConfiguration.getEntityClassesManaged()) {

                log.info("Start to populate Elastic Search with data from {} entity", classname);

                int from = 0;
                int totalProcessed = 0;
                boolean hasMore = true;

                while (hasMore) {
                    int found = elasticSearchIndexPopulationService.populateIndex(classname, from, statistics, client);

                    from = from + INDEX_POPULATE_PAGE_SIZE;
                    totalProcessed = totalProcessed + found;
                    hasMore = found == INDEX_POPULATE_PAGE_SIZE;
                }

                log.info("Finished populating Elastic Search with data from {} entity. Processed {} records.", classname, totalProcessed);
            }

            log.info("Finished repopulating Elastic Search");

        } catch (Exception e) {
            log.error("Failed to repopulate Elastic Search", e);
            statistics.setException(e);
        }

        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Make a REST call to drop all indexes
     * 
     * @throws BusinessException
     */
    private void dropIndexes() throws BusinessException {

        log.debug("Dropping all Elastic Search indexes");
        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(uri + "/*/");

        Response response = target.request().delete();
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            throw new BusinessException("Failed to communicate or process data in Elastic Search. Http status " + response.getStatus() + " "
                    + response.getStatusInfo().getReasonPhrase());
        }
    }

    /**
     * Recreate indexes for all providers
     * 
     * @throws BusinessException
     */
    private void createIndexes() throws BusinessException {

        // Recreate all indexes
        List<Provider> providers = providerService.list();

        for (Provider provider : providers) {
            createIndexes(provider);
        }
    }

    /**
     * Recreate index for a given provider
     * 
     * @param provider Provider
     * @throws BusinessException
     */
    public void createIndexes(Provider provider) throws BusinessException {

        log.debug("Creating Elastic Search indexes for provider {}", provider.getCode());

        ResteasyClient client = new ResteasyClientBuilder().build();

        String providerCode = ElasticClient.cleanUpCode(provider.getCode()).toLowerCase();

        // Create indexes
        for (Entry<String, String> model : esConfiguration.getDataModel().entrySet()) {
            String indexName = model.getKey().replace(INDEX_PROVIDER_PREFIX, providerCode);
            String modelJson = model.getValue().replace(INDEX_PROVIDER_PREFIX, providerCode);

            String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

            ResteasyWebTarget target = client.target(uri + "/" + indexName);

            Response response = target.request().put(javax.ws.rs.client.Entity.entity(modelJson, MediaType.APPLICATION_JSON_TYPE));
            response.close();
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                throw new BusinessException("Failed to create index " + indexName + " in Elastic Search. Http status " + response.getStatus() + " "
                        + response.getStatusInfo().getReasonPhrase());
            }
        }

        // Create mappings for custom entity templates
        List<CustomEntityTemplate> cets = customEntityTemplateService.list(provider);
        for (CustomEntityTemplate cet : cets) {
            createCETMapping(cet);
        }

        // Update model mapping with custom fields
        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForIndex(provider);
        for (CustomFieldTemplate cft : cfts) {
            updateCFMapping(cft);
        }

    }

    /**
     * Update Elastic Search model with custom entity template definition
     * 
     * @param cet Custom entity template
     * @throws BusinessException
     */
    public void createCETMapping(CustomEntityTemplate cet) throws BusinessException {

        String index = esConfiguration.getIndex(CustomEntityInstance.class, cet.getProvider());
        // Not interested in storing and indexing this entity in Elastic Search
        if (index == null) {
            return;
        }

        String type = esConfiguration.getType(CustomEntityInstance.class, cet.getCode());

        String fieldMappingJson = esConfiguration.getCetMapping(cet);
        if (fieldMappingJson == null) {
            log.warn("No matching field mapping found for CET {}", cet);
            return;
        }

        String uri = paramBean.getProperty("elasticsearch.restUri", "http://localhost:9200");

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(uri + "/" + index + "/_mapping/" + type);

        Response response = target.request().put(javax.ws.rs.client.Entity.entity(fieldMappingJson, MediaType.APPLICATION_JSON_TYPE));
        response.close();
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            log.error("Failed to update {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
            throw new BusinessException("Failed to update " + index + "/_mapping/" + type + " in Elastic Search. Http status " + response.getStatus() + " "
                    + response.getStatusInfo().getReasonPhrase());
        } else {
            log.error("Updated {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
        }

    }

    /**
     * Update Elastic Search model with custom field definition
     * 
     * @param cft Custom field template
     * @throws BusinessException
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

        String index = esConfiguration.getIndex(entityClass, cft.getProvider());
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
            log.error("Failed to update {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
            throw new BusinessException("Failed to update " + index + "/_mapping/" + type + " in Elastic Search. Http status " + response.getStatus() + " "
                    + response.getStatusInfo().getReasonPhrase());
        } else {
            log.error("Updated {}/{} mapping in Elastic Search with field mapping {}", index, type, fieldMappingJson);
        }

    }

    protected TransportClient getClient() {
        return client;
    }

    protected static String cleanUpCode(String code) {

        code = code.replace(' ', '_');
        return code;
    }
}