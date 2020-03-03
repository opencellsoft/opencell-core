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
import org.meveo.api.dto.tax.TaxCategoryDto;

/**
 * API response containing a list of Tax category Dto
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxCategoryListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxCategoryListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * A list of Tax category dto
     */
    @XmlElementWrapper(name = "dtos")
    @XmlElement(name = "dto")
    private List<TaxCategoryDto> dtos;

    /**
     * Constructor
     */
    public TaxCategoryListResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public TaxCategoryListResponseDto(GenericSearchResponse<TaxCategoryDto> searchResponse) {
        super(searchResponse.getPaging());
        this.dtos = searchResponse.getSearchResults();
    }

    /**
     * @return A list of Tax category dto
     */
    public List<TaxCategoryDto> getDtos() {
        if (dtos == null) {
            dtos = new ArrayList<>();
        }
        return dtos;
    }

    /**
     * @param dtos A list of TaxCategory Dto
     */
    public void setDtos(List<TaxCategoryDto> dtos) {
        this.dtos = dtos;
    }
}