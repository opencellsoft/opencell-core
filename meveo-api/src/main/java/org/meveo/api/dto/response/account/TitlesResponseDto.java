package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.response.TitlesDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TitlesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitlesResponseDto extends BaseResponse {

	private static final long serialVersionUID = 2597451278315980777L;

	private TitlesDto titles = new TitlesDto();

	public TitlesDto getTitles() {
		return titles;
	}

	public void setTitles(TitlesDto titles) {
		this.titles = titles;
	}

	@Override
	public String toString() {
		return "TitlesResponseDto [titles=" + titles + "]";
	}

}
