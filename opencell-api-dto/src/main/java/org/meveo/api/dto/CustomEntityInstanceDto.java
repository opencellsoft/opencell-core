package org.meveo.api.dto;

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
public class CustomEntityInstanceDto extends EnableBusinessDto {

    private static final long serialVersionUID = 9156372453581362595L;

    @XmlAttribute(required = true)
    private String cetCode;

    private CustomFieldsDto customFields;

    public CustomEntityInstanceDto() {

    }

    /**
     * Construct CustomEntityInstanceDto from a CustomEntityInstance entity
     * 
     * @param cei CustomEntityInstance entity to convert
     * @param customFieldInstances custom field instances.
     */
    public CustomEntityInstanceDto(CustomEntityInstance cei, CustomFieldsDto customFieldInstances) {
        super(cei);

        setCetCode(cei.getCetCode());
        setCustomFields(customFieldInstances);
    }

    public String getCetCode() {
        return cetCode;
    }

    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    @Override
    public String toString() {
        return String.format("CustomEntityInstanceDto [code=%s, description=%s, cetCode=%s, disabled=%s, customFields=%s]", code, description, cetCode, isDisabled(), customFields);
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
}