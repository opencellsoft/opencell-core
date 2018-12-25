package org.meveo.model.filter;

import org.meveo.model.crm.custom.CustomFieldTypeEnum;

/**
 * @author Edward P. Legaspi
 **/
public enum FilterParameterTypeEnum {

    BIG_DECIMAL("cfDecimal", CustomFieldTypeEnum.DOUBLE), BOOLEAN("cfBoolean", CustomFieldTypeEnum.LIST), DATE("cfDate", CustomFieldTypeEnum.DATE), ENTITY("cfEntity",
            CustomFieldTypeEnum.ENTITY), ENUM("cfEnum", CustomFieldTypeEnum.LIST), INTEGER("cfInteger",
                    CustomFieldTypeEnum.LONG), LONG("cfLong", CustomFieldTypeEnum.LONG), STRING("cfString", CustomFieldTypeEnum.STRING);

    private String prefix;
    private CustomFieldTypeEnum fieldType;

    private FilterParameterTypeEnum(String prefix, CustomFieldTypeEnum fieldType) {
        this.prefix = prefix;
        this.fieldType = fieldType;
    }

    public String getPrefix() {
        return prefix;
    }

    public CustomFieldTypeEnum getFieldType() {
        return fieldType;
    }

    public boolean matchesPrefixOf(String prefix) {
        return prefix != null && prefix.startsWith(this.prefix);
    }

}
