package org.meveo.model.persistence;

import java.util.Properties;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

public class JsonBinaryType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType {

    private static final long serialVersionUID = 2098848330584585140L;

    public JsonBinaryType() {
        super(JsonBinarySqlTypeDescriptor.INSTANCE, new JsonTypeDescriptor());
    }

    public String getName() {
        return "jsonb";
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((JsonTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }
}