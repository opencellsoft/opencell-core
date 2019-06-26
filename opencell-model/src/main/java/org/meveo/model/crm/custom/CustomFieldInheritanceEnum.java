package org.meveo.model.crm.custom;

/**
 * How Custom field values should be retrieved for API find/list results
 * 
 * @author Andrius Karpavicius
 *
 */
public enum CustomFieldInheritanceEnum {
    /**
     * Only entity's own custom field values are retrieved.
     * 
     * CustomFieldsDto.customField = entity's custom field values
     */
    INHERIT_NONE,

    /**
     * In addition to entity's own custom field values, custom field values from parent entities as retrieved as separate custom field values
     * 
     * CustomFieldsDto.customField = entity's custom field values, CustomFieldsDto.inheritedCustomField = parent entity's custom field values
     * 
     */
    INHERIT_NO_MERGE,

    /**
     * Entity's own custom field values are merged with custom field values from parent entities (map and matrix type). In addition custom field values from parent entities as
     * retrieved as separate custom field values.
     * 
     * CustomFieldsDto.customField = entity's custom field values + parent entity's custom field values, CustomFieldsDto.inheritedCustomField = parent entity's custom field values
     * 
     */
    INHERIT_MERGED,

    /**
     * Entity's own custom field values are NOT merged with parent entities custom field values. Inherited custom field values are entity's own custom field values merged with
     * parent entities custom field values
     * 
     * CustomFieldsDto.customField = entity's custom field values, CustomFieldsDto.inheritedCustomField = entity's custom field values + parent entity's custom field values
     * 
     */
    ACCUMULATED;

    /**
     * Match inheritance type
     * 
     * @param inherit Should inherited values be retrieved
     * @param merge Should inherited values be merged
     * @return Matched inheritance type
     */
    public static CustomFieldInheritanceEnum getInheritCF(boolean inherit, boolean merge) {
        if (inherit && merge) {
            return INHERIT_MERGED;
        } else if (inherit) {
            return INHERIT_NO_MERGE;
        } else {
            return INHERIT_NONE;
        }
    }
}
