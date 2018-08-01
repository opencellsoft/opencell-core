package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetChargeTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetChargeTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7907466519449995575L;

    /** The charge template. */
    private ChargeTemplateDto chargeTemplate;

    /**
     * Gets the charge template.
     *
     * @return the charge template
     */
    public ChargeTemplateDto getChargeTemplate() {
        return chargeTemplate;
    }

    /**
     * Sets the charge template.
     *
     * @param chargeTemplate the new charge template
     */
    public void setChargeTemplate(ChargeTemplateDto chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

    @Override
    public String toString() {
        return "GetChargeTemplateResponseDto [chargeTemplate=" + chargeTemplate + ", toString()=" + super.toString() + "]";
    }
}