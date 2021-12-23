package org.meveo.apiv2.generic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.model.IEntity;
import org.meveo.service.base.NativePersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.meveo.apiv2.generic.ValidationUtils.checkId;
import static org.meveo.apiv2.generic.ValidationUtils.checkRecords;

@Stateless
public class GenericApiLoadService {
    @Inject
    GenericOpencellRestful genericOpencellRestful;

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @Inject
    private GenericApiPersistenceDelegate persistenceDelegate;

    public String findPaginatedRecords(Boolean extractList, Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> fetchFields, Long nestedDepth) {
        if(genericFields != null && isAggregationQueries(genericFields)){
            searchConfig.setFetchFields(new ArrayList<>(genericFields));
            List<List<Object>> list = (List<List<Object>>) nativePersistenceService.getQuery(entityClass.getCanonicalName(), searchConfig)
                    .find(nativePersistenceService.getEntityManager()).stream()
                    .map(ObjectArrays -> Arrays.asList(ObjectArrays))
                    .collect(Collectors.toList());
            return serializeResult(list.stream()
                    .map(line -> addResultLine(line, genericFields.iterator()))
                    .collect(Collectors.toList()));
        }else{
            SearchResult searchResult = persistenceDelegate.list(entityClass, searchConfig);
            ImmutableGenericPaginatedResource genericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                    .data(checkRecords(searchResult.getEntityList(), entityClass.getSimpleName()))
                    .limit(Long.valueOf(searchConfig.getNumberOfRows()))
                    .offset(Long.valueOf(searchConfig.getFirstRow()))
                    .total(searchResult.getCount())
                    .build();
            return JsonGenericMapper.Builder.getBuilder()
                    .withExtractList(Objects.nonNull(extractList) ? extractList : genericOpencellRestful.shouldExtractList())
                    .withNestedEntities(fetchFields)
                    .withNestedDepth(nestedDepth)
                    .build()
                    .toJson(genericFields, entityClass, genericPaginatedResource);
        }
    }

	public List<Map<String, Object>> findAggregatedPaginatedRecords(Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFieldsAlias) {
		List<List<Object>> list = (List<List<Object>>) nativePersistenceService.getQuery(entityClass.getCanonicalName(), searchConfig)
				.addPaginationConfiguration(searchConfig, "a").find(nativePersistenceService.getEntityManager()).stream().map(ObjectArrays -> Arrays.asList(ObjectArrays)).collect(Collectors.toList());
		return list.stream().map(line -> addResultLine(line, genericFieldsAlias != null ? genericFieldsAlias.iterator() : searchConfig.getFetchFields().iterator())).collect(Collectors.toList());
	}

	public int getAggregatedRecordsCount(Class entityClass, PaginationConfiguration searchConfig) {
		return nativePersistenceService.getQuery(entityClass.getCanonicalName(), searchConfig)
				.find(nativePersistenceService.getEntityManager()).size();
	}

	private Map<String, Object> addResultLine(List<Object> line, Iterator<String> iterator) {
	    return line.stream()
	            .flatMap(array -> array instanceof Object[] ? flatten((Object[])array) : Stream.of(array))
	            .map(l -> Objects.isNull(l) ? "" : l)
	            .collect(Collectors.toMap(x -> iterator.next(), Function.identity(), (existing, replacement) -> existing, LinkedHashMap::new));
	}
    private static Stream<Object> flatten(Object[] array) {
        return Arrays.stream(array)
                .flatMap(o -> o instanceof Object[]? flatten((Object[])o): Stream.of(o));
    }
    private String serializeResult(List<Map<String, Object>> mapResult) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(mapResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json formatting exception", e);
        }
    }

    private boolean isAggregationField(String field) {
        return field.startsWith("SUM(") || field.startsWith("COUNT(") || field.startsWith("AVG(")
                || field.startsWith("MAX(") || field.startsWith("MIN(") || field.startsWith("COALESCE(SUM(");
    }

    private boolean isAggregationQueries(Set<String> genericFields) {
        return genericFields.stream()
                .filter(genericField -> isAggregationField(genericField))
                .findFirst()
                .isPresent();
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
