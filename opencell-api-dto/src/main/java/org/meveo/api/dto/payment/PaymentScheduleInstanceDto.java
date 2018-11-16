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
    private Date endDate;
    
    /** The start date. */
    private Date startDate;
    
    /** The amount. */
    private BigDecimal amount;
 
    /** The calendar code. */
    private String calendarCode;
        
    /** The status. */
    private PaymentScheduleStatusEnum status;
    
    /** The status date. */
    private Date statusDate;
    
    /** The payment schedule template code. */
    private String paymentScheduleTemplateCode;
             
    /** The due date days. */
    private Integer dueDateDays;
    
    /** The service instance code. */
    private String serviceInstanceCode;
    
    /** The subscription code. */
    private String subscriptionCode;
    
    private PaymentScheduleInstanceBalanceDto paymentScheduleInstanceBalanceDto;
    
    @XmlElementWrapper
    @XmlElement(name = "item")
    private List<PaymentScheduleInstanceItemDto> items = new ArrayList<PaymentScheduleInstanceItemDto>();
    
    /** The custom fields. */
    private CustomFieldsDto customFields;
    
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
     * Instantiates a new payment schedule instance dto.
     */
    public PaymentScheduleInstanceDto() {
        
    }
    
    /**
     * Instantiates a new payment schedule instance dto.
     *
     * @param paymentScheduleInstance the payment schedule instance
     */
    public  PaymentScheduleInstanceDto(PaymentScheduleInstance paymentScheduleInstance) {
        this.id = paymentScheduleInstance.getId();
        this.amount = paymentScheduleInstance.getAmount();
        this.calendarCode = paymentScheduleInstance.getCalendar().getCode();
        this.code = paymentScheduleInstance.getCode();
        this.description = paymentScheduleInstance.getDescription();
        this.dueDateDays = paymentScheduleInstance.getDueDateDays();
        this.endDate = paymentScheduleInstance.getEndDate();
        this.startDate = paymentScheduleInstance.getStartDate();
        this.paymentScheduleTemplateCode = paymentScheduleInstance.getPaymentScheduleTemplate().getCode();
        this.statusDate = paymentScheduleInstance.getStatusDate();
        this.status = paymentScheduleInstance.getStatus();
        this.serviceInstanceCode = paymentScheduleInstance.getServiceInstance().getCode();
        this.subscriptionCode = paymentScheduleInstance.getServiceInstance().getSubscription().getCode();
        for(PaymentScheduleInstanceItem item  : paymentScheduleInstance.getPaymentScheduleInstanceItems()) {
          this.items.add(new PaymentScheduleInstanceItemDto(item) );
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
     * Gets the service instance code.
     *
     * @return the serviceInstanceCode
     */
    public String getServiceInstanceCode() {
        return serviceInstanceCode;
    }

    /**
     * Sets the service instance code.
     *
     * @param serviceInstanceCode the serviceInstanceCode to set
     */
    public void setServiceInstanceCode(String serviceInstanceCode) {
        this.serviceInstanceCode = serviceInstanceCode;
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
     * @return the items
     */
    public List<PaymentScheduleInstanceItemDto> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<PaymentScheduleInstanceItemDto> items) {
        this.items = items;
    }

    /**
     * @return the paymentScheduleInstanceBalanceDto
     */
    public PaymentScheduleInstanceBalanceDto getPaymentScheduleInstanceBalanceDto() {
        return paymentScheduleInstanceBalanceDto;
    }

    /**
     * @param paymentScheduleInstanceBalanceDto the paymentScheduleInstanceBalanceDto to set
     */
    public void setPaymentScheduleInstanceBalanceDto(PaymentScheduleInstanceBalanceDto paymentScheduleInstanceBalanceDto) {
        this.paymentScheduleInstanceBalanceDto = paymentScheduleInstanceBalanceDto;
    }
    
    

}
