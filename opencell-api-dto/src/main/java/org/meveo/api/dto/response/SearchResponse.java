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

package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;

/**
 * Pagination and sorting criteria plus total record count.
 * 
 * @author Andrius Karpavicius
 */
public abstract class SearchResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2374431968882480529L;

    /** The paging. */
    private PagingAndFiltering paging;

    /**
     * Generic constructor
     */
    public SearchResponse() {
        super();
    }

    /**
     * Constructor
     * 
     * @param paging Paging and filtering criteria containing total record cound
     */
    public SearchResponse(PagingAndFiltering paging) {
        super();
        this.paging = paging;
    }

    /**
     * Sets the paging.
     *
     * @param paging the new paging
     */
    @SuppressWarnings("rawtypes")
    public void setPaging(PagingAndFiltering paging) {
        paging = SerializationUtils.clone(paging);
        this.paging = paging;

        // Convert filter values to xml serializable format
        if (this.paging != null && this.paging.getFilters() != null) {
            Set<String> keys = new HashSet<>(this.paging.getFilters().keySet());
            for (String filterKey : keys) {
                Object value = this.paging.getFilters().get(filterKey);
                if (value == null || (value instanceof Collection && ((Collection) value).isEmpty())) {
                    this.paging.getFilters().remove(filterKey);

                } else if (value.getClass().isArray()) { // TODO would need to deal with array of dates to format date properly
                    this.paging.getFilters().put(filterKey, StringUtils.concatenate((Object[]) value));

                } else if (value instanceof BusinessEntity) {
                    this.paging.getFilters().put(filterKey, ((BusinessEntity) value).getCode());

                } else if (value instanceof Collection) {
                    Object firstValue = ((Collection) value).iterator().next();
                    if (firstValue instanceof BusinessEntity) {

                        List<String> codes = new ArrayList<>();
                        for (Object valueItem : (Collection) value) {
                            codes.add(((BusinessEntity) valueItem).getCode());
                        }
                        this.paging.getFilters().put(filterKey, StringUtils.concatenate(",", (Collection) codes));
                    } else {
                        this.paging.getFilters().put(filterKey, StringUtils.concatenate(",", (Collection) value));
                    }
                }
            }
        }
    }

    /**
     * Gets the paging.
     *
     * @return the paging
     */
    public PagingAndFiltering getPaging() {
        return paging;
    }

    @Override
    public String toString() {
        return "[paging=" + paging + "]";
    }
}