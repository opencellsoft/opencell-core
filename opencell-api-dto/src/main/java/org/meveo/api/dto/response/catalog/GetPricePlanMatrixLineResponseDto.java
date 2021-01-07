package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.response.BaseResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetPricePlanMatrixLineResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPricePlanMatrixLineResponseDto extends BaseResponse {

    private PricePlanMatrixLineDto pricePlanMatrixLineDto;

    public PricePlanMatrixLineDto getPricePlanMatrixLineDto() {
        return pricePlanMatrixLineDto;
    }

    public void setPricePlanMatrixLineDto(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        this.pricePlanMatrixLineDto = pricePlanMatrixLineDto;
    }
}
