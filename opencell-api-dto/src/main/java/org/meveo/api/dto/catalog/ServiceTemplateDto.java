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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class ServiceTemplateDto.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0.1
 */
@XmlRootElement(name = "ServiceTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceTemplateDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6794700715161690227L;

    /** The long description. */
    private String longDescription;

    /** The invoicing calendar. */
    private String invoicingCalendar;

    /** The service charge template recurrings. */
    private ServiceChargeTemplateRecurringsDto serviceChargeTemplateRecurrings;

    /** The service charge template subscriptions. */
    private ServiceChargeTemplateSubscriptionsDto serviceChargeTemplateSubscriptions;

    /** The service charge template terminations. */
    private ServiceChargeTemplateTerminationsDto serviceChargeTemplateTerminations;

    /** The service charge template usages. */
    private ServiceChargeTemplateUsagesDto serviceChargeTemplateUsages;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The mandatory. */
    @Deprecated
    private boolean mandatory=Boolean.FALSE;

    /**
     * BusinessServiceModel code.
     */
    private String somCode;

    /** The image path. */
    private String imagePath;

    /** The image base 64. */
    private String imageBase64;

    /**
     * Expression to determine minimum amount value
     */
    private String minimumAmountEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    private String minimumLabelEl;

    /**
     * Corresponding to minimum invoice subcategory
     */
    private String minimumInvoiceSubCategory;

    private Boolean autoEndOfEngagement;

    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String minimumChargeTemplate;

    /**
     * The renewal rule.
     */
    @JsonInclude(Include.NON_NULL)
    private SubscriptionRenewalDto renewalRule;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;
    
    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String groupedServiceCode;
     
    /**
     * Corresponding to predefined allowed values
     */
    private List<Object> values;
    
    
    /**
     * Corresponding to the value validator (regex expression)
     */
    private String valueValidator;
    
    
    /** The service type code */
    private String  serviceTypeCode;
    

    /** The display . */ 
    private boolean display=Boolean.FALSE;
    
    
    /**
     * this field will contain the additional information entered in the "CPQ configurator" and linked to the type of service
     */
    private String param;
    
    /** The medias */
    @XmlElementWrapper(name = "medias")
    @XmlElement(name = "medias")
    protected List<MediaDto> medias;

    /**
     * Instantiates a new service template dto.
     */
    public ServiceTemplateDto() {
    }

    /**
     * Instantiates a new service template dto.
     *
     * @param serviceTemplate the service template
     * @param customFieldInstances the custom field instances
     * @param loadServiceChargeTemplate whether to load the charge templates or not
     */
    public ServiceTemplateDto(ServiceTemplate serviceTemplate, CustomFieldsDto customFieldInstances, boolean loadServiceChargeTemplate) {
        super(serviceTemplate);

        longDescription = serviceTemplate.getLongDescription();
        invoicingCalendar = serviceTemplate.getInvoicingCalendar() == null ? null : serviceTemplate.getInvoicingCalendar().getCode();
        imagePath = serviceTemplate.getImagePath();
        minimumAmountEl = serviceTemplate.getMinimumAmountEl();
        minimumLabelEl = serviceTemplate.getMinimumLabelEl();
        languageDescriptions = (LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(serviceTemplate.getDescriptionI18n()));

        if(serviceTemplate.getMinimumInvoiceSubCategory() != null) {
            minimumInvoiceSubCategory = serviceTemplate.getMinimumInvoiceSubCategory().getCode();
        }

        if (serviceTemplate.getBusinessServiceModel() != null) {
            somCode = serviceTemplate.getBusinessServiceModel().getCode();
        }

        if (loadServiceChargeTemplate) {
            // set serviceChargeTemplateRecurrings
            if (!serviceTemplate.getServiceRecurringCharges().isEmpty()) {
                serviceChargeTemplateRecurrings = new ServiceChargeTemplateRecurringsDto();

                for (ServiceChargeTemplateRecurring recCharge : serviceTemplate.getServiceRecurringCharges()) {
                    ServiceChargeTemplateRecurringDto serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurringDto();
                    serviceChargeTemplateRecurring.setCode(recCharge.getChargeTemplate().getCode());

                    for (WalletTemplate wallet : recCharge.getWalletTemplates()) {
                        serviceChargeTemplateRecurring.getWallets().getWallet().add(wallet.getCode());
                    }

                    for (CounterTemplate accumulatorCounterTemplate : recCharge.getAccumulatorCounterTemplates()) {
                        serviceChargeTemplateRecurring.getAccumulatorCounterTemplates().getCounterTemplate().add(accumulatorCounterTemplate.getCode());
                    }

                    serviceChargeTemplateRecurrings.getServiceChargeTemplateRecurring().add(serviceChargeTemplateRecurring);
                }
            }

            // set serviceChargeTemplateSubscriptions
            if (!serviceTemplate.getServiceSubscriptionCharges().isEmpty()) {
                serviceChargeTemplateSubscriptions = new ServiceChargeTemplateSubscriptionsDto();

                for (ServiceChargeTemplateSubscription subCharge : serviceTemplate.getServiceSubscriptionCharges()) {
                    ServiceChargeTemplateSubscriptionDto serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscriptionDto();
                    serviceChargeTemplateSubscription.setCode(subCharge.getChargeTemplate().getCode());

                    for (WalletTemplate wallet : subCharge.getWalletTemplates()) {
                        serviceChargeTemplateSubscription.getWallets().getWallet().add(wallet.getCode());
                    }

                    for (CounterTemplate accumulatorCounterTemplate : subCharge.getAccumulatorCounterTemplates()) {
                        serviceChargeTemplateSubscription.getAccumulatorCounterTemplates().getCounterTemplate().add(accumulatorCounterTemplate.getCode());
                    }

                    serviceChargeTemplateSubscriptions.getServiceChargeTemplateSubscription().add(serviceChargeTemplateSubscription);
                }
            }

            // set serviceChargeTemplateTerminations
            if (!serviceTemplate.getServiceTerminationCharges().isEmpty()) {
                serviceChargeTemplateTerminations = new ServiceChargeTemplateTerminationsDto();

                for (ServiceChargeTemplateTermination terminationCharge : serviceTemplate.getServiceTerminationCharges()) {
                    ServiceChargeTemplateTerminationDto serviceChargeTemplateTermination = new ServiceChargeTemplateTerminationDto();
                    serviceChargeTemplateTermination.setCode(terminationCharge.getChargeTemplate().getCode());

                    for (WalletTemplate wallet : terminationCharge.getWalletTemplates()) {
                        serviceChargeTemplateTermination.getWallets().getWallet().add(wallet.getCode());
                    }

                    for (CounterTemplate accumulatorCounterTemplate : terminationCharge.getAccumulatorCounterTemplates()) {
                        serviceChargeTemplateTermination.getAccumulatorCounterTemplates().getCounterTemplate().add(accumulatorCounterTemplate.getCode());
                    }

                    serviceChargeTemplateTerminations.getServiceChargeTemplateTermination().add(serviceChargeTemplateTermination);
                }

            }

            // add serviceChargeTemplateUsages
            if (!serviceTemplate.getServiceUsageCharges().isEmpty()) {
                serviceChargeTemplateUsages = new ServiceChargeTemplateUsagesDto();

                for (ServiceChargeTemplateUsage usageCharge : serviceTemplate.getServiceUsageCharges()) {
                    ServiceUsageChargeTemplateDto serviceUsageChargeTemplate = new ServiceUsageChargeTemplateDto();
                    serviceUsageChargeTemplate.setCode(usageCharge.getChargeTemplate().getCode());

                    if (usageCharge.getCounterTemplate() != null) {
                        serviceUsageChargeTemplate.setCounterTemplate(usageCharge.getCounterTemplate().getCode());
                    }

                    for (WalletTemplate wallet : usageCharge.getWalletTemplates()) {
                        serviceUsageChargeTemplate.getWallets().getWallet().add(wallet.getCode());
                    }

                    for (CounterTemplate accumulatorCounterTemplate : usageCharge.getAccumulatorCounterTemplates()) {
                        serviceUsageChargeTemplate.getAccumulatorCounterTemplates().getCounterTemplate().add(accumulatorCounterTemplate.getCode());
                    }

                    serviceChargeTemplateUsages.getServiceChargeTemplateUsage().add(serviceUsageChargeTemplate);
                }
            }
        }

        if (serviceTemplate.getServiceRenewal() != null) {
            renewalRule = new SubscriptionRenewalDto(serviceTemplate.getServiceRenewal());
        }

        customFields = customFieldInstances;
    }

    /**
     * Instantiates a new service template dto.
     *
     * @param serviceTemplate the service template
     */
    public ServiceTemplateDto(ServiceTemplate serviceTemplate) {
        super(serviceTemplate);
    }

    /**
     * Gets the invoicing calendar.
     *
     * @return the invoicing calendar
     */
    public String getInvoicingCalendar() {
        return invoicingCalendar;
    }

    /**
     * Sets the invoicing calendar.
     *
     * @param invoicingCalendar the new invoicing calendar
     */
    public void setInvoicingCalendar(String invoicingCalendar) {
        this.invoicingCalendar = invoicingCalendar;
    }

    /**
     * Gets the service charge template recurrings.
     *
     * @return the service charge template recurrings
     */
    public ServiceChargeTemplateRecurringsDto getServiceChargeTemplateRecurrings() {
        return serviceChargeTemplateRecurrings;
    }

    /**
     * Sets the service charge template recurrings.
     *
     * @param serviceChargeTemplateRecurrings the new service charge template recurrings
     */
    public void setServiceChargeTemplateRecurrings(ServiceChargeTemplateRecurringsDto serviceChargeTemplateRecurrings) {
        this.serviceChargeTemplateRecurrings = serviceChargeTemplateRecurrings;
    }

    /**
     * Gets the service charge template subscriptions.
     *
     * @return the service charge template subscriptions
     */
    public ServiceChargeTemplateSubscriptionsDto getServiceChargeTemplateSubscriptions() {
        return serviceChargeTemplateSubscriptions;
    }

    /**
     * Sets the service charge template subscriptions.
     *
     * @param serviceChargeTemplateSubscriptions the new service charge template subscriptions
     */
    public void setServiceChargeTemplateSubscriptions(ServiceChargeTemplateSubscriptionsDto serviceChargeTemplateSubscriptions) {
        this.serviceChargeTemplateSubscriptions = serviceChargeTemplateSubscriptions;
    }

    /**
     * Gets the service charge template terminations.
     *
     * @return the service charge template terminations
     */
    public ServiceChargeTemplateTerminationsDto getServiceChargeTemplateTerminations() {
        return serviceChargeTemplateTerminations;
    }

    /**
     * Sets the service charge template terminations.
     *
     * @param serviceChargeTemplateTerminations the new service charge template terminations
     */
    public void setServiceChargeTemplateTerminations(ServiceChargeTemplateTerminationsDto serviceChargeTemplateTerminations) {
        this.serviceChargeTemplateTerminations = serviceChargeTemplateTerminations;
    }

    /**
     * Gets the service charge template usages.
     *
     * @return the service charge template usages
     */
    public ServiceChargeTemplateUsagesDto getServiceChargeTemplateUsages() {
        return serviceChargeTemplateUsages;
    }

    /**
     * Sets the service charge template usages.
     *
     * @param serviceChargeTemplateUsages the new service charge template usages
     */
    public void setServiceChargeTemplateUsages(ServiceChargeTemplateUsagesDto serviceChargeTemplateUsages) {
        this.serviceChargeTemplateUsages = serviceChargeTemplateUsages;
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
     * Checks if is mandatory.
     *
     * @return true, if is mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * Gets the som code.
     *
     * @return the som code
     */
    public String getSomCode() {
        return somCode;
    }

    /**
     * Sets the som code.
     *
     * @param somCode the new som code
     */
    public void setSomCode(String somCode) {
        this.somCode = somCode;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && StringUtils.isBlank(invoicingCalendar) && StringUtils.isBlank(somCode) && serviceChargeTemplateRecurrings == null
                && serviceChargeTemplateSubscriptions == null && serviceChargeTemplateTerminations == null && serviceChargeTemplateUsages == null
                && (customFields == null || customFields.isEmpty());
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

    @Override
    public String toString() {
        return "ServiceTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", longDescription=" + longDescription + ", invoicingCalendar=" + invoicingCalendar
                + ", serviceChargeTemplateRecurrings=" + serviceChargeTemplateRecurrings + ", serviceChargeTemplateSubscriptions=" + serviceChargeTemplateSubscriptions
                + ", serviceChargeTemplateTerminations=" + serviceChargeTemplateTerminations + ", serviceChargeTemplateUsages=" + serviceChargeTemplateUsages + ", customFields="
                + customFields + ", mandatory=" + mandatory + ", somCode=" + somCode + ", imagePath=" + imagePath + "]";
    }

    /**
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    public SubscriptionRenewalDto getRenewalRule() {
        return renewalRule;
    }

    public void setRenewalRule(SubscriptionRenewalDto renewalRule) {
        this.renewalRule = renewalRule;
	}

    /**
     * @return the autoEndOfEngagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }

    /**
     * @return the minimumInvoiceSubCategory
     */
    public String getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * @param minimumInvoiceSubCategory the minimumInvoiceSubCategory to set
     */
    public void setMinimumInvoiceSubCategory(String minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }

    public String getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    public void setMinimumChargeTemplate(String minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
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
 


	/**
	 * @return the serviceTypeCode
	 */
	public String getServiceTypeCode() {
		return serviceTypeCode;
	}

	/**
	 * @param serviceTypeCode the serviceTypeCode to set
	 */
	public void setServiceTypeCode(String serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
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

	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

	/**
	 * @return the param
	 */
	public String getParam() {
		return param;
	}

	/**
	 * @param param the param to set
	 */
	public void setParam(String param) {
		this.param = param;
	}

	/**
	 * @return the medias
	 */
	public List<MediaDto> getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(List<MediaDto> medias) {
		this.medias = medias;
	}
	
	
	
	
    
}