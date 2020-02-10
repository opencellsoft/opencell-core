package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Search results - Actual search results plus Pagination and sorting criteria plus total record count
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> DTO entity type
 */
@XmlRootElement(name = "SearchResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericSearchResponse<T> extends SearchResponse implements Serializable {

    private static final long serialVersionUID = 2456546239440614366L;

    /**
     * Search result
     */
    private List<T> searchResults;

    /**
     * General constructor
     */
    public GenericSearchResponse() {
        super();
    }

    /**
     * Constructor
     * 
     * @param searchResults Search results - a list of DTO objects
     * @param paging Pagination and filtering criteria including a total record count
     */
    public GenericSearchResponse(List<T> searchResults, PagingAndFiltering paging) {
        super(paging);
        this.searchResults = searchResults;
    }

    /**
     * @return Search result - a list of DTO objects
     */
    public List<T> getSearchResults() {
        return searchResults;
    }

    /**
     * @param searchResults Search result - a list of DTO objects
     */
    public void setSearchResults(List<T> searchResults) {
        this.searchResults = searchResults;
    }
}