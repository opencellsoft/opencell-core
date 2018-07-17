package org.meveo.api.dto.account;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class CustomerAccountsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountsDto {

    /** The customer account. */
    private List<CustomerAccountDto> customerAccount;

    /**
     * Gets the customer account.
     *
     * @return the customer account
     */
    public List<CustomerAccountDto> getCustomerAccount() {
        if (customerAccount == null) {
            customerAccount = new ArrayList<>();
        }

        return customerAccount;
    }

    /**
     * Sets the customer account.
     *
     * @param customerAccount the new customer account
     */
    public void setCustomerAccount(List<CustomerAccountDto> customerAccount) {
        this.customerAccount = customerAccount;
    }

    @Override
    public String toString() {
        return "CustomerAccountsDto [customerAccount=" + customerAccount + "]";
    }

}