package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.order.OrderItemActionEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "UpdateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "orderNumber", "orderItemId", "orderItemAction" })
public class UpdateServicesRequestDto extends BaseDto {

    private static final long serialVersionUID = 8352154466061113933L;

    @XmlElement(required = true)
    private String subscriptionCode;

    @XmlElement(name = "serviceToUpdate")
    @XmlElementWrapper(name = "servicesToUpdate")
    private List<ServiceToUpdateDto> servicesToUpdate;
    
    private String orderNumber;
    private Long orderItemId;
    private OrderItemActionEnum orderItemAction;

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public List<ServiceToUpdateDto> getServicesToUpdate() {
        return servicesToUpdate;
    }

    public void setServicesToUpdate(List<ServiceToUpdateDto> servicesToUpdate) {
        this.servicesToUpdate = servicesToUpdate;
    }

    public void addService(ServiceToUpdateDto serviceToUpdate) {
        if (servicesToUpdate == null) {
            servicesToUpdate = new ArrayList<>();
        }
        servicesToUpdate.add(serviceToUpdate);
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}