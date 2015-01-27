package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;

/**
 * @author R.AITYAAZZA
 * 
 */
@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends BaseDto {

	private static final long serialVersionUID = 1072382628068718580L;

	private String invoiceNumber;
	private String billingAccountCode;
	private Date invoiceDate;
	private Date dueDate;
	private BigDecimal discount;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountTax;
	private BigDecimal amountWithTax;
	private String paymentMathod;
	private boolean PDFpresent;

	private List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregateDto>();

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getBillingAccountCode() {
		return billingAccountCode;
	}

	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}

	public List<SubCategoryInvoiceAgregateDto> getSubCategoryInvoiceAgregates() {
		return subCategoryInvoiceAgregates;
	}

	public void setSubCategoryInvoiceAgregates(List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates) {
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

	public boolean isPDFpresent() {
		return PDFpresent;
	}

	public void setPDFpresent(boolean pDFpresent) {
		PDFpresent = pDFpresent;
	}

	public String getPaymentMathod() {
		return paymentMathod;
	}

	public void setPaymentMathod(String paymentMathod) {
		this.paymentMathod = paymentMathod;
	}

	@Override
	public String toString() {
		return "InvoiceDto [invoiceNumber =" + invoiceNumber + "," + ", billingAccountCode=" + billingAccountCode
				+ ", invoiceDate=" + invoiceDate + ", dueDate=" + dueDate + ", discount=" + discount
				+ ", amountWithoutTax=" + amountWithoutTax + ", amountTax=" + amountTax + ", amountWithTax="
				+ amountWithTax + ", subCategoryInvoiceAgregates=" + subCategoryInvoiceAgregates + "]";
	}
}
