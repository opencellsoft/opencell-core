package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@DiscriminatorValue(value = "N")
public class RecordedCreditNote extends AccountOperation {

	private static final long serialVersionUID = 323458732725714944L;

	@Column(name = "PRODUCTION_DATE")
	private Date productionDate;

	@Column(name = "CREDIT_NOTE_DATE")
	private Date creditNoteDate;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithoutTax;

	@Column(name = "TAX_AMOUNT", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal taxAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_METHOD")
	private PaymentMethodEnum paymentMethod;

	@Column(name = "PAYMENT_INFO")
	private String paymentInfo;// IBAN for direct debit

	@Column(name = "PAYMENT_INFO1")
	private String paymentInfo1;// bank code

	@Column(name = "PAYMENT_INFO2")
	private String paymentInfo2;// code guichet

	@Column(name = "PAYMENT_INFO3")
	private String paymentInfo3;// Num compte

	@Column(name = "PAYMENT_INFO4")
	private String paymentInfo4;// RIB

	@Column(name = "PAYMENT_INFO5")
	private String paymentInfo5;// bankName

	@Column(name = "PAYMENT_INFO6")
	private String paymentInfo6;// bic

	@ManyToOne(optional = true)
	@JoinColumn(name = "DDRequestLOT_ID")
	private DDRequestLOT ddRequestLOT;

	@ManyToOne(optional = true)
	@JoinColumn(name = "DDREQUEST_ITEM_ID")
	private DDRequestItem ddRequestItem;

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public Date getCreditNoteDate() {
		return creditNoteDate;
	}

	public void setCreditNoteDate(Date creditNoteDate) {
		this.creditNoteDate = creditNoteDate;
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

	public PaymentMethodEnum getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public String getPaymentInfo1() {
		return paymentInfo1;
	}

	public void setPaymentInfo1(String paymentInfo1) {
		this.paymentInfo1 = paymentInfo1;
	}

	public String getPaymentInfo2() {
		return paymentInfo2;
	}

	public void setPaymentInfo2(String paymentInfo2) {
		this.paymentInfo2 = paymentInfo2;
	}

	public String getPaymentInfo3() {
		return paymentInfo3;
	}

	public void setPaymentInfo3(String paymentInfo3) {
		this.paymentInfo3 = paymentInfo3;
	}

	public String getPaymentInfo4() {
		return paymentInfo4;
	}

	public void setPaymentInfo4(String paymentInfo4) {
		this.paymentInfo4 = paymentInfo4;
	}

	public String getPaymentInfo5() {
		return paymentInfo5;
	}

	public void setPaymentInfo5(String paymentInfo5) {
		this.paymentInfo5 = paymentInfo5;
	}

	public String getPaymentInfo6() {
		return paymentInfo6;
	}

	public void setPaymentInfo6(String paymentInfo6) {
		this.paymentInfo6 = paymentInfo6;
	}

	public DDRequestLOT getDdRequestLOT() {
		return ddRequestLOT;
	}

	public void setDdRequestLOT(DDRequestLOT ddRequestLOT) {
		this.ddRequestLOT = ddRequestLOT;
	}

	public DDRequestItem getDdRequestItem() {
		return ddRequestItem;
	}

	public void setDdRequestItem(DDRequestItem ddRequestItem) {
		this.ddRequestItem = ddRequestItem;
	}

}
