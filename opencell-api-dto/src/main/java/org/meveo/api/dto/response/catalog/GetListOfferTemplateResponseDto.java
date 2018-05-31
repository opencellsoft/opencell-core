package org.meveo.api.dto.response.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListOfferTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetListOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListOfferTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5535571034571826093L;

    /** The offer templates. */
    @XmlElementWrapper(name = "offerTemplates")
    @XmlElement(name = "offerTemplate")
    private List<OfferTemplateDto> offerTemplates;

    /**
     * Instantiates a new gets the list offer template response dto.
     */
    public GetListOfferTemplateResponseDto() {

    }

    /**
     * Gets the offer templates.
     *
     * @return the offer templates
     */
    public List<OfferTemplateDto> getOfferTemplates() {
        return offerTemplates;
    }

    /**
     * Sets the offer templates.
     *
     * @param offerTemplates the new offer templates
     */
    public void setOfferTemplates(List<OfferTemplateDto> offerTemplates) {
        this.offerTemplates = offerTemplates;
    }

    /**
     * Adds the offer template.
     *
     * @param offerTemplate the offer template
     */
    public void addOfferTemplate(OfferTemplateDto offerTemplate) {
        if (offerTemplates == null) {
            offerTemplates = new ArrayList<>();
        }
        offerTemplates.add(offerTemplate);
    }
}