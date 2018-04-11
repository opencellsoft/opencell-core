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
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * @author Andrius Karpavicius
 **/

@XmlRootElement(name = "CustomEntityTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplateDto extends EnableBusinessDto {

    private static final long serialVersionUID = -6633504145323452803L;

    @XmlAttribute(required = true)
    private String name;

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<CustomFieldTemplateDto> fields;

    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<EntityCustomActionDto> actions;

    public CustomEntityTemplateDto() {

    }

    /**
     * Convert CustomEntityTemplate instance to CustomEntityTemplateDto object including the fields and actions
     * 
     * @param cet CustomEntityTemplate object to convert
     * @param cetFields Fields (CustomFieldTemplate) that are part of CustomEntityTemplate
     * @param cetActions Actions (EntityActionScript) available on CustomEntityTemplate
     */
    public CustomEntityTemplateDto(CustomEntityTemplate cet, Collection<CustomFieldTemplate> cetFields, Collection<EntityCustomAction> cetActions) {
        super(cet);

        setName(cet.getName());

        if (cetFields != null) {
            List<CustomFieldTemplateDto> fields = new ArrayList<CustomFieldTemplateDto>();
            for (CustomFieldTemplate cft : cetFields) {
                fields.add(new CustomFieldTemplateDto(cft));
            }
            setFields(fields);
        }

        if (cetActions != null) {
            List<EntityCustomActionDto> actions = new ArrayList<EntityCustomActionDto>();
            for (EntityCustomAction action : cetActions) {
                actions.add(new EntityCustomActionDto(action));
            }
            setActions(actions);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomFieldTemplateDto> getFields() {
        return fields;
    }

    public void setFields(List<CustomFieldTemplateDto> fields) {
        this.fields = fields;
    }

    public List<EntityCustomActionDto> getActions() {
        return actions;
    }

    public void setActions(List<EntityCustomActionDto> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "CustomEntityTemplateDto [code=" + code + ", name=" + name + ", description=" + description + ", fields=" + fields + "]";
    }
}