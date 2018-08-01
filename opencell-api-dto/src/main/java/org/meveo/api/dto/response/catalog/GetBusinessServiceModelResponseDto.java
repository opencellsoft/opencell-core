package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBusinessServiceModelResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetBusinessServiceModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBusinessServiceModelResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6781250820569600144L;

    /** The business service model. */
    private BusinessServiceModelDto businessServiceModel;

    /**
     * Gets the business service model.
     *
     * @return the business service model
     */
    public BusinessServiceModelDto getBusinessServiceModel() {
        return businessServiceModel;
    }

    /**
     * Sets the business service model.
     *
     * @param businessServiceModel the new business service model
     */
    public void setBusinessServiceModel(BusinessServiceModelDto businessServiceModel) {
        this.businessServiceModel = businessServiceModel;
    }

    @Override
    public String toString() {
        return "GetBusinessServiceModelResponseDto [businessServiceModel=" + businessServiceModel + "]";
    }
}