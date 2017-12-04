package org.meveo.service.catalog.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.custom.CustomFieldValue;

public class BOMInstantiationParameters {

	private BusinessOfferModel businessOfferModel;
	private List<CustomFieldDto> customFields;
	private String code;
	private String name;
	private String offerDescription;
	private List<ServiceConfigurationDto> serviceCodes;
	private List<ServiceConfigurationDto> productCodes;
	private List<Channel> channels;
	private List<BusinessAccountModel> bams;
	private List<OfferTemplateCategory> offerTemplateCategories;
	private LifeCycleStatusEnum lifeCycleStatusEnum;
	private String imagePath;
	private Date validFrom;
	private Date validTo;
	private Map<String, String> descriptionI18n;
	private String longDescription;
	private Map<String, String> longDescriptionI18n;
	private Map<String, List<CustomFieldValue>> offerCfValue;

	public BusinessOfferModel getBusinessOfferModel() {
		return businessOfferModel;
	}

	public void setBusinessOfferModel(BusinessOfferModel businessOfferModel) {
		this.businessOfferModel = businessOfferModel;
	}

	public List<CustomFieldDto> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldDto> customFields) {
		this.customFields = customFields;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOfferDescription() {
		return offerDescription;
	}

	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}

	public List<ServiceConfigurationDto> getServiceCodes() {
		return serviceCodes;
	}

	public void setServiceCodes(List<ServiceConfigurationDto> serviceCodes) {
		this.serviceCodes = serviceCodes;
	}

	public List<ServiceConfigurationDto> getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(List<ServiceConfigurationDto> productCodes) {
		this.productCodes = productCodes;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	public List<BusinessAccountModel> getBams() {
		return bams;
	}

	public void setBams(List<BusinessAccountModel> bams) {
		this.bams = bams;
	}

	public List<OfferTemplateCategory> getOfferTemplateCategories() {
		return offerTemplateCategories;
	}

	public void setOfferTemplateCategories(List<OfferTemplateCategory> offerTemplateCategories) {
		this.offerTemplateCategories = offerTemplateCategories;
	}

	public LifeCycleStatusEnum getLifeCycleStatusEnum() {
		return lifeCycleStatusEnum;
	}

	public void setLifeCycleStatusEnum(LifeCycleStatusEnum lifeCycleStatusEnum) {
		this.lifeCycleStatusEnum = lifeCycleStatusEnum;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Map<String, String> getDescriptionI18n() {
		return descriptionI18n;
	}

	public void setDescriptionI18n(Map<String, String> descriptionI18n) {
		this.descriptionI18n = descriptionI18n;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public Map<String, String> getLongDescriptionI18n() {
		return longDescriptionI18n;
	}

	public void setLongDescriptionI18n(Map<String, String> longDescriptionI18n) {
		this.longDescriptionI18n = longDescriptionI18n;
	}

	public Map<String, List<CustomFieldValue>> getOfferCfValue() {
		return offerCfValue;
	}

	public void setOfferCfValue(Map<String, List<CustomFieldValue>> offerCfValue) {
		this.offerCfValue = offerCfValue;
	}

}
