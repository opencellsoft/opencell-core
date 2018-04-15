package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;

/**
 * The Class OfferTemplate4_1Dto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OfferTemplate4_1")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplate4_1Dto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9156372453581362595L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    @XmlAttribute()
    private String description;

    /** The disabled. */
    private boolean disabled = false;

    /** The service templates. */
    private ServiceTemplatesDto serviceTemplates = new ServiceTemplatesDto();

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The bom code. */
    private String bomCode;

    /** The offer template category code. */
    private String offerTemplateCategoryCode;

    /**
     * Instantiates a new offer template 4 1 dto.
     */
    public OfferTemplate4_1Dto() {

    }

    /**
     * Instantiates a new offer template 4 1 dto.
     *
     * @param e the e
     * @param customFieldInstances the custom field instances
     */
    public OfferTemplate4_1Dto(OfferTemplate e, CustomFieldsDto customFieldInstances) {
        code = e.getCode();
        description = e.getDescription();
        disabled = e.isDisabled();
        if (e.getBusinessOfferModel() != null) {
            bomCode = e.getBusinessOfferModel().getCode();
        }

        if (e.getOfferTemplateCategories() != null) {
            offerTemplateCategoryCode = e.getOfferTemplateCategories().get(0).getCode();
        }

        if (e.getOfferServiceTemplates() != null && e.getOfferServiceTemplates().size() > 0) {
            for (OfferServiceTemplate st : e.getOfferServiceTemplates()) {
                serviceTemplates.getServiceTemplate().add(new ServiceTemplateDto(st.getServiceTemplate()));
            }
        }

        customFields = customFieldInstances;
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
     * Checks if is disabled.
     *
     * @return true, if is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the new disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OfferTemplateDto [code=" + code + ", description=" + description + ", disabled=" + disabled + ", serviceTemplates=" + serviceTemplates + ", customFields="
                + customFields + ", bomCode=" + bomCode + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + "]";
    }

    /**
     * Gets the service templates.
     *
     * @return the service templates
     */
    public ServiceTemplatesDto getServiceTemplates() {
        return serviceTemplates;
    }

    /**
     * Sets the service templates.
     *
     * @param serviceTemplates the new service templates
     */
    public void setServiceTemplates(ServiceTemplatesDto serviceTemplates) {
        this.serviceTemplates = serviceTemplates;
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
     * Gets the offer template category code.
     *
     * @return the offer template category code
     */
    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    /**
     * Sets the offer template category code.
     *
     * @param offerTemplateCategoryCode the new offer template category code
     */
    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

}
