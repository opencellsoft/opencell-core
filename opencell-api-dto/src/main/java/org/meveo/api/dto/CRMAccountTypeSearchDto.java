package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Tony Alejandro.
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class CRMAccountTypeSearchDto implements Serializable, PaginationDto {

	private static final long serialVersionUID = 1L;

	private String searchTerm;

	private String accountTypeCode;

	private int limit;

	private int offset;

	private SortOrder order;

	private String sortField;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getAccountTypeCode() {
		return accountTypeCode;
	}

	public void setAccountTypeCode(String accountTypeCode) {
		this.accountTypeCode = accountTypeCode;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public SortOrder getOrder() {
		return order;
	}

	@Override
	public void setOrder(SortOrder order) {
		this.order = order;
	}

	@Override
	public String getSortField() {
		return sortField;
	}

	@Override
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
}
