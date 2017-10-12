package org.meveo.api.dto.job;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "JobInstanceInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceInfoDto implements Serializable {

    private static final long serialVersionUID = -7091372162470026030L;

    @Deprecated
    @XmlElement(required = false)
    private String timerName;

    @XmlAttribute(required = true)
    private String code;

    private Date lastTransactionDate;

    private Date invoiceDate;

    private String billingCycle;

    /**
     * Ignore a check if job is currently running and launch it anyway
     */
    private boolean forceExecution;

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    @Override
    public String toString() {
        return "JobInstanceInfoDto [timerName=" + timerName + ", code=" + code + ", lastTransactionDate=" + lastTransactionDate + ", invoiceDate=" + invoiceDate + ", billingCycle="
                + billingCycle + "]";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isForceExecution() {
        return forceExecution;
    }

    public void setForceExecution(boolean forceExecution) {
        this.forceExecution = forceExecution;
    }
}