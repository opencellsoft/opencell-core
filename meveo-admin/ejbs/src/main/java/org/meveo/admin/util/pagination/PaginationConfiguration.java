/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.util.pagination;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

public class PaginationConfiguration implements Serializable {

    private static final long serialVersionUID = -2750287256630146681L;

    private int firstRow, numberOfRows;

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
     * @param firstRow
     * @param numberOfRows
     * @param filters
     * @param fetchFields
     * @param sortField
     * @param sortOrder
     */
    public PaginationConfiguration(int firstRow, int numberOfRows, Map<String, Object> filters, List<String> fetchFields, String sortField, SortOrder sortOrder) {
        this(firstRow, numberOfRows, filters, fetchFields, sortField, sortOrder, null);
    }

    /**
     * @param firstRow First row to retrieve
     * @param numberOfRows Number of rows per page
     * @param filters Search criteria
     * @param fetchFields Extra fields to fetch
     * @param sortField
     * @param ordering
     * @param sortOrdering
     * @param filterByProvider Should filtering by provider be applied
     */
    public PaginationConfiguration(int firstRow, int numberOfRows, Map<String, Object> filters, List<String> fetchFields, String sortField, SortOrder ordering,
            Map<String, String> sortOrdering) {
        this.firstRow = firstRow;
        this.numberOfRows = numberOfRows;
        this.filters = filters;
        this.fetchFields = fetchFields;
        this.sortField = sortField;
        this.ordering = ordering;
        this.sortOrdering = sortOrdering;
    }

    /**
     * @param filters
     */
    public PaginationConfiguration(Map<String, Object> filters) {
        this.filters = filters;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getNumberOfRows() {
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
}
