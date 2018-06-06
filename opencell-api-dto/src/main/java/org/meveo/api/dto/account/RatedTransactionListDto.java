package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.RatedTransactionDto;

/**
 * RatedTransactionListDt : A dto holding a list of RatedTransactionDto
 */
public class RatedTransactionListDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The rated transaces. */
    @XmlElementWrapper
    @XmlElement(name = "ratedTransactions")
    private List<RatedTransactionDto> ratedTransactions;

    /** The total number of records. */
    private Long totalNumberOfRecords;
    
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
    
    /**
     * @return the ratedTransactions
     */
    public List<RatedTransactionDto> getRatedTransactions() {
        if (this.ratedTransactions == null) {
            this.ratedTransactions = new ArrayList<>(); 
         }
        return ratedTransactions;
    }

}
