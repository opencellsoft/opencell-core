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
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "UpdateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateServicesRequestDto extends BaseDto {

    private static final long serialVersionUID = 8352154466061113933L;

    @XmlElement(required = true)
    private String subscriptionCode;

    @XmlElement(name = "serviceToUpdate")
    @XmlElementWrapper(name = "servicesToUpdate")
    private List<ServiceToUpdateDto> servicesToUpdate;

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
}