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

package org.meveo.apiv2.services.generic;

import org.assertj.core.util.VisibleForTesting;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.ImmutableGenericPagingAndFiltering;
import org.meveo.apiv2.services.generic.filter.FactoryFilterMapper;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericRequestMapper {
    private final Class entityClass;
    private final Function<Class, PersistenceService> serviceFunction;
    public GenericRequestMapper(Class entityClass, Function<Class, PersistenceService> serviceFunction) {
        this.entityClass = entityClass;
        this.serviceFunction = serviceFunction;
    }

    public PaginationConfiguration mapTo(GenericPagingAndFiltering genericPagingAndFiltering){
        if(genericPagingAndFiltering == null){
            return getPaginationConfiguration(ImmutableGenericPagingAndFiltering.builder().build());
        }
        return getPaginationConfiguration(genericPagingAndFiltering);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering genericPagingAndFiltering) {
        return new PaginationConfiguration(genericPagingAndFiltering.getOffset().intValue(), genericPagingAndFiltering.getLimit().intValue(),
                evaluateFilters(genericPagingAndFiltering.getFilters(), entityClass), genericPagingAndFiltering.getFullTextFilter(),
                new ArrayList(genericPagingAndFiltering.getNestedEntities()), genericPagingAndFiltering.getSortBy(),
                org.primefaces.model.SortOrder.valueOf(genericPagingAndFiltering.getSortOrder()));
    }
    @VisibleForTesting
    public Map<String, Object> evaluateFilters(Map<String, Object> filters, Class<? extends BaseEntity> entity) {
        return Stream.of(filters.keySet().toArray())
                .map(key -> {
                    String keyObject = (String) key;
                    if(!"SQL".equalsIgnoreCase(keyObject) && !"$FILTER".equalsIgnoreCase(keyObject)){

                        String fieldName = keyObject.contains(" ") ? keyObject.substring(keyObject.indexOf(" ")).trim() : keyObject;
                        return Collections.singletonMap(keyObject, new FactoryFilterMapper().create(fieldName, filters.get(key), entity, serviceFunction).map());
                    }
                    return Collections.singletonMap(keyObject, filters.get(key));
                })
                .flatMap (map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
