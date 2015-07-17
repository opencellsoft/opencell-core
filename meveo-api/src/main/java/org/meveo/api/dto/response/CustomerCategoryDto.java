package org.meveo.api.dto.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomerCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCategoryDto implements Serializable {

	private static final long serialVersionUID = -8778571285967620018L;

	private String code;
	private String description;

	public CustomerCategoryDto() {

	}

	public CustomerCategoryDto(CustomerCategory e) {
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
		return "CustomerCategoryDto [code=" + code + ", description=" + description + "]";
	}

}
