package org.meveo.api.dto;

import org.meveo.api.dto.response.Paging;

/**
 * Pagination and sorting criteria.
 * 
 * @author Andrius Karpavicius
 */
public abstract class SearchDto extends BaseDto {

    private static final long serialVersionUID = 2618328333851328648L;

    private Paging paging;

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    @Override
    public String toString() {
        return "[paging=" + paging + "]";
    }
}