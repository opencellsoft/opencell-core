/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
     * @param e the OfferServiceTemplate entity
     * @param customFields the custom fields
     * @param loadServiceChargeTemplate whether to load the charge templates or not.
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