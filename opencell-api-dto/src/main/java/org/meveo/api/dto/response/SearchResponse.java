package org.meveo.api.dto.response;

/**
 * Pagination and sorting criteria plus total record count.
 * 
 * @author Andrius Karpavicius
 */
public abstract class SearchResponse extends BaseResponse {

    private static final long serialVersionUID = -2374431968882480529L;

    private PagingAndFiltering paging;

    public void setPaging(PagingAndFiltering paging) {
        this.paging = paging;
    }

    public PagingAndFiltering getPaging() {
        return paging;
    }

    @Override
    public String toString() {
        return "[paging=" + paging + "]";
    }
}