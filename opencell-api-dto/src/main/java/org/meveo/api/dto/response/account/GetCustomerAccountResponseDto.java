package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetCustomerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerAccountResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8824614133076085044L;

	private CustomerAccountDto customerAccount;

	public GetCustomerAccountResponseDto() {
		super();
	}

	public CustomerAccountDto getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccountDto customerAccount) {
		this.customerAccount = customerAccount;
	}

	@Override
	public String toString() {
		return "GetCustomerAccountResponse [customerAccount=" + customerAccount + ", toString()=" + super.toString()
				+ "]";
	}

}
