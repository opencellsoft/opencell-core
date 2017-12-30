package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ActivateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivateServicesRequestDto extends BaseDto {

    private static final long serialVersionUID = 1150993171011072506L;

    @XmlElement(required = true)
    private String subscription;

    @XmlElement
    private ServicesToActivateDto servicesToActivate = new ServicesToActivateDto();

    private String orderNumber;

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
}