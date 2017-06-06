package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.payments.CreditCategory;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CreditCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCategoryDto extends BusinessDto {

	private static final long serialVersionUID = 9096295121437014513L;

	public CreditCategoryDto() {

	}

	public CreditCategoryDto(CreditCategory e) {
		super(e);
	}

	@Override
	public String toString() {
		return "CreditCategoryDto [code=" + getCode() + ", description=" + getDescription() + "]";
	}

}
