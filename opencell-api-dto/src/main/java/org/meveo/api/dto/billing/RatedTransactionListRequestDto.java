package org.meveo.api.dto.billing;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

/**
 * Dto containing the request to find a list of rated transactions
 * @author Said Ramli
 */
public class RatedTransactionListRequestDto extends BaseEntityDto {
    

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    
    /**
     * Default constructor to avoid Json Mapping Exception
     * Instantiates a new rated transaction list request dto.
     */
    public RatedTransactionListRequestDto() {
    }
    
    /**
     * Instantiates a new rated transaction list request dto.
     *
     * @param query the query
     * @param fields the fields
     * @param offset the offset
     * @param limit the limit
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param returnUserAccountCode the return user account code
     */
    public RatedTransactionListRequestDto (String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder, Boolean returnUserAccountCode) {
        this.pagingAndFiltering = new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder);
        this.returnUserAccountCode = returnUserAccountCode;
    }
    
    /** The with user account code. */
    private Boolean returnUserAccountCode;
    
    /** The paging and filtering. */
    private PagingAndFiltering pagingAndFiltering;

    /**
     * @return the pagingAndFiltering
     */
    public PagingAndFiltering getPagingAndFiltering() {
        return pagingAndFiltering;
    }

    /**
     * @param pagingAndFiltering the pagingAndFiltering to set
     */
    public void setPagingAndFiltering(PagingAndFiltering pagingAndFiltering) {
        this.pagingAndFiltering = pagingAndFiltering;
    }

    /**
     * @return the returnUserAccountCode
     */
    public Boolean getReturnUserAccountCode() {
        return returnUserAccountCode;
    }

    /**
     * @param returnUserAccountCode the returnUserAccountCode to set
     */
    public void setReturnUserAccountCode(Boolean returnUserAccountCode) {
        this.returnUserAccountCode = returnUserAccountCode;
    }

}
