package org.meveo.api.dto.response;

import java.util.HashSet;
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

    private static final long serialVersionUID = -2374431968882480529L;

    private PagingAndFiltering paging;

    public void setPaging(PagingAndFiltering paging) {
        paging = SerializationUtils.clone(paging);
        this.paging = paging;

        // Convert filter values to xml serializable format // TODO would need to deal with array of dates to format date properly
        if (this.paging != null && this.paging.getFilters() != null) {
            Set<String> keys = new HashSet<>(this.paging.getFilters().keySet());
            for (String filterKey : keys) {
                Object value = this.paging.getFilters().get(filterKey);
                if (value == null) {
                    this.paging.getFilters().remove(filterKey);

                } else if (value.getClass().isArray()) {
                    this.paging.getFilters().put(filterKey, StringUtils.concatenate((Object[]) value));

                } else if (value instanceof BusinessEntity) {
                    this.paging.getFilters().put(filterKey, ((BusinessEntity) value).getCode());
                }
            }
        }
    }

    public PagingAndFiltering getPaging() {
        return paging;
    }

    @Override
    public String toString() {
        return "[paging=" + paging + "]";
    }
}