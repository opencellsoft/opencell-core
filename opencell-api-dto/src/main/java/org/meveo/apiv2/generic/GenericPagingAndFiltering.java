/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.api.dto.response.PagingAndFiltering;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableGenericPagingAndFiltering.class)
public interface GenericPagingAndFiltering {
    @Nullable
    @Value.Default default Set<String> getGenericFields(){ return Collections.emptySet();}
    @Nullable
    @Value.Default default Set<String> getNestedEntities(){ return Collections.emptySet();}
    @Nullable
    String getFullTextFilter();
    @Nullable
    @Value.Default default Map<String, Object> getFilters(){ return Collections.emptyMap();}
    @Nullable
    Long getTotal();
    @Nullable
    @Value.Default default Long getLimit(){
        return 100L;
    };
    @Value.Default default Long getOffset(){
        return 0L;
    }
    @Value.Default default String getSortBy(){ return "id";}
    @Value.Default default String getSortOrder(){ return PagingAndFiltering.SortOrder.ASCENDING.name();}
}
