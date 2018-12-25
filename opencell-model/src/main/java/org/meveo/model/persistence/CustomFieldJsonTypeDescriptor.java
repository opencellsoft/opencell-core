package org.meveo.model.persistence;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

public class CustomFieldJsonTypeDescriptor extends AbstractTypeDescriptor<Object> implements DynamicParameterizedType {

    private static final long serialVersionUID = -5030465106663645694L;

    private Class<?> jsonObjectClass;

    @Override
    public void setParameterValues(Properties parameters) {
        jsonObjectClass = ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();

    }

    @SuppressWarnings("serial")
    public CustomFieldJsonTypeDescriptor() {
        super(Object.class, new MutableMutabilityPlan<Object>() {
            @Override
            protected Object deepCopyNotNull(Object value) {
                return JacksonUtil.clone(value);
            }
        });
    }

    @Override
    public boolean areEqual(Object one, Object another) {

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("CF json compare {}", one);
        // log.error("CF json compare to {}", another);

        if (one == another) {
            return true;
            // } else if (one == null || another == null) {
            // return false;
        }

        String json = one != null ? one.toString() : null;
        String json2 = another != null ? another.toString() : null;

        if (json == null && json2 == null) {
            return true;
        } else if (json == null || json2 == null) {
            return false;
        } else {
            return json.equals(json2);
        }
    }

    @Override
    public String toString(Object value) {
        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK Json converter to string");

        if (value == null) {
            return null;
        }

        if (value instanceof CustomFieldValues) {
            return ((CustomFieldValues) value).asJson();

        } else {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Value of type {} can not be converted to CustomFieldValues type. Value: {}", value.getClass().getSimpleName(), value);

            throw new RuntimeException(
                "cfjson field type is for customFieldValues field only. Value of type " + value.getClass().getSimpleName() + " can not be converted to CustomFieldValues type");
        }

    }

    @Override
    public Object fromString(String string) {

        if (string == null) {
            return null;
        }

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK Json converter from string");

        Map<String, List<CustomFieldValue>> cfValues = JacksonUtil.fromString(string, new TypeReference<Map<String, List<CustomFieldValue>>>() {
        });

        // if (cfValues == null || cfValues.isEmpty()) {
        // return null;
        // } else {
        return new CustomFieldValues(cfValues);
        // }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <X> X unwrap(Object value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        if (Object.class.isAssignableFrom(type)) {
            return (X) JacksonUtil.toJsonNode(toString(value));
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> Object wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        return fromString(value.toString());
    }
}