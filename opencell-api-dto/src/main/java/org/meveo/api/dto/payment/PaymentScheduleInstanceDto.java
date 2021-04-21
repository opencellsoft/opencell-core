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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class PaymentScheduleInstanceDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleInstanceDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstanceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The end date. */
    @Schema(description = "The end date")
    private Date endDate;

    /** The start date. */
    @Schema(description = "The start date")
    private Date startDate;

    /** The amount. */
    @Schema(description = "The amount")
    private BigDecimal amount;

    /** The calendar code. */
    @Schema(description = "The calendar code")
    private String calendarCode;

    /** The status. */
    @Schema(description = "The status of the payment schedule instance", example = "possible value are: IN_PROGRESS, OBSOLETE, DONE, CANCELLED, TERMINATED")
    private PaymentScheduleStatusEnum status;

    /** The status date. */
    @Schema(description = "The status date")
    private Date statusDate;

    /** The payment schedule template code. */
    @Schema(description = "The payment schedule template code")
    private String paymentScheduleTemplateCode;

    /** The payment day in month. */
    @Schema(description = "The payment day in month")
    private Integer paymentDayInMonth;

    /** The service instance template code. */
    @Schema(description = "The service instance template code")
    private String serviceInstanceTemplateCode;

    /** The service instance id. */
    @Schema(description = "The service instance id")
    private Long serviceInstanceId;

    /** The subscription code. */
    @Schema(description = "The subscription code")
    private String subscriptionCode;

    /** The payment schedule instance balance dto. */
    @Schema(description = "The payment schedule instance balance")
    private PaymentScheduleInstanceBalanceDto paymentScheduleInstanceBalanceDto;

    /** The items. */
    @XmlElementWrapper
    @XmlElement(name = "item")
    @Schema(description = "List of the payment schedule instance item", example = "item: []")
    private List<PaymentScheduleInstanceItemDto> items = new ArrayList<PaymentScheduleInstanceItemDto>();

    /** The custom fields. */
    @Schema(description = "The custom fields")
    private CustomFieldsDto customFields;

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
     * Instantiates a new payment schedule instance dto.
     */
    public PaymentScheduleInstanceDto() {

    }

    /**
     * Instantiates a new payment schedule instance dto.
     *
     * @param paymentScheduleInstance the payment schedule instance
     */
    public PaymentScheduleInstanceDto(PaymentScheduleInstance paymentScheduleInstance) {
        this.id = paymentScheduleInstance.getId();
        this.amount = paymentScheduleInstance.getAmount();
        this.calendarCode = paymentScheduleInstance.getCalendar().getCode();
        this.code = paymentScheduleInstance.getCode();
        this.description = paymentScheduleInstance.getDescription();
        this.paymentDayInMonth = paymentScheduleInstance.getPaymentDayInMonth();
        this.endDate = paymentScheduleInstance.getEndDate();
        this.startDate = paymentScheduleInstance.getStartDate();
        this.paymentScheduleTemplateCode = paymentScheduleInstance.getPaymentScheduleTemplate().getCode();
        this.statusDate = paymentScheduleInstance.getStatusDate();
        this.status = paymentScheduleInstance.getStatus();
        this.serviceInstanceTemplateCode = paymentScheduleInstance.getServiceInstance().getCode();
        this.serviceInstanceId = paymentScheduleInstance.getServiceInstance().getId();
        this.subscriptionCode = paymentScheduleInstance.getServiceInstance().getSubscription().getCode();
        for (PaymentScheduleInstanceItem item : paymentScheduleInstance.getPaymentScheduleInstanceItems()) {
            this.items.add(new PaymentScheduleInstanceItemDto(item));
        }

    }

    /**
     * Gets the end date.
     *
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
     * Gets the status.
     *
     * @return the status
     */
    public PaymentScheduleStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(PaymentScheduleStatusEnum status) {
        this.status = status;
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
     * Gets the service instance template code.
     *
     * @return the serviceInstanceTemplateCode
     */
    public String getServiceInstanceTemplateCode() {
        return serviceInstanceTemplateCode;
    }

    /**
     * Sets the service instance template code.
     *
     * @param serviceInstanceTemplateCode the serviceInstanceCode to set
     */
    public void setServiceInstanceTemplateCode(String serviceInstanceTemplateCode) {
        this.serviceInstanceTemplateCode = serviceInstanceTemplateCode;
    }

    /**
     * Gets the service instance id.
     * 
     * @return the serviceInstanceId
     */
    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    /**
     * Sets the service instance id.
     * 
     * @param serviceInstanceId the serviceInstanceId to set
     */
    public void setServiceInstanceId(Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    /**
     * Gets the subscription code.
     *
     * @return the subscriptionCode
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * Sets the subscription code.
     *
     * @param subscriptionCode the subscriptionCode to set
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
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
     * Gets the status date.
     *
     * @return the statusDate
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the payment schedule template code.
     *
     * @return the paymentScheduleTemplateCode
     */
    public String getPaymentScheduleTemplateCode() {
        return paymentScheduleTemplateCode;
    }

    /**
     * Sets the payment schedule template code.
     *
     * @param paymentScheduleTemplateCode the paymentScheduleTemplateCode to set
     */
    public void setPaymentScheduleTemplateCode(String paymentScheduleTemplateCode) {
        this.paymentScheduleTemplateCode = paymentScheduleTemplateCode;
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public List<PaymentScheduleInstanceItemDto> getItems() {
        return items;
    }

    /**
     * Sets the items.
     *
     * @param items the items to set
     */
    public void setItems(List<PaymentScheduleInstanceItemDto> items) {
        this.items = items;
    }

    /**
     * Gets the payment schedule instance balance dto.
     *
     * @return the paymentScheduleInstanceBalanceDto
     */
    public PaymentScheduleInstanceBalanceDto getPaymentScheduleInstanceBalanceDto() {
        return paymentScheduleInstanceBalanceDto;
    }

    /**
     * Sets the payment schedule instance balance dto.
     *
     * @param paymentScheduleInstanceBalanceDto the paymentScheduleInstanceBalanceDto to set
     */
    public void setPaymentScheduleInstanceBalanceDto(PaymentScheduleInstanceBalanceDto paymentScheduleInstanceBalanceDto) {
        this.paymentScheduleInstanceBalanceDto = paymentScheduleInstanceBalanceDto;
    }

}
