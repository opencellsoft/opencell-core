package org.meveo.util.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

/**
 * @author Ignas
 *
 */
public class PaginationConfiguration implements Serializable {

	private static final long serialVersionUID = 1l;

	private int firstRow, numberOfRows;
	
	/** Search filters (key = field name, value = search pattern or value). */
	private Map<String, Object> filters;
	
	/** Fields that needs to be fetched when selecting (like lists or other entities). */
	private List<String> fetchFields;
	
	private String sortField;
	
	private SortOrder ordering;
	
	/**
	 * @param firstRow
	 * @param numberOfRows
	 * @param filters
	 * @param fetchFields
	 * @param sortField
	 * @param ordering
	 */
	public PaginationConfiguration(int firstRow, int numberOfRows, Map<String, Object> filters, List<String> fetchFields, String sortField, SortOrder ordering) {
		this.firstRow = firstRow;
		this.numberOfRows = numberOfRows;
		this.filters = filters;
		this.sortField = sortField;
		this.ordering = ordering;
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
		return ordering!=null && sortField!=null && sortField.trim().length()!=0;
	}
	
	public boolean isAscendingSorting() {
		return ordering != null && ordering == SortOrder.ASCENDING;
	}
}
