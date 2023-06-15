/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
public class EntityCustomizationDto extends BaseEntityDto {

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
     * @param cetActions Actions (EntityActionScript) available on CustomEntityTemplate
     * @return A CustomEntityTemplateDto object with fields set
     */
    @SuppressWarnings("rawtypes")
    public static EntityCustomizationDto toDTO(Class clazz, Collection<EntityCustomAction> cetActions) {
        EntityCustomizationDto dto = new EntityCustomizationDto();
        dto.setClassname(clazz.getName());

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