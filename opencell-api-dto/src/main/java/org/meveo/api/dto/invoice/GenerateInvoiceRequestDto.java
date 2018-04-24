package org.meveo.api.dto.invoice;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.FilterDto;

/**
 * The Class GenerateInvoiceRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GenerateInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceRequestDto {

    /** The billing account code. */
    @XmlElement(required = true)
    private String billingAccountCode;

    /** The invoicing date. */
    @XmlElement(required = true)
    private Date invoicingDate;

    /** The first transaction date. */
    @XmlElement()
    private Date firstTransactionDate;

    /** The last transaction date. */
    @XmlElement()
    private Date lastTransactionDate;

    /** The filter. */
    @XmlElement()
    private FilterDto filter;

    /** The order number. */
    @XmlElement()
    private String orderNumber;

    /** The generate XML. */
    private Boolean generateXML;
    
    /** The generate PDF. */
    private Boolean generatePDF;
    
    /** The generate AO. */
    private Boolean generateAO;

    /**
     * Instantiates a new generate invoice request dto.
     */
    public GenerateInvoiceRequestDto() {

    }

    /**
     * Gets the billing account code.
     *
     * @return the billingAccountCode
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    /**
     * Sets the billing account code.
     *
     * @param billingAccountCode the billingAccountCode to set
     */
    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    /**
     * Gets the invoicing date.
     *
     * @return the invoicingDate
     */
    public Date getInvoicingDate() {
        return invoicingDate;
    }

    /**
     * Sets the invoicing date.
     *
     * @param invoicingDate the invoicingDate to set
     */
    public void setInvoicingDate(Date invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

    /**
     * Gets the last transaction date.
     *
     * @return the lastTransactionDate
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the last transaction date.
     *
     * @param lastTransactionDate the lastTransactionDate to set
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public FilterDto getFilter() {
        return filter;
    }

    /**
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public void setFilter(FilterDto filter) {
        this.filter = filter;
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the generate XML.
     *
     * @return the generateXML
     */
    public Boolean getGenerateXML() {
        return generateXML;
    }

    /**
     * Sets the generate XML.
     *
     * @param generateXML the generateXML to set
     */
    public void setGenerateXML(Boolean generateXML) {
        this.generateXML = generateXML;
    }

    /**
     * Gets the generate PDF.
     *
     * @return the generatePDF
     */
    public Boolean getGeneratePDF() {
        return generatePDF;
    }

    /**
     * Sets the generate PDF.
     *
     * @param generatePDF the generatePDF to set
     */
    public void setGeneratePDF(Boolean generatePDF) {
        this.generatePDF = generatePDF;
    }

    /**
     * Gets the generate AO.
     *
     * @return the generateAO
     */
    public Boolean getGenerateAO() {
        return generateAO;
    }

    /**
     * Sets the generate AO.
     *
     * @param generateAO the generateAO to set
     */
    public void setGenerateAO(Boolean generateAO) {
        this.generateAO = generateAO;
    }

    /**
     * Gets the first transaction date.
     *
     * @return the first transaction date
     */
    public Date getFirstTransactionDate() {
        return firstTransactionDate;
    }

    /**
     * Sets the first transaction date.
     *
     * @param firstTransactionDate the new first transaction date
     */
    public void setFirstTransactionDate(Date firstTransactionDate) {
        this.firstTransactionDate = firstTransactionDate;
    }

    @Override
    public String toString() {
        return "GenerateInvoiceRequestDto [billingAccountCode=" + billingAccountCode + ", invoicingDate=" + invoicingDate + ", lastTransactionDate=" + lastTransactionDate
                + ", filter=" + filter + ", orderNumber=" + orderNumber + ", generateXML=" + generateXML + ", generatePDF=" + generatePDF + ", generateAO=" + generateAO + "]";
    }
}