package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The Class Invoice4_2Dto.
 *
 * @author R.AITYAAZZA
 */
@Deprecated
@XmlRootElement(name = "Invoice4_2")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice4_2Dto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1072382628068718580L;

    /** The billing account code. */
    @XmlElement(required = true)
    private String billingAccountCode;

    /** The due date. */
    @XmlElement(required = true)
    private Date dueDate;

    /** The invoice number. */
    private String invoiceNumber;
    
    /** The invoice date. */
    private Date invoiceDate;
    
    /** The discount. */
    private BigDecimal discount;
    
    /** The amount without tax. */
    private BigDecimal amountWithoutTax;
    
    /** The amount tax. */
    private BigDecimal amountTax;
    
    /** The amount with tax. */
    private BigDecimal amountWithTax;
    
    /** The payment method. */
    private PaymentMethodEnum paymentMethod;
    
    /** The PD fpresent. */
    private boolean PDFpresent;
    
    /** The invoice type. */
    private String invoiceType;
    
    /** The pdf. */
    private byte[] pdf;
    
    /** The sub category invoice agregates. */
    private List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates = new ArrayList<SubCategoryInvoiceAgregateDto>();
    
    /** The account operations. */
    private List<AccountOperationDto> accountOperations = new ArrayList<AccountOperationDto>();

    /**
     * Instantiates a new invoice 4 2 dto.
     */
    public Invoice4_2Dto() {
    }

    /**
     * Gets the invoice number.
     *
     * @return the invoice number
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the new invoice number
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the billing account code.
     *
     * @return the billing account code
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    /**
     * Sets the billing account code.
     *
     * @param billingAccountCode the new billing account code
     */
    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    /**
     * Gets the sub category invoice agregates.
     *
     * @return the sub category invoice agregates
     */
    public List<SubCategoryInvoiceAgregateDto> getSubCategoryInvoiceAgregates() {
        return subCategoryInvoiceAgregates;
    }

    /**
     * Sets the sub category invoice agregates.
     *
     * @param subCategoryInvoiceAgregates the new sub category invoice agregates
     */
    public void setSubCategoryInvoiceAgregates(List<SubCategoryInvoiceAgregateDto> subCategoryInvoiceAgregates) {
        this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the new invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the discount.
     *
     * @return the discount
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the discount.
     *
     * @param discount the new discount
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amount tax.
     *
     * @return the amount tax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amount tax.
     *
     * @param amountTax the new amount tax
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Checks if is PD fpresent.
     *
     * @return true, if is PD fpresent
     */
    public boolean isPDFpresent() {
        return PDFpresent;
    }

    /**
     * Sets the pdf present.
     *
     * @param pDFpresent the new pdf present
     */
    public void setPdfPresent(boolean pDFpresent) {
        PDFpresent = pDFpresent;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the invoice type.
     *
     * @return the invoiceType
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * Sets the invoice type.
     *
     * @param invoiceType the invoiceType to set
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * Gets the account operations.
     *
     * @return the account operations
     */
    public List<AccountOperationDto> getAccountOperations() {
        return accountOperations;
    }

    /**
     * Sets the account operations.
     *
     * @param accountOperations the new account operations
     */
    public void setAccountOperations(List<AccountOperationDto> accountOperations) {
        this.accountOperations = accountOperations;
    }

    /**
     * Gets the pdf.
     *
     * @return the pdf
     */
    public byte[] getPdf() {
        return pdf;
    }

    /**
     * Sets the pdf.
     *
     * @param pdf the new pdf
     */
    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    @Override
    public String toString() {
        return "InvoiceDto [billingAccountCode=" + billingAccountCode + ", dueDate=" + dueDate + ", invoiceNumber=" + invoiceNumber + ", invoiceDate=" + invoiceDate + ", discount="
                + discount + ", amountWithoutTax=" + amountWithoutTax + ", amountTax=" + amountTax + ", amountWithTax=" + amountWithTax + ", paymentMethod=" + paymentMethod
                + ", PDFpresent=" + PDFpresent + ", invceType=" + invoiceType + ", subCategoryInvoiceAgregates=" + subCategoryInvoiceAgregates + "accountOperations "
                + accountOperations + "]";
    }
}