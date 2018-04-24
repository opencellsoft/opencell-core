package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateRecurringsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateRecurringsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4649058196780119541L;

    /** The service charge template recurring. */
    private List<ServiceChargeTemplateRecurringDto> serviceChargeTemplateRecurring;

    /**
     * Gets the service charge template recurring.
     *
     * @return the service charge template recurring
     */
    public List<ServiceChargeTemplateRecurringDto> getServiceChargeTemplateRecurring() {
        if (serviceChargeTemplateRecurring == null)
            serviceChargeTemplateRecurring = new ArrayList<ServiceChargeTemplateRecurringDto>();
        return serviceChargeTemplateRecurring;
    }

    /**
     * Sets the service charge template recurring.
     *
     * @param serviceChargeTemplateRecurring the new service charge template recurring
     */
    public void setServiceChargeTemplateRecurring(List<ServiceChargeTemplateRecurringDto> serviceChargeTemplateRecurring) {
        this.serviceChargeTemplateRecurring = serviceChargeTemplateRecurring;
    }

    @Override
    public String toString() {
        return "ServiceChargeTemplateRecurringsDto [serviceChargeTemplateRecurring=" + serviceChargeTemplateRecurring + "]";
    }

}
