package org.meveo.api.dto.response.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * RatedTransactionListResponseDto : A class containing the response of searching a list of Rated transactions
 * 
 * @author Said Ramli
 */
@XmlRootElement(name = "RatedTransactionListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RatedTransactionListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Rated transactions. */
    @XmlElementWrapper
    @XmlElement(name = "ratedTransaction")
    private List<RatedTransactionDto> ratedTransactions;

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