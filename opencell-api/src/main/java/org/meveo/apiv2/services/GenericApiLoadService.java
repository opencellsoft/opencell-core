package org.meveo.apiv2.services;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.generic.GenericPaginatedResponseDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.api.dto.response.generic.SimpleGenericValue;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;
import static org.meveo.apiv2.ValidationUtils.checkId;
import static org.meveo.apiv2.ValidationUtils.checkRecords;
import static org.meveo.apiv2.ValidationUtils.performOperationOnCondition;

@Stateless
public class GenericApiLoadService extends GenericApiService {
    
    public GenericPaginatedResponseDto findPaginatedRecords(String entityName, GenericPagingAndFiltering searchConfig) {
        checkEntityName(entityName);
        Class entityClass = getEntityClass(entityName);
        return buildGenericPaginatedResponse(initIfNull(searchConfig), entityClass);
    }
    
    public GenericResponseDto findByClassNameAndId(String entityName, Long id, GenericRequestDto requestedDto) {
        checkId(id);
        checkEntityName(entityName);
        Class entityClass = getEntityClass(entityName);
        
        List<String> fields = Optional.ofNullable(requestedDto).map(GenericRequestDto::getFields).orElse(Collections.emptyList());
        
        return load(entityClass, id, fields);
    }
    
    GenericPaginatedResponseDto buildGenericPaginatedResponse(GenericPagingAndFiltering searchConfig, Class entityClass) {
        List<String> fields = searchConfig.getGenericFields();
        PaginationConfiguration paginationConfiguration = paginationConfiguration(searchConfig);
        PersistenceService persistenceService = getPersistenceService(entityClass);
        List<BaseEntity> records = checkRecords(persistenceService.list(paginationConfiguration), entityClass.getSimpleName());
        return new GenericPaginatedResponseDto(records.stream().map(record -> buildGenericResponse(record, fields)).collect(Collectors.toList()))
                .withFrom(paginationConfiguration.getFirstRow()).withLimit(searchConfig.getLimit()).withTotalElements(persistenceService.count());
    }
    
    private Class getEntityClass(String entityName) {
        Class entityClass = entitiesByName.get(entityName.toLowerCase());
        checkEntityClass(entityClass);
        return entityClass;
    }
    
    private GenericResponseDto load(Class entityClass, Long id, List<String> requestedFields) {
        Object result = find(entityClass, id);
        return buildGenericResponse(result, requestedFields);
    }
    
    GenericResponseDto buildGenericResponse(Object result, List<String> fields) {
        Map<String, String> values = Optional.ofNullable(result).map(r -> getAllNonStaticFieldValues(r, fields)).orElse(new HashMap<>());
        return new GenericResponseDto(values);
    }
    
    private Map<String, String> getAllNonStaticFieldValues(Object result, List<String> fields) {
        SimpleGenericValue<Map<String, String>> value = new SimpleGenericValue<>();
        performOperationOnCondition(fields, result, List::isEmpty, (v, r) -> value.setValue(getAllFields(r)))
                .performOperationOnCondition(fields, result, l -> !l.isEmpty(), (v, r) -> value.setValue(getDefinedFields(r, v)));
        return value.getValue();
    }
    
    private Map<String, String> getDefinedFields(Object result, List<String> fields) {
        return fields.stream().collect(Collectors.toMap(field -> field, field -> ReflectionUtils.getPropertyValueOrNull(result, field))).entrySet().stream()
                .filter(e -> e.getValue().isPresent()).collect(Collectors.toMap(Map.Entry::getKey, e -> toJson(e.getValue().get())));
    }
    
    private Map<String, String> getAllFields(Object result) {
        return ReflectionUtils.getAllFields(result.getClass()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).filter(Objects::nonNull)
                .collect(Collectors.toMap(Field::getName, field -> toJson(extractValueFromField(result, field))));
    }
    
    Object extractValueFromField(Object result, Field field) {
        field.setAccessible(true);
        try {
            return Optional.ofNullable(field.get(result)).orElse(StringUtils.EMPTY);
        } catch (IllegalAccessException e) {
            return StringUtils.EMPTY;
        }
    }
    
    String toJson(Object property) {
        SimpleGenericValue<String> jsonValue = new SimpleGenericValue<>();
        performOperationOnCondition(property, p -> Collection.class.isAssignableFrom(p.getClass()), po -> jsonValue.setValue(transformCollection((Collection) po)))
                .performOperationOnCondition(property, p -> BaseEntity.class.isAssignableFrom(p.getClass()), p -> jsonValue.setValue(((BaseEntity) p).getId().toString()))
                .performOperationOnCondition(property, p -> p instanceof String, p -> jsonValue.setValue(p.toString()))
                .performOperationOnCondition(jsonValue, property, j -> j.getValue() == null, (j, p) -> j.setValue(JacksonUtil.toString(p)));
        
        return jsonValue.getValue();
    }
    
    private String transformCollection(Collection property) {
        Collection<BaseEntity> value = property;
        return value.stream().map(BaseEntity::getId).map(String::valueOf).collect(Collectors.toList()).toString();
    }
    
    PaginationConfiguration paginationConfiguration(GenericPagingAndFiltering pagingAndFiltering) {
        return new PaginationConfiguration(pagingAndFiltering.getOffset(), pagingAndFiltering.getLimit(), pagingAndFiltering.getFilters(), pagingAndFiltering.getFullTextFilter(),
                null, pagingAndFiltering.getSortByOrDefault("id"), org.primefaces.model.SortOrder.valueOf(pagingAndFiltering.getSortOrderOrDefault(SortOrder.ASCENDING).name()));
    }
    
    private GenericPagingAndFiltering initIfNull(GenericPagingAndFiltering searchConfig) {
        return Optional.ofNullable(searchConfig).orElse(new GenericPagingAndFiltering());
    }
}
