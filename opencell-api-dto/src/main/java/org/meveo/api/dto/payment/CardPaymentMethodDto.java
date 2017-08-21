package org.meveo.api.dto.payment;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;

/**
 * Card payment method
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "CardPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardPaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = 1937059617391182742L;

    /**
     * Card type
     */
    @NotNull
    private CreditCardTypeEnum cardType;

    /**
     * Cardholder: first and last name
     */
    @NotNull
    private String owner;

    /**
     * Card expiration: month
     */
    @NotNull
    @Min(1)
    @Max(12)
    private Integer monthExpiration;

    /**
     * Card expiration: year
     */
    @NotNull
    @Min(0)
    @Max(99)
    private Integer yearExpiration;

    /**
     * Token ID in a payment gateway
     */
    private String tokenId;

    /**
     * Card number: full number or last 4 digits in read operation
     */
    @NotNull
    private String cardNumber;

    /**
     * Issue number
     */
    private String issueNumber;

    /**
     * User identifier
     */
    private String userId;
    
    public CardPaymentMethodDto() {

    }

    public CardPaymentMethodDto(CreditCardTypeEnum cardType, String cardNumber, Integer monthExpiration, Integer yearExpiration, String ownerName) {
        setAlias("Card_" + cardNumber.substring(12, 16));
        setCardNumber(cardNumber);
        setCardType(cardType);
        setMonthExpiration(monthExpiration);
        setYearExpiration(yearExpiration);
        setOwner(ownerName);
    }

    public CardPaymentMethodDto(CardPaymentMethod paymentMethod) {
        super(paymentMethod);
        setCardNumber(paymentMethod.getHiddenCardNumber());
        setCardType(paymentMethod.getCardType());
        setMonthExpiration(paymentMethod.getMonthExpiration());
        setOwner(paymentMethod.getOwner());
        setTokenId(paymentMethod.getTokenId());
        setYearExpiration(paymentMethod.getYearExpiration());
        setUserId(paymentMethod.getUserId());
    }

    public CreditCardTypeEnum getCardType() {
        return cardType;
    }

    public void setCardType(CreditCardTypeEnum cardType) {
        this.cardType = cardType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getMonthExpiration() {
        return monthExpiration;
    }

    public void setMonthExpiration(Integer monthExpiration) {
        this.monthExpiration = monthExpiration;
    }

    public Integer getYearExpiration() {
        return yearExpiration;
    }

    public void setYearExpiration(Integer yearExpiration) {
        this.yearExpiration = yearExpiration;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }

    public CardPaymentMethod fromDto() {
        CardPaymentMethod paymentMethod = new CardPaymentMethod(getAlias(), isPreferred());

        if (tokenId == null) {
            paymentMethod.setCardNumber(getCardNumber());
            paymentMethod.setIssueNumber(getIssueNumber());
        }
        paymentMethod.setHiddenCardNumber(StringUtils.hideCardNumber(getCardNumber()));
        paymentMethod.setOwner(getOwner());
        paymentMethod.setCardType(getCardType());
        paymentMethod.setPreferred(isPreferred());
        paymentMethod.setYearExpiration(getYearExpiration());
        paymentMethod.setMonthExpiration(getMonthExpiration());
        paymentMethod.setUserId(getUserId());

        return paymentMethod;
    }

    @Override
    public String toString() {
        return "CardPaymentMethodDto [alias=" + alias + ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + ", cardType=" + cardType + ", owner=" + owner
                + ", monthExpiration=" + monthExpiration + ", yearExpiration=" + yearExpiration + ", tokenId=" + tokenId + ", cardNumber=" + cardNumber + ", issueNumber="
                + issueNumber + "]";
    }
}