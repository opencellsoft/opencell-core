package org.meveo.apiv2.services.generic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;
import static org.meveo.apiv2.ValidationUtils.checkId;
import static org.meveo.apiv2.ValidationUtils.checkRecords;
import static org.meveo.apiv2.ValidationUtils.performOperationOnCondition;

@Stateless
public class GenericApiLoadService extends GenericApiService {
    private final JsonGenericMapper jsonDynamicSerializer = new JsonGenericMapper();

    public String findPaginatedRecords(Class entityClass, GenericPagingAndFiltering searchConfig) {
        PersistenceService persistenceService = getPersistenceService(entityClass);
        if(searchConfig.getOffset() == null){
            searchConfig.setOffset(0);
        }
        ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .data(checkRecords(persistenceService.list(getPaginationConfiguration(searchConfig)), entityClass.getSimpleName()))
                .limit(Long.valueOf(searchConfig.getLimit()))
                .offset(Long.valueOf(searchConfig.getOffset()))
                .total(getCount(searchConfig, entityClass))
                .build();
        return jsonDynamicSerializer.toJson(searchConfig.getGenericFields(), entityClass, genericPaginatedResource);
    }

    public long getCount(GenericPagingAndFiltering searchConfig, Class entityClass) {
        PaginationConfiguration paginationConfiguration = getPaginationConfiguration(searchConfig);
        PersistenceService persistenceService = getPersistenceService(entityClass);
        return persistenceService.count(paginationConfiguration);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering searchConfig) {
        if(searchConfig == null){
            searchConfig = new GenericPagingAndFiltering(null, null, null, 0,100,null,null);
        }
        List<String> fetchFields = Collections.EMPTY_LIST;
        if(searchConfig.getNestedEntities() != null && !searchConfig.getNestedEntities().isEmpty()){
            fetchFields= Arrays.asList(searchConfig.getNestedEntities().split(","));
        }
        return new PaginationConfiguration(searchConfig.getOffset(), searchConfig.getLimit(), searchConfig.getFilters(),
                searchConfig.getFullTextFilter(), fetchFields, searchConfig.getSortByOrDefault("id"),
                org.primefaces.model.SortOrder.valueOf(searchConfig.getSortOrderOrDefault(SortOrder.ASCENDING).name()));
    }

    public BaseEntity findByClassNameAndId(Class entityClass, Long id, GenericRequestDto requestDto) {
        checkId(id);
        jsonDynamicSerializer.toJson(requestDto.getFields(), entityClass, Collections.singletonMap("data",find(entityClass, id)));
        return find(entityClass, id);
    }

    public Class getEntityClass(String entityName) {
        checkEntityName(entityName);
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        return entityClass;
    }

}
