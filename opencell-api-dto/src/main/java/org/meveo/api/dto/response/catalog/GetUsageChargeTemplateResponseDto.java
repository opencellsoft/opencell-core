package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetUsageChargeTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetUsageChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUsageChargeTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1826809255071018316L;

    /** The usage charge template. */
    private UsageChargeTemplateDto usageChargeTemplate;

    /**
     * Gets the usage charge template.
     *
     * @return the usage charge template
     */
    public UsageChargeTemplateDto getUsageChargeTemplate() {
        return usageChargeTemplate;
    }

    /**
     * Sets the usage charge template.
     *
     * @param usageChargeTemplate the new usage charge template
     */
    public void setUsageChargeTemplate(UsageChargeTemplateDto usageChargeTemplate) {
        this.usageChargeTemplate = usageChargeTemplate;
    }

    @Override
    public String toString() {
        return "GetUsageChargeTemplateResponse [usageChargeTemplate=" + usageChargeTemplate + ", toString()=" + super.toString() + "]";
    }
}