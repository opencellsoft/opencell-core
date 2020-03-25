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

package org.meveo.apiv2.services.generic.filter;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.apiv2.services.generic.filter.filtermapper.*;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public interface FactoryMapper {
    Set<String> simpleField = new HashSet<>(Arrays.asList("id", "type_class", "cfValues"));
    default FilterMapper create(String property, Object value, Class clazz, Function<Class, PersistenceService> entityManagerResolver) {
        if(simpleField.contains(property) || clazz != null && clazz.getSimpleName().equalsIgnoreCase(property)){
            return resolveFilterMapperType(property, value, clazz, entityManagerResolver);
        }
        Field field = FieldUtils.getField(clazz, property, true);
        if(field != null){
            if(List.class.isAssignableFrom(field.getType())){
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    Type[] pt = ((ParameterizedType) type).getActualTypeArguments();
                    return resolveFilterMapperType(property, value, (Class) pt[0], entityManagerResolver);
                }
            }
            return resolveFilterMapperType(property, value, field.getType(), entityManagerResolver);
        }
        throw new IllegalArgumentException("Invalid argument : " + property);
    }

    default FilterMapper resolveFilterMapperType(String property, Object value, Class clazz, Function<Class, PersistenceService> entityManagerResolver){
        // TODO : to switch
        if(value instanceof Map && ((Map) value).containsKey("id")){
            return new ReferenceMapper(property, ((Map) value).get("id"), clazz, entityManagerResolver);
        }
        if("cfValues".equalsIgnoreCase(property)){
            return new CustomFieldMapper(property, value, clazz, entityManagerResolver);
        }
        if(Date.class.isAssignableFrom(clazz)){
            return new DateMapper(property, value);
        }
        if(Number.class.isAssignableFrom(clazz) || "id".equalsIgnoreCase(property)){
            return new NumberMapper(property, value, clazz);
        }
        if(clazz.isEnum()){
            return new EnumMapper(property, value, clazz);
        }
        if(Auditable.class.isAssignableFrom(clazz)){
            return new AuditableMapper(property, value, clazz);
        }
        if("type_class".equalsIgnoreCase(property)){
            return new TypeClassMapper(property, value);
        }
        if(value instanceof Map || BaseEntity.class.isAssignableFrom(clazz)){
            return new ObjectMapper(property, value, clazz, entityManagerResolver);
        }
        return new DefaultMapper(property, value);
    }
}
