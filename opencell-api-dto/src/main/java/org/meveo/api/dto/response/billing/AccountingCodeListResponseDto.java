package org.meveo.api.dto.response.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class AccountingCodeListResponse.
 *
 * @author Edward P. Legaspi
 * @version 26 Feb 2018
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "AccountingCodeListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountingCodeListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3336861374417524813L;

    /** The accounting codes. */
    private List<AccountingCodeDto> accountingCodes;

    /**
     * Gets the accounting codes.
     *
     * @return the accounting codes
     */
    public List<AccountingCodeDto> getAccountingCodes() {
        if (accountingCodes == null) {
            accountingCodes = new ArrayList<>();
        }
        return accountingCodes;
    }

    /**
     * Sets the accounting codes.
     *
     * @param accountingCodes the new accounting codes
     */
    public void setAccountingCodes(List<AccountingCodeDto> accountingCodes) {
        this.accountingCodes = accountingCodes;
    }

}
