package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetOfferTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOfferTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8776189890084137788L;

    /** The offer template. */
    public OfferTemplateDto offerTemplate;

    /**
     * Gets the offer template.
     *
     * @return the offer template
     */
    public OfferTemplateDto getOfferTemplate() {
        return offerTemplate;
    }

    /**
     * Sets the offer template.
     *
     * @param offerTemplate the new offer template
     */
    public void setOfferTemplate(OfferTemplateDto offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    @Override
    public String toString() {
        return "GetOfferTemplateResponse [offerTemplate=" + offerTemplate + ", toString()=" + super.toString() + "]";
    }
}