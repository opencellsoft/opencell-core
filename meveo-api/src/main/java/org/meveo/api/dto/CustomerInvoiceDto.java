package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "customerInvoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoiceDto extends BaseDto {

	private static final long serialVersionUID = 1072382628068718580L;


	private String billingAccount; 
	  
	private Date invoiceDate;

	private Date dueDate;

	private BigDecimal amount;

	private BigDecimal discount;

	private BigDecimal amountWithoutTax;

	private BigDecimal amountTax;
	
	private BigDecimal amountWithTax;

	private String invoiceNumber; 
 
	private Date productDate;
  
	private BigDecimal netToPay;
	 
	private String paymentMethod; 
	
	private String iban;
 
	private String alias;
 
	private byte[] pdf;
	 
	private String invoiceType;
	
	private List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregateDto>();

 

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

	public String getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(String billingAccount) {
		this.billingAccount = billingAccount;
	}
 

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getProductDate() {
		return productDate;
	}

	public void setProductDate(Date productDate) {
		this.productDate = productDate;
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

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public byte[] getPdf() {
		return pdf;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public List<SubCategoryInvoiceAgregateDto> getSubCategoryInvoiceAgregates() {
		return subCategoryInvoiceAgregates;
	}

	public void setSubCategoryInvoiceAgregates(
			List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates) {
		this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
	}

	public void addSubCategoryInvoiceAgregates(SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregate) {
		if(subCategoryInvoiceAgregates==null){
			subCategoryInvoiceAgregates=new ArrayList<SubCategoryInvoiceAgregateDto>();
		}
		this.subCategoryInvoiceAgregates.add(subCategoryInvoiceAgregate);
	}



	
}
