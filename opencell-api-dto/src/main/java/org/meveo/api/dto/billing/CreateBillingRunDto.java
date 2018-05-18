/**
 * 
 */
package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.billing.BillingProcessTypesEnum;

/**
 * The Class CreateBillingRunDto.
 *
 * @author anasseh
 */

@XmlRootElement(name = "CreateBillingRunDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateBillingRunDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The billing cycle code. */
    @XmlAttribute(required = true)
    private String billingCycleCode;

    /** The billing run type enum. */
    @XmlAttribute(required = true)
    private BillingProcessTypesEnum billingRunTypeEnum;

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /** The invoice date. */
    private Date invoiceDate;

    /** The last transaction date. */
    private Date lastTransactionDate;

    /**
     * Instantiates a new creates the billing run dto.
     */
    public CreateBillingRunDto() {

    }

    /**
     * Gets the billing cycle code.
     *
     * @return the billingCycleCode
     */
    public String getBillingCycleCode() {
        return billingCycleCode;
    }

    /**
     * Sets the billing cycle code.
     *
     * @param billingCycleCode the billingCycleCode to set
     */
    public void setBillingCycleCode(String billingCycleCode) {
        this.billingCycleCode = billingCycleCode;
    }

    /**
     * Gets the billing run type enum.
     *
     * @return the billingRunTypeEnum
     */
    public BillingProcessTypesEnum getBillingRunTypeEnum() {
        return billingRunTypeEnum;
    }

    /**
     * Sets the billing run type enum.
     *
     * @param billingRunTypeEnum the billingRunTypeEnum to set
     */
    public void setBillingRunTypeEnum(BillingProcessTypesEnum billingRunTypeEnum) {
        this.billingRunTypeEnum = billingRunTypeEnum;
    }

    /**
     * Gets the start date.
     *
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoiceDate
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the invoiceDate to set
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the last transaction date.
     *
     * @return the lastTransactionDate
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the last transaction date.
     *
     * @param lastTransactionDate the lastTransactionDate to set
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    @Override
    public String toString() {
        return "CreateBillingRunDto [billingCycleCode=" + billingCycleCode + ", billingRunTypeEnum=" + billingRunTypeEnum + ", startDate=" + startDate + ", endDate=" + endDate
                + ", invoiceDate=" + invoiceDate + ", lastTransactionDate=" + lastTransactionDate + "]";
    }

}