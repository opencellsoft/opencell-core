package org.meveo.apiv2.generic.core.mapper;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.Arrays;
import java.util.Set;

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
        if(GenericSimpleBeanPropertyFilterPropertiesToInclude.isEmpty()){
            return true;
        }
        String fullDeclaringClassName = writer.getMember().getDeclaringClass().getName();
        String declaringClassName = fullDeclaringClassName.substring(fullDeclaringClassName.lastIndexOf(".") + 1);
        String fieldPattern = declaringClassName +"."+ writer.getName();
        Object[] propsToInclude = GenericSimpleBeanPropertyFilterPropertiesToInclude.toArray();
        for(int i = 0; i< propsToInclude.length; i++){
            String lowerCase = ((String) propsToInclude[i]).toLowerCase();
            if (lowerCase.equalsIgnoreCase(fieldPattern) || Arrays.asList("id","code","description").contains(writer.getName())) {
                return true;
            }
        }
        return false;
    }
}
