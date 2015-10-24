package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceTypeEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;

/**
 * @author R.AITYAAZZA
 * 
 */
@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends BaseDto {

	private static final long serialVersionUID = 1072382628068718580L;

	@XmlElement(required = true)
	private String billingAccountCode;

	@XmlElement(required = true)
	private Date dueDate;

	private String invoiceNumber;
	private Date invoiceDate;
	private BigDecimal discount;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountTax;
	private BigDecimal amountWithTax;
	private String paymentMathod;
	private boolean PDFpresent;
	private String type;
	
	
	private List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregateDto>();
	
	public InvoiceDto() {}
	
	public InvoiceDto(Invoice invoice, String billingAccountCode) {
		super();
		this.setBillingAccountCode(billingAccountCode);
		this.setInvoiceDate(invoice.getInvoiceDate());
		this.setDueDate(invoice.getDueDate());

		this.setAmountWithoutTax(invoice
				.getAmountWithoutTax());
		this.setAmountTax(invoice.getAmountTax());
		this.setAmountWithTax(invoice
				.getAmountWithTax());
		this.setInvoiceNumber(invoice
				.getInvoiceNumber());
		this.setPaymentMathod(invoice
				.getPaymentMethod().toString());
		this.setType(invoice.getInvoiceTypeEnum().name());
		this.setPDFpresent(invoice.getPdf() != null);
				
		InvoiceTypeEnum type = invoice.getInvoiceTypeEnum();
		if (type != null) {
			this.setType(invoice.getInvoiceTypeEnum().name());
		} else {
			this.setType(InvoiceTypeEnum.COMMERCIAL.name());
		}
		
		SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = null;

		for (InvoiceAgregate invoiceAgregate : invoice
				.getInvoiceAgregates()) {

			subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();

			if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
				subCategoryInvoiceAgregateDto.setType("R");
			} else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
				subCategoryInvoiceAgregateDto.setType("F");
			} else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
				subCategoryInvoiceAgregateDto.setType("T");
			}

			subCategoryInvoiceAgregateDto
			.setItemNumber(invoiceAgregate.getItemNumber());
			subCategoryInvoiceAgregateDto
			.setAccountingCode(invoiceAgregate
					.getAccountingCode());
			subCategoryInvoiceAgregateDto
			.setDescription(invoiceAgregate
					.getDescription());
			subCategoryInvoiceAgregateDto
			.setQuantity(invoiceAgregate.getQuantity());
			subCategoryInvoiceAgregateDto
			.setDiscount(invoiceAgregate.getDiscount());
			subCategoryInvoiceAgregateDto
			.setAmountWithoutTax(invoiceAgregate
					.getAmountWithoutTax());
			subCategoryInvoiceAgregateDto
			.setAmountTax(invoiceAgregate.getAmountTax());
			subCategoryInvoiceAgregateDto
			.setAmountWithTax(invoiceAgregate
					.getAmountWithTax());
			this.getSubCategoryInvoiceAgregates()
			.add(subCategoryInvoiceAgregateDto);
		}
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "InvoiceDto [billingAccountCode=" + billingAccountCode
				+ ", dueDate=" + dueDate + ", invoiceNumber=" + invoiceNumber
				+ ", invoiceDate=" + invoiceDate + ", discount=" + discount
				+ ", amountWithoutTax=" + amountWithoutTax + ", amountTax="
				+ amountTax + ", amountWithTax=" + amountWithTax
				+ ", paymentMathod=" + paymentMathod + ", PDFpresent="
				+ PDFpresent + ", type=" + type
				+ ", subCategoryInvoiceAgregates="
				+ subCategoryInvoiceAgregates + "]";
	}
}
