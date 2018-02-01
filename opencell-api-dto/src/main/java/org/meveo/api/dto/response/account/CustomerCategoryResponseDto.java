package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetCustomerCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCategoryResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8824614133076085044L;

	private CustomerCategoryDto customerCategory;

	public CustomerCategoryResponseDto() {
		super();
	}

	public CustomerCategoryDto getCustomerAccount() {
		return customerCategory;
	}

	public void setCustomerAccount(CustomerCategoryDto customerCategory) {
		this.customerCategory = customerCategory;
	}

	@Override
	public String toString() {
		return "GetcustomerCategoryResponse [customerCategory=" + customerCategory + ", toString()=" + super.toString()
				+ "]";
	}

}
