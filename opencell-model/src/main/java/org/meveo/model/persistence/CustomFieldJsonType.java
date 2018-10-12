package org.meveo.model.persistence;

import java.util.Properties;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

public class CustomFieldJsonType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType {

    private static final long serialVersionUID = -7393846020207110466L;

    public CustomFieldJsonType() {
        super(JsonStringSqlTypeDescriptor.INSTANCE, new CustomFieldJsonTypeDescriptor());
    }

    public String getName() {
        return "cfjson";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((CustomFieldJsonTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }
}