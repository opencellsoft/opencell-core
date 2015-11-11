package org.meveo.api.dto.response;

import org.meveo.api.dto.CustomEntityTemplateDto;

/**
 * @author Andrius Karpavicius
 **/
public class GetCustomEntityTemplateResponseDto extends BaseResponse {

    private static final long serialVersionUID = -1871967200014440842L;

    private CustomEntityTemplateDto customEntityTemplate;

    public CustomEntityTemplateDto getCustomEntityTemplate() {
        return customEntityTemplate;
    }

    public void setCustomEntityTemplate(CustomEntityTemplateDto customEntityTemplate) {
        this.customEntityTemplate = customEntityTemplate;
    }
}