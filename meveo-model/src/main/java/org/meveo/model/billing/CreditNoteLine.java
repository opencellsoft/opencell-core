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

	public BigDecimal computeWithTax() {
		if (amountWithoutTax != null && taxAmount == null) {
			amountWithTax = amountWithoutTax;
		}
		if (amountWithoutTax != null && taxAmount != null) {
			amountWithTax = amountWithoutTax.add(taxAmount);
		}

		return amountWithTax;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((amountWithTax == null) ? 0 : amountWithTax.hashCode());
		result = prime * result + ((amountWithoutTax == null) ? 0 : amountWithoutTax.hashCode());
		result = prime * result + ((creditNote == null) ? 0 : creditNote.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((invoiceSubCategory == null) ? 0 : invoiceSubCategory.hashCode());
		result = prime * result + ((taxAmount == null) ? 0 : taxAmount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CreditNoteLine other = (CreditNoteLine) obj;

		if (amountWithoutTax == null) {
			if (other.amountWithoutTax != null)
				return false;
		} else if (!amountWithoutTax.equals(other.amountWithoutTax))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (invoiceSubCategory == null) {
			if (other.invoiceSubCategory != null)
				return false;
		} else if (!invoiceSubCategory.equals(other.invoiceSubCategory))
			return false;

		return true;
	}

}
