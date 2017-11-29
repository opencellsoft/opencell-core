package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetBusinessProductModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBusinessProductModelResponseDto extends BaseResponse {

    private static final long serialVersionUID = -6781250820569600144L;

    private BusinessProductModelDto businessProductModel;

    public BusinessProductModelDto getBusinessProductModel() {
        return businessProductModel;
    }

    public void setBusinessProductModel(BusinessProductModelDto businessProductModel) {
        this.businessProductModel = businessProductModel;
    }
}
