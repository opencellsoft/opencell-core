/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

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
