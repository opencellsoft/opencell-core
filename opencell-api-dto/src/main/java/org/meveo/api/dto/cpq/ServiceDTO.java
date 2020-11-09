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

package org.meveo.api.dto.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.cpq.enums.ServiceTypeEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * The Class ServiceDto.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.00
 */
@XmlRootElement(name = "ServiceDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceDTO extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6794700715161690227L;

    /** The long description. */
    private String longDescription;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The image path. */
    private String imagePath;

    /** The image base 64. */
    private String imageBase64;

  
    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String groupedServiceCode;
    
    /**
     * Corresponding to minimum one shot charge template code.
     */
    private ServiceTypeEnum serviceType;
    
    /**
     * Corresponding to predefined allowed values
     */
    private List<Object> values;
    
    
    /**
     * Corresponding to the value validator (regex expression)
     */
    private String valueValidator;

    
    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;
    
    /**
     * Instantiates a new service template dto.
     */
    public ServiceDTO() {
    }

 

    /**
     * Instantiates a new service template dto.
     *
     * @param serviceTemplate the service template
     */
    public ServiceDTO(ServiceTemplate serviceTemplate) {
        super(serviceTemplate);
    }

 
    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }


    /**
     * Gets the long description.
     *
     * @return the long description
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the long description.
     *
     * @param longDescription the new long description
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Gets the image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path.
     *
     * @param imagePath the new image path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the image base 64.
     *
     * @return the image base 64
     */
    public String getImageBase64() {
        return imageBase64;
    }

    /**
     * Sets the image base 64.
     *
     * @param imageBase64 the new image base 64
     */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }



    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

	public String getGroupedServiceCode() {
		return groupedServiceCode;
	}

	public void setGroupedServiceCode(String groupedServiceCode) {
		this.groupedServiceCode = groupedServiceCode;
	}

	public ServiceTypeEnum getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceTypeEnum serviceType) {
		this.serviceType = serviceType;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	public String getValueValidator() {
		return valueValidator;
	}

	public void setValueValidator(String valueValidator) {
		this.valueValidator = valueValidator;
	}
    
}