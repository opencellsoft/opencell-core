package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.customEntities.CustomEntityInstance;

/**
 * The Class CustomEntityInstanceDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomEntityInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityInstanceDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9156372453581362595L;

    /** The cet code. */
    @XmlAttribute(required = true)
    private String cetCode;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new custom entity instance dto.
     */
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

    /**
     * Gets the cet code.
     *
     * @return the cet code
     */
    public String getCetCode() {
        return cetCode;
    }

    /**
     * Sets the cet code.
     *
     * @param cetCode the new cet code
     */
    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    @Override
    public String toString() {
        return String.format("CustomEntityInstanceDto [code=%s, description=%s, cetCode=%s, disabled=%s, customFields=%s]", code, description, cetCode, isDisabled(), customFields);
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
}