package org.meveo.api.dto.invoice;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.FilterDto;


@XmlRootElement(name = "GenerateInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceRequestDto {
	
	@XmlElement(required = true)
	private String billingAccountCode;
	
	@XmlElement(required = true)
	private Date invoicingDate;
	
	@XmlElement()
	private Date firstTransactionDate;
	
	@XmlElement()
	private Date lastTransactionDate;
	
	@XmlElement()
	private FilterDto filter;

	@XmlElement()
	private String orderNumber;
	
	private Boolean generateXML;
	private Boolean generatePDF;
	private Boolean generateAO;	
	
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

	public FilterDto getFilter() {
		return filter;
	}


	public void setFilter(FilterDto filter) {
		this.filter = filter;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}



	/**
	 * @return the generateXML
	 */
	public Boolean getGenerateXML() {
		return generateXML;
	}



	/**
	 * @param generateXML the generateXML to set
	 */
	public void setGenerateXML(Boolean generateXML) {
		this.generateXML = generateXML;
	}



	/**
	 * @return the generatePDF
	 */
	public Boolean getGeneratePDF() {
		return generatePDF;
	}



	/**
	 * @param generatePDF the generatePDF to set
	 */
	public void setGeneratePDF(Boolean generatePDF) {
		this.generatePDF = generatePDF;
	}



	/**
	 * @return the generateAO
	 */
	public Boolean getGenerateAO() {
		return generateAO;
	}



	/**
	 * @param generateAO the generateAO to set
	 */
	public void setGenerateAO(Boolean generateAO) {
		this.generateAO = generateAO;
	}

	@Override
	public String toString() {
		return "GenerateInvoiceRequestDto [billingAccountCode=" + billingAccountCode + ", invoicingDate=" + invoicingDate + ", lastTransactionDate=" + lastTransactionDate + ", filter=" + filter + ", orderNumber=" + orderNumber + ", generateXML=" + generateXML + ", generatePDF=" + generatePDF + ", generateAO=" + generateAO + "]";
	}



	public Date getFirstTransactionDate() {
		return firstTransactionDate;
	}



	public void setFirstTransactionDate(Date firstTransactionDate) {
		this.firstTransactionDate = firstTransactionDate;
	}


}
