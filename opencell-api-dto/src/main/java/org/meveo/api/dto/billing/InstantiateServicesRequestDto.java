package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.order.OrderItemActionEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class InstantiateServicesRequestDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "InstantiateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "orderNumber", "orderItemId", "orderItemAction" })
public class InstantiateServicesRequestDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1150993171011072506L;

    /** The subscription. */
    @XmlElement(required = true)
    private String subscription;

    /** The services to instantiate. */
    @XmlElement
    private ServicesToInstantiateDto servicesToInstantiate = new ServicesToInstantiateDto();

    /** The order number. */
    private String orderNumber;

    /** The order item id. */
    private Long orderItemId;

    /** The order item action. */
    private OrderItemActionEnum orderItemAction;

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
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
     * Gets the services to instantiate.
     *
     * @return the services to instantiate
     */
    public ServicesToInstantiateDto getServicesToInstantiate() {
        return servicesToInstantiate;
    }

    /**
     * Sets the services to instantiate.
     *
     * @param servicesToInstantiate the new services to instantiate
     */
    public void setServicesToInstantiate(ServicesToInstantiateDto servicesToInstantiate) {
        this.servicesToInstantiate = servicesToInstantiate;
    }

    /**
     * Gets the order item id.
     *
     * @return the order item id
     */
    public Long getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the order item id.
     *
     * @param orderItemId the new order item id
     */
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    /**
     * Gets the order item action.
     *
     * @return the order item action
     */
    public OrderItemActionEnum getOrderItemAction() {
        return orderItemAction;
    }

    /**
     * Sets the order item action.
     *
     * @param action the new order item action
     */
    public void setOrderItemAction(OrderItemActionEnum action) {
        this.orderItemAction = action;
    }
    
    @Override
    public String toString() {
        return "InstantiateServicesRequestDto [subscription=" + subscription + ", servicesToInstantiate=" + servicesToInstantiate + ", orderNumber=" + orderNumber + "]";
    }    
}