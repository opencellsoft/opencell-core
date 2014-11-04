package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomerAccountDto;

/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "CustomerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountResponse extends BaseResponse {

	private static final long serialVersionUID = -8824614133076085044L;

	private CustomerAccountDto customerAccountDto;

	public CustomerAccountResponse() {
		super();
	}

	public CustomerAccountDto getCustomerAccountDto() {
		return customerAccountDto;
	}

	public void setCustomerAccountDto(CustomerAccountDto customerAccountDto) {
		this.customerAccountDto = customerAccountDto;
	}

}
