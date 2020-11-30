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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.EnableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.document.Document;

/**
 * Payment method
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@ObservableEntity
@Table(name = "ar_payment_token")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "token_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_token_seq"), })
@NamedQueries({
        @NamedQuery(name = "PaymentMethod.updatePreferredPaymentMethod", query = "UPDATE PaymentMethod pm set pm.preferred = false where pm.id <> :id and pm.customerAccount = :ca"),
        @NamedQuery(name = "PaymentMethod.updateFirstPaymentMethodToPreferred1", query = "select min(pmg.id) from PaymentMethod pmg where pmg.customerAccount.id = :caId and pmg.disabled = false"),
        @NamedQuery(name = "PaymentMethod.updateFirstPaymentMethodToPreferred2", query = "UPDATE PaymentMethod pm set pm.preferred = true where pm.customerAccount.id = :caId and pm.id =:id"),
        @NamedQuery(name = "PaymentMethod.updateFirstPaymentMethodToPreferred3", query = "UPDATE PaymentMethod pm set pm.preferred = false where pm.customerAccount.id = :caId and pm.id <>:id"),
        @NamedQuery(name = "PaymentMethod.getNumberOfPaymentMethods", query = "select count(*) from  PaymentMethod pm where pm.customerAccount.id = :caId and pm.disabled = false"),
        @NamedQuery(name = "PaymentMethod.getPreferredPaymentMethodForCA", query = "select m from PaymentMethod m where m.customerAccount.id =:caId and m.preferred=true"),
        @NamedQuery(name = "PaymentMethod.listByCustomerAccount", query = "select m from PaymentMethod m inner join m.customerAccount ca where ca=:customerAccount") })
public abstract class PaymentMethod extends EnableEntity {

    private static final long serialVersionUID = 8726571628074346184L;

    /**
     * Alias
     */
    @Column(name = "alias")
    protected String alias;

    /**
     * Is it a preferred payment method
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_default")
    protected boolean preferred;

    /**
     * Customer account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    protected CustomerAccount customerAccount;

    /**
     * Payment type
     */
    @Column(name = "token_type", insertable = false, updatable = false, length = 12)
    @Enumerated(EnumType.STRING)
    protected PaymentMethodEnum paymentType;

    /**
     * User identifier
     */
    @Column(name = "USER_ID")
    protected String userId;

    /**
     * Additional information
     */
    @Column(name = "INFO_1", columnDefinition = "TEXT")
    private String info1;

    /**
     * Additional information
     */
    @Column(name = "INFO_2", columnDefinition = "TEXT")
    private String info2;

    /**
     * Additional information
     */
    @Column(name = "INFO_3", columnDefinition = "TEXT")
    private String info3;

    /**
     * Additional information
     */
    @Column(name = "INFO_4", columnDefinition = "TEXT")
    private String info4;

    /**
     * Additional information
     */
    @Column(name = "INFO_5", columnDefinition = "TEXT")
    private String info5;

    /**
     * Token identifier
     */
    @Column(name = "token_id")
    private String tokenId;

    /**
     * Document identifier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document referenceDocument;

    /**
     * Add to deal with payment method auditing
     */
    @Transient
    private String action;

    public PaymentMethod() {
    }

    public PaymentMethod(String alias, boolean preferred, CustomerAccount customerAccount) {
        super();
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public PaymentMethodEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentMethodEnum paymentType) {
        this.paymentType = paymentType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public String getInfo4() {
        return info4;
    }

    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    public String getInfo5() {
        return info5;
    }

    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public abstract void updateWith(PaymentMethod otherPaymentMethod);

    public boolean isExpired() {
        return false;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Document getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(Document referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        } 

        if (this == obj) {
            return true;
        }

        if (this.getClass() == CardPaymentMethod.class) {

            CardPaymentMethod thisCardPaymentMethod = (CardPaymentMethod) this;
            CardPaymentMethod other = (CardPaymentMethod) obj;

            if (thisCardPaymentMethod == other) {
                return true;
            } 

            if (thisCardPaymentMethod.getId() != null && other.getId() != null
                    && thisCardPaymentMethod.getId().equals(other.getId())) {
                return true;
            }

            return StringUtils.compare(thisCardPaymentMethod.getHiddenCardNumber(),
                other.getHiddenCardNumber()) == 0
                && thisCardPaymentMethod.getMonthExpiration().equals(other.getMonthExpiration())
                && thisCardPaymentMethod.getYearExpiration().equals(other.getYearExpiration());
        }
        
        if (this.getClass() == DDPaymentMethod.class) {

            DDPaymentMethod thisDDPaymentMethod = (DDPaymentMethod) this;
            DDPaymentMethod other = (DDPaymentMethod) obj;

            if (thisDDPaymentMethod == other) {
                return true;
            } 

            if (thisDDPaymentMethod.getId() != null && other.getId() != null
                    && thisDDPaymentMethod.getId().equals(other.getId())) {
                return true;
            }
            if (thisDDPaymentMethod.getMandateIdentification() != null && thisDDPaymentMethod
                    .getMandateIdentification().equals(other.getMandateIdentification())) {
                return true;
            }
            if (thisDDPaymentMethod.getBankCoordinates() != null) {
                return thisDDPaymentMethod.getBankCoordinates().equals(other.getBankCoordinates());
            }
            
            return false;
        }
        
        return true;
    }

}