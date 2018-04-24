package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class OtherCreditAndChargeDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OtherCreditAndChargeDto extends AccountOperationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5458679584153463383L;

    /**
     * Instantiates a new other credit and charge dto.
     */
    public OtherCreditAndChargeDto() {
        super.setType("OCC");
    }

    /** The operation date. */
    private Date operationDate;

    /**
     * Gets the operation date.
     *
     * @return the operation date
     */
    public Date getOperationDate() {
        return operationDate;
    }

    /**
     * Sets the operation date.
     *
     * @param operationDate the new operation date
     */
    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

}
