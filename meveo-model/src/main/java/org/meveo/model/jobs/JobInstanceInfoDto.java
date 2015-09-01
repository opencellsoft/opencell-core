package org.meveo.model.jobs;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "JobInstanceInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceInfoDto implements Serializable {

	private static final long serialVersionUID = -7091372162470026030L;
    
	@XmlTransient
	private User currentUser;

	
	@XmlElement(required = true)
	private String timerName;
	
	private Date lastTransactionDate;
	private Date invoiceDate;
	private String billingCycle;

	
	public User getCurrentUser() {
		return currentUser;
	}


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
		return "TimerInfoDto [timerName=" + timerName + "invoiceDate=" + invoiceDate + "lastTransactionDate=" + lastTransactionDate + "billingCycle=" + billingCycle + ", toString()="
				+ super.toString() + "]";
	}

}
