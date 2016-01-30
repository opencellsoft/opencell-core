package org.meveo.api.dto.invoice;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "GenerateInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceRequestDto {
	
	@XmlElement(required = true)
	private String billingAccountCode;
	
	@XmlElement(required = true)
	private Date invoicingDate;
	
	@XmlElement(required = true)
	private Date lastTransactionDate;
	
	public GenerateInvoiceRequestDto(){
		
	}

	

	/**
	 * @return the billingAccountCode
	 */
	public String getBillingAccountCode() {
		return billingAccountCode;
	}



	/**
	 * @param billingAccountCode the billingAccountCode to set
	 */
	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}



	/**
	 * @return the invoicingDate
	 */
	public Date getInvoicingDate() {
		return invoicingDate;
	}

	/**
	 * @param invoicingDate the invoicingDate to set
	 */
	public void setInvoicingDate(Date invoicingDate) {
		this.invoicingDate = invoicingDate;
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
		return "GenerateInvoiceRequestDto [billingAccountCode=" + billingAccountCode + ", invoicingDate=" + invoicingDate + ", lastTransactionDate=" + lastTransactionDate + "]";
	}

	
}
