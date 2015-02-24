package org.meveo.model;

import java.util.Map;

import org.meveo.model.crm.CustomFieldInstance;

/**
 * An entity that contains custom fields
 * 
 * @author Andrius Karpavicius
 * 
 */
public interface ICustomFieldEntity {

    public Map<String, CustomFieldInstance> getCustomFields();

    public void setCustomFields(Map<String, CustomFieldInstance> customFields);

}