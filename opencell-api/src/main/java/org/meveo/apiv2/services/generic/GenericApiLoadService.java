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

    public String findPaginatedRecords(Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> fetchFields) {
        PersistenceService persistenceService = getPersistenceService(entityClass);
        ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .data(checkRecords(persistenceService.list(searchConfig), entityClass.getSimpleName()))
                .limit(Long.valueOf(searchConfig.getNumberOfRows()))
                .offset(Long.valueOf(searchConfig.getFirstRow()))
                .total(getCount(searchConfig, entityClass))
                .build();
        return JsonGenericMapper.Builder.getBuilder()
                .withNestedEntities(fetchFields).build()
                .toJson(genericFields, entityClass, genericPaginatedResource);
    }

    private long getCount(PaginationConfiguration searchConfig, Class entityClass) {
        PersistenceService persistenceService = getPersistenceService(entityClass);
        return persistenceService.count(searchConfig);
    }

    public Optional<String> findByClassNameAndId(String entityName, Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
        checkId(id);
        Class entityClass = getEntityClass(entityName);
        PersistenceService persistenceService = getPersistenceService(entityClass);
        return Optional
                .ofNullable(JsonGenericMapper.Builder.getBuilder()
                        .withNestedEntities(nestedEntities).build()
                        .toJson(genericFields, entityClass, Collections.singletonMap("data",persistenceService.findById(id, searchConfig.getFetchFields()))));
    }

}
