package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Invoice;

@XmlRootElement(name = "GenerateInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResultDto {

    private String invoiceNumber;

    private String temporaryInvoiceNumber;

    private String invoiceTypeCode;

    private BigDecimal amount;

    private BigDecimal amountWithoutTax;

    private BigDecimal amountWithTax;

    private BigDecimal amountTax;

    private byte[] pdf;

    public GenerateInvoiceResultDto() {

    }

    public GenerateInvoiceResultDto(Invoice invoice, boolean includePdf) {
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.temporaryInvoiceNumber = invoice.getTemporaryInvoiceNumber();
        this.invoiceTypeCode = invoice.getInvoiceType().getCode();
        this.amount = invoice.getAmount();
        this.amountWithoutTax = invoice.getAmountWithoutTax();
        this.amountWithTax = invoice.getAmountWithTax();
        this.amountTax = invoice.getAmountTax();
        if (includePdf) {
            this.pdf = invoice.getPdf();
        }
    }

    /**
     * @return the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * @param invoiceNumber the invoiceNumber to set
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

    public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * @return the pdf
     */
    public byte[] getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GenerateInvoiceResultDto [invoiceNumber=" + (invoiceNumber != null ? invoiceNumber : temporaryInvoiceNumber) + "]";
    }
}