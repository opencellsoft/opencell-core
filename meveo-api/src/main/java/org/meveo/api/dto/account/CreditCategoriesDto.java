package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CreditCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCategoriesDto implements Serializable {

	private static final long serialVersionUID = 6348231694321723085L;
	
	private List<CreditCategoryDto> creditCategory;

	public List<CreditCategoryDto> getCreditCategory() {
		if (creditCategory == null) {
			creditCategory = new ArrayList<CreditCategoryDto>();
		}
		return creditCategory;
	}

	public void setCreditCategory(List<CreditCategoryDto> creditCategory) {
		this.creditCategory = creditCategory;
	}

}
