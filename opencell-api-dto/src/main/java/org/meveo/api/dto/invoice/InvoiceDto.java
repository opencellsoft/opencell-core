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

@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends BaseDto {

    private static final long serialVersionUID = 1072382628068718580L;

    private Long invoiceId;

    @XmlElement(required = true)
    private String invoiceType;

    @XmlElement(required = true)
    private String billingAccountCode;

    @XmlElement(required = true)
    private Date dueDate;

    @XmlElement(required = true)
    private Date invoiceDate;

    @XmlElementWrapper
    @XmlElement(name = "categoryInvoiceAgregate", required = true)
    private List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregateDto>();

    @XmlElementWrapper
    @XmlElement(name = "invoiceIdToLink")
    private List<Long> listInvoiceIdToLink = new ArrayList<Long>();

    private String invoiceNumber;
    private BigDecimal discount;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountTax;
    private BigDecimal amountWithTax;
    private PaymentMethodEnum paymentMethod;
    /**
     * Deprecated in 4.8. Use pdfFilename!=null instead
     */
    @Deprecated
    private boolean pdfPresent;

    private String xmlFilename;
    private String pdfFilename;
    private byte[] pdf;
    private boolean autoValidation = true;
    private boolean returnXml = false;
    private boolean returnPdf = false;
    private boolean includeBalance = false;

    private RecordedInvoiceDto recordedInvoiceDto;

    private BigDecimal netToPay;

    @XmlElement(required = true)
    private InvoiceModeEnum invoiceMode;

    private CustomFieldsDto customFields;

    public InvoiceDto() {
    }

    /**
     * @return the listInvoiceIdToLink
     */
    public List<Long> getListInvoiceIdToLink() {
        return listInvoiceIdToLink;
    }

    /**
     * @param listInvoiceIdToLink the listInvoiceIdToLink to set
     */
    public void setListInvoiceIdToLink(List<Long> listInvoiceIdToLink) {
        this.listInvoiceIdToLink = listInvoiceIdToLink;
    }

    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
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

    public boolean isPdfPresent() {
        return pdfPresent;
    }

    public void setPdfPresent(boolean pDFpresent) {
        pdfPresent = pDFpresent;
    }

    public String getXmlFilename() {
        return xmlFilename;
    }

    public void setXmlFilename(String xmlFilename) {
        this.xmlFilename = xmlFilename;
    }

    public String getPdfFilename() {
        return pdfFilename;
    }

    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return the invoiceId
     */
    public Long getInvoiceId() {
        return invoiceId;
    }

    /**
     * @param invoiceId
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * @return the categoryInvoiceAgregates
     */
    public List<CategoryInvoiceAgregateDto> getCategoryInvoiceAgregates() {
        return categoryInvoiceAgregates;
    }

    /**
     * @param categoryInvoiceAgregates the categoryInvoiceAgregates to set
     */
    public void setCategoryInvoiceAgregates(List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates) {
        this.categoryInvoiceAgregates = categoryInvoiceAgregates;
    }

    /**
     * @return the autoValidation
     */
    public boolean isAutoValidation() {
        return autoValidation;
    }

    /**
     * @param autoValidation the autoValidation to set
     */
    public void setAutoValidation(boolean autoValidation) {
        this.autoValidation = autoValidation;
    }

    /**
     * @return the returnXml
     */
    public boolean isReturnXml() {
        return returnXml;
    }

    /**
     * @param returnXml the returnXml to set
     */
    public void setReturnXml(boolean returnXml) {
        this.returnXml = returnXml;
    }

    /**
     * @return the returnPdf
     */
    public boolean isReturnPdf() {
        return returnPdf;
    }

    /**
     * @param returnPdf the returnPdf to set
     */
    public void setReturnPdf(boolean returnPdf) {
        this.returnPdf = returnPdf;
    }

    /**
     * @return the invoiceMode
     */
    public InvoiceModeEnum getInvoiceMode() {
        return invoiceMode;
    }

    /**
     * @param invoiceMode the invoiceMode to set
     */
    public void setInvoiceMode(InvoiceModeEnum invoiceMode) {
        this.invoiceMode = invoiceMode;
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

    /**
     * @return the includeBalance
     */
    public boolean isIncludeBalance() {
        return includeBalance;
    }

    /**
     * @param includeBalance the includeBalance to set
     */
    public void setIncludeBalance(boolean includeBalance) {
        this.includeBalance = includeBalance;
    }

    /**
     * @return the recordedInvoiceDto
     */
    public RecordedInvoiceDto getRecordedInvoiceDto() {
        return recordedInvoiceDto;
    }

    /**
     * @param recordedInvoiceDto the recordedInvoiceDto to set
     */
    public void setRecordedInvoiceDto(RecordedInvoiceDto recordedInvoiceDto) {
        this.recordedInvoiceDto = recordedInvoiceDto;
    }

    /**
     * @return the netToPay
     */
    public BigDecimal getNetToPay() {
        return netToPay;
    }

    /**
     * @param netToPay the netToPay to set
     */
    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

}
