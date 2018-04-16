package org.meveo.api.dto.catalog;

import java.util.ArrayList;
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

/**
 * The Class BomOfferDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BomOffer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomOfferDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4557706201829891403L;

    /** The bom code. */
    @NotNull
    @XmlAttribute(required = true)
    private String bomCode;

    /** The code. */
    @NotNull
    @XmlAttribute(required = true)
    private String code;

    /** The name. */
    @NotNull
    @XmlAttribute(required = true)
    private String name;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The custom fields. */
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CustomFieldDto> customFields;

    /** The prefix. */
    @Deprecated
    private String prefix;

    /** The services to activate. */
    @XmlElementWrapper(name = "servicesToActivate")
    @XmlElement(name = "serviceToActivate")
    private List<ServiceConfigurationDto> servicesToActivate = new ArrayList<>();

    /** The products to activate. */
    @XmlElementWrapper(name = "productsToActivate")
    @XmlElement(name = "productToActivate")
    private List<ServiceConfigurationDto> productsToActivate = new ArrayList<>();

    /** The business service models. */
    @XmlElementWrapper(name = "businessServiceModels")
    @XmlElement(name = "businessServiceModel")
    private List<BSMConfigurationDto> businessServiceModels = new ArrayList<>();

    /** The life cycle status enum. */
    private LifeCycleStatusEnum lifeCycleStatusEnum;

    /** The offer template categories. */
    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    private List<OfferTemplateCategoryDto> offerTemplateCategories;

    /**
     * Gets the bom code.
     *
     * @return the bom code
     */
    public String getBomCode() {
        return bomCode;
    }

    /**
     * Sets the bom code.
     *
     * @param bomCode the new bom code
     */
    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    /**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix the new prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the services to activate.
     *
     * @return the services to activate
     */
    public List<ServiceConfigurationDto> getServicesToActivate() {
        return servicesToActivate;
    }

    /**
     * Sets the services to activate.
     *
     * @param servicesToActivate the new services to activate
     */
    public void setServicesToActivate(List<ServiceConfigurationDto> servicesToActivate) {
        this.servicesToActivate = servicesToActivate;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public List<CustomFieldDto> getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the products to activate.
     *
     * @return the products to activate
     */
    public List<ServiceConfigurationDto> getProductsToActivate() {
        return productsToActivate;
    }

    /**
     * Sets the products to activate.
     *
     * @param productsToActivate the new products to activate
     */
    public void setProductsToActivate(List<ServiceConfigurationDto> productsToActivate) {
        this.productsToActivate = productsToActivate;
    }

    /**
     * Gets the business service models.
     *
     * @return the business service models
     */
    public List<BSMConfigurationDto> getBusinessServiceModels() {
        return businessServiceModels;
    }

    /**
     * Sets the business service models.
     *
     * @param businessServiceModels the new business service models
     */
    public void setBusinessServiceModels(List<BSMConfigurationDto> businessServiceModels) {
        this.businessServiceModels = businessServiceModels;
    }

    /**
     * Gets the life cycle status enum.
     *
     * @return the lifeCycleStatusEnum
     */
    public LifeCycleStatusEnum getLifeCycleStatusEnum() {
        return lifeCycleStatusEnum;
    }

    /**
     * Sets the life cycle status enum.
     *
     * @param lifeCycleStatusEnum the lifeCycleStatusEnum to set
     */
    public void setLifeCycleStatusEnum(LifeCycleStatusEnum lifeCycleStatusEnum) {
        this.lifeCycleStatusEnum = lifeCycleStatusEnum;
    }

    /**
     * Gets the offer template categories.
     *
     * @return the offerTemplateCategories
     */
    public List<OfferTemplateCategoryDto> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    /**
     * Sets the offer template categories.
     *
     * @param offerTemplateCategories the offerTemplateCategories to set
     */
    public void setOfferTemplateCategories(List<OfferTemplateCategoryDto> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    @Override
    public String toString() {
        return "BomOfferDto [bomCode=" + bomCode + ", code=" + code + ", name=" + name + ", description=" + description + ", customFields=" + customFields + ", prefix=" + prefix
                + ", servicesToActivate=" + servicesToActivate + ", productsToActivate=" + productsToActivate + ", businessServiceModels=" + businessServiceModels
                + ", lifeCycleStatusEnum=" + lifeCycleStatusEnum + ", offerTemplateCategories=" + offerTemplateCategories + "]";
    }

}