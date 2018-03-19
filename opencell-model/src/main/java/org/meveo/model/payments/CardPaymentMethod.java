package org.meveo.model.payments;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@DiscriminatorValue(value = "CARD")
public class CardPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571628074346184L;

    @Column(name = "token_id")
    private String tokenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    @NotNull
    private CreditCardTypeEnum cardType;

    @Column(name = "owner")
    @NotNull
    private String owner;

    @Column(name = "month_expiration")
    @NotNull
    @Min(1)
    @Max(12)
    private Integer monthExpiration;

    @Column(name = "year_expiration")
    @NotNull
    @Min(0)
    @Max(99)
    private Integer yearExpiration;

    @Column(name = "card_number")
    @NotNull
    private String hiddenCardNumber;

    @Transient
    private String cardNumber;

    @Transient
    private String issueNumber;

    public CardPaymentMethod() {
        this.paymentType = PaymentMethodEnum.CARD;
    }

    public CardPaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.CARD;
        this.alias = alias;
        this.preferred = preferred;
    }

    public CardPaymentMethod(CustomerAccount customerAccount,boolean isDisabled, String alias, String cardNumber, String owner, boolean preferred, String issueNumber, Integer yearExpiration,
            Integer monthExpiration, CreditCardTypeEnum cardType) {
        super();
        setDisabled(isDisabled);
        setPaymentType(PaymentMethodEnum.CARD);
        setAlias(alias);
        setPreferred(preferred);
        this.customerAccount = customerAccount;
        this.cardNumber = cardNumber;
        this.hiddenCardNumber = CardPaymentMethod.hideCardNumber(cardNumber);
        this.owner = owner;
        this.issueNumber = issueNumber;
        this.yearExpiration = yearExpiration;
        this.monthExpiration = monthExpiration;
        this.cardType = cardType;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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

    public String getHiddenCardNumber() {
        return hiddenCardNumber;
    }

    public void setHiddenCardNumber(String hiddenCardNumber) {
        this.hiddenCardNumber = hiddenCardNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CardPaymentMethod)) {
            return false;
        }

        CardPaymentMethod other = (CardPaymentMethod) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        return StringUtils.compare(hiddenCardNumber, other.getHiddenCardNumber()) == 0 && monthExpiration.equals(other.getMonthExpiration())
                && yearExpiration.equals(other.getYearExpiration());
    }

    public String getExpirationMonthAndYear() {
        return (monthExpiration != null && monthExpiration < 10 ? "0" : "") + monthExpiration + "/" + yearExpiration;
    }

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        CardPaymentMethod otherPaymentMethod = (CardPaymentMethod) paymentMethod;

        setAlias(otherPaymentMethod.getAlias());
        setPreferred(otherPaymentMethod.isPreferred());

        // The rest of information is not updatable if token was generated already
        if (tokenId != null) {
            return;
        }
        setCardNumber(otherPaymentMethod.getCardNumber());
        setIssueNumber(otherPaymentMethod.getIssueNumber());

        setHiddenCardNumber(CardPaymentMethod.hideCardNumber(otherPaymentMethod.getCardNumber()));
        setOwner(otherPaymentMethod.getOwner());
        setCardType(otherPaymentMethod.getCardType());
        setPreferred(otherPaymentMethod.isPreferred());
        setYearExpiration(otherPaymentMethod.getYearExpiration());
        setMonthExpiration(otherPaymentMethod.getMonthExpiration());
        setUserId(otherPaymentMethod.getUserId());
        setInfo1(otherPaymentMethod.getInfo1());
        setInfo2(otherPaymentMethod.getInfo2());
        setInfo3(otherPaymentMethod.getInfo3());
        setInfo4(otherPaymentMethod.getInfo4());
        setInfo5(otherPaymentMethod.getInfo5());
    }

    /**
     * Is card valid for a given date
     * 
     * @param date Date to check
     * @return True is expiration date is beyond a given date
     */
    public boolean isValidForDate(Date date) {

        int year = new Integer(DateUtils.getYearFromDate(date).toString().substring(2, 4));
        int month = DateUtils.getMonthFromDate(new Date());
        return yearExpiration.intValue() > year || (yearExpiration.intValue() == year && monthExpiration >= month);
    }

    @Override
    public String toString() {
        return "CardPaymentMethod [tokenId=" + tokenId + ", cardType=" + cardType + ", owner=" + owner + ", monthExpiration=" + monthExpiration + ", yearExpiration="
                + yearExpiration + ", hiddenCardNumber=" + hiddenCardNumber + ", userId=" + getUserId() + ", info1=" + getInfo1() + ", info2=" + getInfo2() + ", info3="
                + getInfo3() + ", info4=" + getInfo4() + ", info5=" + getInfo5() + ", cardNumber=" + cardNumber + ", issueNumber=" + issueNumber + "]";
    }

    public static String hideCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return "invalid";
        }
        cardNumber = cardNumber.replaceAll("\\s+","");
        if (cardNumber.length() == 4 || cardNumber.length() == 16 || cardNumber.length() == 15) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return "invalid";
    }
    
    @Override
    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        
        Calendar expiration = Calendar.getInstance();
        expiration.set(Calendar.MONTH, monthExpiration - 1);
        expiration.set(Calendar.YEAR, yearExpiration + 2000);
        
        return (now.getTime().after(expiration.getTime())) ? true : false;
    }
}