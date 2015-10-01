package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.payments.RecordedInvoice;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "BILLING_CREDIT_NOTE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_CREDIT_NOTE_SEQ")
public class CreditNote extends BusinessEntity {

	private static final long serialVersionUID = 4570187363046364816L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_ACCOUNT_ID")
	private BillingAccount billingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECORDED_INVOICE_ID")
	private RecordedInvoice recordedInvoice;

	@OneToMany(mappedBy = "creditNote", fetch = FetchType.LAZY)
	private List<CreditNoteLine> creditNoteLines = new ArrayList<CreditNoteLine>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_ID")
	private Invoice invoice;

	@Column(name = "CREDIT_NOTE_DATE")
	private Date creditNoteDate;

	@Column(name = "DUE_DATE")
	private Date dueDate;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithoutTax;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "BILLING_CREDIT_NOTE_TAX_AMT")
	private Map<String, BigDecimal> taxAmounts = new HashMap<String, BigDecimal>();

	@Column(name = "AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithTax;

	@Column(name = "NET_TO_PAY", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal netToPay;

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public RecordedInvoice getRecordedInvoice() {
		return recordedInvoice;
	}

	public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
		this.recordedInvoice = recordedInvoice;
	}

	public List<CreditNoteLine> getCreditNoteLines() {
		return creditNoteLines;
	}

	public void setCreditNoteLines(List<CreditNoteLine> creditNoteLines) {
		this.creditNoteLines = creditNoteLines;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Date getCreditNoteDate() {
		return creditNoteDate;
	}

	public void setCreditNoteDate(Date creditNoteDate) {
		this.creditNoteDate = creditNoteDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public Map<String, BigDecimal> getTaxAmounts() {
		return taxAmounts;
	}

	public void setTaxAmounts(Map<String, BigDecimal> taxAmounts) {
		this.taxAmounts = taxAmounts;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getNetToPay() {
		return netToPay;
	}

	public void setNetToPay(BigDecimal netToPay) {
		this.netToPay = netToPay;
	}

}
