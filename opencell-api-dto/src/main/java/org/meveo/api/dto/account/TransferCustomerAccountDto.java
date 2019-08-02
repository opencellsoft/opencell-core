package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents the data necessary to establish an amount transfer from one customer to another .
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@XmlRootElement(name = "TransferCustomerAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferCustomerAccountDto implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -6201276035864455506L;

    /**
     * The source customer account.
     */
    @XmlElement(required = true)
    private String fromCustomerAccountCode;

    /**
     * The recipient customer account.
     */
    @XmlElement(required = true)
    private String toCustomerAccountCode;

    /**
     * The amount.
     */
    @XmlElement()
    private BigDecimal amount;

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
     * Gets the toCustomerAccountCode
     *
     * @return the toCustomerAccountCode
     */
    public String getToCustomerAccountCode() {
        return toCustomerAccountCode;
    }

    /**
     * Sets the toCustomerAccountCode.
     *
     * @param toCustomerAccountCode the new toCustomerAccountCode
     */
    public void setToCustomerAccountCode(String toCustomerAccountCode) {
        this.toCustomerAccountCode = toCustomerAccountCode;
    }

    /**
     * Gets the amount
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferCustomerAccountDto{" + "fromCustomerAccountCode='" + fromCustomerAccountCode + '\'' + ", toCustomerAccountCode='" + toCustomerAccountCode + '\''
                + ", amount=" + amount + '}';
    }
}