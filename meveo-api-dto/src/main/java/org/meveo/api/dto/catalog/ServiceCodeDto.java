package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceCodeDto {

	@NotNull
	@XmlAttribute
	private String code;

	@XmlAttribute
	private String description;

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

	@Override
	public String toString() {
		return "ServiceCodeDto [code=" + code + ", description=" + description + "]";
	}

}
