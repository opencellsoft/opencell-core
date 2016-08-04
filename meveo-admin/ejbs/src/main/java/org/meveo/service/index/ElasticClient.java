package org.meveo.service.index;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.Embeddable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.service.index.ElasticSearchChangeset.ElasticSearchAction;
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

    public static int DEFAULT_PAGE_SIZE = 10;

    @Inject
    private Logger log;

    @Inject
    private ElasticClientQueuedChanges queuedChanges;

    private ParamBean paramBean = ParamBean.getInstance();

    private TransportClient client = null;

    private ElasticSearchConfiguration esConfiguration = new ElasticSearchConfiguration();

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
            portStr = paramBean.getProperty("elasticsearch.port", "9200");
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
     * @param currentUser Current user
     */
    public void createOrFullUpdate(BusinessEntity entity, User currentUser) {

        createOrUpdate(entity, false, currentUser);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     * 
     * @param entity Entity to store in Elastic Search via partial update
     * @param currentUser Current user
     */
    public void partialUpdate(BusinessEntity entity, User currentUser) {
        createOrUpdate(entity, true, currentUser);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     * 
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only
     * @param valuesToUpdate A map of fieldname and values to update in entity
     * @param currentUser Current user
     */
    public void partialUpdate(BusinessEntity entity, String fieldName, Object fieldValue, User currentUser) {

        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put(fieldName, fieldValue);
        partialUpdate(entity, fieldsToUpdate, currentUser);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     * 
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only
     * @param fieldsToUpdate A map of fieldname and values to update in entity
     * @param currentUser Current user
     */
    public void partialUpdate(BusinessEntity entity, Map<String, Object> fieldsToUpdate, User currentUser) {

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
            id = cleanUpCode(entity.getCode());

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
     * @param currentUser Current user
     */
    private void createOrUpdate(BusinessEntity entity, boolean partialUpdate, User currentUser) {

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
            id = cleanUpCode(entity.getCode());
            boolean upsert = esConfiguration.isDoUpsert(entity);

            ElasticSearchAction action = upsert ? ElasticSearchAction.UPSERT : partialUpdate ? ElasticSearchAction.UPDATE : ElasticSearchAction.ADD_REPLACE;

            Map<String, Object> jsonValueMap = convertEntityToJson(entity);

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
     * @param currentUser Current user
     */
    public void remove(BusinessEntity entity, User currentUser) {

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
            id = cleanUpCode(entity.getCode());

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
     * Convert entity to a map of values that is accepted by Elastic Search as document to be stored and indexed
     * 
     * @param entity Entity to store in Elastic Search
     * @return A map of values
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertEntityToJson(BusinessEntity entity) {

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
                    value = FieldUtils.readField(entity, fieldNameFrom, true);

                } else {
                    String fieldNames[] = fieldNameFrom.split("\\.");

                    Object fieldValue = entity;
                    for (String fieldName : fieldNames) {
                        fieldValue = FieldUtils.readField(fieldValue, fieldName, true);
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

            } catch (IllegalAccessException e) {
                log.error("Failed to access field {} of {}", fieldInfo.getValue(), ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()));
            }
        }
        return jsonValueMap;
    }

    /**
     * Execute a search on all fields (_all field)
     * 
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param currentUser Current user
     * @param clazzes Entity classes to match
     * @return Json result
     */
    @SuppressWarnings("unchecked")
    public String search(String query, Integer from, Integer size, User currentUser, Class<? extends BusinessEntity>... clazzes) {

        // Not clear where to look, return empty json
        if (clazzes.length == 0) {
            return "{}";
        }
        Set<String> indexes = esConfiguration.getIndexes(currentUser.getProvider(), clazzes);
        // None of the classes are stored in Elastic Search, return empty json
        if (indexes.isEmpty()) {
            return "{}";
        }
        Set<String> types = esConfiguration.getTypes(clazzes);

        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = DEFAULT_PAGE_SIZE;
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
     * @param clazzes Entity classes to match
     * @return Json result
     */
    @SuppressWarnings("unchecked")
    public String search(Map<String, String> queryValues, Integer from, Integer size, User currentUser, Class<? extends BusinessEntity>... clazzes) {

        // Not clear where to look, return empty json
        if (clazzes.length == 0) {
            return "{}";
        }
        Set<String> indexes = esConfiguration.getIndexes(currentUser.getProvider(), clazzes);
        // None of the classes are stored in Elastic Search, return empty json
        if (indexes.isEmpty()) {
            return "{}";
        }
        Set<String> types = esConfiguration.getTypes(clazzes);

        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = DEFAULT_PAGE_SIZE;
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

    private String cleanUpCode(String code) {

        code = code.replace(' ', '_');
        return code;
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
    public void clearAndReindex() {

        
        
    }
}