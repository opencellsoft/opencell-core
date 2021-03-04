/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.index;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ScrollableHitSource.SearchFailure;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.customEntities.CustomTableRecord;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.ProviderService;
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
@Deprecated
@Stateless
public class ElasticClient {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static int DEFAULT_SEARCH_PAGE_SIZE = 10;
    public static int INDEX_POPULATE_PAGE_SIZE = 1000;
    public static int INDEX_POPULATE_CT_PAGE_SIZE = 10000;

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
    private ElasticSearchIndexPopulationService esPopulationService;

    @Inject
    private CustomFieldsCacheContainerProvider cfCache;

    @Inject
    private ElasticClientConnection esConnection;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private ProviderService providerService;

    @EJB
    private ElasticClient multitenantElasticClient;

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
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only.
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
     * @param entity Entity corresponding to a document in Elastic Search. Is used to construct document id only.
     * @param fieldsToUpdate A map of fieldname and values to update in entity
     */
    public void partialUpdate(ISearchable entity, Map<String, Object> fieldsToUpdate) {

        if (!esConnection.isEnabled()) {
            return;
        }

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entity);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.UPDATE, indexAndType.getIndexName(), indexAndType.getType(), ElasticSearchIndexPopulationService.buildId(entity), fieldsToUpdate);
        queuedChanges.addChange(change);

        log.trace("Queueing Elastic Search document changes {}", change);

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

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entity);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        boolean upsert = esConfiguration.isDoUpsert(entity);

        ElasticSearchAction action = upsert ? ElasticSearchAction.UPSERT : partialUpdate ? ElasticSearchAction.UPDATE : ElasticSearchAction.ADD_REPLACE;

        Map<String, Object> jsonValueMap = esPopulationService.convertEntityToJson(entity, null, null, indexAndType.getType());

        ElasticSearchChangeset change = new ElasticSearchChangeset(action, indexAndType.getIndexName(), indexAndType.getType(), ElasticSearchIndexPopulationService.buildId(entity), jsonValueMap);
        queuedChanges.addChange(change);

        log.trace("Queueing Elastic Search document changes {}", change);
    }

    /**
     * Store and index values in Elastic Search
     *
     * @param entityClass Entity class to store in Elastic Search
     * @param cetCode Custom entity template code
     * @param identifier Record identifier. Expected long or similar number type identifier.
     * @param values Values to store
     * @param partialUpdate Should it be treated as partial update instead of replace if document exists. This value can be overridden in elasticSearchConfiguration.json to always
     *        do upsert.
     * @param immediate True if changes should be propagated immediately to Elastic search. False - changes will be queued until JPA flush event
     * @throws BusinessException Communication with ES/request execution exception
     */
    public void createOrUpdate(Class<? extends ISearchable> entityClass, String cetCode, Object identifier, Map<String, Object> values, boolean partialUpdate, boolean immediate) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entityClass, cetCode);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        boolean upsert = esConfiguration.isDoUpsert(entityClass);

        ElasticSearchAction action = upsert ? ElasticSearchAction.UPSERT : partialUpdate ? ElasticSearchAction.UPDATE : ElasticSearchAction.ADD_REPLACE;

        ElasticSearchChangeset change = new ElasticSearchChangeset(action, indexAndType.getIndexName(), indexAndType.getType(), identifier, values); // Note: 'entityType' value is
                                                                                                                                                     // set in constructor

        queuedChanges.addChange(change);

        log.trace("Queueing Elastic Search document changes {}", change);

        if (immediate) {
            flushChanges();
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

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entity);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, indexAndType.getIndexName(), indexAndType.getType(), ElasticSearchIndexPopulationService.buildId(entity));
        queuedChanges.addChange(change);

        log.trace("Queueing Elastic Search document changes {}", change);

    }

    /**
     * Remove values from Elastic Search
     *
     * @param entityClass Entity class to remove from Elastic Search
     * @param cetCode Custom entity template code
     * @param identifier Record identifier.
     * @param immediate True if changes should be propagated immediately to Elastic search. False - changes will be queued until JPA flush event
     * @throws BusinessException Communication with ES/request execution exception
     */
    public void remove(Class<? extends ISearchable> entityClass, String cetCode, Long identifier, boolean immediate) throws BusinessException {

        if (!esConnection.isEnabled() || identifier == null) {
            return;
        }

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entityClass, cetCode);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, indexAndType.getIndexName(), indexAndType.getType(), identifier);

        queuedChanges.addChange(change);

        log.trace("Queueing Elastic Search document changes {}", change);

        if (immediate) {
            flushChanges();
        }

    }

    /**
     * Remove values from Elastic Search
     *
     * @param entityClass Entity class to remove from Elastic Search
     * @param cetCode Custom entity template code
     * @param identifiers Record identifiers
     * @param immediate True if changes should be propagated immediately to Elastic search. False - changes will be queued until JPA flush event
     * @throws BusinessException Communication with ES/request execution exception
     */
    public void remove(Class<? extends ISearchable> entityClass, String cetCode, Set<Long> identifiers, boolean immediate) throws BusinessException {

        if (!esConnection.isEnabled() || identifiers == null) {
            return;
        }

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entityClass, cetCode);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        String indexName = indexAndType.getIndexName();
        String type = indexAndType.getType();

        for (Object identifier : identifiers) {

            ElasticSearchChangeset change = new ElasticSearchChangeset(ElasticSearchAction.DELETE, indexName, type, identifier);
            queuedChanges.addChange(change);

            log.trace("Queueing Elastic Search document changes {}", change);
        }

        if (immediate) {
            flushChanges();
        }

    }

    /**
     * Remove ALL records of a given entity type from Elastic Search. NOTE: Changes are propagated immediately.
     *
     * @param entityClass Entity class, which ALL records to remove from Elastic Search
     * @param cetCode Custom entity template code
     * @throws BusinessException Communication with ES/request execution exception
     */
    public void remove(Class<? extends ISearchable> entityClass, String cetCode) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        ESIndexNameAndType indexAndType = esPopulationService.getIndexAndType(entityClass, cetCode);

        // Not interested in storing and indexing this entity in Elastic Search
        if (indexAndType == null) {
            return;
        }

        String indexName = indexAndType.getIndexName();

        DeleteByQueryRequest deleteRequest = new DeleteByQueryRequest(indexName);
        if (indexAndType.getType() != null) {
            deleteRequest.setQuery(new TermQueryBuilder(ElasticSearchConfiguration.MAPPING_FIELD_TYPE, indexAndType.getType()));
        } else {
            deleteRequest.setQuery(QueryBuilders.matchAllQuery());
        }

        BulkByScrollResponse bulkResponse = null;
        try {
            bulkResponse = esConnection.getClient().deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("Failed to process delete by query request in Elastic Search", e);
        }

        log.debug("{} records were deleted in Elastic Search for {}", bulkResponse.getDeleted(), StringUtils.concatenate(", ", indexAndType));
        for (SearchFailure failure : bulkResponse.getSearchFailures()) {
            log.error("Failed to process delete by query (search phase) in Elastic Search for {}/{}", failure.getIndex(), indexAndType.getType(), failure.getReason());
        }
        for (Failure failure : bulkResponse.getBulkFailures()) {
            log.error("Failed to process delete by query (bulk phase) in Elastic Search for {}/{} reason: {}", failure.getIndex(), indexAndType.getType(), failure.getMessage(), failure.getCause());
        }
    }

    /**
     * Process pending changes to Elastic Search
     * 
     * @throws BusinessException Communication with ES/bulk request execution exception
     */
    public void flushChanges() throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        if (queuedChanges.isNoChange()) {
//          log.trace("Nothing to flush to ES");
            return;
        }
        RestHighLevelClient client = esConnection.getClient();

        // Prepare bulk request
        BulkRequest bulkRequest = new BulkRequest();

        for (ElasticSearchChangeset change : queuedChanges.getQueuedChanges().values()) {

            if (change.getAction() == ElasticSearchAction.ADD_REPLACE) {
                bulkRequest.add(new IndexRequest(change.getIndex(), ElasticSearchConfiguration.MAPPING_DOC_TYPE, change.getIdForES()).source(change.getSource()));

            } else if (change.getAction() == ElasticSearchAction.UPDATE) {
                bulkRequest.add(new UpdateRequest(change.getIndex(), ElasticSearchConfiguration.MAPPING_DOC_TYPE, change.getIdForES()).doc(change.getSource()));

            } else if (change.getAction() == ElasticSearchAction.UPSERT) {
                bulkRequest.add(new UpdateRequest(change.getIndex(), ElasticSearchConfiguration.MAPPING_DOC_TYPE, change.getIdForES()).upsert(change.getSource()));

            } else if (change.getAction() == ElasticSearchAction.DELETE) {

                if (change.getIdForES() != null) {
                    bulkRequest.add(new DeleteRequest(change.getIndex(), ElasticSearchConfiguration.MAPPING_DOC_TYPE, change.getIdForES()));
                }
            }
        }

        log.debug("Bulk processing {} action Elastic Search requests", bulkRequest.numberOfActions());

        int count = 0;
        int maxTries = 3;
        // Execute bulk request
        BulkResponse bulkResponse = null;
        while (true) {
            try {
                bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                break;
            } catch (IOException e) {
                if (++count == maxTries) {
                    throw new BusinessException("Failed to process bulk request in Elastic Search. Pending changes " + queuedChanges.getQueuedChanges(), e);
                } else {
                    log.info("Retry to process bulk request in Elastic Search. Try #{}.", count);
                }
            }
        }
        if (bulkResponse.hasFailures() || log.isTraceEnabled()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                if (bulkItemResponse.isFailed()) {
                    log.error("Failed to process {} in Elastic Search for {}/{}/{} reason: {}", bulkItemResponse.getOpType(), bulkItemResponse.getIndex(), bulkItemResponse.getType(), bulkItemResponse.getId(),
                        bulkItemResponse.getFailureMessage(), bulkItemResponse.getFailure().getCause());
                } else if (log.isTraceEnabled()) {
                    log.trace("Processed {} in Elastic Search for {}/{}/{} version: {}", bulkItemResponse.getOpType(), bulkItemResponse.getIndex(), bulkItemResponse.getType(), bulkItemResponse.getId(),
                        bulkItemResponse.getVersion());
                }
            }
        }

        queuedChanges.clear();
    }

    /**
     * Execute a primefaces data table component compatible search. See other search methods for documentation on search implementation. A search by query/full text search will be
     * used if paginationConfig.fullTextFilter value is provided.
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

        String[] sortFields = null;
        SortOrder[] sortOrders = null;
        if (paginationConfig.getOrderings() != null) {
            int size = paginationConfig.getOrderings().length / 2;
            sortFields = new String[size];
            sortOrders = new SortOrder[size];
            for (int i = 0; i < size; i++) {
                sortFields[i] = (String) paginationConfig.getOrderings()[i * 2];
                sortOrders[i] = ((org.primefaces.model.SortOrder) paginationConfig.getOrderings()[i * 2 + 1]) == org.primefaces.model.SortOrder.DESCENDING ? SortOrder.DESC : SortOrder.ASC;
            }
        }

        String[] returnFields = paginationConfig.getFetchFields() == null ? null : (String[]) paginationConfig.getFetchFields().toArray();

        // Search either by a field
        if (StringUtils.isBlank(paginationConfig.getFullTextFilter()) && paginationConfig.getFilters() != null && !paginationConfig.getFilters().isEmpty()) {
            return search(paginationConfig.getFilters(), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows(), sortFields, sortOrders, returnFields, getSearchScopeInfo(classnamesOrCetCodes, true));

            // Or by a full text value
        } else {
            return search(paginationConfig.getFullTextFilter(), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows(), sortFields, sortOrders, returnFields, getSearchScopeInfo(classnamesOrCetCodes, true));
        }
    }

    /**
     * Execute a search. See other search() method for a detailed query description.
     *
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortFields - Fields to sort by. If omitted, will sort by score.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classnamesOrCetCodes Entity classes or Custom entity template codes to match. If not provided will look in all indices of a current provider.
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(String query, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields, String[] classnamesOrCetCodes) throws BusinessException {

        List<ElasticSearchClassInfo> classInfo = getSearchScopeInfo(classnamesOrCetCodes, false);

        return search(query, from, size, sortFields, sortOrders, returnFields, classInfo);
    }

    /**
     * Execute a search:
     * <ul>
     * <li>on all fields (_all field) when searching by a single word/phrase - full text search</li>
     * <li>search by a query containing boolean expressions and field:value pairs. Consult Elastic search documentation for a format.</li>
     * </ul>
     *
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortFields - Fields to sort by. If omitted, will sort by score.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classInfo Entity classes to match. If not provided will look in all indices of a current provider.
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(String query, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields, List<ElasticSearchClassInfo> classInfo) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return null;
        }

        Set<ESIndexNameAndType> indicesAndTypes = esPopulationService.getIndexAndTypes(classInfo);

        // None of the classes are stored in Elastic Search, return empty json
        if (indicesAndTypes == null || indicesAndTypes.isEmpty()) {
            return null;
        }

        if (from == null) {
            from = 0;
        }
        if (size == null || size.intValue() == 0) {
            size = DEFAULT_SEARCH_PAGE_SIZE;
        }

        log.debug("Execute Elastic Search search for \"{}\" records {}-{} on {} sort by {} {}", query, from, from + size, StringUtils.concatenate(", ", indicesAndTypes), sortFields, sortOrders);

        Set<String> indices = new HashSet<>();
        String type = null;
        for (ESIndexNameAndType indexAndType : indicesAndTypes) {
            indices.add(indexAndType.getIndexName());
            // TODO For now only a single type is supported. As not possible to search in one index without a type and another one with type - dont know how to write OR clause
            // between indices
            if (indicesAndTypes.size() == 1 && type == null && indexAndType.getType() != null) {
                type = indexAndType.getType();
            }
        }

        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();

        searchBuilder.from(from);
        searchBuilder.size(size);

        // Limit return to only certain fields. In that case, source is not returned.
        if (returnFields != null && returnFields.length > 0) {
            for (String field : returnFields) {
                searchBuilder.docValueField(field, "use_field_mapping");
            }
            searchBuilder.fetchSource(false);
        }

        // Add filter by type if entity searched uses type. Works only for a single entity class query, as can not mix indexes with and without type
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder = null;
        boolean useBoolQuery = type != null;

        if (type != null) {
            boolQuery.filter(QueryBuilders.termQuery(ElasticSearchConfiguration.MAPPING_FIELD_TYPE, type));
        }

        if (StringUtils.isBlank(query)) {
            queryBuilder = QueryBuilders.matchAllQuery();
        } else {
            Map<String, String> queryValues = null;
            try {
                queryValues = Splitter.onPattern("\\+").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(query.replace("*", ""));
            } catch (IllegalArgumentException e) {
                queryValues = Maps.newHashMap();
            }

            if (!queryValues.isEmpty()) {
                useBoolQuery = useBoolQuery || queryValues.size() > 1;
                for (Entry<String, ?> fieldValue : queryValues.entrySet()) {
                    queryBuilder = QueryBuilders.matchQuery(fieldValue.getKey(), fieldValue.getValue()).operator(Operator.AND);
                    if (queryValues.size() > 1) {
                        boolQuery.must(queryBuilder);
                        queryBuilder = null;
                    }
                }
            } else if (query.contains("+")) {
                queryBuilder = QueryBuilders.queryStringQuery(query.replace("+", " ")).lenient(true).defaultOperator(Operator.AND);
            } else {
                queryBuilder = QueryBuilders.queryStringQuery(query).lenient(true);
            }
        }

        // Use boolean query when multiple query values were parsed, o filtering by type is used
        if (useBoolQuery) {
            // Add single query to boolean query if needed
            if (queryBuilder != null) {
                boolQuery.must(queryBuilder);
            }
            searchBuilder.query(boolQuery);
        } else {
            searchBuilder.query(queryBuilder);
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
                searchBuilder.sort(sortFields[i], sortOrder);
            }
        }

        SearchRequest searchRequest = new SearchRequest(indices.toArray(new String[] {}), searchBuilder);

        SearchResponse response;
        try {
            response = esConnection.getClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("Failed to execute ES search request " + searchRequest.toString(), e);
        }

        log.trace("Data retrieved from Elastic Search in full text search is {}", response.toString());
        return response;
    }

    /**
     * Execute a search on given fields for given query values. See other search() method for a detailed description of query values.
     * 
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record. Defaults to 0.
     * @param size Pagination - number of records per page. Defaults to DEFAULT_SEARCH_PAGE_SIZE.
     * @param sortFields - Fields to sort by. If omitted, will sort by score. If search query contains a 'closestMatch' expression, sortFields and sortOrder will be overwritten
     *        with a corresponding field and descending order, unless sorting included valid_xxx fields, in which case they will be given priority first.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classnamesOrCetCodes Entity classes or Custom entity template codes to match. If not provided will look in all indices of a current provider.
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(Map<String, ?> queryValues, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields, String[] classnamesOrCetCodes) throws BusinessException {

        List<ElasticSearchClassInfo> classInfo = getSearchScopeInfo(classnamesOrCetCodes, false);

        return search(queryValues, from, size, sortFields, sortOrders, returnFields, classInfo);
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
     * <li>term and filter_term. Do not analyze the value (term) supplied. See term query in Elastic search documentation</li>
     * <li>terms and filter_terms. Match any of the values (terms) supplied without analyzing them first. Multiple terms are separated by '|' character</li>
     * <li>closestMatch and filter_closestMatch. Do a closest match to the value provided. E.g. Search by value '1234' will try to match '1234', '123', '12', '1' values in this
     * order. Note: A descending ordering by this field will be added automatically to the query.</li>
     * <li>fromRange and filter_fromRange. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value&lt;=field value. Applies to date and
     * number type fields.</li>
     * <li>toRange and filter_toRange. Ranged search - field value in between from - to values. Specifies "to" part value: e.g field.value&lt;value</li>
     * <li>minmaxRange and filter_minmaxRange. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields.</li>
     * <li>minmaxOptionalRange and filter_minmaxOptionalRange. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must
     * be specified.</li>
     * </ul>
     * 
     * Note: filter_xxx conditions are filters and wont be considered for scoring.
     *
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record. Defaults to 0.
     * @param size Pagination - number of records per page. Defaults to DEFAULT_SEARCH_PAGE_SIZE.
     * @param sortFields - Fields to sort by. If omitted, will sort by score. If search query contains a 'closestMatch' expression, sortFields and sortOrder will be overwritten
     *        with a corresponding field and descending order, unless sorting included valid_xxx fields, in which case they will be given priority first.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classInfo Entity classes to match. If not provided will look in all indices of a current provider.
     * @return Search result
     * @throws BusinessException General business exception
     */
    public SearchResponse search(Map<String, ?> queryValues, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields, List<ElasticSearchClassInfo> classInfo)
            throws BusinessException {

        if (!esConnection.isEnabled()) {
            return null;
        }

        Set<ESIndexNameAndType> indicesAndTypes = esPopulationService.getIndexAndTypes(classInfo);

        // None of the classes are stored in Elastic Search, return empty json
        if (indicesAndTypes == null || indicesAndTypes.isEmpty()) {
            return null;
        }

        if (from == null) {
            from = 0;
        }
        if (size == null || size.intValue() == 0) {
            size = DEFAULT_SEARCH_PAGE_SIZE;
        }

        log.debug("Execute Elastic Search field search for {} records {}-{} on {} sort by {} {}", queryValues, from, from + size, StringUtils.concatenate(", ", indicesAndTypes), sortFields, sortOrders);

        Set<String> indices = new HashSet<>();
        String type = null;
        for (ESIndexNameAndType indexAndType : indicesAndTypes) {
            indices.add(indexAndType.getIndexName());
            // TODO For now only a single type is supported. As not possible?? to search in one index without a type and another one with type - dont know how to write OR clause
            // between indices
            if (indicesAndTypes.size() == 1 && type == null && indexAndType.getType() != null) {
                type = indexAndType.getType();
            }
        }

        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();

        searchBuilder.from(from);
        searchBuilder.size(size);

        // Limit return to only certain fields. In that case, source is not returned.
        if (returnFields != null && returnFields.length > 0) {
            for (String field : returnFields) {
                searchBuilder.docValueField(field, "use_field_mapping");
            }
            searchBuilder.fetchSource(false);
        }

        // Add filter by type if entity searched uses type. Works only for a single entity class query, as can not mix indexes with and without type
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder = null;
        boolean useBoolQuery = type != null || queryValues.size() > 1;

        if (type != null) {
            boolQuery.filter(QueryBuilders.termQuery(ElasticSearchConfiguration.MAPPING_FIELD_TYPE, type));
        }

        if (queryValues.isEmpty()) {
            queryBuilder = QueryBuilders.matchAllQuery();
            if (useBoolQuery) {
                boolQuery.must(QueryBuilders.matchAllQuery());
            }

        } else {

            for (Entry<String, ?> fieldValue : queryValues.entrySet()) {

                String[] fieldInfo = fieldValue.getKey().split(" ");
                String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1];
                String fieldName2 = fieldInfo.length == 3 ? fieldInfo[2] : null;

                Object filterValue = fieldValue.getValue();

                if (fieldName.contains(",")) {
                    queryBuilder = QueryBuilders.multiMatchQuery(filterValue, org.apache.commons.lang.StringUtils.stripAll(fieldName.split(",")));

                } else if ("term".equals(condition) || "filter_term".equals(condition)) {
                    queryBuilder = QueryBuilders.termQuery(fieldName, filterValue);

                } else if ("terms".equals(condition) || "filter_terms".equals(condition)) {
                    String valueTxt = filterValue.toString();
                    String[] values = valueTxt.split("\\|");
                    queryBuilder = QueryBuilders.termsQuery(fieldName, values);

                } else if ("closestMatch".equals(condition) || "filter_closestMatch".equals(condition)) {

                    String valueTxt = filterValue.toString();

                    int valueLength = valueTxt.length();
                    String[] values = new String[valueLength];

                    for (int i = valueLength - 1; i >= 0; i--) {
                        values[i] = valueTxt.substring(0, i + 1);
                    }

                    queryBuilder = QueryBuilders.termsQuery(fieldName, values);

                    // If sorting is done by valid_xxx, then insert sorting after it
                    if (sortFields != null && sortFields.length > 0) {

                        String[] newSortFields = new String[sortFields.length + 1];
                        SortOrder[] newSortOrders = new SortOrder[sortOrders.length + 1];

                        int newI = 0;
                        for (int i = 0; i < sortFields.length; i++) {

                            if (newI == i && !sortFields[i].startsWith("valid_")) {
                                newSortFields[newI] = fieldName;
                                newSortOrders[newI] = SortOrder.DESC;
                                newI++;
                            }
                            newSortFields[newI] = sortFields[i];
                            newSortOrders[newI] = sortOrders[i];
                            newI++;
                        }
                        if (newI == sortFields.length) {
                            newSortFields[newI] = fieldName;
                            newSortOrders[newI] = SortOrder.DESC;
                        }

                        sortFields = newSortFields;
                        sortOrders = newSortOrders;

                        // Or if no sorting was specified - add sorting by closestMatch field
                    } else {
                        sortFields = new String[] { fieldName };
                        sortOrders = new SortOrder[] { SortOrder.DESC };
                    }

                } else if ("fromRange".equals(condition) || "filter_fromRange".equals(condition)) {

                    queryBuilder = QueryBuilders.rangeQuery(fieldName).gte(filterValue);

                } else if ("toRange".equals(condition) || "filter_toRange".equals(condition)) {

                    queryBuilder = QueryBuilders.rangeQuery(fieldName).lt(filterValue);

                    // The value is in between two field values
                } else if ("minmaxRange".equals(condition) || "filter_minmaxRange".equals(condition)) {
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
                    filterValue = Optional.ofNullable(filterValue).orElse(queryValues.get("id"));
                    queryBuilder = QueryBuilders.matchQuery(fieldName, filterValue);

                }

                if (condition != null && condition.startsWith("filter")) {
                    boolQuery.filter(queryBuilder);
                    useBoolQuery = true;

                } else if (useBoolQuery) {
                    boolQuery.must(queryBuilder);
                }
            }
        }

        if (useBoolQuery) {
            searchBuilder.query(boolQuery);
        } else {
            searchBuilder.query(queryBuilder);
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
                searchBuilder.sort(sortFields[i], sortOrder);
            }
        }

        SearchRequest searchRequest = new SearchRequest(indices.toArray(new String[] {}), searchBuilder);

        SearchResponse response;
        try {
            response = esConnection.getClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("Failed to execute ES search request " + searchRequest.toString(), e);
        }

        log.trace("Data retrieved from Elastic Search in full text search is {}", response.toString());
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
     * Delete and recreate Elastic search index structure and populate it with data for a <b>current provider</b>
     * 
     * @param lastCurrentUser Current user
     * @param reinitESConnection Should connection to ES be re-established
     * @return Reindexing statistics
     * @throws BusinessException General exception in communication with or action execution in Elastic search
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> cleanAndReindex(MeveoUser lastCurrentUser, boolean reinitESConnection) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        ReindexingStatistics statistics = cleanAndReindex(true, reinitESConnection, lastCurrentUser.getProviderCode());

        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Delete and recreate Elastic search index structure and populate it with data for a current provider
     * 
     * @param dropIndexes Shall current indexes for provider be dropped first
     * @param reinitESConnection Should connection to ES be re-established
     * @param providerCode Current provider code. For information purpose only, so dont need to look it up
     * @return Reindexing statistics
     * @throws BusinessException General exception in communication with or action execution in Elastic search
     */
    private ReindexingStatistics cleanAndReindex(boolean dropIndexes, boolean reinitESConnection, String providerCode) throws BusinessException {

        ReindexingStatistics statistics = new ReindexingStatistics();

        if (!esConnection.isEnabled()) {
            return statistics;
        }

        log.info("Started to repopulate Elastic Search for provider {}", providerCode);

        try {

            if (reinitESConnection) {
                esConnection.reinitES();
            }

            // Drop all indexes for the current provider
            if (dropIndexes) {
                esPopulationService.dropIndexes();
            }
            // Recreate all indexes for the current provider
            esPopulationService.createIndexes();

            // Repopulate index from DB

            // Process each class
            for (String classname : esConfiguration.getEntityClassesManaged()) {

                if (classname.equals(CustomTableRecord.class.getName())) {

                    List<CustomEntityTemplate> cets = customEntityTemplateService.listCustomTableTemplates();

                    for (CustomEntityTemplate cet : cets) {

                        populateAll(statistics, classname, cet);
                    }

                } else {
                    populateAll(statistics, classname, null);
                }
            }

            log.info("Finished repopulating Elastic Search for provider {}", providerCode);

        } catch (Exception e) {
            log.error("Failed to repopulate Elastic Search for provider {}", providerCode, e);
            statistics.setException(e);
        }

        return statistics;
    }

    /**
     * Delete and recreate Elastic search index structure and populate it with data for a current provider
     * 
     * @param lastCurrentUser Current user
     * @param reinitESConnection Should connection to ES be re-established and configuration reloaded
     * @return Reindexing statistics
     * @throws BusinessException General exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> cleanAndReindexAll(MeveoUser lastCurrentUser, boolean reinitESConnection) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        ReindexingStatistics statistics = new ReindexingStatistics();

        if (!esConnection.isEnabled()) {
            return new AsyncResult<ReindexingStatistics>(statistics);
        }

        log.info("Started to repopulate Elastic Search for all providers");

        try {

            if (reinitESConnection) {
                esConnection.reinitES();
            }

            // Drop all indexes - of all providers
            esPopulationService.dropAllIndexes();

            final List<Provider> providers = providerService.list(new PaginationConfiguration("id", org.primefaces.model.SortOrder.ASCENDING));

            int i = 0;

            // Process each provider, aggregating its statistics
            for (Provider provider : providers) {

                Future<ReindexingStatistics> reindexProvider;

                try {

                    reindexProvider = multitenantElasticClient.cleanAndReindex(provider.getCode(), i == 0);
                    statistics.updateStatistics(reindexProvider.get());
                    if (statistics.getException() != null) {
                        return new AsyncResult<ReindexingStatistics>(statistics);
                    }

                } catch (InterruptedException | ExecutionException | BusinessException e) {
                    log.error("Failed to initialize a provider {}", provider.getCode());
                }
                i++;
            }

        } catch (Exception e) {
            log.error("Failed to repopulate Elastic Search for all providers", e);
            statistics.setException(e);
        }
        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Recreate Elastic search index structure and populate it with data for a current provider. Part of cleanAndReindexAll() method. DOES not delete indexes - they should have
     * been deleted before in cleanAndReindexAll().
     * 
     * @param providerCode Provider code that index should be rebuild for
     * @param isMainProvider Is it a main provider
     * @return Reindexing statistics
     * @throws BusinessException General exception in communication with or action execution in Elastic search
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> cleanAndReindex(String providerCode, boolean isMainProvider) throws BusinessException {

        currentUserProvider.forceAuthentication("applicationInitializer", isMainProvider ? null : providerCode);

        ReindexingStatistics statistics = cleanAndReindex(false, false, isMainProvider ? null : providerCode);

        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Repopulate ALL data for a given entity class/custom entity code. Note: assumes that current data has been deleted already.
     * 
     * @param lastCurrentUser Current user
     * @param entityClass Entity class to rebuild
     * @param cetCode Custom entity template to rebuild. Applies only to custom tables. Custom entity instances are rebuild all together.
     * @return Reindexing statistics
     * @throws BusinessException General exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ReindexingStatistics> populateAll(MeveoUser lastCurrentUser, Class<? extends ISearchable> entityClass, String cetCode) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        ReindexingStatistics statistics = new ReindexingStatistics();

        if (!esConnection.isEnabled()) {
            return new AsyncResult<ReindexingStatistics>(statistics);
        }

        log.info("Started to repopulate Elastic Search for {}/{}", entityClass, cetCode);

        CustomEntityTemplate cet = null;

        if (CustomTableRecord.class.isAssignableFrom(entityClass)) {
            cet = customEntityTemplateService.findByCode(cetCode);
        }

        populateAll(statistics, entityClass.getName(), cet);

        return new AsyncResult<ReindexingStatistics>(statistics);
    }

    /**
     * Repopulate ALL data for a given entity class/custom entity code. Note: assumes that current data has been deleted already.
     * 
     * @param lastCurrentUser Current user
     * @param classname Full name of an Entity class to rebuild
     * @param cet Custom entity template to rebuild. Applies ONLY to custom tables.
     * @throws BusinessException General exception
     */
    private void populateAll(ReindexingStatistics statistics, String classname, CustomEntityTemplate cet) {

        if (cet != null && !cet.isStoreInES()) {
            return;
        }

        log.info("Started to repopulate Elastic Search for {}/{}", classname, cet != null ? cet.getCode() : null);

        try {

            // Repopulate index from DB

            if (classname.equals(CustomTableRecord.class.getName())) {

                log.info("Started to populate Elastic Search with data from {} table", cet.getDbTablename());

                Object fromId = 0;

                int recordCount = esPopulationService.getRecordCountInNativeTable(cet.getDbTablename());
                int recordsRemaining = recordCount;
                int totalProcessed = 0;
                while (recordsRemaining > 0) {

                    Object[] processedInfo = esPopulationService.populateIndexFromNativeTable(cet, fromId, recordsRemaining > INDEX_POPULATE_CT_PAGE_SIZE ? INDEX_POPULATE_CT_PAGE_SIZE : -1, statistics);

                    totalProcessed = totalProcessed + (int) processedInfo[0];
                    fromId = processedInfo[1];
                    recordsRemaining = recordsRemaining - INDEX_POPULATE_CT_PAGE_SIZE;
                }

                log.info("Finished populating Elastic Search with data from {} table. Processed {} records.", cet.getDbTablename(), totalProcessed);

            } else {

                log.info("Started to populate Elastic Search with data from {} entity", classname);

                Object fromId = -1000;
                int totalProcessed = 0;
                boolean hasMore = true;

                while (hasMore) {
                    Object[] processedInfo = esPopulationService.populateIndex(classname, fromId, INDEX_POPULATE_PAGE_SIZE, statistics);

                    totalProcessed = totalProcessed + (int) processedInfo[0];
                    fromId = processedInfo[1];
                    hasMore = (int) processedInfo[0] == INDEX_POPULATE_PAGE_SIZE;
                }

                log.info("Finished populating Elastic Search with data from {} entity. Processed {} records.", classname, totalProcessed);
            }

            log.info("Finished repopulating Elastic Search for {}/{}", classname, cet != null ? cet.getCode() : null);

        } catch (Exception e) {
            log.error("Failed to repopulate Elastic Search for {}/{}", classname, cet != null ? cet.getCode() : null, e);
            statistics.setException(e);
        }
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

        if (!cet.isStoreInES()) {
            return;
        }

        esPopulationService.createCETIndex(cet);
    }

    /**
     * Remove from Elastic Search model a custom entity template definition
     *
     * @param cet Custom entity template
     * @throws BusinessException General exception
     */
    public void removeCETMapping(CustomEntityTemplate cet) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        esPopulationService.removeCETIndex(cet);
    }

    /**
     * Update Elastic Search model with custom entity template definition or remove it based on CustomEntityTemplate.storeInES field value
     *
     * @param cet Custom entity template
     * @throws BusinessException business exception
     */
    public void createOrRemoveCETMapping(CustomEntityTemplate cet) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return;
        }

        esPopulationService.createOrRemoveCETIndex(cet);
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

        esPopulationService.updateCFMapping(cft);
    }

    /**
     * Convert classnames (full or simple name) or CET codes into ElasticSearchClassInfo object containing info for search scope (index and type) calculation
     *
     * @param classnamesOrCetCodes An array of classnames (full or simple name) or CET codes
     * @param ignoreUnknownNames Should unknown classnames or CET codes throw an exception?
     * @return List of elastic search class info.
     * @throws BusinessException business exception
     */
    private List<ElasticSearchClassInfo> getSearchScopeInfo(String[] classnamesOrCetCodes, boolean ignoreUnknownNames) throws BusinessException {

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
     * Convert classname (full or simple name) or CET code into a information used to determine index and type in Elastic Search. Note: passing a simple classname takes much, much
     * longer.
     *
     * @param classnameOrCetCode Classname (full or simple name ) or CET code
     * @return Information used to determine index and type in Elastic Search
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ElasticSearchClassInfo getSearchScopeInfo(String classnameOrCetCode) {
        ElasticSearchClassInfo classInfo = null;
        try {
            classInfo = new ElasticSearchClassInfo((Class<? extends ISearchable>) Class.forName(classnameOrCetCode), null);

            // If not a real class, then might be a Custom Entity Instance. Check if CustomEntityTemplate exists with such name
        } catch (ClassNotFoundException e) {

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
                    if (BaseEntity.cleanUpAndLowercaseCodeOrId(cetToClean.getCode()).equals(classnameOrCetCodeCL) || classnameOrCetCodeCL.equals(cetToClean.getDbTablename())) {
                        if (cetToClean.isStoreAsTable()) {
                            classInfo = new ElasticSearchClassInfo(CustomTableRecord.class, cetToClean.getCode());
                        } else {
                            classInfo = new ElasticSearchClassInfo(CustomEntityInstance.class, cetToClean.getCode());
                        }

                        break;
                    }
                }
            }

            // And if still not matched - try classname in case its a simple name and not a full classname
            if (classInfo == null) {
                Class clazz = ReflectionUtils.getClassBySimpleNameAndParentClass(classnameOrCetCode, ISearchable.class);
                if (clazz != null) {
                    classInfo = new ElasticSearchClassInfo((Class<? extends ISearchable>) clazz, null);
                }
            }
        }
        return classInfo;
    }

    /**
     * Do a backward conversion of index and/or type into a classname and custom entity template code.
     *
     * @param indexName Index name
     * @param type Type value
     * @return Information used to determine index and type in Elastic Search - classname and custom entity template code. Returns null if was not able to determine it frem index
     *         name and type.
     */
    @SuppressWarnings("unchecked")
    public ElasticSearchClassInfo getSearchScopeInfo(String indexName, String type) {

        String[] classInfo = esPopulationService.getClassnameAndCETCodeFromIndex(indexName, type);
        if (classInfo != null) {
            try {
                return new ElasticSearchClassInfo((Class<? extends ISearchable>) Class.forName(classInfo[0]), classInfo[1]);
            } catch (ClassNotFoundException e) {
                log.error("Unknown class determined from ES index and type {}/{}", classInfo[0], classInfo[1]);
            }
        }

        return null;
    }

    /**
     * Repopulate cache for a <b>current provider</b>, that stores index and type mapping to class and custom entity template code.
     */
    public void repopulateIndexAndTypeCache() {
        esPopulationService.repopulateIndexAndTypeCache(true);
    }
}