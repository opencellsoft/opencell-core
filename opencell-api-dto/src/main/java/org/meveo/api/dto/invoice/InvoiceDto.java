package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.payment.RecordedInvoiceDto;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The Class InvoiceDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1072382628068718580L;

    /** The invoice id. */
    private Long invoiceId;

    /** The invoice type. */
    @XmlElement(required = true)
    private String invoiceType;

    /** The billing account code. */
    @XmlElement(required = true)
    private String billingAccountCode;

    /** The due date. */
    @XmlElement(required = true)
    private Date dueDate;

    /** The invoice date. */
    @XmlElement(required = true)
    private Date invoiceDate;

    /** The category invoice agregates. */
    @XmlElementWrapper
    @XmlElement(name = "categoryInvoiceAgregate", required = true)
    private List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregateDto>();

    /** The list invoice id to link. */
    @XmlElementWrapper
    @XmlElement(name = "invoiceIdToLink")
    private List<Long> listInvoiceIdToLink = new ArrayList<Long>();

    /** The invoice number. */
    private String invoiceNumber;
    
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
    /**
     * Deprecated in 4.8. Use pdfFilename!=null instead
     */
    @Deprecated
    private boolean pdfPresent;

    /** The xml filename. */
    private String xmlFilename;
    
    /** The xml. */
    private String xml;
    
    /** The pdf filename. */
    private String pdfFilename;
    
    /** The pdf. */
    private byte[] pdf;
    
    /** The auto validation. */
    private boolean autoValidation = true;
    
    /** The return xml. */
    private boolean returnXml = false;
    
    /** The return pdf. */
    private boolean returnPdf = false;
    
    /** The include balance. */
    private boolean includeBalance = false;

    /** The recorded invoice dto. */
    private RecordedInvoiceDto recordedInvoiceDto;

    /** The net to pay. */
    private BigDecimal netToPay;

    /** The invoice mode. */
    @XmlElement(required = true)
    private InvoiceModeEnum invoiceMode;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new invoice dto.
     */
    public InvoiceDto() {
    }

    /**
     * Gets the list invoice id to link.
     *
     * @return the listInvoiceIdToLink
     */
    public List<Long> getListInvoiceIdToLink() {
        return listInvoiceIdToLink;
    }

    /**
     * Sets the list invoice id to link.
     *
     * @param listInvoiceIdToLink the listInvoiceIdToLink to set
     */
    public void setListInvoiceIdToLink(List<Long> listInvoiceIdToLink) {
        this.listInvoiceIdToLink = listInvoiceIdToLink;
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
     * Checks if is pdf present.
     *
     * @return true, if is pdf present
     */
    public boolean isPdfPresent() {
        return pdfPresent;
    }

    /**
     * Sets the pdf present.
     *
     * @param pDFpresent the new pdf present
     */
    public void setPdfPresent(boolean pDFpresent) {
        pdfPresent = pDFpresent;
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
     * @param invoiceId id of invoice.
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the category invoice agregates.
     *
     * @return the categoryInvoiceAgregates
     */
    public List<CategoryInvoiceAgregateDto> getCategoryInvoiceAgregates() {
        return categoryInvoiceAgregates;
    }

    /**
     * Sets the category invoice agregates.
     *
     * @param categoryInvoiceAgregates the categoryInvoiceAgregates to set
     */
    public void setCategoryInvoiceAgregates(List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates) {
        this.categoryInvoiceAgregates = categoryInvoiceAgregates;
    }

    /**
     * Checks if is auto validation.
     *
     * @return the autoValidation
     */
    public boolean isAutoValidation() {
        return autoValidation;
    }

    /**
     * Sets the auto validation.
     *
     * @param autoValidation the autoValidation to set
     */
    public void setAutoValidation(boolean autoValidation) {
        this.autoValidation = autoValidation;
    }

    /**
     * Checks if is return xml.
     *
     * @return the returnXml
     */
    public boolean isReturnXml() {
        return returnXml;
    }

    /**
     * Sets the return xml.
     *
     * @param returnXml the returnXml to set
     */
    public void setReturnXml(boolean returnXml) {
        this.returnXml = returnXml;
    }

    /**
     * Checks if is return pdf.
     *
     * @return the returnPdf
     */
    public boolean isReturnPdf() {
        return returnPdf;
    }

    /**
     * Sets the return pdf.
     *
     * @param returnPdf the returnPdf to set
     */
    public void setReturnPdf(boolean returnPdf) {
        this.returnPdf = returnPdf;
    }

    /**
     * Gets the invoice mode.
     *
     * @return the invoiceMode
     */
    public InvoiceModeEnum getInvoiceMode() {
        return invoiceMode;
    }

    /**
     * Sets the invoice mode.
     *
     * @param invoiceMode the invoiceMode to set
     */
    public void setInvoiceMode(InvoiceModeEnum invoiceMode) {
        this.invoiceMode = invoiceMode;
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
     * Checks if is include balance.
     *
     * @return the includeBalance
     */
    public boolean isIncludeBalance() {
        return includeBalance;
    }

    /**
     * Sets the include balance.
     *
     * @param includeBalance the includeBalance to set
     */
    public void setIncludeBalance(boolean includeBalance) {
        this.includeBalance = includeBalance;
    }

    /**
     * Gets the recorded invoice dto.
     *
     * @return the recordedInvoiceDto
     */
    public RecordedInvoiceDto getRecordedInvoiceDto() {
        return recordedInvoiceDto;
    }

    /**
     * Sets the recorded invoice dto.
     *
     * @param recordedInvoiceDto the recordedInvoiceDto to set
     */
    public void setRecordedInvoiceDto(RecordedInvoiceDto recordedInvoiceDto) {
        this.recordedInvoiceDto = recordedInvoiceDto;
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

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
    }
}