package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class BusinessAccountModelResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BusinessAccountModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessAccountModelResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2059945254478663407L;

    /** The business account model. */
    private BusinessAccountModelDto businessAccountModel;

    /**
     * Gets the business account model.
     *
     * @return the business account model
     */
    public BusinessAccountModelDto getBusinessAccountModel() {
        return businessAccountModel;
    }

    /**
     * Sets the business account model.
     *
     * @param businessAccountModel the new business account model
     */
    public void setBusinessAccountModel(BusinessAccountModelDto businessAccountModel) {
        this.businessAccountModel = businessAccountModel;
    }
}