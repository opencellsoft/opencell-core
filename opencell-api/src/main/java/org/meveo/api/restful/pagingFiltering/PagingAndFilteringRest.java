package org.meveo.api.restful.pagingFiltering;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutablePagingAndFilteringRest.class)
public interface PagingAndFilteringRest {
    @Value.Default default Integer getLimit(){
        return 100;
    }
    @Value.Default default Integer getOffset(){
        return 0;
    }
    @Value.Default default String getSort(){
        return "id";
    }
    @Nullable
    @Value.Default default Set<String> getFields(){ return Collections.emptySet();}
    @Nullable
    @Value.Default default Map<String, Object> getFilters(){ return Collections.emptyMap();}
}
