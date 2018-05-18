package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldTemplateDto;

/**
 * The Class GetCustomFieldTemplateReponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetCustomFieldTemplateReponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomFieldTemplateReponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2634417925663198816L;

    /** The custom field template. */
    private CustomFieldTemplateDto customFieldTemplate;

    /**
     * Gets the custom field template.
     *
     * @return the custom field template
     */
    public CustomFieldTemplateDto getCustomFieldTemplate() {
        return customFieldTemplate;
    }

    /**
     * Sets the custom field template.
     *
     * @param customFieldTemplate the new custom field template
     */
    public void setCustomFieldTemplate(CustomFieldTemplateDto customFieldTemplate) {
        this.customFieldTemplate = customFieldTemplate;
    }

}