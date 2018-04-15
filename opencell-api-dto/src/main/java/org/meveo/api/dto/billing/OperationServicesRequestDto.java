package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class OperationServicesRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OperationServicesRequestDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationServicesRequestDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1150993171011072506L;

    /** The subscription code. */
    @XmlElement(required = true)
    private String subscriptionCode;

    /** The services to update. */
    @XmlElementWrapper(name = "ListServiceToUpdate")
    @XmlElement(name = "serviceToUpdate")
    private List<ServiceToUpdateDto> servicesToUpdate = new ArrayList<ServiceToUpdateDto>();

    /**
     * Gets the subscription code.
     *
     * @return the subscription code
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * Sets the subscription code.
     *
     * @param subscriptionCode the new subscription code
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * Gets the services to update.
     *
     * @return the services to update
     */
    public List<ServiceToUpdateDto> getServicesToUpdate() {
        return servicesToUpdate;
    }

    /**
     * Sets the services to update.
     *
     * @param servicesToUpdate the new services to update
     */
    public void setServicesToUpdate(List<ServiceToUpdateDto> servicesToUpdate) {
        this.servicesToUpdate = servicesToUpdate;
    }

    @Override
    public String toString() {
        return "OperationServicesRequestDto [subscriptionCode=" + subscriptionCode + ", servicesToUpdate=" + servicesToUpdate + "]";
    }

}