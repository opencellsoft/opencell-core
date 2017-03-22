package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * Filtering or search response
 * 
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "FilteredListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilteredListResponseDto extends BaseResponse {

    private static final long serialVersionUID = 852442319825480499L;

    /**
     * Search results as a json formatted string
     */
    private String searchResults;

    public String getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(String searchResults) {
        this.searchResults = searchResults;
    }
}