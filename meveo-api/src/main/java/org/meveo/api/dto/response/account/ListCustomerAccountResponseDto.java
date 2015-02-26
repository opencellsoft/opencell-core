package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListCustomerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListCustomerAccountResponseDto extends BaseResponse {

	private static final long serialVersionUID = 7705676034964165327L;

	private CustomerAccountsDto customerAccounts = new CustomerAccountsDto();

	public CustomerAccountsDto getCustomerAccounts() {
		return customerAccounts;
	}

	public void setCustomerAccounts(CustomerAccountsDto customerAccounts) {
		this.customerAccounts = customerAccounts;
	}

	@Override
	public String toString() {
		return "ListCustomerAccountResponseDto [customerAccounts=" + customerAccounts + ", toString()="
				+ super.toString() + "]";
	}

}
