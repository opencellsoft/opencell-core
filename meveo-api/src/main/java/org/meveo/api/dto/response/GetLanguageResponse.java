package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetLanguageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLanguageResponse extends BaseResponse {

	private static final long serialVersionUID = -1697478352703038101L;

	private LanguageDto language;

	public GetLanguageResponse() {
		super();
	}

	public LanguageDto getLanguage() {
		return language;
	}

	public void setLanguage(LanguageDto language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "GetLanguageResponse [language=" + language + "]";
	}

}
