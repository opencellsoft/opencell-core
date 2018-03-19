package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 * @version 26 Feb 2018
 **/
public class AccountingCodeGetResponseDto extends BaseResponse {

    private static final long serialVersionUID = 1112540952883899321L;

    private AccountingCodeDto accountingCode;

    public AccountingCodeDto getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCodeDto accountingCode) {
        this.accountingCode = accountingCode;
    }

}
