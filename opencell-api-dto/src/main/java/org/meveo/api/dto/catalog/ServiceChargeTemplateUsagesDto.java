package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateUsagesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateUsagesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5175410607345470193L;

    /** The service charge template usage. */
    private List<ServiceUsageChargeTemplateDto> serviceChargeTemplateUsage;

    /**
     * Gets the service charge template usage.
     *
     * @return the service charge template usage
     */
    public List<ServiceUsageChargeTemplateDto> getServiceChargeTemplateUsage() {
        if (serviceChargeTemplateUsage == null)
            serviceChargeTemplateUsage = new ArrayList<ServiceUsageChargeTemplateDto>();
        return serviceChargeTemplateUsage;
    }

    /**
     * Sets the service charge template usage.
     *
     * @param serviceChargeTemplateUsage the new service charge template usage
     */
    public void setServiceChargeTemplateUsage(List<ServiceUsageChargeTemplateDto> serviceChargeTemplateUsage) {
        this.serviceChargeTemplateUsage = serviceChargeTemplateUsage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ServiceChargeTemplateUsagesDto [serviceChargeTemplateUsage=" + serviceChargeTemplateUsage + "]";
    }

}
