package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListCustomerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListCustomerAccountResponse extends BaseResponse {

	private static final long serialVersionUID = 7705676034964165327L;

	private List<CustomerAccountDto> customerAccounts;

	public List<CustomerAccountDto> getCustomerAccounts() {
		return customerAccounts;
	}

	public void setCustomerAccounts(List<CustomerAccountDto> customerAccounts) {
		this.customerAccounts = customerAccounts;
	}

}
