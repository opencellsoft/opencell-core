package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageIsoDto;

/**
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 **/
@XmlRootElement(name = "GetLanguagesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLanguagesIsoResponse extends BaseResponse {

	private static final long serialVersionUID = -1697478352703038101L;

	private List<LanguageIsoDto> languages;

	public GetLanguagesIsoResponse() {
		super();
	}

	public List<LanguageIsoDto> getLanguages() {
		return languages;
	}

	public void setLanguages(List<LanguageIsoDto> languages) {
		this.languages = languages;
	}

	@Override
	public String toString() {
		return "GetLanguagesIsoResponse [languages=" + languages + "]";
	}

}
