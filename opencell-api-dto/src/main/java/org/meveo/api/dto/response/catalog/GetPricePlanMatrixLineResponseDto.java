package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.response.BaseResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "GetPricePlanMatrixLineResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPricePlanMatrixLineResponseDto extends BaseResponse {

    private List<PricePlanMatrixLineDto> pricePlanMatrixLinesDto;



    public void setPricePlanMatrixLineDto(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        this.pricePlanMatrixLinesDto = List.of(pricePlanMatrixLineDto);
    }

    public List<PricePlanMatrixLineDto> getPricePlanMatrixLinesDto() {
        return pricePlanMatrixLinesDto;
    }

    public void setPricePlanMatrixLinesDto(List<PricePlanMatrixLineDto> pricePlanMatrixLinesDto) {
        this.pricePlanMatrixLinesDto = pricePlanMatrixLinesDto;
    }
}
