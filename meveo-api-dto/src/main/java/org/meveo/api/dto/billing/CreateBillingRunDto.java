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

/**
 * @author anasseh
 *
 */

@XmlRootElement(name = "CreateBillingRunDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateBillingRunDto extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String  billingCycleCode;
	
	@XmlAttribute(required = true)
	private  BillingRunTypeEnum  billingRunTypeEnum;
	
	private Date startDate;
	private Date endDate;
	private Date invoiceDate;
	private Date lastTransactionDate;
	
	public CreateBillingRunDto(){
		
	}

	/**
	 * @return the billingCycleCode
	 */
	public String getBillingCycleCode() {
		return billingCycleCode;
	}

	/**
	 * @param billingCycleCode the billingCycleCode to set
	 */
	public void setBillingCycleCode(String billingCycleCode) {
		this.billingCycleCode = billingCycleCode;
	}

	/**
	 * @return the billingRunTypeEnum
	 */
	public BillingRunTypeEnum getBillingRunTypeEnum() {
		return billingRunTypeEnum;
	}

	/**
	 * @param billingRunTypeEnum the billingRunTypeEnum to set
	 */
	public void setBillingRunTypeEnum(BillingRunTypeEnum billingRunTypeEnum) {
		this.billingRunTypeEnum = billingRunTypeEnum;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the invoiceDate
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	/**
	 * @return the lastTransactionDate
	 */
	public Date getLastTransactionDate() {
		return lastTransactionDate;
	}

	/**
	 * @param lastTransactionDate the lastTransactionDate to set
	 */
	public void setLastTransactionDate(Date lastTransactionDate) {
		this.lastTransactionDate = lastTransactionDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CreateBillingRunDto [billingCycleCode=" + billingCycleCode + ", billingRunTypeEnum=" + billingRunTypeEnum + ", startDate=" + startDate + ", endDate=" + endDate + ", invoiceDate=" + invoiceDate + ", lastTransactionDate=" + lastTransactionDate + "]";
	}
	
	
	
}
