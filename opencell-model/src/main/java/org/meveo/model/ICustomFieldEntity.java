package org.meveo.model;

import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * An entity that contains custom fields
 * 
 * @author Andrius Karpavicius
 * 
 */
public interface ICustomFieldEntity {

    /**
     * Get unique identifier
     * 
     * @return
     */
    public String getUuid();

    /**
     * Set a new UUID value
     * 
     * @return Old UUID value
     */
    public String clearUuid();

    /**
     * Get an array of parent custom field entity in case custom field values should be inherited from a parent entity
     * 
     * @return An entity
     */
    public ICustomFieldEntity[] getParentCFEntities();
    
    public CustomFieldValues getCfValues();
    
    public CustomFieldValues getCfValuesNullSafe();
    
    public void clearCfValues();
}