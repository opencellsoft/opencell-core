package org.meveo.model.persistence;

import java.util.List;
import java.util.Map;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import com.fasterxml.jackson.core.type.TypeReference;

public class CustomFieldJsonTypeDescriptor extends AbstractTypeDescriptor<CustomFieldValues> {

    private static final long serialVersionUID = -5030465106663645694L;

    public static final CustomFieldJsonTypeDescriptor INSTANCE = new CustomFieldJsonTypeDescriptor();

    @SuppressWarnings("unchecked")
    public CustomFieldJsonTypeDescriptor() {

        super(CustomFieldValues.class, ImmutableMutabilityPlan.INSTANCE);

    }

    @Override
    public String toString(CustomFieldValues value) {
        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK Json converter to string");

        if (value == null) {
            return null;
        }

        return ((CustomFieldValues) value).asJson();

    }

    @Override
    public CustomFieldValues fromString(String string) {

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

    @Override
    public <X> X unwrap(CustomFieldValues value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        } else if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> CustomFieldValues wrap(X value, WrapperOptions options) {

        if (value == null) {
            return null;
        } else if (String.class.isInstance(value)) {
            return fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }
}