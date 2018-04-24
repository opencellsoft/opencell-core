package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class SubscriptionWithCreditLimitUpdateDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "SubscriptionWithCreditLimitUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitUpdateDto extends SubscriptionWithCreditLimitDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6700315102709912658L;

    /** The services to terminate. */
    private List<ServiceToTerminateDto> servicesToTerminate;

    /**
     * Gets the services to terminate.
     *
     * @return the services to terminate
     */
    public List<ServiceToTerminateDto> getServicesToTerminate() {
        return servicesToTerminate;
    }

    /**
     * Sets the services to terminate.
     *
     * @param servicesToTerminate the new services to terminate
     */
    public void setServicesToTerminate(List<ServiceToTerminateDto> servicesToTerminate) {
        this.servicesToTerminate = servicesToTerminate;
    }
}