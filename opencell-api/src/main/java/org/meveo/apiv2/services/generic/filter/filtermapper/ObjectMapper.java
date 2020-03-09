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

package org.meveo.apiv2.services.generic.filter.filtermapper;


import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.apiv2.services.generic.filter.FactoryFilterMapper;
import org.meveo.apiv2.services.generic.filter.FilterMapper;
import org.meveo.service.base.PersistenceService;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class ObjectMapper extends FilterMapper {
    private final Class<?> type;
    private final Function<Class, PersistenceService> serviceFunction;

    public ObjectMapper(String property, Object value, Class<?> type, Function<Class, PersistenceService> serviceFunction) {
        super(property, value);
        this.type = type;
        this.serviceFunction = serviceFunction;
    }

    @Override
    public Object mapStrategy(Object value) {
        Object target = null;
        try {
            if(value instanceof Map && !((Map) value).containsKey("id")){
                final Object targetInstanceHolder = type.newInstance();
                Map<String, Object> innerValue = ((Map) value);
                innerValue.keySet()
                        .stream()
                        .map(key -> Collections.singletonMap(key, new FactoryFilterMapper().create(key, innerValue.get(key), type, serviceFunction).map()))
                        .flatMap(entries -> entries.entrySet().stream())
                        .forEach(entry -> {
                            try {
                                FieldUtils.writeField(targetInstanceHolder, entry.getKey(), entry.getValue(), true);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                target = targetInstanceHolder;
            } else if(((Map) value).containsKey("id")){
                target = new FactoryFilterMapper().create("id", ((Map) value).get("id"), type, serviceFunction).map();
            }else{
                target = new FactoryFilterMapper().create("id", value, type, serviceFunction).map();
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return target;
    }
}
