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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.service.index.ElasticClient;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data model implementation to query Elastic search and provide results to primefaces datatable component.
 * 
 * Elastic Search search returns results as Json: <code>{
 * "took" : 85,
 *   "timed_out" : false,
 *   "_shards" : {
 *     "total" : 18,
 *     "successful" : 18,
 *     "failed" : 0
 *   },
 *   "hits" : {
 *     "total" : 43,
 *     "max_score" : 1.0,
 *     "hits" : [ {
 *       "_index" : "demo_accounts_v1",
 *       "_type" : "AccountEntity",
 *       "_id" : "WS_BASE_UA",
 *       "_score" : 1.0,
 *       "_source" : {
 *         "address" : {
 *           "zipCode" : "21000",
 *           "state" : null,
 *           "address1" : "adress1",
 *           "address2" : "",
 *           "address3" : "adress3",
 *           "country" : "FR",
 *           "city" : "Dijon"
 *         },
 *         "description" : "WS_BASE_UA",
 *         "name" : {
 *           "lastName" : "lastname",
 *           "title" : "M",
 *           "firstName" : "firstname"
 *         },
 *         "code" : "WS_BASE_UA"
 *       }
 *     }, {
 *       "_index" : "demo_accounts_v1",
 *       "_type" : "AccountEntity",
 *       "_id" : "WS_FULL_22_CA_ES",
 *       "_score" : 1.0,
 *       "_source" : {
 *         "address" : {
 *           "zipCode" : "21000",
 *           "state" : null,
 *           "address1" : "adress1",
 *           "address2" : "address2",
 *           "address3" : "adress3",
 *           "country" : "FR",
 *           "city" : "Dijon"
 *         },
 *         "description" : "Andrius Argentina account",
 *         "name" : {
 *           "lastName" : "lastname",
 *           "title" : "M",
 *           "firstName" : "firstname"
 *         },
 *         "code" : "WS_FULL_22_CA_ES"
 *       }
 *     }, </code>
 * 
 * @author Andrius Karpavicius
 */
public abstract class ESBasedDataModel extends LazyDataModel<Map<String, Object>> {

    private static final long serialVersionUID = -5796910936316457321L;

    public static String RECORD_ES_ID = "_id";
    public static String RECORD_SCORE = "_score";
    public static String RECORD_INDEX = "index";
    public static String RECORD_ID = "id";
    public static String FILTER_FULL_TEXT = "fullText";
    protected static String FILTER_PE_FULL_TEXT = "globalFilter";

    private Integer rowCount;

    private Integer rowIndex;

    @Override
    public List<Map<String, Object>> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

        PaginationConfiguration paginationConfig = new PaginationConfiguration(first, pageSize, getSearchCriteria(), getFullTextSearchValue(loadingFilters), null, sortField,
            sortOrder);

        SearchResponse searchResult = retrieveData(paginationConfig);

        setRowCount(parseRowCount(searchResult));
        return parseData(searchResult);

    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getRowData(String rowKey) {

        for (Map<String, Object> recordValues : ((List<Map<String, Object>>) getWrappedData())) {
            if (recordValues.get(getObjectIdField()).equals(rowKey)) {
                return recordValues;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Map<String, Object> object) {
        return object.get(getObjectIdField());
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
    public Map<String, Object> getRowData() {
        return ((List<Map<String, Object>>) getWrappedData()).get(rowIndex);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean isRowAvailable() {
        if (getWrappedData() == null) {
            return false;
        }

        return rowIndex >= 0 && rowIndex < ((List<Map<String, Object>>) getWrappedData()).size();
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
            return 0;
        } else {
            return rowCount;
        }
    }

    /**
     * Perform search in Elastic Search
     * 
     * @param paginationConfig PaginationConfiguration data holds filtering/pagination information
     * @return Search results
     */
    private SearchResponse retrieveData(PaginationConfiguration paginationConfig) {
        try {

            SearchResponse searchResult = getElasticClientImpl().search(paginationConfig, getSearchScope());

            return searchResult;

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to search in ES with {}", paginationConfig, e);
        }
        return null;
    }

    /**
     * Parse number of hits from search results returned by search in Elastic Search
     * 
     * @param searchResult Search response as returned by search in Elastic Search
     * @return Number of records
     */
    private int parseRowCount(SearchResponse searchResult) {
        if (searchResult == null) {
            return 0;
        }
        return new Long(searchResult.getHits().getTotalHits()).intValue();
    }

    /**
     * Parse data from search results returned by search in Elastic Search
     * 
     * @param searchResult Search response as returned by search in Elastic Search
     * @return Records found converted to a map of values
     */
    private List<Map<String, Object>> parseData(SearchResponse searchResult) {

        List<Map<String, Object>> results = new ArrayList<>();

        if (searchResult == null) {
            return results;
        }

        searchResult.getHits().forEach(hit -> {

            Map<String, Object> result = new HashMap<>(hit.getSourceAsMap());
            result.put(RECORD_ES_ID, hit.getId());
            result.put(RECORD_INDEX, hit.getIndex());
            result.put(RECORD_SCORE, new Float(hit.getScore()).doubleValue());

            results.add(result);
        });

        return results;
    }

    /**
     * A method to mock List/Set/Collection size property, so it is easy to be used in EL expressions.
     * 
     * @return rows size
     */
    public Integer size() {
        return rowCount;
    }

    /**
     * Get a fieldname to act as an identifier for an object returned for Elastic Search
     * 
     * @return Fieldname
     */
    public String getObjectIdField() {
        return "code";
    }

    /**
     * Get a list of classes (full or simple name) or CET codes to determine a search scope
     * 
     * @return An array of classnames (full or simple name) or CET codes. Null will search all entities.
     */
    public String[] getSearchScope() {
        return null;
    }

    /**
     * Get a value for full text search.
     * 
     * @param loadingFilters Datatable filters
     * @return fullText search value
     */
    protected String getFullTextSearchValue(Map<String, Object> loadingFilters) {
        return (String) loadingFilters.get(ESBasedDataModel.FILTER_PE_FULL_TEXT);
    }

    /**
     * Get search criteria for data searching. Search criteria is a map with filter criteria name as a key and value as a value. Criteria name consist of [&lt;condition&gt;
     * ]&lt;field name&gt; (e.g. "like firstName") where &lt;condition&gt; is a condition to apply to field value comparison and &lt;field name&gt; is an entity attribute name.
     * 
     * @return Map of search criteria
     */
    protected Map<String, Object> getSearchCriteria() {
        return null;
    }

    /**
     * Method that return Elastic client
     * 
     * @return Elastic client
     */
    protected abstract ElasticClient getElasticClientImpl();

}