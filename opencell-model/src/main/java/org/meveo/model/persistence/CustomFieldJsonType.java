package org.meveo.model.persistence;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.meveo.model.crm.custom.CustomFieldValues;

public class CustomFieldJsonType extends AbstractSingleColumnStandardBasicType<CustomFieldValues> {

    private static final long serialVersionUID = -7393846020207110466L;

    public CustomFieldJsonType() {
        super(VarcharTypeDescriptor.INSTANCE, CustomFieldJsonTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return "cfjson";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}