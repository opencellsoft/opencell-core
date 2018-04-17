package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;


/**
 * The Class CRMAccountTypeSearchDto.
 *
 * @author Tony Alejandro.
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class CRMAccountTypeSearchDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The search term. */
    private String searchTerm;

    /** The account type code. */
    private String accountTypeCode;

    /** The limit. */
    private int limit;

    /** The offset. */
    private int offset;

    /** The order. */
    private SortOrder order;

    /** The sort field. */
    private String sortField;

    /**
     * Gets the search term.
     *
     * @return the search term
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * Sets the search term.
     *
     * @param searchTerm the new search term
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Gets the account type code.
     *
     * @return the account type code
     */
    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    /**
     * Sets the account type code.
     *
     * @param accountTypeCode the new account type code
     */
    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the new limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset.
     *
     * @param offset the new offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public SortOrder getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order the new order
     */
    public void setOrder(SortOrder order) {
        this.order = order;
    }

    /**
     * Gets the sort field.
     *
     * @return the sort field
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * Sets the sort field.
     *
     * @param sortField the new sort field
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
}
