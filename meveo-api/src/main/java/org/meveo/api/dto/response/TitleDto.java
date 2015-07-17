package org.meveo.api.dto.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.shared.Title;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Title")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleDto implements Serializable {

	private static final long serialVersionUID = -1332916104721562522L;

	private String code;
	private String description;
	private Boolean isCompany = Boolean.FALSE;

	public TitleDto() {

	}

	public TitleDto(Title e) {
		code = e.getCode();
		description = e.getDescription();
		isCompany = e.getIsCompany();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(Boolean isCompany) {
		this.isCompany = isCompany;
	}

	@Override
	public String toString() {
		return "TitleDto [code=" + code + ", description=" + description + ", isCompany=" + isCompany + "]";
	}

}
