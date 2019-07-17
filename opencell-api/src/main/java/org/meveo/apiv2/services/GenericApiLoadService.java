package org.meveo.apiv2.services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.Embeddable;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.generic.GenericPaginatedResponseDto;
import org.meveo.api.dto.response.generic.GenericResponseDto;
import org.meveo.api.dto.response.generic.SimpleGenericValue;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.service.base.PersistenceService;

import static org.meveo.apiv2.ValidationUtils.checkEntityClass;
import static org.meveo.apiv2.ValidationUtils.checkEntityName;
import static org.meveo.apiv2.ValidationUtils.checkId;
import static org.meveo.apiv2.ValidationUtils.checkRecords;
import static org.meveo.apiv2.ValidationUtils.performOperationOnCondition;

@Stateless
public class GenericApiLoadService extends GenericApiService {
    private static List<Class> commonTypes = Arrays.asList(
            String.class,
            Boolean.class,
            Long.class,
            Integer.class,
            Double.class,
            Float.class,
            Date.class,
            BigDecimal.class,
            Character.class

    );
    static List<String> forbiddenFieldNames = Arrays.asList(
            "NB_DECIMALS",
            "historized",
            "notified",
            "NB_PRECISION",
            "appendGeneratedCode",
            "serialVersionUID"
            );
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
                .withFrom(paginationConfiguration.getFirstRow()).withLimit(searchConfig.getLimit()).withTotalElements(persistenceService.count(paginationConfiguration));
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
                .performOperationOnCondition(property, getEntityPredicate(), p -> jsonValue.setValue(transform((Serializable) p)))
                .performOperationOnCondition(property, p -> p instanceof String, p -> jsonValue.setValue(p.toString()))
                .performOperationOnCondition(jsonValue, property, j -> j.getValue() == null, (j, p) -> j.setValue(JacksonUtil.toString(p)));
        
        return jsonValue.getValue();
    }

    private Predicate<Object> getEntityPredicate() {
        return p -> p != null &&  (p instanceof BaseEntity || p.getClass().isAnnotationPresent(Embeddable.class));
    }

    String transform(Serializable dependency) {

        Map<String, Object> entityRepresentation = new HashMap<>();
        performOperationOnCondition(dependency, p -> p instanceof BaseEntity, po -> entityRepresentation.put("id", ((BaseEntity) po).getId()));
        performOperationOnCondition(dependency.getClass(), clazz -> clazz.isAnnotationPresent(ExportIdentifier.class), cl -> extractIdentifiers(entityRepresentation, dependency, ""));

        List<Field> fields = ReflectionUtils.getAllFields(dependency.getClass());
        extractDependencies(dependency, entityRepresentation, fields);

        fields.stream()
                .filter(field -> commonTypes.contains(field.getType()) || field.getType().isPrimitive())
                .filter(field -> !forbiddenFieldNames.contains(field.getName()))
        .forEach(field -> ReflectionUtils.getPropertyValueOrNull(dependency, field.getName()).ifPresent(property -> entityRepresentation.put(field.getName(), property)));

        return JacksonUtil.toString(entityRepresentation.isEmpty() ? dependency : entityRepresentation);
    }

    private void extractDependencies(Serializable dependency, Map<String, Object> entityRepresentation, List<Field> fields) {
        fields.stream().filter(field -> field.getType().isAnnotationPresent(ExportIdentifier.class))
                .forEach(field -> ReflectionUtils.getPropertyValueOrNull(dependency, field.getName()).ifPresent(property -> extractIdentifiers(entityRepresentation, property, field.getName())));
    }

    private void extractIdentifiers(Map<String, Object> entityRepresentation, Object dependency, String dependencyName) {
       final Object unProxyDependency = PersistenceUtils.initializeAndUnproxy(dependency);
        Map<String, Object> dependencyRep = Arrays.stream((dependency.getClass().getAnnotation(ExportIdentifier.class)).value())
                .collect(Collectors.toMap(v -> v, v -> ReflectionUtils.getPropertyValueOrNull(unProxyDependency, v).orElse(StringUtils.EMPTY)));
        if(StringUtils.isBlank(dependencyName)){
            entityRepresentation.putAll(dependencyRep);
        }else{
            entityRepresentation.put(dependencyName, dependencyRep);
        }
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
