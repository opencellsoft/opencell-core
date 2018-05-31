package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetTriggeredEdrResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetTriggeredEdrResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTriggeredEdrResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -408801271188966214L;

    /** The triggered edr template. */
    private TriggeredEdrTemplateDto triggeredEdrTemplate;

    /**
     * Gets the triggered edr template.
     *
     * @return the triggered edr template
     */
    public TriggeredEdrTemplateDto getTriggeredEdrTemplate() {
        return triggeredEdrTemplate;
    }

    /**
     * Sets the triggered edr template.
     *
     * @param triggeredEdrTemplate the new triggered edr template
     */
    public void setTriggeredEdrTemplate(TriggeredEdrTemplateDto triggeredEdrTemplate) {
        this.triggeredEdrTemplate = triggeredEdrTemplate;
    }

    @Override
    public String toString() {
        return "GetTriggeredEdrResponseDto [triggeredEdrTemplate=" + triggeredEdrTemplate + ", getActionStatus()=" + getActionStatus() + "]";
    }
}