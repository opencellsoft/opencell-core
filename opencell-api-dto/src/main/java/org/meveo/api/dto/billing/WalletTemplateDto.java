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

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.catalog.WalletTemplate;

/**
 * The Class WalletTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2681139334253613359L;

    /** The wallet type. */
    private BillingWalletTypeEnum walletType;

    /** The consumption alert set. */
    private boolean consumptionAlertSet;

    /** The fast rating level. */
    private int fastRatingLevel;

    /** The low balance level. */
    private BigDecimal lowBalanceLevel;

    /**
     * Balance level at which further consumption should be rejected
     */
    private BigDecimal rejectLevel;

    /**
     * Instantiates a new wallet template dto.
     */
    public WalletTemplateDto() {
    }

    /**
     * Instantiates a new wallet template dto.
     *
     * @param walletTemplate the WalletTemplate entity
     */
    public WalletTemplateDto(WalletTemplate walletTemplate) {
        super(walletTemplate);
        walletType = walletTemplate.getWalletType();
        consumptionAlertSet = walletTemplate.isConsumptionAlertSet();
        fastRatingLevel = walletTemplate.getFastRatingLevel();
        lowBalanceLevel = walletTemplate.getLowBalanceLevel();
        rejectLevel = walletTemplate.getRejectLevel();
    }

    /**
     * Gets the wallet type.
     *
     * @return the wallet type
     */
    public BillingWalletTypeEnum getWalletType() {
        return walletType;
    }

    /**
     * Sets the wallet type.
     *
     * @param walletType the new wallet type
     */
    public void setWalletType(BillingWalletTypeEnum walletType) {
        this.walletType = walletType;
    }

    /**
     * Checks if is consumption alert set.
     *
     * @return true, if is consumption alert set
     */
    public boolean isConsumptionAlertSet() {
        return consumptionAlertSet;
    }

    /**
     * Sets the consumption alert set.
     *
     * @param consumptionAlertSet the new consumption alert set
     */
    public void setConsumptionAlertSet(boolean consumptionAlertSet) {
        this.consumptionAlertSet = consumptionAlertSet;
    }

    /**
     * Gets the fast rating level.
     *
     * @return the fast rating level
     */
    public int getFastRatingLevel() {
        return fastRatingLevel;
    }

    /**
     * Sets the fast rating level.
     *
     * @param fastRatingLevel the new fast rating level
     */
    public void setFastRatingLevel(int fastRatingLevel) {
        this.fastRatingLevel = fastRatingLevel;
    }

    /**
     * @return Balance level at which LowBalance event should be fired
     */
    public BigDecimal getLowBalanceLevel() {
        return lowBalanceLevel;
    }

    /**
     * @param lowBalanceLevel Balance level at which LowBalance event should be fired
     */
    public void setLowBalanceLevel(BigDecimal lowBalanceLevel) {
        this.lowBalanceLevel = lowBalanceLevel;
    }

    /**
     * @return Balance level at which further consumption should be rejected
     */
    public BigDecimal getRejectLevel() {
        return rejectLevel;
    }

    /**
     * @param rejectLevel Balance level at which further consumption should be rejected
     */
    public void setRejectLevel(BigDecimal rejectLevel) {
        this.rejectLevel = rejectLevel;
    }

    @Override
    public String toString() {
        return "WalletTemplateDto [code=" + code + ", description=" + description + ", walletType=" + walletType + ", consumptionAlertSet=" + consumptionAlertSet
                + ", fastRatingLevel=" + fastRatingLevel + ", lowBalanceLevel=" + lowBalanceLevel + ", rejectLevel=" + rejectLevel + "]";
    }
}