package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetCustomerConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerConfigurationResponseDto extends BaseResponse {

	private static final long serialVersionUID = 6164457513010272879L;

	private CustomerBrandsDto customerBrands = new CustomerBrandsDto();
	private CustomerCategoriesDto customerCategories = new CustomerCategoriesDto();
	private TitlesDto titles = new TitlesDto();

	public CustomerBrandsDto getCustomerBrands() {
		return customerBrands;
	}

	public void setCustomerBrands(CustomerBrandsDto customerBrands) {
		this.customerBrands = customerBrands;
	}

	public CustomerCategoriesDto getCustomerCategories() {
		return customerCategories;
	}

	public void setCustomerCategories(CustomerCategoriesDto customerCategories) {
		this.customerCategories = customerCategories;
	}

	public TitlesDto getTitles() {
		return titles;
	}

	public void setTitles(TitlesDto titles) {
		this.titles = titles;
	}

	@Override
	public String toString() {
		return "GetCustomerConfigurationResponseDto [customerBrands=" + customerBrands + ", customerCategories="
				+ customerCategories + ", titles=" + titles + ", toString()=" + super.toString() + "]";
	}

}
