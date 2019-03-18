/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.util.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.IEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.index.ElasticClient;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceBasedLazyDataModel<T extends IEntity> extends LazyDataModel<T> {

    private static final long serialVersionUID = -5796910936316457321L;

    private Integer rowCount;

    private Integer rowIndex;

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

        if (StringUtils.isBlank(sortField) && !StringUtils.isBlank(getDefaultSortImpl())) {
            sortField = getDefaultSortImpl();
        }

        if ((sortOrder == null || sortOrder == SortOrder.UNSORTED) && getDefaultSortOrderImpl() != null) {
            sortOrder = getDefaultSortOrderImpl();
        }

        String fullTextSearchValue = getFullTextSearchValue(loadingFilters);

        // Do a regular search
        if (fullTextSearchValue == null) {

            PaginationConfiguration paginationConfig = new PaginationConfiguration(first, pageSize, getSearchCriteria(loadingFilters), null, getListFieldsToFetchImpl(), sortField,
                sortOrder);

            setRowCount(countRecords(paginationConfig));

            if (getRowCount() > 0) {
                return loadData(paginationConfig);
            }

            // Do a full text search first and then do a regular search limiting with what full text search has returned
        } else {

            // Do a full text search to retrieve relevant entity identifiers (codes)
            PaginationConfiguration paginationConfig = new PaginationConfiguration(first, pageSize, null, fullTextSearchValue, Arrays.asList("code"), sortField, sortOrder);

            ElasticSearchResults esResults = retrieveDataFromES(paginationConfig);
            setRowCount(esResults.hits);

            // Retrieve the actual data limited to entity codes
            if (getRowCount() > 0) {

                Map<String, Object> dataFilters = new HashMap<String, Object>();
                dataFilters.put("id", esResults.ids);

                paginationConfig = new PaginationConfiguration(0, pageSize, dataFilters, null, getListFieldsToFetchImpl(), sortField, sortOrder);
                return loadData(paginationConfig);
            }
        }

        return new ArrayList<T>();

    }

    /**
     * Perform search in Elastic Search retrieving only identifiers (codes). Json returned from Elastic Search is :<code>{
     *   "took" : 12,
     *   "timed_out" : false,
     *   "_shards" : {
     *     "total" : 5,
     *     "successful" : 5,
     *     "failed" : 0
     *   },
     *   "hits" : {
     *     "total" : 2,
     *     "max_score" : null,
     *     "hits" : [ {
     *       "_index" : "demo_accounts_v1",
     *       "_type" : "AccountEntity",
     *       "_id" : "Andrius_",
     *       "_score" : null,
     *       "fields" : {
     *         "code" : [ "Andrius " ]
     *       },
     *       "sort" : [ "Andrius " ]
     *     }, {
     *       "_index" : "demo_accounts_v1",
     *       "_type" : "AccountEntity",
     *       "_id" : "Andrius_3",
     *       "_score" : null,
     *       "fields" : {
     *         "code" : [ "Andrius 3" ]
     *       },
     *       "sort" : [ "Andrius 3" ]
     *     } ]
     *   }
     * }
     * </code>
     * 
     * @param paginationConfig PaginationConfiguration data holds filtering/pagination information
     * @return A number of total records matched and entity identifiers (codes) corresponding to a given page.
     */
    private ElasticSearchResults retrieveDataFromES(PaginationConfiguration paginationConfig) {

        SearchResponse searchResult = null;
        try {
            searchResult = getElasticClientImpl().search(paginationConfig, new String[] { getPersistenceServiceImpl().getEntityClass().getName() });

            if (searchResult == null) {
                return new ElasticSearchResults(0);
            }

            // Get number of hits

            int rowCount = new Long(searchResult.getHits().getTotalHits()).intValue();

            List<String> ids = new ArrayList<>();

            searchResult.getHits().forEach(hit -> {
                ids.add((String) hit.getSourceAsMap().get(ESBasedDataModel.RECORD_ID));
                // codes.add(hit.getFields().get(ESBasedDataModel.RECORD_CODE).getValue());
            });

            return new ElasticSearchResults(rowCount, ids);

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to search in ES with {} or parse data retrieved {}", paginationConfig, searchResult, e);
        }
        return new ElasticSearchResults(0);
    }

    @Override
    public T getRowData(String rowKey) {
        return getPersistenceServiceImpl().findById(Long.valueOf(rowKey));
    }

    @Override
    public Object getRowKey(T object) {
        return object.getId();
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex == -1 || getPageSize() == 0) {
            this.rowIndex = rowIndex;
        } else {
            this.rowIndex = rowIndex % getPageSize();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getRowData() {
        return ((List<T>) getWrappedData()).get(rowIndex);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean isRowAvailable() {
        if (getWrappedData() == null) {
            return false;
        }

        return rowIndex >= 0 && rowIndex < ((List<T>) getWrappedData()).size();
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public int getRowCount() {
        if (rowCount == null) {
            rowCount = (int) getPersistenceServiceImpl().count();
        }
        return rowCount;
    }

    /**
     * Load a list of entities matching search criteria
     * 
     * @param paginationConfig PaginationConfiguration data holds filtering/pagination information
     * @return A list of entities matching search criteria
     */
    protected List<T> loadData(PaginationConfiguration paginationConfig) {
        return getPersistenceServiceImpl().list(paginationConfig);
    }

    /**
     * Determine a number of records matching search criteria
     * 
     * @param paginationConfig PaginationConfiguration data holds filtering/pagination information
     * @return A number of records matching search criteria
     */
    protected int countRecords(PaginationConfiguration paginationConfig) {
        return (int) getPersistenceServiceImpl().count(paginationConfig);
    }

    /**
     * Get search criteria for data searching.&lt;br/&gt; Search criteria is a map with filter criteria name as a key and value as a value. &lt;br/&gt; Criteria name consist of
     * [&lt;condition&gt; ]&lt;field name&gt; (e.g. "like firstName") where &lt;condition&gt; is a condition to apply to field value comparison and &lt;field name&gt; is an entity
     * attribute name.
     *
     * @param filters the filters
     * @return the search criteria
     */
    protected Map<String, Object> getSearchCriteria(Map<String, Object> filters) {
        return getSearchCriteria();
    }

    /**
     * Get search criteria for data searching.&lt;br/&gt; Search criteria is a map with filter criteria name as a key and value as a value. &lt;br/&gt; Criteria name consist of
     * [&lt;condition&gt; ]&lt;field name&gt; (e.g. "like firstName") where &lt;condition&gt; is a condition to apply to field value comparison and &lt;field name&gt; is an entity
     * attribute name.
     * 
     * @return Map of search criteria
     */
    protected abstract Map<String, Object> getSearchCriteria();

    /**
     * Method that returns concrete PersistenceService. That service is then used for operations on concrete entities (eg. save, delete etc).
     * 
     * @return Persistence service
     */
    protected abstract IPersistenceService<T> getPersistenceServiceImpl();

    /**
     * Get default sort.
     * 
     * @return default sort implementation
     */
    protected String getDefaultSortImpl() {
        return "";
    }

    protected SortOrder getDefaultSortOrderImpl() {
        return SortOrder.DESCENDING;
    }

    /**
     * Override this method if you need to fetch any fields when selecting list of entities in data table. Return list of field names that has to be fetched.
     * 
     * @return List of fields to fetch
     */
    protected List<String> getListFieldsToFetchImpl() {
        return null;
    }

    /**
     * A method to mock List/Set/Collection size property, so it is easy to be used in EL expressions.
     * 
     * @return Size of rows
     */
    public Integer size() {
        return rowCount;
    }

    /**
     * Get a value for full text search.
     * 
     * @param loadingFilters Datatable filters
     * @return fullText search value
     */
    protected String getFullTextSearchValue(Map<String, Object> loadingFilters) {
        if (loadingFilters != null) {
            return (String) loadingFilters.get(ESBasedDataModel.FILTER_PE_FULL_TEXT);
        }
        return null;
    }

    /**
     * Method that return Elastic client
     * 
     * @return Elastic client
     */
    protected abstract ElasticClient getElasticClientImpl();

    /**
     * Elastic Search search results- number of total records matched and entity identifiers of records matched in current page
     */
    private class ElasticSearchResults {
        protected int hits;
        protected List<String> ids;

        public ElasticSearchResults(int hits) {
            this.hits = hits;
        }

        public ElasticSearchResults(int hits, List<String> ids) {
            this.hits = hits;
            this.ids = ids;
        }
    }
}