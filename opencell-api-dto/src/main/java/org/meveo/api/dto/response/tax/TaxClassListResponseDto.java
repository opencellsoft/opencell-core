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
import org.meveo.api.dto.tax.TaxClassDto;

/**
 * API response containing a list of Tax class Dto
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxClassListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxClassListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * A list of Tax class dto
     */
    @XmlElementWrapper(name = "dtos")
    @XmlElement(name = "dto")
    private List<TaxClassDto> dtos;

    /**
     * Constructor
     */
    public TaxClassListResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public TaxClassListResponseDto(GenericSearchResponse<TaxClassDto> searchResponse) {
        super(searchResponse.getPaging());
        this.dtos = searchResponse.getSearchResults();
    }

    /**
     * @return A list of Tax class dto
     */
    public List<TaxClassDto> getDtos() {
        if (dtos == null) {
            dtos = new ArrayList<>();
        }
        return dtos;
    }

    /**
     * @param dtos A list of TaxClass Dto
     */
    public void setDtos(List<TaxClassDto> dtos) {
        this.dtos = dtos;
    }
}