package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.order.OrderItemActionEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InstantiateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstantiateServicesRequestDto extends BaseDto {

    private static final long serialVersionUID = 1150993171011072506L;

    @XmlElement(required = true)
    private String subscription;

    @XmlElement
    private ServicesToInstantiateDto servicesToInstantiate = new ServicesToInstantiateDto();

    private String orderNumber;
    private Long orderItemId;
    private OrderItemActionEnum orderItemAction;

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ServicesToInstantiateDto getServicesToInstantiate() {
        return servicesToInstantiate;
    }

    public void setServicesToInstantiate(ServicesToInstantiateDto servicesToInstantiate) {
        this.servicesToInstantiate = servicesToInstantiate;
    }

    @Override
    public String toString() {
        return "InstantiateServicesRequestDto [subscription=" + subscription + ", servicesToInstantiate=" + servicesToInstantiate + ", orderNumber=" + orderNumber + "]";
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