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
    
    /** The amount. */
    private BigDecimal amount;
 
    /** The calendar code. */
    private String calendarCode;
        
    private PaymentScheduleStatusEnum status;
    
    private String paymentScheduleTemplate;
             
    /** The due date days. */
    private Integer dueDateDays;
    
    private String serviceInstanceCode;
    private String subscriptionCode;

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the calendarCode
     */
    public String getCalendarCode() {
        return calendarCode;
    }

    /**
     * @param calendarCode the calendarCode to set
     */
    public void setCalendarCode(String calendarCode) {
        this.calendarCode = calendarCode;
    }

    /**
     * @return the status
     */
    public PaymentScheduleStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(PaymentScheduleStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the paymentScheduleTemplate
     */
    public String getPaymentScheduleTemplate() {
        return paymentScheduleTemplate;
    }

    /**
     * @param paymentScheduleTemplate the paymentScheduleTemplate to set
     */
    public void setPaymentScheduleTemplate(String paymentScheduleTemplate) {
        this.paymentScheduleTemplate = paymentScheduleTemplate;
    }

    /**
     * @return the dueDateDays
     */
    public Integer getDueDateDays() {
        return dueDateDays;
    }

    /**
     * @param dueDateDays the dueDateDays to set
     */
    public void setDueDateDays(Integer dueDateDays) {
        this.dueDateDays = dueDateDays;
    }

    /**
     * @return the serviceInstanceCode
     */
    public String getServiceInstanceCode() {
        return serviceInstanceCode;
    }

    /**
     * @param serviceInstanceCode the serviceInstanceCode to set
     */
    public void setServiceInstanceCode(String serviceInstanceCode) {
        this.serviceInstanceCode = serviceInstanceCode;
    }

    /**
     * @return the subscriptionCode
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * @param subscriptionCode the subscriptionCode to set
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }
    
    

}
