package org.meveo.service.index;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.ISearchable;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.index.ElasticSearchChangeset.ElasticSearchAction;
import org.slf4j.Logger;

/**
 * Provides functionality to interact with Elastic Search cluster
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.0
 * 
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
            id = cleanUpCode(buildId(entity));

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
            id = cleanUpCode(buildId(entity));
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
            id = cleanUpCode(buildId(entity));

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
     * Execute a search compatible primefaces data table component search
     * 
     * @param paginationConfig Query, pagination and sorting configuration
     * @param classnamesOrCetCodes An array of full classnames or CET codes
     * @return Json result
     * @throws BusinessException business exception
     */
    public String search(PaginationConfiguration paginationConfig, String[] classnamesOrCetCodes) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return "{}";
        }

        SortOrder sortOrder = (paginationConfig.getOrdering() == null || paginationConfig.getOrdering() == org.primefaces.model.SortOrder.UNSORTED) ? null
                : paginationConfig.getOrdering() == org.primefaces.model.SortOrder.ASCENDING ? SortOrder.ASC : SortOrder.DESC;

        String[] returnFields = paginationConfig.getFetchFields() == null ? null : (String[]) paginationConfig.getFetchFields().toArray();

        // Search either by a field
        if (StringUtils.isBlank(paginationConfig.getFullTextFilter()) && paginationConfig.getFilters() != null && !paginationConfig.getFilters().isEmpty()) {
            return search(paginationConfig.getFilters(), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows(), paginationConfig.getSortField(), sortOrder,
                returnFields, getSearchScopeInfo(classnamesOrCetCodes, true));
        } else {
            return search(paginationConfig.getFullTextFilter(), null, paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows(), paginationConfig.getSortField(),
                    sortOrder, returnFields, getSearchScopeInfo(classnamesOrCetCodes, true));
        }
    }

    /**
     * Execute a search on all fields (_all field)
     * 
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param category - search by category that is directly taken from the name of the entity found in entityMapping.
     *                 property of elasticSearchConfiguration.json.
     *                 e.g. Customer, CustomerAccount, AccountOperation, etc.
     *                 See elasticSearchConfiguration.json entityMapping keys for a list of categories.
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField - Field to sort by. If omitted, will sort by score.
     * @param sortOrder Sorting order
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classInfo Entity classes to match
     * @return Json result
     * @throws BusinessException business exception
     */
    public String search(String query, String category, Integer from, Integer size, String sortField, SortOrder sortOrder, String[] returnFields,
                         List<ElasticSearchClassInfo> classInfo) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return "{}";
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
            return "{}";
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

        log.debug("Execute Elastic Search search for \"{}\" records {}-{} on {}, {} sort by {} {}", query, from, from + size, indexes, types, sortField, sortOrder);

        SearchRequestBuilder reqBuilder = esConnection.getClient().prepareSearch(indexes.toArray(new String[0]));

        if (!StringUtils.isBlank(category)){
            String[] categories = new String[]{category};
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

        // Add sorting if requested
        if (sortField != null) {
            if (sortOrder == null) {
                sortOrder = SortOrder.ASC;
            }
            reqBuilder.addSort(sortField, sortOrder);
        }

        if (StringUtils.isBlank(query)) {
            reqBuilder.setQuery(QueryBuilders.matchAllQuery());
        } else {
            reqBuilder.setQuery(QueryBuilders.queryStringQuery(query).lenient(true));
        }
        SearchResponse response = reqBuilder.execute().actionGet();

        String result = response.toString();
        // log.trace("Data retrieved from Elastic Search in full text search is {}", result);
        return result;
    }

    /**
     * Execute a search on given fields for given values
     * 
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField - Field to sort by. If omitted, will sort by score.
     * @param sortOrder Sorting order
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @param classInfo Entity classes to match
     * @return Json result
     * @throws BusinessException business exception
     */
    public String search(Map<String, ?> queryValues, Integer from, Integer size, String sortField, SortOrder sortOrder, String[] returnFields,
            List<ElasticSearchClassInfo> classInfo) throws BusinessException {

        if (!esConnection.isEnabled()) {
            return "{}";
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
            return "{}";
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

        log.debug("Execute Elastic Search search for {} records {}-{} on {}, {} sort by {} {}", queryValues, from, from + size, indexes, types, sortField, sortOrder);

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

        // Add sorting if requested
        if (sortField != null) {
            if (sortOrder == null) {
                sortOrder = SortOrder.ASC;
            }
            reqBuilder.addSort(sortField, sortOrder);
        }

        if (queryValues.isEmpty()) {
            reqBuilder.setQuery(QueryBuilders.matchAllQuery());

        } else if (queryValues.size() == 1) {
            Entry<String, ?> fieldValue = queryValues.entrySet().iterator().next();
            if (fieldValue.getKey().contains(",")) {
                reqBuilder.setQuery(QueryBuilders.multiMatchQuery(fieldValue.getValue(), StringUtils.stripAll(fieldValue.getKey().split(","))));
            } else {
                reqBuilder.setQuery(QueryBuilders.matchQuery(fieldValue.getKey(), fieldValue.getValue()));
            }
        } else {

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            for (Entry<String, ?> fieldValue : queryValues.entrySet()) {
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
        // log.trace("Data retrieved from Elastic Search in full text search is {}", result);
        return result;
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

            log.info("Finished repopulating Elastic Search");

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

    protected static String cleanUpCode(String code) {

        code = code.replace(' ', '_');
        return code;
    }

    protected static String cleanUpAndLowercaseCode(String code) {

        code = cleanUpCode(code).toLowerCase();
        return code;
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
                    classInfo = new ElasticSearchClassInfo(CustomEntityInstance.class, classnameOrCetCode);

                    // If still not matched - try how code is stored in ES with spaces cleanedup
                } else {
                    Collection<CustomEntityTemplate> cets = cfCache.getCustomEntityTemplates();
                    for (CustomEntityTemplate cetToClean : cets) {
                        if (cleanUpCode(cetToClean.getCode()).equals(classnameOrCetCode)) {
                            classInfo = new ElasticSearchClassInfo(CustomEntityInstance.class, cetToClean.getCode());
                        }
                        break;
                    }
                }
            }
        }
        return classInfo;
    }

    protected static String buildId(ISearchable entity) {
        return entity.getCode() + "__" + entity.getId();
    }
}