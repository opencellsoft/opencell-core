package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetOneShotChargeTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetOneShotChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOneShotChargeTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5607296225072318694L;

    /** The one shot charge template. */
    private OneShotChargeTemplateDto oneShotChargeTemplate;

    /**
     * Gets the one shot charge template.
     *
     * @return the one shot charge template
     */
    public OneShotChargeTemplateDto getOneShotChargeTemplate() {
        return oneShotChargeTemplate;
    }

    /**
     * Sets the one shot charge template.
     *
     * @param oneShotChargeTemplate the new one shot charge template
     */
    public void setOneShotChargeTemplate(OneShotChargeTemplateDto oneShotChargeTemplate) {
        this.oneShotChargeTemplate = oneShotChargeTemplate;
    }

    @Override
    public String toString() {
        return "GetOneShotChargeTemplateResponse [oneShotChargeTemplate=" + oneShotChargeTemplate + ", toString()=" + super.toString() + "]";
    }
}