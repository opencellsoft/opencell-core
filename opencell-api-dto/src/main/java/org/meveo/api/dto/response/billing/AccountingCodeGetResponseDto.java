package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class AccountingCodeGetResponseDto.
 *
 * @author Edward P. Legaspi
 * @version 26 Feb 2018
 * @lastModifiedVersion 5.0
 */
public class AccountingCodeGetResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1112540952883899321L;

    /** The accounting code. */
    private AccountingCodeDto accountingCode;

    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public AccountingCodeDto getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(AccountingCodeDto accountingCode) {
        this.accountingCode = accountingCode;
    }

}