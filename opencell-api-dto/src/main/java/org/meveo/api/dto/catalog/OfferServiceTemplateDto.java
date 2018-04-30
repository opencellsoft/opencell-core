package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * The Class OfferServiceTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OfferServiceTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferServiceTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7137259235916807339L;

    /** The service template. */
    private ServiceTemplateDto serviceTemplate;

    /** The mandatory. */
    private Boolean mandatory;

    /** The incompatible services. */
    @XmlElementWrapper(name = "incompatibleServices")
    @XmlElement(name = "incompatibleServiceTemplate")
    private List<ServiceTemplateDto> incompatibleServices = new ArrayList<>();

    /**
     * Instantiates a new offer service template dto.
     */
    public OfferServiceTemplateDto() {

    }

    /**
     * Instantiates a new offer service template dto.
     *
     * @param offerServiceTemplate the OfferServiceTemplate entity
     * @param customFields the custom fields
     */
    public OfferServiceTemplateDto(OfferServiceTemplate e, CustomFieldsDto customFields, boolean loadServiceChargeTemplate) {
        if (e.getServiceTemplate() != null) {
            serviceTemplate = new ServiceTemplateDto(e.getServiceTemplate(), customFields, loadServiceChargeTemplate);
        }
        mandatory = e.isMandatory();
        if (e.getIncompatibleServices() != null) {
            for (ServiceTemplate st : e.getIncompatibleServices()) {
                incompatibleServices.add(new ServiceTemplateDto(st));
            }
        }
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Gets the incompatible services.
     *
     * @return the incompatible services
     */
    public List<ServiceTemplateDto> getIncompatibleServices() {
        return incompatibleServices;
    }

    /**
     * Sets the incompatible services.
     *
     * @param incompatibleServices the new incompatible services
     */
    public void setIncompatibleServices(List<ServiceTemplateDto> incompatibleServices) {
        this.incompatibleServices = incompatibleServices;
    }

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

    /**
     * Gets the mandatory.
     *
     * @return the mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }
    
    @Override
    public String toString() {
        return "OfferServiceTemplateDto [serviceTemplate=" + serviceTemplate + ", mandatory=" + mandatory + ", incompatibleServices=" + incompatibleServices + "]";
    }
}