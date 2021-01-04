/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
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

    /** The payment day in month. */
    private Integer paymentDayInMonth;

    /** The service template code. */
    private String serviceTemplateCode;

    /**
     * The advance payment invoice type code.
     */
    private String advancePaymentInvoiceTypeCode;

    /**
     * The generate advance payment invoice.
     */
    private Boolean generateAdvancePaymentInvoice;

    /**
     * The do payment.
     */
    private Boolean doPayment;

    /**
     * The advance payment invoice sub category code.
     */
    private String advancePaymentInvoiceSubCategoryCode;

    /**
     * The apply agreement.
     */
    private Boolean applyAgreement = false;

    /**
     * The amount el.
     */
    private String amountEl;

    /**
     * The filter el.
     */
    private String filterEl;

    /**
     * The custom fields.
     */
    private CustomFieldsDto customFields;

    /**
     * The tax class code.
     */
    private String taxClassCode;

    /**
     * An expression to get the payment day.
     */
    private String paymentDayInMonthEl;

    /**
     * Script instance code.
     */
    private String scriptInstanceCode;

    /**
     * Use banking calendar.
     */
    private Boolean useBankingCalendar;

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
        this.advancePaymentInvoiceSubCategoryCode =
                paymentScheduleTemplate.getAdvancePaymentInvoiceSubCategory() == null ? null : paymentScheduleTemplate.getAdvancePaymentInvoiceSubCategory().getCode();
        this.advancePaymentInvoiceTypeCode =
                paymentScheduleTemplate.getAdvancePaymentInvoiceType() == null ? null : paymentScheduleTemplate.getAdvancePaymentInvoiceType().getCode();
        this.calendarCode = paymentScheduleTemplate.getCalendar() == null ? null : paymentScheduleTemplate.getCalendar().getCode();
        this.code = paymentScheduleTemplate.getCode();
        this.description = paymentScheduleTemplate.getDescription();
        this.amount = paymentScheduleTemplate.getAmount();
        this.paymentDayInMonth = paymentScheduleTemplate.getPaymentDayInMonth();
        this.id = paymentScheduleTemplate.getId();
        this.paymentLabel = paymentScheduleTemplate.getPaymentLabel();
        this.serviceTemplateCode = paymentScheduleTemplate.getServiceTemplate().getCode();
        this.doPayment = paymentScheduleTemplate.isDoPayment();
        this.generateAdvancePaymentInvoice = paymentScheduleTemplate.isGenerateAdvancePaymentInvoice();
        this.applyAgreement = paymentScheduleTemplate.isApplyAgreement();
        this.amountEl = paymentScheduleTemplate.getAmountEl();
        this.filterEl = paymentScheduleTemplate.getFilterEl();
        this.customFields = customFields;
        this.taxClassCode = paymentScheduleTemplate.getTaxClass() == null ? null : paymentScheduleTemplate.getTaxClass().getCode();
        this.paymentDayInMonthEl = paymentScheduleTemplate.getPaymentDayInMonthEl();
        this.scriptInstanceCode = (paymentScheduleTemplate.getScriptInstance() != null) ? paymentScheduleTemplate.getScriptInstance().getCode() : null;
        this.useBankingCalendar = paymentScheduleTemplate.getUseBankingCalendar();
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
     * Gets the payment day in month.
     *
     * @return the payment day in month
     */
    public Integer getPaymentDayInMonth() {
        return paymentDayInMonth;
    }

    /**
     * Sets the payment day in month.
     *
     * @param paymentDayInMonth the new payment day in month
     */
    public void setPaymentDayInMonth(Integer paymentDayInMonth) {
        this.paymentDayInMonth = paymentDayInMonth;
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

    /**
     * Checks if is apply agreement.
     *
     * @return the applyAgreement
     */
    public Boolean isApplyAgreement() {
        return applyAgreement;
    }

    /**
     * Sets the apply agreement.
     *
     * @param applyAgreement the applyAgreement to set
     */
    public void setApplyAgreement(Boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    /**
     * Gets the amount el.
     *
     * @return the amountEl
     */
    public String getAmountEl() {
        return amountEl;
    }

    /**
     * Sets the amount el.
     *
     * @param amountEl the amountEl to set
     */
    public void setAmountEl(String amountEl) {
        this.amountEl = amountEl;
    }

    /**
     * Gets the filter el.
     *
     * @return the filterEl
     */
    public String getFilterEl() {
        return filterEl;
    }

    /**
     * Sets the filter el.
     *
     * @param filterEl the filterEl to set
     */
    public void setFilterEl(String filterEl) {
        this.filterEl = filterEl;
    }

    /**
     * Gets the tax class code.
     *
     * @return the tax class code
     */
    public String getTaxClassCode() {
        return taxClassCode;
    }

    /**
     * Sets the tax class code.
     *
     * @param taxClassCode the new tax class code
     */
    public void setTaxClassCode(String taxClassCode) {
        this.taxClassCode = taxClassCode;
    }

    /**
     * Gets the payment day expression.
     *
     * @return the payment day expression.
     */
    public String getPaymentDayInMonthEl() {
        return paymentDayInMonthEl;
    }

    /**
     * Sets payment day expression.
     *
     * @param paymentDayInMonthEl
     */
    public void setPaymentDayInMonthEl(String paymentDayInMonthEl) {
        this.paymentDayInMonthEl = paymentDayInMonthEl;
    }

    /**
     * Gets the script instance code.
     *
     * @return the script instance code
     */
    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    /**
     * Sets the script Instance code.
     *
     * @param scriptInstanceCode
     */
    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    /**
     * Use the banking calendar.
     *
     * @return
     */
    public Boolean getUseBankingCalendar() {
        return useBankingCalendar;
    }

    /**
     * Sets the use of the banking calendar.
     *
     * @param useBankingCalendar
     */
    public void setUseBankingCalendar(Boolean useBankingCalendar) {
        this.useBankingCalendar = useBankingCalendar;
    }
}