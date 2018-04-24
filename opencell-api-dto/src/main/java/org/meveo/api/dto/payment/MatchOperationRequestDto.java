package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MatchOperationRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "MatchOperationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchOperationRequestDto {

    /** The customer account code. */
    private String customerAccountCode;
    
    /** The account operations. */
    private AccountOperationsDto accountOperations;

    /**
     * Gets the customer account code.
     *
     * @return the customer account code
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * Sets the customer account code.
     *
     * @param customerAccountCode the new customer account code
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * Gets the account operations.
     *
     * @return the account operations
     */
    public AccountOperationsDto getAccountOperations() {
        return accountOperations;
    }

    /**
     * Sets the account operations.
     *
     * @param accountOperations the new account operations
     */
    public void setAccountOperations(AccountOperationsDto accountOperations) {
        this.accountOperations = accountOperations;
    }

}
