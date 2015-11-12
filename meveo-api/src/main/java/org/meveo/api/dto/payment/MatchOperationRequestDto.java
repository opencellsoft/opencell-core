package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "MatchOperationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchOperationRequestDto {

	private String customerAccount;
	private AccountOperationsDto accountOperations;

	public String getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}

	public AccountOperationsDto getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(AccountOperationsDto accountOperations) {
		this.accountOperations = accountOperations;
	}

}
