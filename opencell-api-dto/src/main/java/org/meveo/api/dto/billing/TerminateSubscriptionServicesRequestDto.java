package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.order.OrderItemActionEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TerminateSubscriptionServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "orderNumber", "orderItemId", "orderItemAction" })
public class TerminateSubscriptionServicesRequestDto extends BaseDto {

    private static final long serialVersionUID = 7356243821434866938L;

    @XmlElement()
    private List<String> services;

    @XmlElement()
    private List<Long> serviceIds;

    @XmlElement(required = true)
    private String subscriptionCode;

    @XmlElement(required = true)
    private String terminationReason;

    @XmlElement(required = true)
    private Date terminationDate;

    private String orderNumber;
    private Long orderItemId;
    private OrderItemActionEnum orderItemAction;

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public void addServiceId(Long serviceId) {
        if (serviceIds == null) {
            serviceIds = new ArrayList<>();
        }
        serviceIds.add(serviceId);
    }

    public List<String> getServices() {
        return services;
    }

    public void addServiceCode(String serviceCode) {
        if (services == null) {
            services = new ArrayList<>();
        }
        services.add(serviceCode);
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "TerminateSubscriptionServicesRequestDto [services=" + services + ", serviceIds=" + serviceIds + ", subscriptionCode=" + subscriptionCode + ", terminationReason="
                + terminationReason + ", terminationDate=" + terminationDate + ", orderNumber=" + orderNumber + "]";
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public OrderItemActionEnum getOrderItemAction() {
        return orderItemAction;
    }

    public void setAction(OrderItemActionEnum action) {
        this.orderItemAction = action;
    }
}