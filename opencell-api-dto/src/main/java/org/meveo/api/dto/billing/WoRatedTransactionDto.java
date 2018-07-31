package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;

/**
 * WoRatedTransactionDto : A dto class containing a minimum of Rated Transaction informations like "id" and "status" ... 
 */
@XmlRootElement(name = "ratedTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class WoRatedTransactionDto extends BusinessEntityDto {
    

    /**
     * Default constructor
     */
    public WoRatedTransactionDto() {
    }
    
    public WoRatedTransactionDto(RatedTransaction rt) {
        this.id = rt.getId();
        this.status = rt.getStatus();
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    
    /** The status. */
    private RatedTransactionStatusEnum status;

    /**
     * @return the status
     */
    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

}
