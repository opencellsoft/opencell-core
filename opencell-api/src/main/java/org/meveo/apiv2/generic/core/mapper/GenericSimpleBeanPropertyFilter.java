package org.meveo.apiv2.generic.core.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.meveo.commons.utils.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

class GenericSimpleBeanPropertyFilter extends SimpleBeanPropertyFilter.FilterExceptFilter {
    private final Set<String> GenericSimpleBeanPropertyFilterPropertiesToInclude;
    public GenericSimpleBeanPropertyFilter(Set<String> properties) {
        super(properties);
        GenericSimpleBeanPropertyFilterPropertiesToInclude = properties;
    }

    @Override
    protected boolean include(BeanPropertyWriter writer) {
        return super.include(writer);
    }

    @Override
    protected boolean include(PropertyWriter writer) {
        return this.include(null, writer);
    }

    private boolean include(Object pojo, PropertyWriter writer) {
        if(GenericSimpleBeanPropertyFilterPropertiesToInclude.isEmpty()){
            return true;
        }
        String fullDeclaringClassName = pojo != null ? pojo.getClass().getName() : writer.getMember().getDeclaringClass().getName();
        String declaringClassName = fullDeclaringClassName.substring(fullDeclaringClassName.lastIndexOf(".") + 1);
        String fieldPattern = declaringClassName +"."+ writer.getName();
        if(Modifier.isAbstract(writer.getMember().getDeclaringClass().getModifiers())) {
        	fieldPattern = writer.getName();
        }
        Object[] propsToInclude = GenericSimpleBeanPropertyFilterPropertiesToInclude.toArray();
        for(int i = 0; i< propsToInclude.length; i++){
            String lowerCase = ((String) propsToInclude[i]).toLowerCase();
            if ( lowerCase.endsWith(fieldPattern.toLowerCase()) || Arrays.asList("id","code","description").contains(writer.getName())) {
                return true;
            }
        }
        return false;
    }

    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (this.include(pojo, writer)) {
            writer.serializeAsField(pojo, jgen, provider);
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
}
