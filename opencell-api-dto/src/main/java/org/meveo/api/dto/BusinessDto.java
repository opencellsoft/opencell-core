package org.meveo.api.dto;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BusinessDto extends BaseDto {

	private static final long serialVersionUID = 4451119256601996946L;

	@Pattern(regexp = "^[@A-Za-z0-9_\\.-]+$")
	@XmlAttribute(required = true)
	protected String code;

	@XmlAttribute()
	protected String description;

	protected String updatedCode;

	public BusinessDto() {

	}

	public BusinessDto(BusinessEntity e) {
		if (e != null) {
			code = e.getCode();
			description = e.getDescription();
		}
	}

	public String getUpdatedCode() {
		return updatedCode;
	}

	public void setUpdatedCode(String updatedCode) {
		this.updatedCode = updatedCode;
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

}
