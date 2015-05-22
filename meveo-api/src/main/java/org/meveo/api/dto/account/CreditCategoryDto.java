package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.CreditCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CreditCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCategoryDto implements Serializable {

	private static final long serialVersionUID = 9096295121437014513L;

	private String code;
	private String description;

	public CreditCategoryDto() {

	}

	public CreditCategoryDto(CreditCategory e) {
		code = e.getCode();
		description = e.getDescription();
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
		return "CreditCategoryDto [code=" + code + ", description=" + description + "]";
	}

}
