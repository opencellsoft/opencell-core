package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.account.RatedTransactionListDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * RatedTransactionListResponseDto : A class containing the response of searching a list of Rated transactions 
 * 
 * @author Said Ramli
 */
public class RatedTransactionListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The rated transaction list dto. */
    private RatedTransactionListDto ratedTransactionListDto; 
    
    /**
     * @return the ratedTransactionListDto
     */
    public RatedTransactionListDto getRatedTransactionListDto() {
        return ratedTransactionListDto;
    }

    /**
     * @param ratedTransactionListDto the ratedTransactionListDto to set
     */
    public void setRatedTransactionListDto(RatedTransactionListDto ratedTransactionListDto) {
        this.ratedTransactionListDto = ratedTransactionListDto;
    }
}
