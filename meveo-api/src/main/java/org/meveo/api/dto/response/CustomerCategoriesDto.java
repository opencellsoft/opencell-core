package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomerCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCategoriesDto implements Serializable {

	private static final long serialVersionUID = 88289090554367171L;

	private List<CustomerCategoryDto> customerCategory;

	public List<CustomerCategoryDto> getCustomerCategory() {
		if (customerCategory == null)
			customerCategory = new ArrayList<CustomerCategoryDto>();
		return customerCategory;
	}

	public void setCustomerCategory(List<CustomerCategoryDto> customerCategory) {
		this.customerCategory = customerCategory;
	}

	@Override
	public String toString() {
		return "CustomerCategoriesDto [customerCategory=" + customerCategory + "]";
	}

}
