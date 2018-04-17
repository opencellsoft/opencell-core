package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class WalletReservationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "WalletReservation")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletReservationDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6900140578404875714L;

    /** The reservation id. */
    private Long reservationId;

    /** The provider code. */
    private String providerCode;

    /** The seller code. */
    private String sellerCode;

    /** The offer code. */
    private String offerCode;

    /** The user account code. */
    private String userAccountCode;

    /** The subscription date. */
    private Date subscriptionDate;

    /** The expiration date. */
    private Date expirationDate;

    /** The termination date. */
    private Date terminationDate;

    /** The credit limit. */
    private BigDecimal creditLimit;

    /** The param 1. */
    private String param1;

    /** The param 2. */
    private String param2;

    /** The param 3. */
    private String param3;

    /** The amount with tax. */
    private boolean amountWithTax;

    /**
     * Gets the provider code.
     *
     * @return the provider code
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * Sets the provider code.
     *
     * @param providerCode the new provider code
     */
    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    /**
     * Gets the seller code.
     *
     * @return the seller code
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * Sets the seller code.
     *
     * @param sellerCode the new seller code
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * Gets the offer code.
     *
     * @return the offer code
     */
    public String getOfferCode() {
        return offerCode;
    }

    /**
     * Sets the offer code.
     *
     * @param offerCode the new offer code
     */
    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    /**
     * Gets the user account code.
     *
     * @return the user account code
     */
    public String getUserAccountCode() {
        return userAccountCode;
    }

    /**
     * Sets the user account code.
     *
     * @param userAccountCode the new user account code
     */
    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the credit limit.
     *
     * @return the credit limit
     */
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the credit limit.
     *
     * @param creditLimit the new credit limit
     */
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    /**
     * Gets the param 1.
     *
     * @return the param 1
     */
    public String getParam1() {
        return param1;
    }

    /**
     * Sets the param 1.
     *
     * @param param1 the new param 1
     */
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    /**
     * Gets the param 2.
     *
     * @return the param 2
     */
    public String getParam2() {
        return param2;
    }

    /**
     * Sets the param 2.
     *
     * @param param2 the new param 2
     */
    public void setParam2(String param2) {
        this.param2 = param2;
    }

    /**
     * Gets the param 3.
     *
     * @return the param 3
     */
    public String getParam3() {
        return param3;
    }

    /**
     * Sets the param 3.
     *
     * @param param3 the new param 3
     */
    public void setParam3(String param3) {
        this.param3 = param3;
    }

    /**
     * Gets the expiration date.
     *
     * @return the expiration date
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiration date.
     *
     * @param expirationDate the new expiration date
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the reservation id.
     *
     * @return the reservation id
     */
    public Long getReservationId() {
        return reservationId;
    }

    /**
     * Sets the reservation id.
     *
     * @param reservationId the new reservation id
     */
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Checks if is amount with tax.
     *
     * @return true, if is amount with tax
     */
    public boolean isAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(boolean amountWithTax) {
        this.amountWithTax = amountWithTax;
    }
}