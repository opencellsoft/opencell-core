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
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "CustomEntityTemplatesResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityTemplatesResponseDto extends BaseResponse {

    private static final long serialVersionUID = 2198425912826143580L;

    @XmlElementWrapper(name = "customEntityTemplates")
    @XmlElement(name = "customEntityTemplate")
    private List<CustomEntityTemplateDto> customEntityTemplates = new ArrayList<CustomEntityTemplateDto>();

    public List<CustomEntityTemplateDto> getCustomEntityTemplates() {
        return customEntityTemplates;
    }

    public void setCustomEntityTemplates(List<CustomEntityTemplateDto> customEntityTemplates) {
        this.customEntityTemplates = customEntityTemplates;
    }
}