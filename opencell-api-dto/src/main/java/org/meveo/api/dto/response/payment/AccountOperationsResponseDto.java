package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.AccountOperationsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "AccountOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationsResponseDto extends BaseResponse {

	private static final long serialVersionUID = 6443115315543724968L;

	private AccountOperationsDto accountOperations = new AccountOperationsDto();

	public AccountOperationsDto getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(AccountOperationsDto accountOperations) {
		this.accountOperations = accountOperations;
	}

	@Override
	public String toString() {
		return "ListAccountOperationsDto [accountOperations=" + accountOperations + "]";
	}

}
