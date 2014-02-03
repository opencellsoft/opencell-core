package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends BaseDto {

	
	private static final long serialVersionUID = 1072382628068718580L;
	
	private String billingAccountCode;
	
	
	private Date invoiceDate;

	private Date dueDate;

	private BigDecimal amount;

	private BigDecimal discount;

	private BigDecimal amountWithoutTax;

	private BigDecimal amountTax;
	private BigDecimal amountWithTax;
	
	private List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregateDto>();


	public String getBillingAccountCode() {
		return billingAccountCode;
	}

	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}



	public List<SubCategoryInvoiceAgregateDto> getSubCategoryInvoiceAgregates() {
		return subCategoryInvoiceAgregates;
	}

	public void setSubCategoryInvoiceAgregates(
			List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates) {
		this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
	}



	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}




}
