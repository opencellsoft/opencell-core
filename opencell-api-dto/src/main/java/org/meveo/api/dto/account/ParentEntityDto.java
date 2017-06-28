package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Tony Alejandro.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParentEntityDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	public ParentEntityDto() {
	}

	public ParentEntityDto(String code, String description) {
		this.code = code;
		this.description = description;
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

	@Override
	public String toString() {
		return "ParentEntityDto [code=" + code + ", description=" + description + "]";
	}
}
