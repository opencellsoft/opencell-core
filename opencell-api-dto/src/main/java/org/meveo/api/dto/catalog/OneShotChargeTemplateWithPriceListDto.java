package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class OneShotChargeTemplateWithPriceListDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "OneShotChargeTemplateList")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateWithPriceListDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8879818156156191005L;

    /** The one shot charge template dtos. */
    private List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplateDtos = new ArrayList<OneShotChargeTemplateWithPriceDto>();

    /**
     * Gets the one shot charge template dtos.
     *
     * @return the one shot charge template dtos
     */
    public List<OneShotChargeTemplateWithPriceDto> getOneShotChargeTemplateDtos() {
        return oneShotChargeTemplateDtos;
    }

    /**
     * Sets the one shot charge template dtos.
     *
     * @param oneShotChargeTemplateDtos the new one shot charge template dtos
     */
    public void setOneShotChargeTemplateDtos(List<OneShotChargeTemplateWithPriceDto> oneShotChargeTemplateDtos) {
        this.oneShotChargeTemplateDtos = oneShotChargeTemplateDtos;
    }
}
