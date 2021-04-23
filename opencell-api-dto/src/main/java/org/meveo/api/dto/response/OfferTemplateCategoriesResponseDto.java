package org.meveo.api.dto.response;

import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class OfferTemplateCategoriesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "OfferTemplateCategoriesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateCategoriesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The OfferTemplateCategories DTO. */
    private List<OfferTemplateCategoryDto> offerTemplateCategoriesDto = new ArrayList<>();

    /**
     * Constructor of OfferTemplateCategoriesResponseDto.
     */
    public OfferTemplateCategoriesResponseDto() {
    }

    public OfferTemplateCategoriesResponseDto(GenericSearchResponse<OfferTemplateCategoryDto> searchResponse) {
        super(searchResponse.getPaging());
        this.setOfferTemplateCategories(searchResponse.getSearchResults());
    }

    /**
     * Get the list of offerTemplateCategoriesDto.
     *
     * @return the list of offerTemplateCategoriesDto
     */
    public List<OfferTemplateCategoryDto> getOfferTemplateCategories() {
        return offerTemplateCategoriesDto;
    }

    /**
     * Sets the offerTemplateCategories DTO.
     *
     * @param offerTemplateCategoriesDto the offerTemplateCategories DTO
     */
    public void setOfferTemplateCategories(List<OfferTemplateCategoryDto> offerTemplateCategoriesDto) {
        this.offerTemplateCategoriesDto = offerTemplateCategoriesDto;
    }

    @Override
    public String toString() {
        return "ListOfferTemplateCategoriesResponseDto [offerTemplateCategories=" + offerTemplateCategoriesDto + ", toString()=" + super.toString() + "]";
    }
}
