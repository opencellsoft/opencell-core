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

import org.meveo.apiv2.services.generic.filter.FilterMapper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuditableMapper extends FilterMapper {
    public AuditableMapper(String property, Object value, Class clazz) {
        super(property, value);
    }

    @Override
    public Object mapStrategy(Object value) {
        Map valueMap = (Map) value;
        Map<String, Object> mappedValue = new HashMap<>();
        if(valueMap.containsKey("created")){
            mappedValue.put("created", new DateMapper("created", valueMap.get("created")).map());
        }
        if(valueMap.containsKey("updated")){
            mappedValue.put("updated", new DateMapper("updated", valueMap.get("updated")).map());
        }
        if(mappedValue.entrySet().isEmpty()){
            throw new IllegalArgumentException("Invalid parameter exception : " + property);
        }
        return mappedValue;
    }
}
