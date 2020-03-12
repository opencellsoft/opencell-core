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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.service.base.PersistenceService;

import static org.meveo.apiv2.ValidationUtils.checkId;
import static org.meveo.apiv2.ValidationUtils.checkRecords;

public class GenericApiLoadService extends GenericApiService {

    public String findPaginatedRecords(Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFields) {
        PersistenceService persistenceService = getPersistenceService(entityClass);
        ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .data(checkRecords(persistenceService.list(searchConfig), entityClass.getSimpleName()))
                .limit(Long.valueOf(searchConfig.getNumberOfRows()))
                .offset(Long.valueOf(searchConfig.getFirstRow()))
                .total(getCount(searchConfig, entityClass))
                .build();
        return JsonGenericMapper.Builder.getBuilder()
                .withNestedEntities(new HashSet(searchConfig.getFetchFields())).build()
                .toJson(genericFields, entityClass, genericPaginatedResource);
    }

    private long getCount(PaginationConfiguration searchConfig, Class entityClass) {
        PersistenceService persistenceService = getPersistenceService(entityClass);
        return persistenceService.count(searchConfig);
    }

    public Optional<String> findByClassNameAndId(String entityName, Long id, PaginationConfiguration searchConfig, Set<String> genericFields) {
        checkId(id);
        Class entityClass = getEntityClass(entityName);
        PersistenceService persistenceService = getPersistenceService(entityClass);
        return Optional
                .ofNullable(JsonGenericMapper.Builder.getBuilder()
                        .withNestedEntities(new HashSet(searchConfig.getFetchFields())).build()
                        .toJson(genericFields, entityClass, Collections.singletonMap("data",persistenceService.findById(id, searchConfig.getFetchFields()))));
    }

}
