package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlElement;

/**
 * The Class SubscriptionAndServicesToActivateRequestDto.
 *
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */
public class SubscriptionAndServicesToActivateRequestDto extends SubscriptionDto {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1150993171011072506L;
    /** The subscription. */
    @XmlElement(required = true)
    private ServicesToActivateDto servicesToActivate;

    /**
     * Gets the services to activate dto.
     *
     * @return the services to activate dto
     */
    public ServicesToActivateDto getServicesToActivateDto() {
        return servicesToActivate;
    }

    /**
     * Sets the services to activate dto.
     *
     * @param servicesToActivate the new services to activate dto
     */
    public void setServicesToActivateDto(ServicesToActivateDto servicesToActivate) {
        this.servicesToActivate = servicesToActivate;
    }

}
