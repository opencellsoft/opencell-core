package org.meveo.api.dto.response;

import org.meveo.api.dto.CustomFieldTemplateDto;

/**
 * @author Edward P. Legaspi
 **/
public class GetCustomFieldTemplateReponseDto extends BaseResponse {

	private static final long serialVersionUID = 2634417925663198816L;

	private CustomFieldTemplateDto customFieldTemplate;

	public CustomFieldTemplateDto getCustomFieldTemplate() {
		return customFieldTemplate;
	}

	public void setCustomFieldTemplate(CustomFieldTemplateDto customFieldTemplate) {
		this.customFieldTemplate = customFieldTemplate;
	}

}
