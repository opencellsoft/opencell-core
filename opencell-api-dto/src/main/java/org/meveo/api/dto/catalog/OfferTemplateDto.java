package org.meveo.api.dto.catalog;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;



import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto extends BusinessDto {

    private static final long serialVersionUID = 9156372453581362595L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

	@XmlElement(required = true)
    private String name;

    private String longDescription;

    private boolean disabled = false;
    private String bomCode;
    private Date validFrom;
    private Date validTo;

    @Deprecated
    private String offerTemplateCategoryCode;

    @XmlElementWrapper(name = "offerTemplateCategories")
    @XmlElement(name = "offerTemplateCategory")
    private List<String> offerTemplateCategories;

    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    private List<OfferServiceTemplateDto> offerServiceTemplates;

    @XmlElementWrapper(name = "offerProductTemplates")
    @XmlElement(name = "offerProductTemplate")
    private List<OfferProductTemplateDto> offerProductTemplates;

    private CustomFieldsDto customFields = new CustomFieldsDto();
    
    /**
     * This field is populated on find and list. Use to pull the image from a servlet later on.
     */
    private String imagePath;
    private String imageBase64;

    public OfferTemplateDto() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "OfferTemplateDto [code=" + code + ", description=" + description + ", longDescription=" + longDescription + ", disabled=" + disabled + ", bomCode=" + bomCode
                + ", offerTemplateCategoryCode=" + offerTemplateCategoryCode + ", offerServiceTemplates=" + offerServiceTemplates + ", customFields=" + customFields + ", validFrom="+validFrom+", validTo="+validTo+"]";
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getBomCode() {
        return bomCode;
    }

    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

    public List<OfferServiceTemplateDto> getOfferServiceTemplates() {
        return offerServiceTemplates;
    }

    public void setOfferServiceTemplates(List<OfferServiceTemplateDto> offerServiceTemplates) {
        this.offerServiceTemplates = offerServiceTemplates;
    }

    public List<OfferProductTemplateDto> getOfferProductTemplates() {
        return offerProductTemplates;
    }

    public void setOfferProductTemplates(List<OfferProductTemplateDto> offerProductTemplates) {
        this.offerProductTemplates = offerProductTemplates;
    }

    public boolean isCodeOnly() {
        return StringUtils.isBlank(description) && StringUtils.isBlank(bomCode) && StringUtils.isBlank(offerTemplateCategoryCode)
                && (offerServiceTemplates == null || offerServiceTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    public void setOfferTemplateCategories(List<String> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
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
	
	

}