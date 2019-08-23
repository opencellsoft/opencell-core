package org.meveo.apiv2.services.generic;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.Embedded;
import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.service.base.PersistenceService;
import org.reflections.Reflections;

import static org.meveo.apiv2.ValidationUtils.checkId;
import static org.meveo.apiv2.ValidationUtils.checkRecords;

@Stateless
public class GenericApiLoadService extends GenericApiService {
    private final JsonGenericMapper jsonDynamicSerializer = new JsonGenericMapper();
    public String findPaginatedRecords(String entityName, GenericPagingAndFiltering searchConfig) {
        Class entityClass = getEntityClass(entityName);
        PersistenceService persistenceService = getPersistenceService(entityClass);
        if(searchConfig == null){
            searchConfig = new GenericPagingAndFiltering(null, null, null, 0,100,null,null);
        } else if(searchConfig.getOffset() == null){
            searchConfig.setOffset(0);
        }
        PaginationConfiguration paginationConfiguration = getPaginationConfiguration(searchConfig, entityClass);
        ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .data(checkRecords(persistenceService.list(paginationConfiguration), entityClass.getSimpleName()))
                .limit(Long.valueOf(searchConfig.getLimit()))
                .offset(Long.valueOf(searchConfig.getOffset()))
                .total(getCount(paginationConfiguration, entityClass))
                .build();
        return jsonDynamicSerializer.toJson(searchConfig.getGenericFields(), entityClass, genericPaginatedResource);
    }

    private long getCount(PaginationConfiguration paginationConfiguration, Class entityClass) {
        PersistenceService persistenceService = getPersistenceService(entityClass);
        return persistenceService.count(paginationConfiguration);
    }

    private PaginationConfiguration getPaginationConfiguration(GenericPagingAndFiltering searchConfig, Class entityClass) {
        List<String> fetchFields = Collections.EMPTY_LIST;
        if(searchConfig.getNestedEntities() != null && !searchConfig.getNestedEntities().isEmpty()){
            fetchFields= Arrays.asList(searchConfig.getNestedEntities().split(","));
        }
        return new PaginationConfiguration(searchConfig.getOffset(), searchConfig.getLimit(), normalize(searchConfig.getFilters(), entityClass),
                searchConfig.getFullTextFilter(), fetchFields, searchConfig.getSortByOrDefault("id"),
                org.primefaces.model.SortOrder.valueOf(searchConfig.getSortOrderOrDefault(SortOrder.ASCENDING).name()));
    }

    private static Map<String, Object> normalize(Map<String, Object> filters, Class entityClass) {
        if(filters == null){
            return null;
        }
        filters.replaceAll((key, entry) -> {
            Class type = null;
            Class declaredFieldType = null;
            try {
                type = entityClass.getDeclaredField(key).getType();
            }catch (Exception e){ }

            if(type == null){
                type = entityClass.getSuperclass();
                do {
                    try {
                        declaredFieldType = type.getDeclaredField(key).getType();
                    }catch (Exception e){
                        type = type.getSuperclass();
                    }
                } while(declaredFieldType == null && type.getSuperclass() != null);
                type = declaredFieldType;
            }

            if(type != null && type.isEnum()){
                return Enum.valueOf(type, (String) entry);
            }
            return entry;
        });
        return filters;
    }

    public Optional<String> findByClassNameAndId(String entityName, Long id, GenericPagingAndFiltering searchConfig) {
        checkId(id);
        Class entityClass = getEntityClass(entityName);
        if(searchConfig == null){
            searchConfig = new GenericPagingAndFiltering(null, null, null, 0,100,null,null);
        }
        return Optional
                .ofNullable(jsonDynamicSerializer
                        .toJson(searchConfig.getGenericFields(), entityClass, Collections.singletonMap("data",find(entityClass, id))));
    }

}
