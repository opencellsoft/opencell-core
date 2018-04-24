package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class BillingAccountsResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BillingAccountsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingAccountsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 583740580596077812L;

    /** The billing accounts. */
    private BillingAccountsDto billingAccounts = new BillingAccountsDto();

    /**
     * Gets the billing accounts.
     *
     * @return the billing accounts
     */
    public BillingAccountsDto getBillingAccounts() {
        return billingAccounts;
    }

    /**
     * Sets the billing accounts.
     *
     * @param billingAccounts the new billing accounts
     */
    public void setBillingAccounts(BillingAccountsDto billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

    @Override
    public String toString() {
        return "ListBillingAccountResponseDto [billingAccounts=" + billingAccounts + ", toString()=" + super.toString() + "]";
    }
}