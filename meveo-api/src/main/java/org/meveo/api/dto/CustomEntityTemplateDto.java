package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * @author Andrius Karpavicius
 **/

@XmlRootElement(name = "CustomEntityTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplateDto extends BaseDto {

    private static final long serialVersionUID = -6633504145323452803L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute(required = false)
    private String description;

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<CustomFieldTemplateDto> fields;

    public CustomEntityTemplateDto() {

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CustomFieldTemplateDto> getFields() {
        return fields;
    }

    public void setFields(List<CustomFieldTemplateDto> fields) {
        this.fields = fields;
    }

    /**
     * Convert CustomEntityTemplate instance to CustomEntityTemplateDto object including the fields
     * 
     * @param cet CustomEntityTemplate object to convert
     * @param cetFields Fields (CustomFieldTemplate) that are part of CustomEntityTemplate
     * @return A CustomEntityTemplateDto object with fields set
     */
    public static CustomEntityTemplateDto toDTO(CustomEntityTemplate cet, List<CustomFieldTemplate> cetFields) {
        CustomEntityTemplateDto dto = new CustomEntityTemplateDto();
        dto.setCode(cet.getCode());
        dto.setName(cet.getName());
        dto.setDescription(cet.getDescription());

        if (cetFields != null) {
            List<CustomFieldTemplateDto> fields = new ArrayList<CustomFieldTemplateDto>();
            for (CustomFieldTemplate cft : cetFields) {
                fields.add(new CustomFieldTemplateDto(cft));
            }
            dto.setFields(fields);
        }

        return dto;
    }

    /**
     * Convert CustomEntityTemplateDto to a CustomEntityTemplate instance. Note: does not convert custom fields that are part of DTO
     * 
     * @param dto CustomEntityTemplateDto object to convert
     * @param cetToUpdate CustomEntityTemplate to update with values from dto, or if null create a new one
     * @return A new or updated CustomEntityTemplate instance
     */
    public static CustomEntityTemplate fromDTO(CustomEntityTemplateDto dto, CustomEntityTemplate cetToUpdate) {
        CustomEntityTemplate cet = new CustomEntityTemplate();
        if (cetToUpdate != null) {
            cet = cetToUpdate;
        }
        cet.setCode(dto.getCode());
        cet.setName(dto.getName());
        cet.setDescription(dto.getDescription());

        return cet;
    }
}