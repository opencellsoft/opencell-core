package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.scripts.EntityActionScript;

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

    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<EntityActionScriptDto> actions;

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

    public List<EntityActionScriptDto> getActions() {
        return actions;
    }

    public void setActions(List<EntityActionScriptDto> actions) {
        this.actions = actions;
    }

    /**
     * Convert CustomEntityTemplate instance to CustomEntityTemplateDto object including the fields and actions
     * 
     * @param cet CustomEntityTemplate object to convert
     * @param cetFields Fields (CustomFieldTemplate) that are part of CustomEntityTemplate
     * @param cetActions Actions (EntityActionScript) available on CustomEntityTemplate
     * @return A CustomEntityTemplateDto object with fields set
     */
    public static CustomEntityTemplateDto toDTO(CustomEntityTemplate cet, Collection<CustomFieldTemplate> cetFields, Collection<EntityActionScript> cetActions) {
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

        if (cetActions != null) {
            List<EntityActionScriptDto> actions = new ArrayList<EntityActionScriptDto>();
            for (EntityActionScript action : cetActions) {
                actions.add(new EntityActionScriptDto(action));
            }
            dto.setActions(actions);
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

    @Override
    public String toString() {
        return "CustomEntityTemplateDto [code=" + code + ", name=" + name + ", description=" + description + ", fields=" + fields + "]";
    }

}