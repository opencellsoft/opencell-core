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
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * The Class CustomEntityTemplateDto.
 *
 * @author Andrius Karpavicius
 * @author Mbarek-Ay
 */

@XmlRootElement(name = "CustomEntityTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplateDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6633504145323452803L;

    /**
     * The name
     */
    @XmlAttribute(required = true)
    private String name;

    /**
     * Store as a separate table
     */
    @XmlAttribute
    private Boolean storeAsTable;

    /**
     * Store data in Elastic search
     */
    @XmlAttribute
    private Boolean storeInES;

    /** The fields. */
    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<CustomFieldTemplateDto> fields;

    /** The actions. */
    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    private List<EntityCustomActionDto> actions;

    @XmlAttribute
    private Boolean disableable;
    
    @XmlAttribute
    private Boolean versioned;
    /**
     * Instantiates a new custom entity template dto.
     */
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
        if (cet.isStoreAsTable()) {
            setStoreAsTable(true);
        }

        if (!cet.isStoreInES()) {
            setStoreInES(false);
        }

        if (!cet.isDisableable()) {
            setDisabled(false);
        }
        if (!cet.isVersioned()) {
            setVersioned(false);
        }
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

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Shall data be stored in a separate db table
     */
    public Boolean getStoreAsTable() {
        return storeAsTable;
    }

    /**
     * @param storeAsTable Shall data be stored in a separate db table
     */
    public void setStoreAsTable(Boolean storeAsTable) {
        this.storeAsTable = storeAsTable;
    }

    /**
     * @return Store data in Elastic search
     */
    public Boolean getStoreInES() {
        return storeInES;
    }

    /**
     * @param storeInES Store data in Elastic search
     */
    public void setStoreInES(Boolean storeInES) {
        this.storeInES = storeInES;
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
    
    

    public Boolean getDisableable() {
		return disableable;
	}

	public void setDisableable(Boolean disableable) {
		this.disableable = disableable;
	}

	public Boolean getVersioned() {
		return versioned;
	}

	public void setVersioned(Boolean versioned) {
		this.versioned = versioned;
	}

	/*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomEntityTemplateDto [code=" + code + ", name=" + name + ", description=" + description + ", fields=" + fields + "]";
    }
}