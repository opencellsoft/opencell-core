package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomEntityTemplateDto;

/**
 * The Class CustomEntityTemplateResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomEntityTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1871967200014440842L;

    /** The custom entity template. */
    private CustomEntityTemplateDto customEntityTemplate;

    /**
     * Gets the custom entity template.
     *
     * @return the custom entity template
     */
    public CustomEntityTemplateDto getCustomEntityTemplate() {
        return customEntityTemplate;
    }

    /**
     * Sets the custom entity template.
     *
     * @param customEntityTemplate the new custom entity template
     */
    public void setCustomEntityTemplate(CustomEntityTemplateDto customEntityTemplate) {
        this.customEntityTemplate = customEntityTemplate;
    }
}