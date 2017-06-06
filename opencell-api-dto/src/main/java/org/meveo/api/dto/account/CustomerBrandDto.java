package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.crm.CustomerBrand;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerBrand")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerBrandDto extends BusinessDto {

	private static final long serialVersionUID = -6809423084709875338L;

	public CustomerBrandDto() {

	}

	public CustomerBrandDto(CustomerBrand e) {
		super(e);
	}

	@Override
	public String toString() {
		return "CustomerBrandDto [code=" + getCode() + ", description=" + getDescription() + "]";
	}

}
