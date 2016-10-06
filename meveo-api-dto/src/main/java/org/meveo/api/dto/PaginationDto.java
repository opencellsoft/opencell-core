package org.meveo.api.dto;

/**
 * @author Tony Alejandro.
 */
public interface PaginationDto {

	enum SortOrder {
		ASCENDING, DESCENDING
	}

	int getLimit();

	void setLimit(int limit);

	int getOffset();

	void setOffset(int offset);

	SortOrder getOrder();

	void setOrder(SortOrder order);

	String getSortField();

	void setSortField(String field);

}
