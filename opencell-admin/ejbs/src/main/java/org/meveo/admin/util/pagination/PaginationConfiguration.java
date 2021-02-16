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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

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

    /**
     * Fields that needs to be fetched when selecting (like lists or other entities).
     */
    private List<String> fetchFields;

    /**
     * Sort field and order repeated multiple times
     */
    private Object[] ordering;

    /**
     * 
     * @param sortField Field to sort by
     * @param sortOrder Sort order
     */
    public PaginationConfiguration(String sortField, SortOrder sortOrder) {
        this(null, null, null, null, null, sortField, sortOrder);
    }

    /**
     * Constructor
     * 
     * @param firstRow Number of the first row to retrieve
     * @param numberOfRows Number of rows to retrieve
     * @param filters Search criteria
     * @param fullTextFilter full text filter.
     * @param fetchFields Lazy loaded fields to fetch
     * @param sortFieldsAndOrder Sort field and order repeated multiple times
     */
    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, Object... sortFieldsAndOrder) {
        this.firstRow = firstRow;
        this.numberOfRows = numberOfRows;
        this.filters = filters;
        this.fullTextFilter = fullTextFilter;
        this.fetchFields = fetchFields;

        List<Object> sortValues = new ArrayList<Object>();
        for (int i = 0; i < sortFieldsAndOrder.length; i = i + 2) {
            if (sortFieldsAndOrder[i] == null) {
                continue;
            }
            sortValues.add(sortFieldsAndOrder[i]);
            sortValues.add(sortFieldsAndOrder[i + 1] == null ? SortOrder.ASCENDING : sortFieldsAndOrder[i + 1]);
        }

        this.ordering = sortValues.size() > 0 ? sortValues.toArray() : null;
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

    public String getFullTextFilter() {
        return fullTextFilter;
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
}