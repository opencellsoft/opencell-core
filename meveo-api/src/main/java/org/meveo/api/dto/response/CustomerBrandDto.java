package org.meveo.api.dto.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomerBrand;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerBrand")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerBrandDto implements Serializable {

	private static final long serialVersionUID = -6809423084709875338L;

	private String code;
	private String description;

	public CustomerBrandDto() {

	}

	public CustomerBrandDto(CustomerBrand e) {
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
		return "CustomerBrandDto [code=" + code + ", description=" + description + "]";
	}

}
