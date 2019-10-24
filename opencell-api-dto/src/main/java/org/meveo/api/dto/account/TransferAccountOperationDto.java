package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Represents the data necessary to establish an account operation transfer from one customer to another .
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@XmlRootElement(name = "TransferCustomerAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferAccountOperationDto implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2310786528437213936L;

    /**
     * The source customer account.
     */
    @XmlElement(required = true)
    private String fromCustomerAccountCode;

    /**
     * The account operation id.
     */
    @XmlElement()
    private Long accountOperationId;

    /**
     * The recipient customer accounts.
     */
    private List<TransferCustomerAccountDto> toCustomerAccounts;

    /**
     * Gets the fromCustomerAccountCode
     *
     * @return the fromCustomerAccountCode
     */
    public String getFromCustomerAccountCode() {
        return fromCustomerAccountCode;
    }

    /**
     * Sets the fromCustomerAccountCode.
     *
     * @param fromCustomerAccountCode the new fromCustomerAccountCode
     */
    public void setFromCustomerAccountCode(String fromCustomerAccountCode) {
        this.fromCustomerAccountCode = fromCustomerAccountCode;
    }

    /**
     * Gets the accountOperationId
     *
     * @return the accountOperationId
     */
    public Long getAccountOperationId() {
        return accountOperationId;
    }

    /**
     * Sets the accountOperationId.
     *
     * @param accountOperationId the new accountOperationId
     */
    public void setAccountOperationId(Long accountOperationId) {
        this.accountOperationId = accountOperationId;
    }

    /**
     * Gets the toCustomerAccounts
     *
     * @return the toCustomerAccounts
     */
    public List<TransferCustomerAccountDto> getToCustomerAccounts() {
        return toCustomerAccounts;
    }

    /**
     * Sets the toCustomerAccounts.
     *
     * @param toCustomerAccounts the new toCustomerAccounts
     */
    public void setToCustomerAccounts(List<TransferCustomerAccountDto> toCustomerAccounts) {
        this.toCustomerAccounts = toCustomerAccounts;
    }

    @Override
    public String toString() {
        return "TransferAccountOperationDto{" + "fromCustomerAccountCode='" + fromCustomerAccountCode + '\'' + ", accountOperationId=" + accountOperationId
                + ", toCustomerAccounts=" + toCustomerAccounts + '}';
    }
}