package org.meveo.api.dto.catalog;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.catalog.LifeCycleStatusEnum;

@XmlRootElement(name = "BomOffer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomOfferDto extends BaseDto {

    private static final long serialVersionUID = 4557706201829891403L;

    @NotNull
    @XmlAttribute(required = true)
    private String bomCode;

    @NotNull
    @XmlAttribute(required = true)
    private String code;

    @NotNull
    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute
    private String description;

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;

    @Deprecated
    private String prefix;

    @XmlElementWrapper(name = "servicesToActivate")
    @XmlElement(name = "serviceToActivate")
    private List<ServiceConfigurationDto> servicesToActivate;

    @XmlElementWrapper(name = "productsToActivate")
    @XmlElement(name = "productToActivate")
    private List<ServiceConfigurationDto> productsToActivate;

    @XmlElementWrapper(name = "businessServiceModels")
    @XmlElement(name = "businessServiceModel")
    private List<BSMConfigurationDto> businessServiceModels;

    private LifeCycleStatusEnum lifeCycleStatusEnum;

    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    private List<OfferTemplateCategoryDto> offerTemplateCategories;

    public String getBomCode() {
        return bomCode;
    }

    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ServiceConfigurationDto> getServicesToActivate() {
        return servicesToActivate;
    }

    public void setServicesToActivate(List<ServiceConfigurationDto> servicesToActivate) {
        this.servicesToActivate = servicesToActivate;
    }

    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    @Override
    public String toString() {
        return "BomOfferDto [bomCode=" + bomCode + ", code=" + code + ", name=" + name + ", description=" + description + ", customFields=" + customFields + ", prefix=" + prefix
                + ", servicesToActivate=" + servicesToActivate + ", productsToActivate=" + productsToActivate + ", businessServiceModels=" + businessServiceModels
                + ", lifeCycleStatusEnum=" + lifeCycleStatusEnum + ", offerTemplateCategories=" + offerTemplateCategories + "]";
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

    public List<ServiceConfigurationDto> getProductsToActivate() {
        return productsToActivate;
    }

    public void setProductsToActivate(List<ServiceConfigurationDto> productsToActivate) {
        this.productsToActivate = productsToActivate;
    }

    public List<BSMConfigurationDto> getBusinessServiceModels() {
        return businessServiceModels;
    }

    public void setBusinessServiceModels(List<BSMConfigurationDto> businessServiceModels) {
        this.businessServiceModels = businessServiceModels;
    }

    public LifeCycleStatusEnum getLifeCycleStatusEnum() {
        return lifeCycleStatusEnum;
    }

    public void setLifeCycleStatusEnum(LifeCycleStatusEnum lifeCycleStatusEnum) {
        this.lifeCycleStatusEnum = lifeCycleStatusEnum;
    }

    public List<OfferTemplateCategoryDto> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    public void setOfferTemplateCategories(List<OfferTemplateCategoryDto> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

}
