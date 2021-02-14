/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.payments;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.shared.DateUtils;

/**
 * Payment by card method
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@DiscriminatorValue(value = "CARD")
@NamedQueries({ @NamedQuery(name = "PaymentMethod.getNumberOfTokenId", query = "select count(*) from  CardPaymentMethod pm where pm.tokenId = :tokenId and pm.disabled = false"),
        @NamedQuery(name = "PaymentMethod.getNumberOfCardCustomerAccount", query = "select count(*) from  CardPaymentMethod pm where    pm.customerAccount.id = :customerAccountId and pm.monthExpiration = :monthExpiration and pm.yearExpiration = :yearExpiration and pm.hiddenCardNumber = :hiddenCardNumber and pm.cardType = :cardType and pm.disabled = false") })
public class CardPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571628074346184L;

    /**
     * Card type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CreditCardTypeEnum cardType;

    /**
     * Owner as printed on the card
     */
    @Column(name = "owner")
    private String owner;

    /**
     * Expiration month
     */
    @Column(name = "month_expiration")
    @Min(1)
    @Max(12)
    private Integer monthExpiration;

    /**
     * Expiration year
     */
    @Column(name = "year_expiration")
    @Min(0)
    @Max(99)
    private Integer yearExpiration;

    /**
     * Card number last 4 digits
     */
    @Column(name = "card_number")
    private String hiddenCardNumber;

    /**
     * Full card number. Used at data entry time.
     */
    @Transient
    private String cardNumber;

    /**
     * Issue number
     */
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

    public CardPaymentMethod(CustomerAccount customerAccount, boolean isDisabled, String alias, String cardNumber, String owner, boolean preferred, String issueNumber,
            Integer yearExpiration, Integer monthExpiration, CreditCardTypeEnum cardType) {
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
    
    public void anonymize(String code) {
        super.anonymize(code);
        setOwner(code);
        setMonthExpiration(null);
        setYearExpiration(null);
        setCardNumber(code);
        setCardType(null);
        setIssueNumber(code);
     }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getExpirationMonthAndYear() {
        if(monthExpiration != null && yearExpiration != null) {
            return (monthExpiration != null && monthExpiration < 10 ? "0" : "") + monthExpiration + "/" + yearExpiration;
        }
        
        return "";
    }

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        CardPaymentMethod otherPaymentMethod = (CardPaymentMethod) paymentMethod;

        setAlias(otherPaymentMethod.getAlias());
        setPreferred(otherPaymentMethod.isPreferred());

        // The rest of information is not updatable if token was generated already
        if (getTokenId() != null) {
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
        if(yearExpiration != null && monthExpiration != null) {
            int year = new Integer(DateUtils.getYearFromDate(date).toString().substring(2, 4));
            int month = DateUtils.getMonthFromDate(new Date());
            return yearExpiration.intValue() > year || (yearExpiration.intValue() == year && monthExpiration >= month);
        }
        return true;
    }

    @Override
    public String toString() {
        return "CardPaymentMethod [tokenId=" + getTokenId() + ", cardType=" + getCardType() + ", owner=" + getOwner() + ", monthExpiration=" + getMonthExpiration()
                + ", yearExpiration=" + getYearExpiration() + ", cardNumber=" + getHiddenCardNumber() + ", userId=" + getUserId() + ", info1=" + getInfo1() + ", info2="
                + getInfo2() + ", info3=" + getInfo3() + ", info4=" + getInfo4() + ", info5=" + getInfo5() + ", issueNumber=" + getIssueNumber() + "]";
    }

    public static String hideCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return "invalid";
        }
        cardNumber = cardNumber.replaceAll("\\s+", "");
        if (cardNumber.length() == 4 || cardNumber.length() == 16 || cardNumber.length() == 15) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return "invalid";
    }

    @Override
    public boolean isExpired() {
        if(yearExpiration == null || monthExpiration == null) {
            return false;
        }
        
        Calendar now = Calendar.getInstance();

        Calendar expiration = Calendar.getInstance();
        expiration.set(Calendar.MONTH, monthExpiration - 1);
        expiration.set(Calendar.YEAR, yearExpiration + 2000);

        return (now.getTime().after(expiration.getTime())) ? true : false;
    }
}