package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.response.TitleDto;

@XmlRootElement(name = "TitleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleResponseDto extends BaseResponse {

	private static final long serialVersionUID = -1990918305354682187L;

	private TitleDto titleDto;

	public TitleDto getTitleDto() {
		return titleDto;
	}

	public void setTitleDto(TitleDto titleDto) {
		this.titleDto = titleDto;
	}
	
	@Override
	public String toString() {
		return "TitleResponse [title=" + titleDto + ", toString()=" + super.toString() + "]";
	}
}
