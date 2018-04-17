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
import org.meveo.model.crm.custom.EntityCustomAction;

/**
 * The Class EntityCustomizationDto.
 *
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */

@XmlRootElement(name = "EntityCustomization")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomizationDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5242092476533516746L;

    /** The classname. */
    @XmlAttribute(required = true)
    private String classname;

    /** The fields. */
    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<CustomFieldTemplateDto> fields;

    /** The actions. */
    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<EntityCustomActionDto> actions;

    /**
     * Instantiates a new entity customization dto.
     */
    public EntityCustomizationDto() {

    }

    /**
     * Gets the classname.
     *
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * Sets the classname.
     *
     * @param classname the new classname
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public List<CustomFieldTemplateDto> getFields() {
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields the new fields
     */
    public void setFields(List<CustomFieldTemplateDto> fields) {
        this.fields = fields;
    }

    /**
     * Gets the actions.
     *
     * @return the actions
     */
    public List<EntityCustomActionDto> getActions() {
        return actions;
    }

    /**
     * Sets the actions.
     *
     * @param actions the new actions
     */
    public void setActions(List<EntityCustomActionDto> actions) {
        this.actions = actions;
    }

    /**
     * Convert CustomEntityTemplate instance to CustomEntityTemplateDto object including the fields and actions.
     * 
     * @param clazz class
     * @param cetFields Fields (CustomFieldTemplate) that are part of CustomEntityTemplate
     * @param cetActions Actions (EntityActionScript) available on CustomEntityTemplate
     * @return A CustomEntityTemplateDto object with fields set
     */
    @SuppressWarnings("rawtypes")
    public static EntityCustomizationDto toDTO(Class clazz, Collection<CustomFieldTemplate> cetFields, Collection<EntityCustomAction> cetActions) {
        EntityCustomizationDto dto = new EntityCustomizationDto();
        dto.setClassname(clazz.getName());

        if (cetFields != null) {
            List<CustomFieldTemplateDto> fields = new ArrayList<>();
            for (CustomFieldTemplate cft : cetFields) {
                fields.add(new CustomFieldTemplateDto(cft));
            }
            dto.setFields(fields);
        }

        if (cetActions != null) {
            List<EntityCustomActionDto> actions = new ArrayList<>();
            for (EntityCustomAction action : cetActions) {
                actions.add(new EntityCustomActionDto(action));
            }
            dto.setActions(actions);
        }

        return dto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("EntityCustomizationDto [classname=%s, fields=%s, actions=%s]", classname, fields != null ? fields.subList(0, Math.min(fields.size(), maxLen)) : null,
            actions != null ? actions.subList(0, Math.min(actions.size(), maxLen)) : null);
    }
}