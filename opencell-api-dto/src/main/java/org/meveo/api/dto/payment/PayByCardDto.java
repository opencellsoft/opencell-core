package org.meveo.api.dto.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.payments.CreditCardTypeEnum;

/**
 * The Class PayByCardDto.
 */
@XmlRootElement(name = "PayByCard")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayByCardDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3586462140358234151L;

    /** The customer account code. */
    private String customerAccountCode;

    /** The cts amount. */
    private Long ctsAmount;

    /** The card number. */
    private String cardNumber;

    /** The owner name. */
    private String ownerName;

    /** The cvv. */
    private String cvv;

    /** The expiry date. */
    private String expiryDate;

    /** The card type. */
    private CreditCardTypeEnum cardType;

    /** The ao to pay. */
    private List<Long> aoToPay;

    /** The comment. */
    private String comment;

    /** The create AO. */
    private boolean createAO;

    /** The to match. */
    private boolean toMatch;

    /**
     * Instantiates a new pay by card dto.
     */
    public PayByCardDto() {

    }

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
     * Gets the cts amount.
     *
     * @return the cts amount
     */
    public Long getCtsAmount() {
        return ctsAmount;
    }

    /**
     * Sets the cts amount.
     *
     * @param ctsAmount the new cts amount
     */
    public void setCtsAmount(Long ctsAmount) {
        this.ctsAmount = ctsAmount;
    }

    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment the new comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Checks if is creates the AO.
     *
     * @return true, if is creates the AO
     */
    public boolean isCreateAO() {
        return createAO;
    }

    /**
     * Sets the creates the AO.
     *
     * @param createAO the new creates the AO
     */
    public void setCreateAO(boolean createAO) {
        this.createAO = createAO;
    }

    /**
     * Checks if is to match.
     *
     * @return true, if is to match
     */
    public boolean isToMatch() {
        return toMatch;
    }

    /**
     * Sets the to match.
     *
     * @param isToMatch the new to match
     */
    public void setToMatch(boolean isToMatch) {
        this.toMatch = isToMatch;
    }

    /**
     * Gets the card number.
     *
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number.
     *
     * @param cardNumber the new card number
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the owner name.
     *
     * @return the owner name
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the owner name.
     *
     * @param ownerName the new owner name
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Gets the cvv.
     *
     * @return the cvv
     */
    public String getCvv() {
        return cvv;
    }

    /**
     * Sets the cvv.
     *
     * @param cvv the new cvv
     */
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    /**
     * Gets the expiry date.
     *
     * @return the expiry date
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the expiry date.
     *
     * @param expiryDate the new expiry date
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Gets the card type.
     *
     * @return the card type
     */
    public CreditCardTypeEnum getCardType() {
        return cardType;
    }

    /**
     * Sets the card type.
     *
     * @param cardType the new card type
     */
    public void setCardType(CreditCardTypeEnum cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the ao to pay.
     *
     * @return the ao to pay
     */
    public List<Long> getAoToPay() {
        return aoToPay;
    }

    /**
     * Sets the ao to pay.
     *
     * @param aoToPay the new ao to pay
     */
    public void setAoToPay(List<Long> aoToPay) {
        this.aoToPay = aoToPay;
    }

    @Override
    public String toString() {
        return "PayByCardDto [customerAccountCode=" + customerAccountCode + ", ctsAmount=" + ctsAmount + ", cardNumber=" + cardNumber + ", ownerName=" + ownerName + ", cvv=" + cvv
                + ", expiryDate=" + expiryDate + ", cardType=" + cardType + ", aoToPay=" + aoToPay + ", comment=" + comment + ", createAO=" + createAO + ", toMatch=" + toMatch
                + "]";
    }
}