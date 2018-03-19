package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.order.OrderItemActionEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "ActivateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "orderNumber", "orderItemId", "orderItemAction" })
public class ActivateServicesRequestDto extends BaseDto {

    private static final long serialVersionUID = 1150993171011072506L;

    @XmlElement(required = true)
    private String subscription;

    @XmlElement
    private ServicesToActivateDto servicesToActivate = new ServicesToActivateDto();

    private String orderNumber;
    private Long orderItemId;
    private OrderItemActionEnum orderItemAction;

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public ServicesToActivateDto getServicesToActivateDto() {
        return servicesToActivate;
    }

    public void setServicesToActivateDto(ServicesToActivateDto servicesToActivate) {
        this.servicesToActivate = servicesToActivate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "ActivateServicesRequestDto [subscription=" + subscription + ", servicesToActivate=" + servicesToActivate + ", orderNumber=" + orderNumber + "]";
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

    public void setOrderItemAction(OrderItemActionEnum action) {
        this.orderItemAction = action;
    }
}