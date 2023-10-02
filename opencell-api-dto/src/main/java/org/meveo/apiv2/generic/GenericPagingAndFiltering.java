package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.api.dto.response.PagingAndFiltering;

import javax.annotation.Nullable;
import javax.persistence.criteria.JoinType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableGenericPagingAndFiltering.class)
public interface GenericPagingAndFiltering {
    @Nullable
    @Value.Default default Set<String> getGenericFields(){ return Collections.emptySet();}
    @Nullable
    @Value.Default default List<GenericFieldDetails> getGenericFieldDetails(){ return Collections.emptyList();}
    @Nullable
    @Value.Default default Set<String> getNestedEntities(){ return Collections.emptySet();}
    @Nullable
    @Value.Default default Long getNestedDepth(){ return 0L;}
    @Nullable
    String getFullTextFilter();
    @Nullable
    @Value.Default default Map<String, Object> getFilters(){ return Collections.emptyMap();}
    @Nullable
    Long getTotal();
    @Nullable
    Long getLimit();
    @Value.Auxiliary default Long getLimitOrDefault(Long defaultValue){
        return getLimit() != null ? getLimit() : defaultValue;
    }
    @Value.Default default Long getOffset(){
        return 0L;
    }
    @Value.Default default String getSortBy(){ return "id";}
    @Value.Default default String getSortOrder(){ return PagingAndFiltering.SortOrder.ASCENDING.name();}
    @Nullable
    @Value.Default default Set<String> getExcluding(){ return Collections.emptySet();}
    @Nullable
    @Value.Default default Set<String> getGroupBy(){
        return Collections.emptySet();
    }
    @Nullable
    @Value.Default default Set<String> getHaving(){
        return Collections.emptySet();
    }
    @Nullable
    @Value.Default default Map<String, String> getTranslations(){ return Collections.emptyMap();}
    
    @Nullable
    JoinType getJoinType();

    @Nullable
    @Value.Default default Boolean getForceCount(){ return false; }
}
