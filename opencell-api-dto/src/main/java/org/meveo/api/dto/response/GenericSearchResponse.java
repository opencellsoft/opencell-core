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

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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