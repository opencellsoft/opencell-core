package org.meveo.apiv2.generic.services;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.model.IEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.meveo.apiv2.generic.ValidationUtils.checkId;
import static org.meveo.apiv2.generic.ValidationUtils.checkRecords;

@Stateless
public class GenericApiLoadService {

    @Inject
    private GenericApiPersistenceDelegate persistenceDelegate;

    public String findPaginatedRecords(Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> fetchFields, Long nestedDepth) {
        SearchResult searchResult = persistenceDelegate.list(entityClass, searchConfig);

        ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .data(checkRecords(searchResult.getEntityList(), entityClass.getSimpleName()))
                .limit(Long.valueOf(searchConfig.getNumberOfRows()))
                .offset(Long.valueOf(searchConfig.getFirstRow()))
                .total(searchResult.getCount())
                .build();
        return JsonGenericMapper.Builder.getBuilder()
                .withNestedEntities(fetchFields)
                .withNestedDepth(nestedDepth)
                .build()
                .toJson(genericFields, entityClass, genericPaginatedResource);
    }

    public Optional<String> findByClassNameAndId(Class entityClass, Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities, Long nestedDepth) {
        checkId(id);
        IEntity iEntity = persistenceDelegate.find(entityClass, id, searchConfig.getFetchFields());

        return Optional
                .ofNullable(iEntity)
                .map(entity -> JsonGenericMapper.Builder.getBuilder()
                        .withNestedEntities(nestedEntities)
                        .withNestedDepth(nestedDepth)
                        .build()
                        .toJson(genericFields, entityClass, Collections.singletonMap("data", entity)));
    }

}
