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