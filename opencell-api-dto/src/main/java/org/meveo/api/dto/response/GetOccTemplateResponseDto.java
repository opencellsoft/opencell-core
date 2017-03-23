package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.OccTemplateDto;


@XmlRootElement(name = "GetOccTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOccTemplateResponseDto extends BaseResponse {

	private static final long serialVersionUID = 4612709775410582280L;

	private OccTemplateDto occTemplate;

	public OccTemplateDto getOccTemplate() {
		return occTemplate;
	}

	public void setOccTemplate(OccTemplateDto occTemplate) {
		this.occTemplate = occTemplate;
	}

	@Override
	public String toString() {
		return "GetOccTemplateResponse [occTemplate=" + occTemplate + ", toString()=" + super.toString()
				+ "]";
	}

}
