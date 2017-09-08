package org.meveo.api.dto.response;

/**
 * Pagination and sorting criteria plus total record count.
 * 
 * @author Andrius Karpavicius
 */
public abstract class SearchResponse extends BaseResponse {

    private static final long serialVersionUID = -2374431968882480529L;

    private Paging paging;

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public Paging getPaging() {
        return paging;
    }

    @Override
    public String toString() {
        return "[paging=" + paging + "]";
    }
}