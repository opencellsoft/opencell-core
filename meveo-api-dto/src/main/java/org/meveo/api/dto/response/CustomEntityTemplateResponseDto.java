package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomEntityTemplateDto;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "CustomEntityTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplateResponseDto extends BaseResponse {

    private static final long serialVersionUID = -1871967200014440842L;

    private CustomEntityTemplateDto customEntityTemplate;

    public CustomEntityTemplateDto getCustomEntityTemplate() {
        return customEntityTemplate;
    }

    public void setCustomEntityTemplate(CustomEntityTemplateDto customEntityTemplate) {
        this.customEntityTemplate = customEntityTemplate;
    }
}