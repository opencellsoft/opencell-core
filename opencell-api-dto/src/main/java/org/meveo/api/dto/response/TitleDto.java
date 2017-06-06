package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.shared.Title;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Title")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleDto extends BusinessDto {

	private static final long serialVersionUID = -1332916104721562522L;

	private Boolean isCompany = Boolean.FALSE;

	public TitleDto() {

	}

	public TitleDto(Title e) {
		super(e);
		isCompany = e.getIsCompany();
	}

	public Boolean getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(Boolean isCompany) {
		this.isCompany = isCompany;
	}

	@Override
	public String toString() {
		return "TitleDto [code=" + getCode() + ", description=" + getDescription() + ", isCompany=" + isCompany + "]";
	}

}
