package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.RatedTransactionDto;

/**
 * RatedTransactionListDt : A dto holding a list of RatedTransactionDto
 */
public class RatedTransactionListDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The rated transaces. */
    private List<RatedTransactionDto> ratedTransaces;

    /** The total number of records. */
    private Long totalNumberOfRecords;
    
    /**
     * @return the ratedTransaces
     */
    public List<RatedTransactionDto> getRatedTransaces() {
        if (this.ratedTransaces == null) {
           this.ratedTransaces = new ArrayList<>(); 
        }
        return ratedTransaces;
    }

    /**
     * @return the totalNumberOfRecords
     */
    public Long getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    /**
     * @param totalNumberOfRecords the totalNumberOfRecords to set
     */
    public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

}
