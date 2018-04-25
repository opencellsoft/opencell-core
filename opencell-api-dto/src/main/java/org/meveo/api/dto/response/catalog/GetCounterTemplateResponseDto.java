package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetCounterTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetCounterTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCounterTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4612709775410582280L;

    /** The counter template. */
    private CounterTemplateDto counterTemplate;

    /**
     * Gets the counter template.
     *
     * @return the counter template
     */
    public CounterTemplateDto getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Sets the counter template.
     *
     * @param counterTemplate the new counter template
     */
    public void setCounterTemplate(CounterTemplateDto counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    @Override
    public String toString() {
        return "GetCounterTemplateResponse [counterTemplate=" + counterTemplate + ", toString()=" + super.toString() + "]";
    }
}