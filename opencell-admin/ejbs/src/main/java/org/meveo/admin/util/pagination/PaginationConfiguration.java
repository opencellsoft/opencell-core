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

    private Map<String, String> sortOrdering;

    /**
     * Fields that needs to be fetched when selecting (like lists or other entities).
     */
    private List<String> fetchFields;

    private String sortField;

    private SortOrder ordering;

    /**
     * 
     * @param sortField Field to sort by
     * @param sortOrder Sort order
     */
    public PaginationConfiguration(String sortField, SortOrder sortOrder) {
        this(null, null, null, null, null, sortField, sortOrder, null);
    }

    /**
     * @param firstRow Number of the first row to retrieve
     * @param numberOfRows Number of rows to retrieve
     * @param filters Search criteria to apply
     * @param fullTextFilter full text filter
     * @param fetchFields Lazy loaded fields to fetch
     * @param sortField Field to sort by
     * @param sortOrder Sort order
     */
    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, String sortField,
            SortOrder sortOrder) {
        this(firstRow, numberOfRows, filters, fullTextFilter, fetchFields, sortField, sortOrder, null);
    }

    /**
     * Constructor
     * 
     * @param firstRow Number of the first row to retrieve
     * @param numberOfRows Number of rows to retrieve
     * @param filters Search criteria
     * @param fullTextFilter full text filter.
     * @param fetchFields Lazy loaded fields to fetch
     * @param sortField Field to sort by
     * @param sortOrder Sort order
     * @param sortOrdering sort ordering.
     */
    public PaginationConfiguration(Integer firstRow, Integer numberOfRows, Map<String, Object> filters, String fullTextFilter, List<String> fetchFields, String sortField,
            SortOrder sortOrder, Map<String, String> sortOrdering) {
        this.firstRow = firstRow;
        this.numberOfRows = numberOfRows;
        this.filters = filters;
        this.fullTextFilter = fullTextFilter;
        this.fetchFields = fetchFields;
        this.sortField = sortField;
        this.ordering = sortOrder;
        this.sortOrdering = sortOrdering;
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
        this.sortField = sortField;
        this.ordering = sortOrder;
    }

    public Integer getFirstRow() {
        return firstRow;
    }

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public String getSortField() {
        return sortField;
    }

    public Map<String, String> getOrderings() {
        return sortOrdering;
    }

    public SortOrder getOrdering() {
        return ordering;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public String getFullTextFilter() {
        return fullTextFilter;
    }

    public List<String> getFetchFields() {
        return fetchFields;
    }

    public void setFetchFields(List<String> fetchFields) {
        this.fetchFields = fetchFields;
    }

    public boolean isSorted() {
        return ordering != null && sortField != null && sortField.trim().length() != 0;
    }

    public boolean isAscendingSorting() {
        return ordering != null && ordering == SortOrder.ASCENDING;
    }

    @Override
    public String toString() {
        return String.format("PaginationConfiguration [firstRow=%s, numberOfRows=%s, fullTextFilter=%s, filters=%s, sortOrdering=%s, fetchFields=%s, sortField=%s, ordering=%s]",
            firstRow, numberOfRows, fullTextFilter, filters, sortOrdering, fetchFields, sortField, ordering);
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}