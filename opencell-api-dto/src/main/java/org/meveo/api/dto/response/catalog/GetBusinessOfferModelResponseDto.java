package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBusinessOfferModelResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetBusinessOfferModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBusinessOfferModelResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6781250820569600144L;

    /** The business offer model. */
    private BusinessOfferModelDto businessOfferModel;

    /**
     * Gets the business offer model.
     *
     * @return the business offer model
     */
    public BusinessOfferModelDto getBusinessOfferModel() {
        return businessOfferModel;
    }

    /**
     * Sets the business offer model.
     *
     * @param businessOfferModel the new business offer model
     */
    public void setBusinessOfferModel(BusinessOfferModelDto businessOfferModel) {
        this.businessOfferModel = businessOfferModel;
    }

    @Override
    public String toString() {
        return "GetBusinessOfferModelResponseDto [businessOfferModel=" + businessOfferModel + "]";
    }
}