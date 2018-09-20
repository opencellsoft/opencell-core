/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto.payment;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.payments.PaymentScheduleTemplate;

/**
 * The Class PaymentScheduleTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleTemplateDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The start date. */
    private Date startDate;

    /** The amount. */
    private BigDecimal amount;

    /** The calendar code. */
    private String calendarCode;

    /** The payment label. */
    private String paymentLabel;

    /** The due date days. */
    private Integer dueDateDays;

    /** The service template code. */
    private String serviceTemplateCode;

    /** The advance payment invoice type code. */
    private String advancePaymentInvoiceTypeCode;

    /** The generate advance payment invoice. */
    private Boolean generateAdvancePaymentInvoice;

    /** The do payment. */
    private Boolean doPayment;

    /** The advance payment invoice sub category code. */
    private String advancePaymentInvoiceSubCategoryCode;

    /** The custom fields. */
    private CustomFieldsDto customFields;
    
    
    /**
     * Instantiates a new payment schedule template dto.
     */
    public PaymentScheduleTemplateDto() {
        
    }

    /**
     * Instantiates a new payment schedule template dto.
     *
     * @param paymentScheduleTemplate the payment schedule template
     * @param customFields the custom fields
     */
    public PaymentScheduleTemplateDto(PaymentScheduleTemplate paymentScheduleTemplate, CustomFieldsDto customFields) {
        this.advancePaymentInvoiceSubCategoryCode = paymentScheduleTemplate.getAdvancePaymentInvoiceSubCategory() == null ? null
                : paymentScheduleTemplate.getAdvancePaymentInvoiceSubCategory().getCode();
        this.advancePaymentInvoiceTypeCode = paymentScheduleTemplate.getAdvancePaymentInvoiceType() == null ? null
                : paymentScheduleTemplate.getAdvancePaymentInvoiceType().getCode();
        this.calendarCode = paymentScheduleTemplate.getCalendar() == null ? null : paymentScheduleTemplate.getCalendar().getCode();
        this.code = paymentScheduleTemplate.getCode();
        this.description = paymentScheduleTemplate.getDescription();
        this.amount = paymentScheduleTemplate.getAmount();
        this.dueDateDays = paymentScheduleTemplate.getDueDateDays();
        this.id = paymentScheduleTemplate.getId();
        this.paymentLabel = paymentScheduleTemplate.getPaymentLabel();
        this.serviceTemplateCode = paymentScheduleTemplate.getServiceTemplate().getCode();
        this.doPayment = paymentScheduleTemplate.isDoPayment();
        this.generateAdvancePaymentInvoice = paymentScheduleTemplate.isGenerateAdvancePaymentInvoice();
        this.customFields = customFields;
    }

    /**
     * Gets the start date.
     *
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

   

    /**
     * Gets the payment label.
     *
     * @return the paymentLabel
     */
    public String getPaymentLabel() {
        return paymentLabel;
    }

    /**
     * Sets the payment label.
     *
     * @param paymentLabel the paymentLabel to set
     */
    public void setPaymentLabel(String paymentLabel) {
        this.paymentLabel = paymentLabel;
    }

    /**
     * Gets the due date days.
     *
     * @return the dueDateDays
     */
    public Integer getDueDateDays() {
        return dueDateDays;
    }

    /**
     * Sets the due date days.
     *
     * @param dueDateDays the dueDateDays to set
     */
    public void setDueDateDays(Integer dueDateDays) {
        this.dueDateDays = dueDateDays;
    }

    /**
     * Gets the service template code.
     *
     * @return the serviceTemplateCode
     */
    public String getServiceTemplateCode() {
        return serviceTemplateCode;
    }

    /**
     * Sets the service template code.
     *
     * @param serviceTemplateCode the serviceTemplateCode to set
     */
    public void setServiceTemplateCode(String serviceTemplateCode) {
        this.serviceTemplateCode = serviceTemplateCode;
    }

    /**
     * Gets the advance payment invoice type code.
     *
     * @return the advancePaymentInvoiceTypeCode
     */
    public String getAdvancePaymentInvoiceTypeCode() {
        return advancePaymentInvoiceTypeCode;
    }

    /**
     * Sets the advance payment invoice type code.
     *
     * @param advancePaymentInvoiceTypeCode the advancePaymentInvoiceTypeCode to set
     */
    public void setAdvancePaymentInvoiceTypeCode(String advancePaymentInvoiceTypeCode) {
        this.advancePaymentInvoiceTypeCode = advancePaymentInvoiceTypeCode;
    }

    /**
     * Gets the generate advance payment invoice.
     *
     * @return the generateAdvancePaymentInvoice
     */
    public Boolean getGenerateAdvancePaymentInvoice() {
        return generateAdvancePaymentInvoice;
    }

    /**
     * Sets the generate advance payment invoice.
     *
     * @param generateAdvancePaymentInvoice the generateAdvancePaymentInvoice to set
     */
    public void setGenerateAdvancePaymentInvoice(Boolean generateAdvancePaymentInvoice) {
        this.generateAdvancePaymentInvoice = generateAdvancePaymentInvoice;
    }

    /**
     * Gets the advance payment invoice sub category code.
     *
     * @return the advancePaymentInvoiceSubCategoryCode
     */
    public String getAdvancePaymentInvoiceSubCategoryCode() {
        return advancePaymentInvoiceSubCategoryCode;
    }

    /**
     * Sets the advance payment invoice sub category code.
     *
     * @param advancePaymentInvoiceSubCategoryCode the advancePaymentInvoiceSubCategoryCode to set
     */
    public void setAdvancePaymentInvoiceSubCategoryCode(String advancePaymentInvoiceSubCategoryCode) {
        this.advancePaymentInvoiceSubCategoryCode = advancePaymentInvoiceSubCategoryCode;
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
     * Gets the calendar code.
     *
     * @return the calendarCode
     */
    public String getCalendarCode() {
        return calendarCode;
    }

    /**
     * Sets the calendar code.
     *
     * @param calendarCode the calendarCode to set
     */
    public void setCalendarCode(String calendarCode) {
        this.calendarCode = calendarCode;
    }

    /**
     * Gets the do payment.
     *
     * @return the doPayment
     */
    public Boolean getDoPayment() {
        return doPayment;
    }

    /**
     * Sets the do payment.
     *
     * @param doPayment the doPayment to set
     */
    public void setDoPayment(Boolean doPayment) {
        this.doPayment = doPayment;
    }

}
