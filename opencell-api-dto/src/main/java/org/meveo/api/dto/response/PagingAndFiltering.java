package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PagingAndFiltering implements Serializable {

    private static final long serialVersionUID = 4367485228070123385L;

    /**
     * Full text search filter. Mutually exclusive with filters attribute. fullTextFilter has priority
     */
    private String fullTextFilter;

    /**
     * Search filters (key = field name, value = search pattern or value).
     */
    private Map<String, Object> filters;

    /**
     * Data retrieval options/fieldnames separated by a comma
     */
    private String fields;

    /**
     * Pagination - from record number
     */
    private Integer offset;

    /**
     * Pagination - number of items to retrieve
     */
    private Integer limit = 100;

    /**
     * Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     */
    private String sortBy;

    /**
     * Sorting - sort order.
     */
    private SortOrder sortOrder;

    /**
     * Total number of records. Note - filled on response only.
     */
    private Integer totalNumberOfRecords;

    public enum SortOrder {
        ASCENDING, DESCENDING;
    }

    public PagingAndFiltering() {

    }

    public PagingAndFiltering(String encodedQuery, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super();
        this.filters = decodeQuery(encodedQuery);
        this.fields = fields;
        this.offset = offset;
        this.limit = limit;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public PagingAndFiltering(String fullTextFilter, Map<String, Object> filters, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super();
        this.fullTextFilter = fullTextFilter;
        this.filters = filters;
        this.fields = fields;
        this.offset = offset;
        this.limit = limit;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public String getFullTextFilter() {
        return fullTextFilter;
    }

    public String getFields() {
        return fields;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public void setTotalNumberOfRecords(Integer count) {
        this.totalNumberOfRecords = count;
    }

    /**
     * Decode string type query to a filter criteria map. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * 
     * @param query Encoded filter criteria as a string.
     * @return A decoded filter criteria map
     */
    private Map<String, Object> decodeQuery(String query) {

        if (query == null) {
            return null;
        }

        Map<String, Object> filters = new HashMap<String, Object>();

        String[] splitByItem = query.split("\\|");

        for (String filterItem : splitByItem) {
            String[] filterItems = filterItem.split(":");
            filters.put(filterItems[0], filterItems[1]);
        }

        return filters;
    }

    /**
     * Check if a given field retrieval option is enabled
     * 
     * @param fieldOption
     * @return
     */
    public boolean hasFieldOption(String fieldOption) {
        if (fields == null) {
            return false;
        } else {
            return fields.contains(fieldOption);
        }
    }

    public void addFilter(String key, Object value) {
        if (filters == null) {
            filters = new HashMap<>();
        }

        filters.put(key, value);
    }
}