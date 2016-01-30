package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "RecordedInvoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordedInvoiceDto extends AccountOperationDto implements Serializable {

	private static final long serialVersionUID = -6965598553420278018L;

	private Date productionDate;
	private Date invoiceDate;
	private BigDecimal amountWithoutTax;
	private BigDecimal taxAmount;
	private BigDecimal netToPay;
	private String paymentMethod;
	private String paymentInfo;// IBAN for direct debit
	private String paymentInfo1;// bank code
	private String paymentInfo2;// code guichet
	private String paymentInfo3;// Num compte
	private String paymentInfo4;// RIB
	private String paymentInfo5;// bankName
	private String paymentInfo6;// bic
	// private DDRequestLOT ddRequestLOT;
	// private DDRequestItem ddRequestItem;
	private String billingAccountName;

	public RecordedInvoiceDto() {
		super.setType("I");
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public BigDecimal getNetToPay() {
		return netToPay;
	}

	public void setNetToPay(BigDecimal netToPay) {
		this.netToPay = netToPay;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
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

	public String getBillingAccountName() {
		return billingAccountName;
	}

	public void setBillingAccountName(String billingAccountName) {
		this.billingAccountName = billingAccountName;
	}

}
