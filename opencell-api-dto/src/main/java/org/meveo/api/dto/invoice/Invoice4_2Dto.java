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
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author R.AITYAAZZA
 * 
 */
@Deprecated
@XmlRootElement(name = "Invoice4_2")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice4_2Dto extends BaseDto {

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
    private PaymentMethodEnum paymentMethod;
    private boolean PDFpresent;
    private String invoiceType;
    private byte[] pdf;
    private List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregateDto>();
    private List<AccountOperationDto> accountOperations = new ArrayList<AccountOperationDto>();   
    
    public Invoice4_2Dto() {
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

    public void setPdfPresent(boolean pDFpresent) {
        PDFpresent = pDFpresent;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    

    /**
	 * @return the invoiceType
	 */
	public String getInvoiceType() {
		return invoiceType;
	}

	/**
	 * @param invoiceType the invoiceType to set
	 */
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public List<AccountOperationDto> getAccountOperations() {
        return accountOperations;
    }

    public void setAccountOperations(List<AccountOperationDto> accountOperations) {
        this.accountOperations = accountOperations;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

 

	@Override
    public String toString() {
        return "InvoiceDto [billingAccountCode=" + billingAccountCode + ", dueDate=" + dueDate + ", invoiceNumber=" + invoiceNumber + ", invoiceDate=" + invoiceDate
                + ", discount=" + discount + ", amountWithoutTax=" + amountWithoutTax + ", amountTax=" + amountTax + ", amountWithTax=" + amountWithTax + ", paymentMethod="
                + paymentMethod + ", PDFpresent=" + PDFpresent + ", invceType=" + invoiceType + ", subCategoryInvoiceAgregates=" + subCategoryInvoiceAgregates + "accountOperations "
                + accountOperations + "]";
    }
}
