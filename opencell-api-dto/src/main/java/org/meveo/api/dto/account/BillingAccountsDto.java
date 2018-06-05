package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class BillingAccountsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingAccountsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2034319035301583131L;

    /** The billing account. */
    private List<BillingAccountDto> billingAccount = new ArrayList<>();

    /**
     * Gets the billing account.
     *
     * @return the billing account
     */
	public List<BillingAccountDto> getBillingAccount() {
		if (billingAccount == null) {
			billingAccount = new ArrayList<>();
		}
		return billingAccount;
	}

    /**
     * Sets the billing account.
     *
     * @param billingAccount the new billing account
     */
    public void setBillingAccount(List<BillingAccountDto> billingAccount) {
        this.billingAccount = billingAccount;
    }

    @Override
    public String toString() {
        return "BillingAccountsDto [billingAccount=" + billingAccount + "]";
    }
}