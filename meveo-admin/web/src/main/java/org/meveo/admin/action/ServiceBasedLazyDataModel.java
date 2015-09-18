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
package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.IEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public abstract class ServiceBasedLazyDataModel<T extends IEntity> extends
		LazyDataModel<T> {

	private static final long serialVersionUID = -5796910936316457321L;

	private Integer rowCount;

	private Integer rowIndex;

	@Override
	public List<T> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, Object> loadingFilters) {

		if (StringUtils.isBlank(sortField)
				&& !StringUtils.isBlank(getDefaultSortImpl())) {
			sortField = getDefaultSortImpl();
		}

        if ((sortOrder == null || sortOrder == SortOrder.UNSORTED) && getDefaultSortOrderImpl() != null) {
            sortOrder = getDefaultSortOrderImpl();
        }

		PaginationConfiguration paginationConfig = new PaginationConfiguration(
				first, pageSize, getSearchCriteria(),
				getListFieldsToFetchImpl(), sortField, sortOrder);

		setRowCount(countRecords(paginationConfig));

		if (getRowCount() > 0) {
			return loadData(paginationConfig);

		} else {
			return new ArrayList<T>();
		}
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
	 * @param paginationConfig
	 *            PaginationConfiguration data holds filtering/pagination
	 *            information
	 * @return A list of entities matching search criteria
	 */
	protected List<T> loadData(PaginationConfiguration paginationConfig) {
		return getPersistenceServiceImpl().list(paginationConfig);
	}

	/**
	 * Determine a number of records matching search criteria
	 * 
	 * @param paginationConfig
	 *            PaginationConfiguration data holds filtering/pagination
	 *            information
	 * @return A number of records matching search criteria
	 */
	protected int countRecords(PaginationConfiguration paginationConfig) {
		return (int) getPersistenceServiceImpl().count(paginationConfig);
	}

	/**
	 * Get search criteria for data searching.<br/>
	 * Search criteria is a map with filter criteria name as a key and value as
	 * a value. <br/>
	 * Criteria name consist of [<condition> ]<field name> (e.g.
	 * "like firstName") where <condition> is a condition to apply to field
	 * value comparison and <field name> is an entity attribute name.
	 * 
	 * @return Map of search criteria
	 */
	protected abstract Map<String, Object> getSearchCriteria();

	/**
	 * Method that returns concrete PersistenceService. That service is then
	 * used for operations on concrete entities (eg. save, delete etc).
	 * 
	 * @return Persistence service
	 */
	protected abstract IPersistenceService<T> getPersistenceServiceImpl();

	/**
	 * Get default sort
	 * 
	 * @return
	 */
	protected String getDefaultSortImpl() {
		return "";
	}

	protected SortOrder getDefaultSortOrderImpl() {
		return SortOrder.DESCENDING;
	}

	/**
	 * Override this method if you need to fetch any fields when selecting list
	 * of entities in data table. Return list of field names that has to be
	 * fetched.
	 */
	protected List<String> getListFieldsToFetchImpl() {
		return null;
	}
	
    /**
     * A method to mock List/Set/Collection size property, so it is easy to be used in EL expressions
     */
    public Integer size() {
        return rowCount;
    }
}
