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

	private String customerAccountCode;
	private AccountOperationsDto accountOperations;



	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}

	public AccountOperationsDto getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(AccountOperationsDto accountOperations) {
		this.accountOperations = accountOperations;
	}

}
