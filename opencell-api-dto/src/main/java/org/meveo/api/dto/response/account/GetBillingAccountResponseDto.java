package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBillingAccountResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetBillingAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingAccountResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8538364402251002467L;

    /** The billing account. */
    private BillingAccountDto billingAccount;

    /**
     * Gets the billing account.
     *
     * @return the billing account
     */
    public BillingAccountDto getBillingAccount() {
        return billingAccount;
    }

    /**
     * Sets the billing account.
     *
     * @param billingAccount the new billing account
     */
    public void setBillingAccount(BillingAccountDto billingAccount) {
        this.billingAccount = billingAccount;
    }

    @Override
    public String toString() {
        return "GetBillingAccountResponse [billingAccount=" + billingAccount + ", toString()=" + super.toString() + "]";
    }
}