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

package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UsageChargeInstance;

/**
 * The Class ServiceInstanceDto.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.2.2
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInstanceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4084004747483067153L;

    /** The status. */
    private InstanceStatusEnum status;

    /** The status date. */
    private Date statusDate;

    /** The subscription date. */
    private Date subscriptionDate;

    /** The reactivation date. */
    private Date reactivationDate;

    /** The termination date. */
    private Date terminationDate;

    /** The quantity. */
    private BigDecimal quantity;

    /** The termination reason. */
    private String terminationReason;

    /** The end agreement date. */
    private Date endAgreementDate;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The recurring charge instances. */
    @XmlElementWrapper(name = "recurringChargeInstances")
    @XmlElement(name = "recurringChargeInstance")
    private List<ChargeInstanceDto> recurringChargeInstances;

    /** The subscription charge instances. */
    @XmlElementWrapper(name = "subscriptionChargeInstances")
    @XmlElement(name = "subscriptionChargeInstance")
    private List<ChargeInstanceDto> subscriptionChargeInstances;

    /** The termination charge instances. */
    @XmlElementWrapper(name = "terminationChargeInstances")
    @XmlElement(name = "terminationChargeInstance")
    private List<ChargeInstanceDto> terminationChargeInstances;

    /** The usage charge instances. */
    @XmlElementWrapper(name = "usageChargeInstances")
    @XmlElement(name = "usageChargeInstance")
    private List<ChargeInstanceDto> usageChargeInstances;

    /** The order number. */
    private String orderNumber;

    /** The rate until date. */
    private Date rateUntilDate;

    /** The amount PS. */
    private BigDecimal amountPS;

    /** The calendar PS code. */
    private String calendarPSCode;

    /**
     * The payment day in month PS.
     */
    private Integer paymentDayInMonthPS;

    /**
     * Expression to determine minimum amount value.
     */
    private String minimumAmountEl;

    /**
     * Expression to determine minimum amount value - for Spark.
     */
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value.
     */
    private String minimumLabelEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark.
     */
    private String minimumLabelElSpark;

    /**
     * The due date days PS.
     */
    private Integer dueDateDaysPS;

    private Boolean autoEndOfEngagement;

    private Boolean doNotApplyEngagement;

    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String minimumChargeTemplate;

    /**
     * A date till which service is activate. After this date it will either be extended or terminated
     */
    private Date subscribedTillDate;

    /**
     * The renewal service.
     */
    private SubscriptionRenewalDto serviceRenewal;

    /**
     * Instantiates a new service instance dto.
     */
    public ServiceInstanceDto() {

    }

    /**
     * Instantiates a new service instance dto.
     *
     * * @param serviceInstance the ServiceInstance entity
     */
    public ServiceInstanceDto(ServiceInstance serviceInstance) {
        super(serviceInstance);
        id = serviceInstance.getId();
        status = serviceInstance.getStatus();
        statusDate = serviceInstance.getStatusDate();
        subscriptionDate = serviceInstance.getSubscriptionDate();
        terminationDate = serviceInstance.getTerminationDate();
        reactivationDate = serviceInstance.getReactivationDate();
        quantity = serviceInstance.getQuantity();
        orderNumber = serviceInstance.getOrderNumber();
        if (serviceInstance.getSubscriptionTerminationReason() != null) {
            terminationReason = serviceInstance.getSubscriptionTerminationReason().getCode();
        }
        endAgreementDate = serviceInstance.getEndAgreementDate();
        autoEndOfEngagement = serviceInstance.getAutoEndOfEngagement();
        doNotApplyEngagement = serviceInstance.getDoNotApplyEngagement();

        subscribedTillDate = serviceInstance.getSubscribedTillDate();
        serviceRenewal = new SubscriptionRenewalDto(serviceInstance.getServiceRenewal());

        setMinimumAmountEl(serviceInstance.getMinimumAmountEl());
        setMinimumAmountElSpark(serviceInstance.getMinimumAmountElSpark());
        setMinimumLabelEl(serviceInstance.getMinimumLabelEl());
        setMinimumLabelElSpark(serviceInstance.getMinimumLabelElSpark());
    }

    /**
     * Instantiates a new service instance dto.
     *
     * @param serviceInstance the ServiceInstance entity
     * @param recurringChargeInstances the recurring charge instances
     * @param subscriptionChargeInstances the subscription charge instances
     * @param terminationChargeInstances the termination charge instances
     * @param usageChargeInstances the usage charge instances
     * @param customFieldInstances the custom field instances
     */
    public ServiceInstanceDto(ServiceInstance serviceInstance, List<ChargeInstanceDto> recurringChargeInstances, List<ChargeInstanceDto> subscriptionChargeInstances,
            List<ChargeInstanceDto> terminationChargeInstances, List<ChargeInstanceDto> usageChargeInstances, CustomFieldsDto customFieldInstances) {

        this(serviceInstance);
        this.recurringChargeInstances = recurringChargeInstances;
        this.subscriptionChargeInstances = subscriptionChargeInstances;
        this.terminationChargeInstances = terminationChargeInstances;
        this.usageChargeInstances = usageChargeInstances;
        customFields = customFieldInstances;
    }

    /**
     * Instantiates a new service instance dto.
     *
     * @param serviceInstance the ServiceInstance entity
     * @param customFieldInstances the custom field instances
     */
    public ServiceInstanceDto(ServiceInstance serviceInstance, CustomFieldsDto customFieldInstances) {
        this(serviceInstance);

        if (serviceInstance.getRecurringChargeInstances() != null) {
            recurringChargeInstances = new ArrayList<ChargeInstanceDto>();

            for (RecurringChargeInstance ci : serviceInstance.getRecurringChargeInstances()) {
                recurringChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(),
                    ci.getSeller().getCode(), ci.getUserAccount().getCode()));
            }
        }

        if (serviceInstance.getSubscriptionChargeInstances() != null) {
            subscriptionChargeInstances = new ArrayList<ChargeInstanceDto>();

            for (OneShotChargeInstance ci : serviceInstance.getSubscriptionChargeInstances()) {
                subscriptionChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(),
                    ci.getSeller().getCode(), ci.getUserAccount().getCode()));
            }
        }

        if (serviceInstance.getTerminationChargeInstances() != null) {
            terminationChargeInstances = new ArrayList<ChargeInstanceDto>();

            for (OneShotChargeInstance ci : serviceInstance.getTerminationChargeInstances()) {
                terminationChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(),
                    ci.getSeller().getCode(), ci.getUserAccount().getCode()));
            }
        }

        if (serviceInstance.getUsageChargeInstances() != null) {
            usageChargeInstances = new ArrayList<ChargeInstanceDto>();

            for (UsageChargeInstance ci : serviceInstance.getUsageChargeInstances()) {
                usageChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(),
                    ci.getSeller().getCode(), ci.getUserAccount().getCode()));
            }
        }

        customFields = customFieldInstances;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public InstanceStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(InstanceStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the status date.
     *
     * @return the status date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the new status date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Get reactivation date
     *
     * @return reactivation date
     */
    public Date getReactivationDate() {
        return reactivationDate;
    }

    /**
     * Set reactivation date
     *
     * @param reactivationDate reactivation date
     */
    public void setReactivationDate(Date reactivationDate) {
        this.reactivationDate = reactivationDate;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    /**
     * Gets the end agreement date.
     *
     * @return the end agreement date
     */
    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    /**
     * Sets the end agreement date.
     *
     * @param endAgreementDate the new end agreement date
     */
    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
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
     * Gets the rate until date.
     *
     * @return the rate until date
     */
    public Date getRateUntilDate() {
        return rateUntilDate;
    }

    /**
     * Sets the rate until date.
     *
     * @param rateUntilDate the new rate until date
     */
    public void setRateUntilDate(Date rateUntilDate) {
        this.rateUntilDate = rateUntilDate;
    }

    /**
     * Gets the amount PS.
     *
     * @return the amountPS
     */
    public BigDecimal getAmountPS() {
        return amountPS;
    }

    /**
     * Sets the amount PS.
     *
     * @param amountPS the amountPS to set
     */
    public void setAmountPS(BigDecimal amountPS) {
        this.amountPS = amountPS;
    }

    /**
     * Gets the calendar PS code.
     *
     * @return the calendarPSCode
     */
    public String getCalendarPSCode() {
        return calendarPSCode;
    }

    /**
     * Sets the calendar PS code.
     *
     * @param calendarPSCode the calendarPSCode to set
     */
    public void setCalendarPSCode(String calendarPSCode) {
        this.calendarPSCode = calendarPSCode;
    }

    /**
     * Gets the payment day in month PS.
     *
     * @return the payment day in month PS
     */
    public Integer getPaymentDayInMonthPS() {
        return paymentDayInMonthPS;
    }

    /**
     * Sets the payment day in month PS.
     *
     * @param paymentDayInMonthPS the new payment day in month PS
     */
    public void setPaymentDayInMonthPS(Integer paymentDayInMonthPS) {
        this.paymentDayInMonthPS = paymentDayInMonthPS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ServiceInstanceDto [code=" + code + ", description=" + description + ", status=" + status + ", subscriptionDate=" + subscriptionDate + ", terminationDate="
                + terminationDate + ", quantity=" + quantity + ", terminationReason=" + terminationReason + ", orderNumber=" + orderNumber + "]";
    }

    /**
     * Gets the minimum amount el.
     *
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * Sets the minimum amount el.
     *
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * Gets the minimum amount el spark.
     *
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    /**
     * Sets the minimum amount el spark.
     *
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    /**
     * Gets the minimum label el.
     *
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * Sets the minimum label el.
     *
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * Gets the minimum label el spark.
     *
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * Sets the minimum label el spark.
     *
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
    }

    /**
     * Gets the auto end of engagement.
     *
     * @return the auto end of engagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }

    /**
     * Gets the doNotApplyEngagement flag.
     *
     * @return the doNotApplyEngagement
     */
    public Boolean getDoNotApplyEngagement() {
        return doNotApplyEngagement;
    }

    /**
     * Sets the doNotApplyEngagement flag.
     *
     * @param doNotApplyEngagement the autoEndOfEngagement to set
     */
    public void setDoNotApplyEngagement(Boolean doNotApplyEngagement) {
        this.doNotApplyEngagement = doNotApplyEngagement;
    }

    /**
     * Gets the subscribed till date.
     *
     * @return the subscribedTillDate
     */
    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    /**
     * Sets the subscribed till date.
     *
     * @param subscribedTillDate the new subscribedTillDate
     */
    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    /**
     * Gets the service renewal
     *
     * @return the service renewal
     */
    public SubscriptionRenewalDto getServiceRenewal() {
        return serviceRenewal;
    }

    /**
     * Sets the service renewal.
     *
     * @param serviceRenewal the new service renewal
     */
    public void setServiceRenewal(SubscriptionRenewalDto serviceRenewal) {
        this.serviceRenewal = serviceRenewal;
    }

    public String getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    public void setMinimumChargeTemplate(String minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }
}