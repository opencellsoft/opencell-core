package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.AccountOperationsDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class AccountOperationsResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "AccountOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6443115315543724968L;

    /** The account operations. */
    private AccountOperationsDto accountOperations = new AccountOperationsDto();

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

    @Override
    public String toString() {
        return "ListAccountOperationsDto [accountOperations=" + accountOperations + "]";
    }
}