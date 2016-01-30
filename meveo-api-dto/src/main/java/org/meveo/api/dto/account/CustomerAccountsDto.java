package org.meveo.api.dto.account;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "CustomerAccounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountsDto {

	private List<CustomerAccountDto> customerAccount;

	public List<CustomerAccountDto> getCustomerAccount() {
		if (customerAccount == null) {
			customerAccount = new ArrayList<CustomerAccountDto>();
		}

		return customerAccount;
	}

	public void setCustomerAccount(List<CustomerAccountDto> customerAccount) {
		this.customerAccount = customerAccount;
	}

	@Override
	public String toString() {
		return "CustomerAccountsDto [customerAccount=" + customerAccount + "]";
	}

}
