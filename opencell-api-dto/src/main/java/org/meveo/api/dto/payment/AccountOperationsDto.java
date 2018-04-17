package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class AccountOperationsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "AccountOperationsDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6969737909477126088L;

    /** The account operation. */
    private List<AccountOperationDto> accountOperation;

    /**
     * Gets the account operation.
     *
     * @return the account operation
     */
    public List<AccountOperationDto> getAccountOperation() {
        if (accountOperation == null) {
            accountOperation = new ArrayList<AccountOperationDto>();
        }
        return accountOperation;
    }

    /**
     * Sets the account operation.
     *
     * @param accountOperation the new account operation
     */
    public void setAccountOperation(List<AccountOperationDto> accountOperation) {
        this.accountOperation = accountOperation;
    }


    @Override
    public String toString() {
        return "AccountOperationsDto [accountOperation=" + accountOperation + "]";
    }

}