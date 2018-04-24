package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBusinessProductModelResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetBusinessProductModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBusinessProductModelResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6781250820569600144L;

    /** The business product model. */
    private BusinessProductModelDto businessProductModel;

    /**
     * Gets the business product model.
     *
     * @return the business product model
     */
    public BusinessProductModelDto getBusinessProductModel() {
        return businessProductModel;
    }

    /**
     * Sets the business product model.
     *
     * @param businessProductModel the new business product model
     */
    public void setBusinessProductModel(BusinessProductModelDto businessProductModel) {
        this.businessProductModel = businessProductModel;
    }
}
