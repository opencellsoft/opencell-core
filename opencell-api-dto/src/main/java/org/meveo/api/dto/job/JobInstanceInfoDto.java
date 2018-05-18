package org.meveo.api.dto.job;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class JobInstanceInfoDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "JobInstanceInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceInfoDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7091372162470026030L;

    /** The timer name. */
    @Deprecated
    @XmlElement(required = false)
    private String timerName;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The last transaction date. */
    private Date lastTransactionDate;

    /** The invoice date. */
    private Date invoiceDate;

    /** The billing cycle. */
    private String billingCycle;

    /** Ignore a check if job is currently running and launch it anyway. */
    private boolean forceExecution;

    /**
     * Gets the last transaction date.
     *
     * @return the last transaction date
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the last transaction date.
     *
     * @param lastTransactionDate the new last transaction date
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the new invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the billing cycle.
     *
     * @return the billing cycle
     */
    public String getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the new billing cycle
     */
    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * Gets the timer name.
     *
     * @return the timer name
     */
    public String getTimerName() {
        return timerName;
    }

    /**
     * Sets the timer name.
     *
     * @param timerName the new timer name
     */
    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Checks if is force execution.
     *
     * @return true, if is force execution
     */
    public boolean isForceExecution() {
        return forceExecution;
    }

    /**
     * Sets the force execution.
     *
     * @param forceExecution the new force execution
     */
    public void setForceExecution(boolean forceExecution) {
        this.forceExecution = forceExecution;
    }
    
    @Override
    public String toString() {
        return "JobInstanceInfoDto [timerName=" + timerName + ", code=" + code + ", lastTransactionDate=" + lastTransactionDate + ", invoiceDate=" + invoiceDate + ", billingCycle="
                + billingCycle + "]";
    }
}