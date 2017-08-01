package org.meveo.api.dto.response.payment;

import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
public class AccountOperationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -9033500265480026870L;

	private AccountOperationDto accountOperation;

	public AccountOperationDto getAccountOperation() {
		return accountOperation;
	}

	public void setAccountOperation(AccountOperationDto accountOperation) {
		this.accountOperation = accountOperation;
	}

}
