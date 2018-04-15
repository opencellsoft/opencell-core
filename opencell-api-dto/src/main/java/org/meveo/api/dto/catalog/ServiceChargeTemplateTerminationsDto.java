package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateTerminationsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateTerminationsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7148418773670960365L;

    /** The service charge template termination. */
    private List<ServiceChargeTemplateTerminationDto> serviceChargeTemplateTermination;

    /**
     * Gets the service charge template termination.
     *
     * @return the service charge template termination
     */
    public List<ServiceChargeTemplateTerminationDto> getServiceChargeTemplateTermination() {
        if (serviceChargeTemplateTermination == null)
            serviceChargeTemplateTermination = new ArrayList<ServiceChargeTemplateTerminationDto>();
        return serviceChargeTemplateTermination;
    }

    /**
     * Sets the service charge template termination.
     *
     * @param serviceChargeTemplateTermination the new service charge template termination
     */
    public void setServiceChargeTemplateTermination(List<ServiceChargeTemplateTerminationDto> serviceChargeTemplateTermination) {
        this.serviceChargeTemplateTermination = serviceChargeTemplateTermination;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ServiceChargeTemplateTerminationsDto [serviceChargeTemplateTermination=" + serviceChargeTemplateTermination + "]";
    }

}
