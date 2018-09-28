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
    
    /** The due date days PS. */
    private Integer dueDateDaysPS;

    /**
     * Instantiates a new service instance dto.
     */
    public ServiceInstanceDto() {

    }

    /**
     * Instantiates a new service instance dto.
     *
     * @param serviceInstance the ServiceInstance entity
     * @param customFieldInstances the custom field instances
     */
    public ServiceInstanceDto(ServiceInstance serviceInstance, CustomFieldsDto customFieldInstances) {
        super(serviceInstance);
        id = serviceInstance.getId();
        status = serviceInstance.getStatus();
        statusDate = serviceInstance.getStatusDate();
        subscriptionDate = serviceInstance.getSubscriptionDate();
        terminationDate = serviceInstance.getTerminationDate();
        quantity = serviceInstance.getQuantity();
        orderNumber = serviceInstance.getOrderNumber();
        if (serviceInstance.getSubscriptionTerminationReason() != null) {
            terminationReason = serviceInstance.getSubscriptionTerminationReason().getCode();
        }
        endAgreementDate = serviceInstance.getEndAgreementDate();

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
     * Gets the due date days PS.
     *
     * @return the dueDateDaysPS
     */
    public Integer getDueDateDaysPS() {
        return dueDateDaysPS;
    }

    /**
     * Sets the due date days PS.
     *
     * @param dueDateDaysPS the dueDateDaysPS to set
     */
    public void setDueDateDaysPS(Integer dueDateDaysPS) {
        this.dueDateDaysPS = dueDateDaysPS;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ServiceInstanceDto [code=" + code + ", description=" + description + ", status=" + status + ", subscriptionDate=" + subscriptionDate + ", terminationDate="
                + terminationDate + ", quantity=" + quantity + ", terminationReason=" + terminationReason + ", orderNumber=" + orderNumber + "]";
    }
}