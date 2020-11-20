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

package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;

/**
 * The Class ApplyOneShotChargeInstanceRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ApplyOneShotChargeInstanceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyOneShotChargeInstanceRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3910185882621015476L;

    /** The one shot charge. */
    @XmlElement(required = true)
    private String oneShotCharge;

    /**
     * The subscription.
     */
    @XmlElement(required = true)
    private String subscription;


    private Date subscriptionValidityDate;

    /**
     * The wallet.
     */
    private String wallet;

    /**
     * The create wallet.
     */
    private Boolean createWallet;

    /**
     * The operation date.
     */
    @XmlElement(required = true)
    private Date operationDate;

    /**
     * The quantity.
     */
    private BigDecimal quantity;

    /**
     * The description.
     */
    private String description;

    /**
     * The amount without tax.
     */
    private BigDecimal amountWithoutTax;

    /**
     * The amount with tax.
     */
    private BigDecimal amountWithTax;

    /**
     * The criteria 1.
     */
    private String criteria1;

    /**
     * The criteria 2.
     */
    private String criteria2;

    /**
     * The criteria 3.
     */
    private String criteria3;

    /**
     * The custom fields.
     */
    @XmlElement(required = false)
    private CustomFieldsDto customFields;

    /**
     * Gets the one shot charge.
     *
     * @return the one shot charge
     */
    public String getOneShotCharge() {
        return oneShotCharge;
    }

    /**
     * Sets the one shot charge.
     *
     * @param oneShotCharge the new one shot charge
     */
    public void setOneShotCharge(String oneShotCharge) {
        this.oneShotCharge = oneShotCharge;
    }

    /**
     * Gets the wallet.
     *
     * @return the wallet
     */
    public String getWallet() {
        return wallet;
    }

    /**
     * Sets the wallet.
     *
     * @param wallet the new wallet
     */
    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    /**
     * Gets the creates the wallet.
     *
     * @return the creates the wallet
     */
    public Boolean getCreateWallet() {
        return createWallet;
    }

    /**
     * Sets the creates the wallet.
     *
     * @param createWallet the new creates the wallet
     */
    public void setCreateWallet(Boolean createWallet) {
        this.createWallet = createWallet;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the operation date.
     *
     * @return the operation date
     */
    public Date getOperationDate() {
        return operationDate;
    }

    /**
     * Sets the operation date.
     *
     * @param operationDate the new operation date
     */
    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Gets the criteria 1.
     *
     * @return the criteria 1
     */
    public String getCriteria1() {
        return criteria1;
    }

    /**
     * Sets the criteria 1.
     *
     * @param criteria1 the new criteria 1
     */
    public void setCriteria1(String criteria1) {
        this.criteria1 = criteria1;
    }

    /**
     * Gets the criteria 2.
     *
     * @return the criteria 2
     */
    public String getCriteria2() {
        return criteria2;
    }

    /**
     * Sets the criteria 2.
     *
     * @param criteria2 the new criteria 2
     */
    public void setCriteria2(String criteria2) {
        this.criteria2 = criteria2;
    }

    /**
     * Gets the criteria 3.
     *
     * @return the criteria 3
     */
    public String getCriteria3() {
        return criteria3;
    }

    /**
     * Sets the criteria 3.
     *
     * @param criteria3 the new criteria 3
     */
    public void setCriteria3(String criteria3) {
        this.criteria3 = criteria3;
    }

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    /**
     * Get Custom fields DTO.
     *
     * @return
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Set Custom fields DTO.
     *
     * @param customFields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public Date getSubscriptionValidityDate() {
        return this.subscriptionValidityDate;
    }

    public void setSubscriptionValidityDate(Date subscriptionValidityDate) {
        this.subscriptionValidityDate = subscriptionValidityDate;
    }

    @Override
    public String toString() {
        return "ApplyOneShotChargeInstanceDto [oneShotCharge=" + oneShotCharge + ", subscription=" + subscription + ", wallet=" + wallet + ", operationDate=" + operationDate
                + ", description=" + description + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + ", criteria1=" + criteria1 + ", criteria2="
                + criteria2 + ", criteria3=" + criteria3 + "]";
    }
}
