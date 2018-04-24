package org.meveo.api.dto.response.payment;

import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class AccountOperationResponseDto.
 *
 * @author Edward P. Legaspi
 */
public class AccountOperationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9033500265480026870L;

    /** The account operation. */
    private AccountOperationDto accountOperation;

    /**
     * Gets the account operation.
     *
     * @return the account operation
     */
    public AccountOperationDto getAccountOperation() {
        return accountOperation;
    }

    /**
     * Sets the account operation.
     *
     * @param accountOperation the new account operation
     */
    public void setAccountOperation(AccountOperationDto accountOperation) {
        this.accountOperation = accountOperation;
    }

}
