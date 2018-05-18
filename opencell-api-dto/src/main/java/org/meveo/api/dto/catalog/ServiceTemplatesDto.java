package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ServiceTemplatesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServiceTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceTemplatesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4923256325444663405L;

    /** The service template. */
    private List<ServiceTemplateDto> serviceTemplate;

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public List<ServiceTemplateDto> getServiceTemplate() {
        if (serviceTemplate == null)
            serviceTemplate = new ArrayList<ServiceTemplateDto>();
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(List<ServiceTemplateDto> serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }


    @Override
    public String toString() {
        return "ServiceTemplatesDto [serviceTemplate=" + serviceTemplate + "]";
    }

}