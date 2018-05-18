package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.payments.RejectedType;

/**
 * The Class RejectedPaymentDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectedPaymentDto extends AccountOperationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4498720672406401363L;

    /** The rejected type. */
    private RejectedType rejectedType;
    
    /** The rejected date. */
    private Date rejectedDate;
    
    /** The rejected description. */
    private String rejectedDescription;
    
    /** The rejected code. */
    private String rejectedCode;

    /**
     * Instantiates a new rejected payment dto.
     */
    public RejectedPaymentDto() {
        super.setType("R");
    }

    /**
     * Gets the rejected type.
     *
     * @return the rejected type
     */
    public RejectedType getRejectedType() {
        return rejectedType;
    }

    /**
     * Sets the rejected type.
     *
     * @param rejectedType the new rejected type
     */
    public void setRejectedType(RejectedType rejectedType) {
        this.rejectedType = rejectedType;
    }

    /**
     * Gets the rejected date.
     *
     * @return the rejected date
     */
    public Date getRejectedDate() {
        return rejectedDate;
    }

    /**
     * Sets the rejected date.
     *
     * @param rejectedDate the new rejected date
     */
    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    /**
     * Gets the rejected description.
     *
     * @return the rejected description
     */
    public String getRejectedDescription() {
        return rejectedDescription;
    }

    /**
     * Sets the rejected description.
     *
     * @param rejectedDescription the new rejected description
     */
    public void setRejectedDescription(String rejectedDescription) {
        this.rejectedDescription = rejectedDescription;
    }

    /**
     * Gets the rejected code.
     *
     * @return the rejected code
     */
    public String getRejectedCode() {
        return rejectedCode;
    }

    /**
     * Sets the rejected code.
     *
     * @param rejectedCode the new rejected code
     */
    public void setRejectedCode(String rejectedCode) {
        this.rejectedCode = rejectedCode;
    }

}
