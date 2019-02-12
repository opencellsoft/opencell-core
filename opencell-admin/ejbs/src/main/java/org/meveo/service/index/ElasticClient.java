package org.meveo.service.index;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.sort.SortOrder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.customEntities.CustomTableRecord;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.index.ElasticSearchChangeset.ElasticSearchAction;
import org.slf4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 * Provides functionality to interact with Elastic Search cluster
 *
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.0
 */
@Stateless
public class ElasticClient {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static int DEFAULT_SEARCH_PAGE_SIZE = 10;
    public static int INDEX_POPULATE_PAGE_SIZE = 100;

    @Inject
    private Logger log;

    @Inject
    private ElasticClientQueuedChanges queuedChanges;

    @Inject
    private ElasticSearchConfiguration esConfiguration;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private ElasticSearchIndexPopulationService elasticSearchIndexPopulationService;

    @Inject
    private CustomFieldsCacheContainerProvider cfCache;

    @Inject
    private ElasticClientConnection esConnection;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Store and index entity in Elastic Search. In case of update, a full update will be performed unless it is configured in elasticSearchConfiguration.json to always do upsert.
     *
     * @param entity Entity to store in Elastic Search
     */
    public void createOrFullUpdate(ISearchable entity) {

        createOrUpdate(entity, false);
    }

    /**
     * Apply a partial update to the entity in Elastic Search
     *
     * @param entity Entity to store in Elastic Search via partial update
     */
    public void partialUpdate(ISearchable entity) {
        createOrUpdate(entity, true);
    }

    /**
     * Apply a partial update to the entity in Elastic Search. Used to update CF field values of an entity
     *
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only
     * @param fieldName Field name
     * @param fieldValue Field value
     */
    public void partialUpdate(ISearchable entity, String fieldName, Object fieldValue) {

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
    public void partialUpdate(ISearchable entity, Map<String, Object> fieldsToUpdate) {

        if (!esConnection.isEnabled()) {
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
            id = BaseEntity.cleanUpCodeOrId(buildId(entity));

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
     * @param partialUpdate Should it be treated as partial update instead of replace if document exists. This value can be overridden in elasticSearchConfiguration.json to always
     *        do upsert.
     */
    private void createOrUpdate(ISearchable entity, boolean partialUpdate) {

        if (!esConnection.isEnabled()) {
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
            id = BaseEntity.cleanUpCodeOrId(buildId(entity));
            boolean upsert = esConfiguration.isDoUpsert(entity);

            ElasticSearchAction action = upsert ? ElasticSearchAction.UPSERT : partialUpdate ? ElasticSearchAction.UPDATE : ElasticSearchAction.ADD_REPLACE;

            Map<String, Object> jsonValueMap = elasticSearchIndexPopulationService.convertEntityToJson(entity, null, null);

            ElasticSearchChangeset change = new ElasticSearchChangeset(action, index, type, id, entity.getClass(), jsonValueMap);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);

        } catch (Exception e) {
            log.error("Failed to queue document store to Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Store and index entity/values in Elastic Search
     *
     * @param entityClass Entity class to store in Elastic Search
     * @param cetCode Custom entity template code
     * @param identifier Record identifier
     * @param values Values to store
     * @param partialUpdate Should it be treated as partial update instead of replace if document exists. This value can be overridden in elasticSearchConfiguration.json to always
     *        do upsert.
     * @param immediate True if changes should be propagated immediately to Elastic search. False - changes will be queued until JPA flush event
     */
    public void createOrUpdate(Class<? extends ISearchable> entityClass, String cetCode, Object identifier, Map<String, Object> values, boolean partialUpdate, boolean immediate) {

        if (!esConnection.isEnabled()) {
            return;
        }

        String index = null;
        String type = null;
        String id = null;
        try {

            index = esConfiguration.getIndex(entityClass);
            // Not interested in storing and indexing this entity in Elastic Search
            if (index == null) {
                return;
            }

            type = esConfiguration.getType(entityClass, cetCode);
            id = BaseEntity.cleanUpCodeOrId(identifier);
            boolean upsert = esConfiguration.isDoUpsert(entityClass);

            ElasticSearchAction action = upsert ? ElasticSearchAction.UPSERT : partialUpdate ? ElasticSearchAction.UPDATE : ElasticSearchAction.ADD_REPLACE;

            ElasticSearchChangeset change = new ElasticSearchChangeset(action, index, type, id, entityClass, values);
            queuedChanges.addChange(change);
            log.trace("Queueing Elastic Search document changes {}", change);

            if (immediate) {
                flushChanges();
            }

        } catch (Exception e) {
            log.error("Failed to queue document store to Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entityClass.getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Remove entity from Elastic Search
     *
     * @param entity Entity to remove from Elastic Search
     */
    public void remove(ISearchable entity) {

        if (!esConnection.isEnabled()) {
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
            id = BaseEntity.cleanUpCodeOrId(buildId(entity));

            ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, index, type, id, entity.getClass(), null);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);

        } catch (Exception e) {
            log.error("Failed to queue document delete from Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Remove entity from Elastic Search
     *
     * @param entityClass Entity class to remove from Elastic Search
     * @param cetCode Custom entity template code
     * @param identifier Record identifier. Optional. Will delete all data of that type if not provided.
     * @param immediate True if changes should be propagated immediately to Elastic search. False - changes will be queued until JPA flush event
     */
    public void remove(Class<? extends ISearchable> entityClass, String cetCode, Long identifier, boolean immediate) {

        if (!esConnection.isEnabled()) {
            return;
        }

        String index = null;
        String type = null;
        String id = null;
        try {

            index = esConfiguration.getIndex(entityClass);
            // Not interested in storing and indexing this entity in Elastic Search
            if (index == null) {
                return;
            }

            type = esConfiguration.getType(entityClass, cetCode);
            id = BaseEntity.cleanUpCodeOrId(identifier);

            ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, index, type, id, entityClass, null);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);

            if (immediate) {
                flushChanges();
            }

        } catch (Exception e) {
            log.error("Failed to queue document delete from Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entityClass.getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Remove entities from Elastic Search
     *
     * @param entityClass Entity class to remove from Elastic Search
     * @param cetCode Custom entity template code
     * @param identifiers Record identifiers
     * @param immediate True if changes should be propagated immediately to Elastic search. False - changes will be queued until JPA flush event
     */
    public void remove(Class<? extends ISearchable> entityClass, String cetCode, Set<Long> identifiers, boolean immediate) {

        if (!esConnection.isEnabled()) {
            return;
        }

        String index = null;
        String type = null;
        String id = null;
        try {

            index = esConfiguration.getIndex(entityClass);
            // Not interested in storing and indexing this entity in Elastic Search
            if (index == null) {
                return;
            }

            type = esConfiguration.getType(entityClass, cetCode);
            for (Object identifier : identifiers) {
                id = BaseEntity.cleanUpCodeOrId(identifier);

                ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, index, type, id, entityClass, null);
                queuedChanges.addChange(change);
                log.trace("Queueing Elastic Search document changes {}", change);
            }

            if (immediate) {
                flushChanges();
            }

        } catch (Exception e) {
            log.error("Failed to queue document delete from Elastic Search to {}/{}/{}", ReflectionUtils.getCleanClassName(entityClass.getSimpleName()), index, type, id, e);
        }
    }

    /**
     * Process pending changes to Elastic Search
     */
    public void flushChanges() {

        if (!esConnection.isEnabled()) {
            return;
        }

        if (queuedChanges.isNoChange()) {
            log.trace("Nothing to flush to ES");
            return;
        }
        TransportClient client = esConnection.getClient();

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
                    
                    if (change.getId()!=null){                    
                        bulkRequest.add(client.prepareDelete(change.getIndex(), change.getType(), change.getId()));
                    } else {
                        // need to drop the type/index and recreate it again
                    }
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
     * Execute a search compatible primefaces data table component search. See other search methods for documentation on search implementation. A search by query/full text search
     * will be used if paginationConfig.fullTextFilter value is provided.
     *
     * @param paginationConfig Query, pagination and sorting configuration
     * @param classnamesOrCetCodes An array of full classnames or CET codes
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(PaginationConfiguration paginationConfig, String[] classnamesOrCetCodes) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return null;
        }

        SortOrder sortOrder = (paginationConfig.getOrdering() == null || paginationConfig.getOrdering() == org.primefaces.model.SortOrder.UNSORTED) ? null
                : paginationConfig.getOrdering() == org.primefaces.model.SortOrder.ASCENDING ? SortOrder.ASC : SortOrder.DESC;

        String[] returnFields = paginationConfig.getFetchFields() == null ? null : (String[]) paginationConfig.getFetchFields().toArray();

        // Search either by a field
        if (StringUtils.isBlank(paginationConfig.getFullTextFilter()) && paginationConfig.getFilters() != null && !paginationConfig.getFilters().isEmpty()) {
            return search(paginationConfig.getFilters(), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows(),
                paginationConfig.getSortField() != null ? new String[] { paginationConfig.getSortField() } : null, sortOrder != null ? new SortOrder[] { sortOrder } : null,
                returnFields, getSearchScopeInfo(classnamesOrCetCodes, true));
        } else {
            return search(paginationConfig.getFullTextFilter(), null, paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows(),
                paginationConfig.getSortField() != null ? new String[] { paginationConfig.getSortField() } : null, sortOrder != null ? new SortOrder[] { sortOrder } : null,
                returnFields, getSearchScopeInfo(classnamesOrCetCodes, true));
        }
    }

    /**
     * Execute a search:
     * <ul>
     * <li>on all fields (_all field) when searching by a single word/phrase - full text search</li>
     * <li>search by a query containing boolean expressions and field:value pairs. Consult Elastic search documentation for a format.</li>
     * </ul>
     *
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param category - search by category that is directly taken from the name of the entity found in entityMapping. property of elasticSearchConfiguration.json. e.g. Customer,
     *        CustomerAccount, AccountOperation, etc. See elasticSearchConfiguration.json entityMapping keys for a list of categories.
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortFields - Fields to sort by. If omitted, will sort by score.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classInfo Entity classes to match
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(String query, String category, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields,
            List<ElasticSearchClassInfo> classInfo) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return null;
        }

        Set<String> indexes = null;
        // Not clear where to look, return all indexes for provider
        if (classInfo == null || classInfo.isEmpty()) {
            indexes = esConfiguration.getIndexes();
        } else {
            indexes = esConfiguration.getIndexes(classInfo);
        }

        // None of the classes are stored in Elastic Search, return empty json
        if (indexes.isEmpty()) {
            return null;
        }

        Set<String> types = null;
        if (classInfo != null && !classInfo.isEmpty()) {
            types = esConfiguration.getTypes(classInfo);
        }

        if (from == null) {
            from = 0;
        }
        if (size == null || size.intValue() == 0) {
            size = DEFAULT_SEARCH_PAGE_SIZE;
        }

        log.debug("Execute Elastic Search search for \"{}\" records {}-{} on {}, {} sort by {} {}", query, from, from + size, indexes, types, sortFields, sortOrders);

        SearchRequestBuilder reqBuilder = esConnection.getClient().prepareSearch(indexes.toArray(new String[0]));

        if (!StringUtils.isBlank(category)) {
            String[] categories = new String[] { category };
            reqBuilder.setTypes(categories);
        } else if (types != null) {
            reqBuilder.setTypes(types.toArray(new String[0]));
        }

        reqBuilder.setFrom(from);
        reqBuilder.setSize(size);

        // Limit return to only certain fields
        if (returnFields != null && returnFields.length > 0) {
            reqBuilder.addFields(returnFields);
        }

        if (StringUtils.isBlank(query)) {
            reqBuilder.setQuery(QueryBuilders.matchAllQuery());
        } else {
            Map<String, String> queryValues = null;
            try {
                queryValues = Splitter.onPattern("\\+").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(query.replace("*", ""));
            } catch (IllegalArgumentException e) {
                queryValues = Maps.newHashMap();
            }
            if (!queryValues.isEmpty()) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                for (Entry<String, ?> fieldValue : queryValues.entrySet()) {
                    boolQuery.must(QueryBuilders.matchQuery(fieldValue.getKey(), fieldValue.getValue()).operator(MatchQueryBuilder.Operator.AND));
                }
                reqBuilder.setQuery(boolQuery);
            } else if (query.contains("+")) {
                reqBuilder.setQuery(QueryBuilders.queryStringQuery(query.replace("+", " ")).lenient(true).defaultOperator(Operator.AND));
            } else {
                reqBuilder.setQuery(QueryBuilders.queryStringQuery(query).lenient(true));
            }
        }

        // Add sorting if requested
        if (sortFields != null && sortFields.length > 0) {
            for (int i = 0; i < sortFields.length; i++) {
                SortOrder sortOrder = null;
                if (sortOrders.length <= i) {
                    sortOrder = SortOrder.ASC;
                } else {
                    sortOrder = sortOrders[i];
                }
                reqBuilder.addSort(sortFields[i], sortOrder);
            }
        }

        SearchResponse response = reqBuilder.execute().actionGet();

        // log.trace("Data retrieved from Elastic Search in full text search is {}", response.toString());
        return response;
    }

    /**
     * Execute a search on given fields for given query values
     * 
     * 
     * Query format (key = Query key, value = search pattern or value).
     * 
     * Query key can be:
     * <ul>
     * <li>&lt;condition&gt; &lt;fieldname1&gt; &lt;fieldname2&gt; ... &lt;fieldnameN&gt;. Value is a value to apply in condition</li>
     * <li>&lt;fieldname1&gt;,&lt;fieldname2&gt;,&lt;fieldnameN&gt;. Value is a value to apply in condition. Matchis done on any of the listed fields.
     * </ul>
     * 
     * A union between different query items is AND.
     * 
     * Following conditions are supported:
     * <ul>
     * <li>term. Do not analyze the value (term) supplied. See term query in Elastic search documentation</li>
     * <li>terms. Match any of the values (terms) supplied without analyzing them first. Multiple terms are separated by '|' character</li>
     * <li>closestMatch. Do a closest match to the value provided. E.g. Search by value '1234' will try to match '1234', '123', '12', '1' values in this order. Note: A descending
     * ordering by this field will be added automatically to the query.</li>
     * <li>fromRange. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value&lt;=fiel.value. Applies to date and number type fields.</li>
     * <li>toRange. Ranged search - field value in between from - to values. Specifies "to" part value: e.g field.value&lt;=value</li>
     * <li>minmaxRange. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields.</li>
     * <li>minmaxOptionalRange. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must be specified.</li>
     * </ul>
     *
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record. Defaults to 0.
     * @param size Pagination - number of records per page. Defaults to DEFAULT_SEARCH_PAGE_SIZE.
     * @param sortFields - Fields to sort by. If omitted, will sort by score. If search query contains a 'closestMatch' expression, sortFields and sortOrder will be overwritten
     *        with a corresponding field and descending order.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classInfo Entity classes to match
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(Map<String, ?> queryValues, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields,
            List<ElasticSearchClassInfo> classInfo) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return null;
        }

        Set<String> indexes = null;
        // Not clear where to look, return all indexes for provider
        if (classInfo == null || classInfo.isEmpty()) {
            indexes = esConfiguration.getIndexes();
        } else {
            indexes = esConfiguration.getIndexes(classInfo);
        }

        // None of the classes are stored in Elastic Search, return empty json
        if (indexes.isEmpty()) {
            return null;
        }

        Set<String> types = null;
        if (classInfo != null && !classInfo.isEmpty()) {
            types = esConfiguration.getTypes(classInfo);
        }

        if (from == null) {
            from = 0;
        }
        if (size == null || size.intValue() == 0) {
            size = DEFAULT_SEARCH_PAGE_SIZE;
        }

        log.debug("Execute Elastic Search search for {} records {}-{} on {}, {} sort by {} {}", queryValues, from, from + size, indexes, types, sortFields, sortOrders);

        SearchRequestBuilder reqBuilder = esConnection.getClient().prepareSearch(indexes.toArray(new String[0]));
        if (types != null) {
            reqBuilder.setTypes(types.toArray(new String[0]));
        }

        reqBuilder.setFrom(from);
        reqBuilder.setSize(size);

        // Limit return to only certain fields
        if (returnFields != null && returnFields.length > 0) {
            reqBuilder.addFields(returnFields);
        }

        if (queryValues.isEmpty()) {
            reqBuilder.setQuery(QueryBuilders.matchAllQuery());

        } else {

            QueryBuilder queryBuilder = null;
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            for (Entry<String, ?> fieldValue : queryValues.entrySet()) {

                String[] fieldInfo = fieldValue.getKey().split(" ");
                String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1];
                String fieldName2 = fieldInfo.length == 3 ? fieldInfo[2] : null;

                Object filterValue = fieldValue.getValue();

                if (fieldName.contains(",")) {
                    queryBuilder = QueryBuilders.multiMatchQuery(filterValue, StringUtils.stripAll(fieldName.split(",")));

                } else if ("term".equals(condition)) {
                    queryBuilder = QueryBuilders.termQuery(fieldName, filterValue);

                } else if ("terms".equals(condition)) {
                    String valueTxt = filterValue.toString();
                    String[] values = valueTxt.split("\\|");
                    queryBuilder = QueryBuilders.termsQuery(fieldName, values);

                } else if ("closestMatch".equals(condition)) {

                    String valueTxt = filterValue.toString();

                    int valueLength = valueTxt.length();
                    String[] values = new String[valueLength];

                    for (int i = valueLength - 1; i >= 0; i--) {
                        values[i] = valueTxt.substring(0, i + 1);
                    }

                    queryBuilder = QueryBuilders.termsQuery(fieldName, values);
                    sortFields = new String[] { fieldName };
                    sortOrders = new SortOrder[] { SortOrder.DESC };

                } else if ("fromRange".equals(condition)) {

                    queryBuilder = QueryBuilders.rangeQuery(fieldName).gte(filterValue);

                } else if ("toRange".equals(condition)) {

                    queryBuilder = QueryBuilders.rangeQuery(fieldName).lt(filterValue);

                    // The value is in between two field values
                } else if ("minmaxRange".equals(condition)) {
                    if (filterValue instanceof Number) {
                        queryBuilder = QueryBuilders.boolQuery();
                        ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.rangeQuery(fieldName).lte(filterValue));
                        ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.rangeQuery(fieldName2).gte(filterValue));
                    } else if (filterValue instanceof Date) {
                        Date dateValue = (Date) filterValue;
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateValue);
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int date = c.get(Calendar.DATE);
                        c.set(year, month, date, 0, 0, 0);
                        dateValue = c.getTime();

                        queryBuilder = QueryBuilders.boolQuery();
                        ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.rangeQuery(fieldName).lte(dateValue));
                        ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.rangeQuery(fieldName2).gte(dateValue));
                    }

                } else {
                    queryBuilder = QueryBuilders.matchQuery(fieldName, filterValue);
                }

                if (queryValues.size() == 1) {
                    reqBuilder.setQuery(queryBuilder);
                } else {
                    boolQuery.must(queryBuilder);
                }
            }

            if (queryValues.size() > 1) {
                reqBuilder.setQuery(boolQuery);
            }
        }

        // Add sorting if requested
        if (sortFields != null && sortFields.length > 0) {
            for (int i = 0; i < sortFields.length; i++) {
                SortOrder sortOrder = null;
                if (sortOrders.length <= i) {
                    sortOrder = SortOrder.ASC;
                } else {
                    sortOrder = sortOrders[i];
                }
                reqBuilder.addSort(sortFields[i], sortOrder);
            }
        }

        SearchResponse response = reqBuilder.execute().actionGet();

        // log.trace("Data retrieved from Elastic Search in full text search is {}", response.toString());
        return response;
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
        return esConnection.isEnabled();
    }

    /**
     * Delete and recrete Elastic search index structure and populate it with data
     * 
     * @param lastCurrentUser Current user
     * @return Reindexing statistics
     * @throws BusinessException General exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> cleanAndReindex(MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        ReindexingStatistics statistics = new ReindexingStatistics();

        if (!esConnection.isEnabled()) {
            return new AsyncResult<ReindexingStatistics>(statistics);
        }

        log.info("Start to repopulate Elastic Search");

        try {

            esConnection.reinitES();

            // Drop all indexes
            elasticSearchIndexPopulationService.dropIndexes();

            // Recreate all indexes
            elasticSearchIndexPopulationService.createIndexes();

            // Repopulate index from DB

            // Process each class
            for (String classname : esConfiguration.getEntityClassesManaged()) {

                if (classname.equals(CustomTableRecord.class.getSimpleName())) {

                    List<CustomEntityTemplate> cets = customEntityTemplateService.listCustomTableTemplates();

                    for (CustomEntityTemplate cet : cets) {

                        log.info("Start to populate Elastic Search with data from {} table", cet.getDbTablename());

                        int from = 0;
                        int totalProcessed = 0;
                        boolean hasMore = true;

                        while (hasMore) {
                            int found = elasticSearchIndexPopulationService.populateIndexFromNativeTable(cet.getDbTablename(), from, statistics);

                            from = from + INDEX_POPULATE_PAGE_SIZE;
                            totalProcessed = totalProcessed + found;
                            hasMore = found == INDEX_POPULATE_PAGE_SIZE;
                        }

                        log.info("Finished populating Elastic Search with data from {} table. Processed {} records.", cet.getDbTablename(), totalProcessed);

                    }

                } else {

                    log.info("Start to populate Elastic Search with data from {} entity", classname);

                    int from = 0;
                    int totalProcessed = 0;
                    boolean hasMore = true;

                    while (hasMore) {
                        int found = elasticSearchIndexPopulationService.populateIndex(classname, from, statistics);

                        from = from + INDEX_POPULATE_PAGE_SIZE;
                        totalProcessed = totalProcessed + found;
                        hasMore = found == INDEX_POPULATE_PAGE_SIZE;
                    }

                    log.info("Finished populating Elastic Search with data from {} entity. Processed {} records.", classname, totalProcessed);
                }
            }

            log.info("Finished repopulating Elastic Search");

        } catch (Exception e) {
            log.error("Failed to repopulate Elastic Search", e);
            statistics.setException(e);
        }

        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Delete and repopulate data for a given entity class/custom entity code. NOTE: does not change the index structure.
     * 
     * @param lastCurrentUser Current user
     * @param entityClass Entity class to rebuild
     * @param cetCode Custom entity template to rebuild
     * @return Reindexing statistics
     * @throws BusinessException General exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> repopulate(MeveoUser lastCurrentUser, Class<? extends ISearchable> entityClass, String cetCode) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        ReindexingStatistics statistics = new ReindexingStatistics();

        if (!esConnection.isEnabled()) {
            return new AsyncResult<ReindexingStatistics>(statistics);
        }

        log.info("Start to repopulate Elastic Search for {}/{}", entityClass, cetCode);

        try {

            // Repopulate index from DB

            if (CustomEntityInstance.class.isAssignableFrom(entityClass) || CustomTableRecord.class.isAssignableFrom(entityClass)) {

                CustomEntityTemplate cet = customEntityTemplateService.findByCode(cetCode);

                log.info("Start to populate Elastic Search with data from {} table", cet.getDbTablename());

                int from = 0;
                int totalProcessed = 0;
                boolean hasMore = true;

                while (hasMore) {
                    int found = elasticSearchIndexPopulationService.populateIndexFromNativeTable(cet.getDbTablename(), from, statistics);

                    from = from + INDEX_POPULATE_PAGE_SIZE;
                    totalProcessed = totalProcessed + found;
                    hasMore = found == INDEX_POPULATE_PAGE_SIZE;
                }

                log.info("Finished populating Elastic Search with data from {} table. Processed {} records.", cet.getDbTablename(), totalProcessed);

            } else {
                String classname = ReflectionUtils.getCleanClassName(entityClass.getSimpleName());

                log.info("Start to populate Elastic Search with data from {} entity", classname);

                int from = 0;
                int totalProcessed = 0;
                boolean hasMore = true;

                while (hasMore) {
                    int found = elasticSearchIndexPopulationService.populateIndex(classname, from, statistics);

                    from = from + INDEX_POPULATE_PAGE_SIZE;
                    totalProcessed = totalProcessed + found;
                    hasMore = found == INDEX_POPULATE_PAGE_SIZE;
                }

                log.info("Finished populating Elastic Search with data from {} entity. Processed {} records.", classname, totalProcessed);
            }

            log.info("Finished repopulating Elastic Search for {}/{}", entityClass, cetCode);

        } catch (Exception e) {
            log.error("Failed to repopulate Elastic Search", e);
            statistics.setException(e);
        }

        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Recreate index.
     *
     * @throws BusinessException business exception
     */
    public void createIndexes() throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        elasticSearchIndexPopulationService.createIndexes();
    }

    /**
     * Update Elastic Search model with custom entity template definition
     *
     * @param cet Custom entity template
     * @throws BusinessException business exception
     */
    public void createCETMapping(CustomEntityTemplate cet) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        elasticSearchIndexPopulationService.createCETMapping(cet);
    }

    /**
     * Update Elastic Search model with custom field definition
     *
     * @param cft Custom field template
     * @throws BusinessException business exception
     */
    public void updateCFMapping(CustomFieldTemplate cft) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        elasticSearchIndexPopulationService.updateCFMapping(cft);
    }

    /**
     * Convert classnames (full or simple name) or CET codes into ElasticSearchClassInfo object containing info for search scope (index and type) calculation
     *
     * @param classnamesOrCetCodes An array of classnames (full or simple name) or CET codes
     * @param ignoreUnknownNames Should unknown classnames or CET codes throw an exception?
     * @return list of elastic search class info.
     * @throws BusinessException business exception
     */
    public List<ElasticSearchClassInfo> getSearchScopeInfo(String[] classnamesOrCetCodes, boolean ignoreUnknownNames) throws BusinessException {

        List<ElasticSearchClassInfo> classInfos = new ArrayList<>();

        if (classnamesOrCetCodes != null) {
            for (String classnameOrCetCode : classnamesOrCetCodes) {

                ElasticSearchClassInfo classInfo = getSearchScopeInfo(classnameOrCetCode);
                if (classInfo == null) {
                    if (ignoreUnknownNames) {
                        log.warn("Class or custom entity template by name {} not found", classnameOrCetCode);
                    } else {
                        throw new BusinessException("Class or custom entity template by name " + classnameOrCetCode + " not found");
                    }
                } else {
                    classInfos.add(classInfo);
                }
            }
        }
        return classInfos;
    }

    /**
     * Convert classname (full or simple name) or CET code into a information used to determine index and type in Elastic Search
     *
     * @param classnameOrCetCode Classname (full or simple name ) or CET code
     * @return Information used to determine index and type in Elastic Search
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ElasticSearchClassInfo getSearchScopeInfo(String classnameOrCetCode) {
        ElasticSearchClassInfo classInfo = null;
        try {
            classInfo = new ElasticSearchClassInfo((Class<? extends ISearchable>) Class.forName(classnameOrCetCode), null);

            // If not a real class, then might be a Custom Entity Instance. Check if CustomEntityTemplate exists with such name
        } catch (ClassNotFoundException e) {

            Class clazz = ReflectionUtils.getClassBySimpleNameAndParentClass(classnameOrCetCode, ISearchable.class);
            if (clazz != null) {
                classInfo = new ElasticSearchClassInfo((Class<? extends ISearchable>) clazz, null);

            } else {

                // Try first matching the CET name as is
                CustomEntityTemplate cet = cfCache.getCustomEntityTemplate(classnameOrCetCode);
                if (cet != null) {
                    if (cet.isStoreAsTable()) {
                        classInfo = new ElasticSearchClassInfo(CustomTableRecord.class, BaseEntity.cleanUpAndLowercaseCodeOrId(classnameOrCetCode));
                    } else {
                        classInfo = new ElasticSearchClassInfo(CustomEntityInstance.class, classnameOrCetCode);
                    }

                    // If still not matched - try how code is stored in ES with spaces cleanedup or cleaned up and lowercased if its a custom table
                } else {
                    String classnameOrCetCodeCL = BaseEntity.cleanUpAndLowercaseCodeOrId(classnameOrCetCode);
                    Collection<CustomEntityTemplate> cets = cfCache.getCustomEntityTemplates();
                    for (CustomEntityTemplate cetToClean : cets) {
                        if (BaseEntity.cleanUpAndLowercaseCodeOrId(cetToClean.getCode()).equals(classnameOrCetCodeCL)) {
                            if (cetToClean.isStoreAsTable()) {
                                classInfo = new ElasticSearchClassInfo(CustomTableRecord.class, classnameOrCetCodeCL); // cet.getDbTableName() should be same as
                                                                                                                       // classnameOrCetCodeCL
                            } else {
                                classInfo = new ElasticSearchClassInfo(CustomEntityInstance.class, cetToClean.getCode());
                            }
                        }
                        break;
                    }
                }
            }
        }
        return classInfo;
    }

    protected static String buildId(ISearchable entity) {
        if (entity instanceof BusinessEntity) {
            return entity.getCode();
        } else if (entity instanceof CustomTableRecord) {
            return ((CustomTableRecord) entity).getId().toString();
        } else {
            return entity.getCode() + "__" + entity.getId();
        }
    }
}