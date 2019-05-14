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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.service.base.NativePersistenceService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Lazy datatable model based on a native table data
 * 
 * @author Andrius Karpavicius
 */
public abstract class NativeTableBasedDataModel extends LazyDataModel<Map<String, Object>> {

    private static final long serialVersionUID = -5796910936316457321L;

    private Integer rowCount;

    private Integer rowIndex;

    @Override
    public List<Map<String, Object>> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

        if (StringUtils.isBlank(sortField) && !StringUtils.isBlank(getDefaultSortImpl())) {
            sortField = getDefaultSortImpl();
        }

        if ((sortOrder == null || sortOrder == SortOrder.UNSORTED) && getDefaultSortOrderImpl() != null) {
            sortOrder = getDefaultSortOrderImpl();
        }

        PaginationConfiguration paginationConfig = new PaginationConfiguration(first, pageSize, getSearchCriteria(loadingFilters), null, getListFieldsToFetchImpl(), sortField,
            sortOrder);

        setRowCount(countRecords(paginationConfig));

        if (getRowCount() > 0) {
            return loadData(paginationConfig);
        }

        return new ArrayList<Map<String, Object>>();

    }

    @Override
    public Map<String, Object> getRowData(String rowKey) {
        return getPersistenceServiceImpl().findById(getTableName(), Long.valueOf(rowKey));
    }

    @Override
    public Object getRowKey(Map<String, Object> object) {
        return object.get("id");
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
            rowCount = (int) getPersistenceServiceImpl().count(getTableName(), null);
        }
        return rowCount;
    }

    /**
     * Load a list of entities matching search criteria
     * 
     * @param paginationConfig PaginationConfiguration data holds filtering/pagination information
     * @return A list of entities matching search criteria
     */
    protected List<Map<String, Object>> loadData(PaginationConfiguration paginationConfig) {
        return getPersistenceServiceImpl().list(getTableName(), paginationConfig);
    }

    /**
     * Determine a number of records matching search criteria
     * 
     * @param paginationConfig PaginationConfiguration data holds filtering/pagination information
     * @return A number of records matching search criteria
     */
    protected int countRecords(PaginationConfiguration paginationConfig) {
        return (int) getPersistenceServiceImpl().count(getTableName(), paginationConfig);
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
    protected abstract NativePersistenceService getPersistenceServiceImpl();

    /**
     * Method that returns a table name to load the data from
     * 
     * @return Table name
     */
    protected abstract String getTableName();

    /**
     * Get default sort.
     * 
     * @return default sort implementation
     */
    protected String getDefaultSortImpl() {
        return "id";
    }

    protected SortOrder getDefaultSortOrderImpl() {
        return SortOrder.ASCENDING;
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
}