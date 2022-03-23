package org.meveo.apiv2.generic.services;

import static org.meveo.apiv2.generic.ValidationUtils.checkId;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.model.IEntity;

@Stateless
public class GenericApiLoadService {
    @Inject
    GenericOpencellRestful genericOpencellRestful;
    @Inject
    private GenericApiPersistenceDelegate persistenceDelegate;

    public String findPaginatedRecords(Boolean extractList, Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> fetchFields, Long nestedDepth) {
        SearchResult searchResult = persistenceDelegate.list(entityClass, searchConfig);

        ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                    .data(searchResult.getEntityList())
                .limit(Long.valueOf(searchConfig.getNumberOfRows()))
                .offset(Long.valueOf(searchConfig.getFirstRow()))
                .total(searchResult.getCount())
                    .filters(searchConfig.getFilters())
                .build();
        return JsonGenericMapper.Builder.getBuilder()
                .withExtractList(Objects.nonNull(extractList) ? extractList : genericOpencellRestful.shouldExtractList())
                .withNestedEntities(fetchFields)
                .withNestedDepth(nestedDepth)
                .build()
                .toJson(genericFields, entityClass, genericPaginatedResource);
    }

    public Optional<String> findByClassNameAndId(Boolean extractList, Class entityClass, Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities, Long nestedDepth) {
        checkId(id);
        IEntity iEntity = persistenceDelegate.find(entityClass, id, searchConfig.getFetchFields());

        return Optional
                .ofNullable(iEntity)
                .map(entity -> JsonGenericMapper.Builder.getBuilder()
                        .withExtractList(Objects.nonNull(extractList) ? extractList : genericOpencellRestful.shouldExtractList())
                        .withNestedEntities(nestedEntities)
                        .withNestedDepth(nestedDepth)
                        .build()
                        .toJson(genericFields, entityClass, Collections.singletonMap("data", entity)));
    }

}
