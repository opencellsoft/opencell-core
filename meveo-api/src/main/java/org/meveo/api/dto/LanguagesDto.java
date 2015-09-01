package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Languages")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguagesDto extends BaseDto {

	private static final long serialVersionUID = 4455041168159380792L;

	private List<LanguageDto> language;

	public List<LanguageDto> getLanguage() {
		if (language == null)
			language = new ArrayList<LanguageDto>();
		return language;
	}

	public void setLanguage(List<LanguageDto> language) {
		this.language = language;
	}

}
