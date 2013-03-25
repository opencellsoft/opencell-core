/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.commons.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PaginationConfiguration implements Serializable {

	private static final long serialVersionUID = -2750287256630146681L;

	public enum Ordering { ASCENDING, DESCENDING };
	
	private int firstRow, numberOfRows;
	
	/** Search filters (key = field name, value = search pattern or value). */
	private Map<String, Object> filters;
    private Map<String, String> sortOrdering;
	
	/** Fields that needs to be fetched when selecting (like lists or other entities). */
	private List<String> fetchFields;
	
	private String sortField;
	private Ordering ordering;
	
	/**
	 * @param firstRow
	 * @param numberOfRows
	 * @param filters
	 * @param fetchFields
	 * @param sortField
	 * @param ordering
	 */
	public PaginationConfiguration(int firstRow, int numberOfRows, Map<String, Object> filters, List<String> fetchFields, String sortField, Ordering ordering,Map<String, String> sortOrdering) {
		this.firstRow = firstRow;
		this.numberOfRows = numberOfRows;
		this.filters = filters;
		this.sortField = sortField;
		this.sortOrdering = sortOrdering;
		this.fetchFields = fetchFields;
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

	public Ordering getOrdering() {
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
		return ordering!=null && sortField!=null && sortField.trim().length()!=0;
	}
	
	public boolean isAscendingSorting() {
		return ordering!=null && ordering == Ordering.ASCENDING;
	}
}
