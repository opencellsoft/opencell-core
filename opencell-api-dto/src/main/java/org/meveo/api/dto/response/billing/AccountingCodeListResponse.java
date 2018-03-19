package org.meveo.api.dto.response.billing;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * @author Edward P. Legaspi
 * @version 26 Feb 2018
 * @lastModifiedVersion 5.0
 **/
public class AccountingCodeListResponse extends SearchResponse {

    private static final long serialVersionUID = 3336861374417524813L;

    private List<AccountingCodeDto> accountingCodes;

    public List<AccountingCodeDto> getAccountingCodes() {
        if (accountingCodes == null) {
            accountingCodes = new ArrayList<>();
        }
        return accountingCodes;
    }

    public void setAccountingCodes(List<AccountingCodeDto> accountingCodes) {
        this.accountingCodes = accountingCodes;
    }

}
