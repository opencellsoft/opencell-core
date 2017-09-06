package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.customEntities.CustomEntityInstance;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "CustomEntityInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityInstanceDto implements Serializable {

    private static final long serialVersionUID = 9156372453581362595L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    @XmlAttribute(required = true)
    private String cetCode;

    private boolean disabled;

    private CustomFieldsDto customFields;

    public CustomEntityInstanceDto() {

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

    public String getCetCode() {
        return cetCode;
    }

    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    @Override
    public String toString() {
        return String.format("CustomEntityInstanceDto [code=%s, description=%s, cetCode=%s, disabled=%s, customFields=%s]", code, description, cetCode, disabled, customFields);
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Convert CustomEntityInstance entity to CustomEntityInstanceDto object including custom field values.
     * 
     * @param cei CustomEntityInstance entity to convert
     * @return CustomEntityInstanceDto object
     */
    public static CustomEntityInstanceDto toDTO(CustomEntityInstance cei, CustomFieldsDto customFieldInstances) {
        CustomEntityInstanceDto dto = new CustomEntityInstanceDto();

        dto.setCode(cei.getCode());
        dto.setCetCode(cei.getCetCode());
        dto.setDescription(cei.getDescription());
        dto.setDisabled(cei.isDisabled());

        dto.setCustomFields(customFieldInstances);

        return dto;
    }

    /**
     * Convert CustomEntityInstanceDto object to CustomEntityInstance object. Note: does not convert custom field values
     * 
     * @param dto CustomEntityInstanceDto to convert
     * @param ceiToUpdate CustomEntityInstance to update with values from dto, or if null create a new one
     * @return A new or updated CustomEntityInstance instance
     */
    public static CustomEntityInstance fromDTO(CustomEntityInstanceDto dto, CustomEntityInstance ceiToUpdate) {

        CustomEntityInstance cei = new CustomEntityInstance();
        if (ceiToUpdate != null) {
            cei = ceiToUpdate;
        }
        cei.setCode(dto.getCode());
        cei.setCetCode(dto.getCetCode());
        cei.setDescription(dto.getDescription());

        return cei;
    }
}