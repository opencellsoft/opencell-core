package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldTemplateDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetCustomFieldTemplateReponse")
@XmlAccessorType(XmlAccessType.FIELD)
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
