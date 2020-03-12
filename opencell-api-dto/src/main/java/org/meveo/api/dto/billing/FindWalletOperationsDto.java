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

package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.billing.WalletOperationStatusEnum;

/**
 * The Class FindWalletOperationsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class FindWalletOperationsDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4342970913973071312L;

    /** The status. */
    private WalletOperationStatusEnum status;

    /** The wallet template. */
    private String walletTemplate;

    /** The user account. */
    @XmlElement(required = true)
    private String userAccount;

    /** The from date. */
    private Date fromDate;

    /** The to date. */
    private Date toDate;

    /** The charge template code. */
    private String chargeTemplateCode;

    /** The parameter 1. */
    private String parameter1;

    /** The parameter 2. */
    private String parameter2;

    /** The parameter 3. */
    private String parameter3;

    /** The offer template code. */
    private String offerTemplateCode;

    /** The subscription code. */
    private String subscriptionCode;

    /** The order number. */
    private String orderNumber;

    /**
     * Gets the status.
     *
     * @return the status
     */
    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the wallet template.
     *
     * @return the wallet template
     */
    public String getWalletTemplate() {
        return walletTemplate;
    }

    /**
     * Sets the wallet template.
     *
     * @param walletTemplate the new wallet template
     */
    public void setWalletTemplate(String walletTemplate) {
        this.walletTemplate = walletTemplate;
    }

    /**
     * Gets the user account.
     *
     * @return the user account
     */
    public String getUserAccount() {
        return userAccount;
    }

    /**
     * Sets the user account.
     *
     * @param userAccount the new user account
     */
    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the new from date
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Gets the charge template code.
     *
     * @return the charge template code
     */
    public String getChargeTemplateCode() {
        return chargeTemplateCode;
    }

    /**
     * Sets the charge template code.
     *
     * @param chargeTemplateCode the new charge template code
     */
    public void setChargeTemplateCode(String chargeTemplateCode) {
        this.chargeTemplateCode = chargeTemplateCode;
    }

    /**
     * Gets the parameter 1.
     *
     * @return the parameter 1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Sets the parameter 1.
     *
     * @param parameter1 the new parameter 1
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * Gets the parameter 2.
     *
     * @return the parameter 2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Sets the parameter 2.
     *
     * @param parameter2 the new parameter 2
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    /**
     * Gets the parameter 3.
     *
     * @return the parameter 3
     */
    public String getParameter3() {
        return parameter3;
    }

    /**
     * Sets the parameter 3.
     *
     * @param parameter3 the new parameter 3
     */
    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    /**
     * Gets the offer template code.
     *
     * @return the offer template code
     */
    public String getOfferTemplateCode() {
        return offerTemplateCode;
    }

    /**
     * Sets the offer template code.
     *
     * @param offerTemplateCode the new offer template code
     */
    public void setOfferTemplateCode(String offerTemplateCode) {
        this.offerTemplateCode = offerTemplateCode;
    }

    /**
     * Gets the subscription code.
     *
     * @return the subscription code
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * Sets the subscription code.
     *
     * @param subscriptionCode the new subscription code
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}