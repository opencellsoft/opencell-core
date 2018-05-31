package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomEntityTemplateDto;

/**
 * The Class CustomEntityTemplatesResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomEntityTemplatesResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplatesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2198425912826143580L;

    /** The custom entity templates. */
    @XmlElementWrapper(name = "customEntityTemplates")
    @XmlElement(name = "customEntityTemplate")
    private List<CustomEntityTemplateDto> customEntityTemplates = new ArrayList<CustomEntityTemplateDto>();

    /**
     * Gets the custom entity templates.
     *
     * @return the custom entity templates
     */
    public List<CustomEntityTemplateDto> getCustomEntityTemplates() {
        return customEntityTemplates;
    }

    /**
     * Sets the custom entity templates.
     *
     * @param customEntityTemplates the new custom entity templates
     */
    public void setCustomEntityTemplates(List<CustomEntityTemplateDto> customEntityTemplates) {
        this.customEntityTemplates = customEntityTemplates;
    }
}