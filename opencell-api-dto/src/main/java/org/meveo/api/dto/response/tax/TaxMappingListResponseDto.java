package org.meveo.api.dto.response.tax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.GenericSearchResponse;
import org.meveo.api.dto.response.SearchResponse;
import org.meveo.api.dto.tax.TaxMappingDto;

/**
 * API response containing a list of Tax mapping Dto
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxMappingListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxMappingListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * A list of Tax mapping dto
     */
    @XmlElementWrapper(name = "dtos")
    @XmlElement(name = "dto")
    private List<TaxMappingDto> dtos;

    /**
     * Constructor
     */
    public TaxMappingListResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public TaxMappingListResponseDto(GenericSearchResponse<TaxMappingDto> searchResponse) {
        super(searchResponse.getPaging());
        this.dtos = searchResponse.getSearchResults();
    }

    /**
     * @return A list of Tax mapping dto
     */
    public List<TaxMappingDto> getDtos() {
        if (dtos == null) {
            dtos = new ArrayList<>();
        }
        return dtos;
    }

    /**
     * @param dtos A list of TaxMapping Dto
     */
    public void setDtos(List<TaxMappingDto> dtos) {
        this.dtos = dtos;
    }
}