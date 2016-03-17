package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OneShotChargeTemplateList")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateWithPriceListDto extends BaseResponse {

    private static final long serialVersionUID = -8879818156156191005L;

    private List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplateDtos = new ArrayList<OneShotChargeTemplateWithPriceDto>();

    public List<OneShotChargeTemplateWithPriceDto> getOneShotChargeTemplateDtos() {
        return oneShotChargeTemplateDtos;
    }

    public void setOneShotChargeTemplateDtos(List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplateDtos) {
        this.oneShotChargeTemplateDtos = oneShotChargeTemplateDtos;
    }
}
