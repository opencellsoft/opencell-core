package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;

/**
 * The Class GenerateInvoiceResultDto.
 */
@XmlRootElement(name = "GenerateInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResultDto {

    /** The invoice id. */
    private Long invoiceId;

    /** The invoice number. */
    private String invoiceNumber;

    /** The temporary invoice number. */
    private String temporaryInvoiceNumber;

    /** The invoice type code. */
    private String invoiceTypeCode;

    /** The amount. */
    private BigDecimal amount;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The amount tax. */
    private BigDecimal amountTax;

    /** The pdf. */
    private byte[] pdf;

    /** Invoice XML filename. */
    private String xmlFilename;

    /** Invoice PDF filename. */
    private String pdfFilename;

    /** The account operation id. */
    private Long accountOperationId;

    /** The discount. */
    private BigDecimal discount;

    /** The discount aggregates. */
    @XmlElementWrapper(name = "discountAggregates")
    @XmlElement(name = "discountAggregate")
    private List<SubCategoryInvoiceAgregateDto> discountAggregates = new ArrayList<>();

    /**
     * Gets the invoice number.
     *
     * @return the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the invoiceNumber to set
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the temporary invoice number.
     *
     * @return the temporary invoice number
     */
    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

    /**
     * Sets the temporary invoice number.
     *
     * @param temporaryInvoiceNumber the new temporary invoice number
     */
    public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

    /**
     * Gets the invoice type code.
     *
     * @return the invoice type code
     */
    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    /**
     * Sets the invoice type code.
     *
     * @param invoiceTypeCode the new invoice type code
     */
    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
     * @param pdf the pdf to set
     */
    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    /**
     * Gets the account operation id.
     *
     * @return the account operation id
     */
    public Long getAccountOperationId() {
        return accountOperationId;
    }

    /**
     * Sets the account operation id.
     *
     * @param accountOperationId the new account operation id
     */
    public void setAccountOperationId(Long accountOperationId) {
        this.accountOperationId = accountOperationId;
    }

    /**
     * Gets the invoice id.
     *
     * @return the invoice id
     */
    public Long getInvoiceId() {
        return invoiceId;
    }

    /**
     * Sets the invoice id.
     *
     * @param invoiceId the new invoice id
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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
     * Gets the discount aggregates.
     *
     * @return the discount aggregates
     */
    public List<SubCategoryInvoiceAgregateDto> getDiscountAggregates() {
        return discountAggregates;
    }

    /**
     * Sets the discount aggregates.
     *
     * @param discountAggregates the new discount aggregates
     */
    public void setDiscountAggregates(List<SubCategoryInvoiceAgregateDto> discountAggregates) {
        this.discountAggregates = discountAggregates;
    }

    /**
     * Gets the xml filename.
     *
     * @return the xml filename
     */
    public String getXmlFilename() {
        return xmlFilename;
    }

    /**
     * Sets the xml filename.
     *
     * @param xmlFilename the new xml filename
     */
    public void setXmlFilename(String xmlFilename) {
        this.xmlFilename = xmlFilename;
    }

    /**
     * Gets the pdf filename.
     *
     * @return the pdf filename
     */
    public String getPdfFilename() {
        return pdfFilename;
    }

    /**
     * Sets the pdf filename.
     *
     * @param pdfFilename the new pdf filename
     */
    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }
    
    @Override
    public String toString() {
        return "GenerateInvoiceResultDto [invoiceNumber=" + (invoiceNumber != null ? invoiceNumber : temporaryInvoiceNumber) + " ,invoiceId:" + invoiceId + "]";
    }    
}