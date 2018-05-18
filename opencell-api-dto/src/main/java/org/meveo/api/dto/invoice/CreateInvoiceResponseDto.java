package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CreateInvoiceResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CreateInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateInvoiceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5811304676103235597L;

    /** The invoice id. */
    private Long invoiceId;
    
    /** The invoice number. */
    private String invoiceNumber;
    
    /** The invoice date. */
    private Date invoiceDate;
    
    /** The due date. */
    private Date dueDate;
    
    /** The amount without tax. */
    private BigDecimal amountWithoutTax;
    
    /** The amount tax. */
    private BigDecimal amountTax;
    
    /** The amount with tax. */
    private BigDecimal amountWithTax;
    
    /** The net to pay. */
    private BigDecimal netToPay;
    
    /** The xml filename. */
    private String xmlFilename;
    
    /** The xml invoice. */
    private String xmlInvoice;
    
    /** The pdf filename. */
    private String pdfFilename;
    
    /** The pdf invoice. */
    private byte[] pdfInvoice;

    /**
     * Instantiates a new creates the invoice response dto.
     */
    public CreateInvoiceResponseDto() {

    }

    /**
     * Gets the invoice id.
     *
     * @return the invoiceId
     */
    public Long getInvoiceId() {
        return invoiceId;
    }

    /**
     * Sets the invoice id.
     *
     * @param invoiceId the invoiceId to set
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

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
     * Gets the invoice date.
     *
     * @return the invoiceDate
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the invoiceDate to set
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the due date.
     *
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amountWithoutTax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the amountWithoutTax to set
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amount tax.
     *
     * @return the amountTax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amount tax.
     *
     * @param amountTax the amountTax to set
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amountWithTax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the amountWithTax to set
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
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
     * Gets the xml filename.
     *
     * @return the xml filename
     */
    public String getXmlFilename() {
        return xmlFilename;
    }

    /**
     * Gets the xml invoice.
     *
     * @return the xmlInvoice
     */
    public String getXmlInvoice() {
        return xmlInvoice;
    }

    /**
     * Sets the xml invoice.
     *
     * @param xmlInvoice the xmlInvoice to set
     */
    public void setXmlInvoice(String xmlInvoice) {
        this.xmlInvoice = xmlInvoice;
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

    /**
     * Gets the pdf invoice.
     *
     * @return the pdfInvoice
     */
    public byte[] getPdfInvoice() {
        return pdfInvoice;
    }

    /**
     * Sets the pdf invoice.
     *
     * @param pdfInvoice the pdfInvoice to set
     */
    public void setPdfInvoice(byte[] pdfInvoice) {
        this.pdfInvoice = pdfInvoice;
    }

    /**
     * Gets the net to pay.
     *
     * @return the netToPay
     */
    public BigDecimal getNetToPay() {
        return netToPay;
    }

    /**
     * Sets the net to pay.
     *
     * @param netToPay the netToPay to set
     */
    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

}