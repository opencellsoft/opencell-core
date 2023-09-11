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
package org.meveo.admin.util.pagination;

import  org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.JoinType;

/**
 * @author Andrius
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 */
public class PaginationConfiguration implements Serializable {

    private static final long serialVersionUID = -2750287256630146681L;

    private Integer firstRow;

    private Integer numberOfRows;

    /**
     * Full text search filter. Mutually exclusive with filters attribute. fullTextFilter has priority
     */
    private String fullTextFilter;

    /** Search filters (key = field name, value = search pattern or value). */
    private Map<String, Object> filters;

    /** apply fetch to left join fetchFields */
    private boolean doFetch = true;
    
    /**
     * Fields to return as query results (regular comma separated field list). If not provided, a full entity will be retrieved
     */
    private String selectFields;

    /**
     * Fields that needs to be fetched when selecting (like lists or other entities).
     */
    private List<String> fetchFields;

    /**
     * Result should be grouped by following fields.
     */
    private Set<String> groupBy;

    /**
     * Result should be filtered by having clause.
     */
    private Set<String> having;

    /**
     * Sort field and order repeated multiple times
     */
    private Object[] ordering;
    
    private JoinType joinType;


    private Integer limit;

    /**
     * Shall query results be cached - see Hibernate query cache behavior
     */
    private boolean cacheable = false;
    
    /**
     * Operator to use when building where statement
     */
    private FilterOperatorEnum filterOperator = FilterOperatorEnum.AND;

    private boolean distinctQuery = Boolean.FALSE;

    private boolean queryReportQuery = Boolean.FALSE;

    /**
     * 
     * @param sortField Field to sort by
     * @param sortOrder Sort order
     */
    public PaginationConfiguration(String sortField, SortOrder sortOrder) {
        this(null, null, null, null, null, sortField, sortOrder);
    }
    
    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, Object... sortFieldsAndOrder) {
        this(firstRow, numberOfRows, filters, fullTextFilter, true, fetchFields, sortFieldsAndOrder);
    }

    /**
     * Constructor
     * 
     * @param firstRow Number of the first row to retrieve
     * @param numberOfRows Number of rows to retrieve
     * @param filters Search criteria
     * @param fullTextFilter full text filter.
     * @param doFetch fetch fields or not
     * @param fetchFields Lazy loaded fields to fetch
     * @param sortFieldsAndOrder Sort field and order repeated multiple times
     */
    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, boolean doFetch, List<String> fetchFields, Object... sortFieldsAndOrder) {
        this.firstRow = firstRow;
        this.numberOfRows = numberOfRows;
        this.filters = filters;
        this.fullTextFilter = fullTextFilter;
        this.doFetch = doFetch;
        this.fetchFields = fetchFields;
        this.limit = numberOfRows;

        List<Object> sortValues = new ArrayList<Object>();
        for (int i = 0; i < sortFieldsAndOrder.length; i = i + 2) {
            if (sortFieldsAndOrder[i] == null) {
                continue;
            }
            sortValues.add(sortFieldsAndOrder[i]);
            sortValues.add(sortFieldsAndOrder[i + 1] == null ? SortOrder.ASCENDING : sortFieldsAndOrder[i + 1]);
        }

        this.ordering = sortValues.size() > 0 ? sortValues.toArray() : null;
        if (filters != null) {
        	this.filterOperator = (FilterOperatorEnum) filters.getOrDefault("$operator", FilterOperatorEnum.AND);
        }
    }

    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, Set<String> groupBy, Set<String> having, JoinType joinType, Object... sortFieldsAndOrder) {
    	this(firstRow, numberOfRows, filters, fullTextFilter, fetchFields, groupBy, having, sortFieldsAndOrder);
    	this.joinType=joinType;
    }

    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, Set<String> groupBy, Set<String> having, JoinType joinType, Boolean distinct, Object... sortFieldsAndOrder) {
        this(firstRow, numberOfRows, filters, fullTextFilter, fetchFields, groupBy, having, joinType, sortFieldsAndOrder);
        this.distinctQuery = distinct;
    }
    
    /**
     * Constructor
     *
     * @param firstRow Number of the first row to retrieve
     * @param numberOfRows Number of rows to retrieve
     * @param filters Search criteria
     * @param fullTextFilter full text filter.
     * @param fetchFields Lazy loaded fields to fetch
     * @param groupBy result grouped by fields
     * @param sortFieldsAndOrder Sort field and order repeated multiple times
     */
    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, Set<String> groupBy, Set<String> having, Object... sortFieldsAndOrder) {
        this(firstRow, numberOfRows, filters, fullTextFilter, fetchFields, sortFieldsAndOrder);
        this.groupBy = groupBy;
        this.having = having;
    }

    /**
     * Constructor
     * 
     * @param filters Search criteria
     */
    public PaginationConfiguration(Map<String, Object> filters) {
        this.filters = filters;
    }

    /**
     * Constructor
     * 
     * @param filters Search criteria
     * @param sortField Field to sort by
     * @param sortOrder Sort order
     */
    public PaginationConfiguration(Map<String, Object> filters, String sortField, SortOrder sortOrder) {
        this.filters = filters;
        if (sortField != null && sortOrder != null) {
            ordering = new Object[] { sortField, sortOrder };
        } else if (sortField != null) {
            ordering = new Object[] { sortField, SortOrder.ASCENDING };
        }
    }

    public PaginationConfiguration(Map<String, Object> filters, String sortField, SortOrder sortOrder, int numberOfRows) {
        this.filters = filters;
        if (sortField != null && sortOrder != null) {
            ordering = new Object[] { sortField, sortOrder };
        } else if (sortField != null) {
            ordering = new Object[] { sortField, SortOrder.ASCENDING };
        }
        this.numberOfRows = numberOfRows;
    }

    /**
     * @return A row to retrieve from
     */
    public Integer getFirstRow() {
        return firstRow;
    }

    /**
     * @return Number of rows to retrieve
     */
    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @return Sort field and sort order repeated multiple times
     */
    public Object[] getOrderings() {
        return ordering;
    }

    /**
     * @return A first field to sort by
     */
    public String getFirstSortField() {
        return ordering != null ? (String) ordering[0] : null;
    }

    /**
     * @return A sort order of the first field to sort
     */
    public SortOrder getFirstSortOrder() {
        return ordering != null && ordering.length > 1 ? (SortOrder) ordering[1] : null;
    }

    /**
     * @return Is the first field should be sorted in ascending order
     */
    public boolean isFirstSortAscending() {
        return getFirstSortOrder() == SortOrder.ASCENDING;
    }

    /**
     * @return Search criteria
     */
    public Map<String, Object> getFilters() {
        return filters;
    }

    /**
     * @param filters Search criteria
     */
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
    
    /**
     * @param Sort field and sort order
     */
    public void setOrderings(Object[] ordering) {
        this.ordering = ordering;
    }

    public String getFullTextFilter() {
        return fullTextFilter;
    }

    public boolean isDoFetch() {
		return doFetch;
	}

	public void setDoFetch(boolean doFetch) {
		this.doFetch = doFetch;
	}

	public List<String> getFetchFields() {
        return fetchFields;
    }

    /**
     * @param fetchFields Related entity fields to fetch eagerly
     */
    public void setFetchFields(List<String> fetchFields) {
        this.fetchFields = fetchFields;
    }

    public Set<String> getGroupBy() {
        return groupBy;
    }

    /**
     * @param groupBy Related fields to group
     */
    public void setGroupBy(Set<String> groupBy) {
        this.groupBy = groupBy;
    }

    public Set<String> getHaving() {
        return having;
    }

    /**
     * @param having Related fields to filter
     */
    public void setHaving(Set<String> having) {
        this.having = having;
    }

    /**
     * @return Should any sorting be applied to search results
     */
    public boolean isSorted() {
        return ordering != null && ordering.length > 0;
    }

    @Override
    public String toString() {
        return String.format("PaginationConfiguration [firstRow=%s, numberOfRows=%s, fullTextFilter=%s, filters=%s, fetchFields=%s, ordering=%s]", firstRow, numberOfRows, fullTextFilter, filters, fetchFields, ordering);
    }

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public FilterOperatorEnum getFilterOperator() {
		return filterOperator;
	}

	public void setFilterOperator(FilterOperatorEnum filterOperator) {
		this.filterOperator = filterOperator;
	}


    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

	/**
	 * @param cacheable Shall query results be cached - see Hibernate query cache behavior
	 */
    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * @return Shall query results be cached - see Hibernate query cache behavior
     */
    public boolean isCacheable() {
        return cacheable;
    }


    public boolean isDistinctQuery() {
        return distinctQuery;
    }

    public void setDistinctQuery(boolean distinctQuery) {
        this.distinctQuery = distinctQuery;
    }

    public boolean isQueryReportQuery() {
        return queryReportQuery;
    }

    public void setQueryReportQuery(boolean queryReportQuery) {
        this.queryReportQuery = queryReportQuery;
    }

    /**
     * @return Fields to return as query results (regular comma separated field list). If not provided, a full entity will be retrieved
     */
    public String getSelectFields() {
        return selectFields;
    }

    /**
     * @param selectFields Fields to return as query results (regular comma separated field list). If not provided, a full entity will be retrieved
     */
    public void setSelectFields(String selectFields) {
        this.selectFields = selectFields;
    }
}