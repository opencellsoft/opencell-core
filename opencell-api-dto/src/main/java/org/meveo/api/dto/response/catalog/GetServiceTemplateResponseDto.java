package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetServiceTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetServiceTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetServiceTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2517820224350375764L;

    /** The service template. */
    private ServiceTemplateDto serviceTemplate;

    /**
     * Gets the service template.
     *
     * @return the service template
     */
    public ServiceTemplateDto getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the new service template
     */
    public void setServiceTemplate(ServiceTemplateDto serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    @Override
    public String toString() {
        return "GetServiceTemplateResponse [serviceTemplate=" + serviceTemplate + ", toString()=" + super.toString() + "]";
    }
}