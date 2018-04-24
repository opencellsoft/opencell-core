package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetOfferTemplateCategoryResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOfferTemplateCategoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The offer template category. */
    private OfferTemplateCategoryDto offerTemplateCategory;

    /**
     * Gets the offer template category.
     *
     * @return the offer template category
     */
    public OfferTemplateCategoryDto getOfferTemplateCategory() {
        return offerTemplateCategory;
    }

    /**
     * Sets the offer template category.
     *
     * @param offerTemplateCategory the new offer template category
     */
    public void setOfferTemplateCategory(OfferTemplateCategoryDto offerTemplateCategory) {
        this.offerTemplateCategory = offerTemplateCategory;
    }

    @Override
    public String toString() {
        return "GetOfferTemplateCategoryResponseDto [offerTemplateCategory=" + offerTemplateCategory + "]";
    }
}