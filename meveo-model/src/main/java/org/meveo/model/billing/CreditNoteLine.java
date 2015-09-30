package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "BILLING_CREDIT_NOTE_LINE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_CREDIT_NOTE_LINE_SEQ")
public class CreditNoteLine extends BaseEntity {

	private static final long serialVersionUID = 1018209249184313628L;

	@Column(name = "DESCRIPTION", length = 255)
	private String description;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "CREDIT_NOTE_ID", nullable = false)
	public CreditNote creditNote;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_SUB_CATEGORY", nullable = false)
	private InvoiceSubCategory invoiceSubCategory;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithoutTax;

	@Column(name = "TAX_AMOUNT", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal taxAmount;

	@Column(name = "AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithTax;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CreditNote getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(CreditNote creditNote) {
		this.creditNote = creditNote;
	}

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

}
