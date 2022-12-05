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
package org.meveo.model.catalog;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.BillingWalletTypeEnum;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 * Prepaid wallet template
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@ObservableEntity
@ExportIdentifier({ "code" })
@Table(name = "cat_wallet_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_wallet_template_seq"), })
@NamedQueries({
        @NamedQuery(name = "WalletTemplate.listPrepaidBySubscription", query = "select distinct wi.walletTemplate from ChargeInstance ci JOIN ci.walletInstances wi where wi.walletTemplate.walletType='PREPAID' and ci.subscription=:subscription order by wi.walletTemplate.code") })
public class WalletTemplate extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    public static final String PRINCIPAL = "PRINCIPAL";

    /**
     * Wallet type
     */
    @Column(name = "wallet_type")
    @Enumerated(EnumType.STRING)
    private BillingWalletTypeEnum walletType;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "consumption_alert_set")
    private boolean consumptionAlertSet;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "fast_rating_level")
    private int fastRatingLevel;

    /**
     * Balance level at which LowBalance event should be fired
     */
    @Column(name = "low_balance_level", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal lowBalanceLevel;

    /**
     * Balance level at which further consumption should be rejected
     */
    @Column(name = "reject_level", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rejectLevel;

    /**
     * Expression to determine low Balance Level
     */
    @Column(name = "low_balance_level_el", length = 2000)
    @Size(max = 2000)
    private String lowBalanceLevelEl;
    
	/**
     * Expression to determine reject Level
     */
    @Column(name = "reject_level_el", length = 2000)
    @Size(max = 2000)
    private String rejectLevelEl;
    
    /**
     * @return Wallet type
     */
    public BillingWalletTypeEnum getWalletType() {
        return walletType;
    }

    /**
     * @param walletType Wallet type
     */
    public void setWalletType(BillingWalletTypeEnum walletType) {
        this.walletType = walletType;
    }

    public boolean isConsumptionAlertSet() {
        return consumptionAlertSet;
    }

    public void setConsumptionAlertSet(boolean consumptionAlertSet) {
        this.consumptionAlertSet = consumptionAlertSet;
    }

    public int getFastRatingLevel() {
        return fastRatingLevel;
    }

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
    
    /**
     * @return lowBalanceLevelEl expression language to calculate lowBalanceLevel Balance level at which LowBalance event should be fired
     */
    public String getLowBalanceLevelEl() {
		return lowBalanceLevelEl;
	}

    /**
     * @param lowBalanceLevelEl expression language to calculate lowBalanceLevel Balance level at which LowBalance event should be fired
     */
	public void setLowBalanceLevelEl(String lowBalanceLevelEl) {
		this.lowBalanceLevelEl = lowBalanceLevelEl;
	}

	/**
     * @return RejectLevelEl Balance level el to calculate RejectLevel at which further consumption should be rejected
     */
	public String getRejectLevelEl() {
		return rejectLevelEl;
	}

	/**
     * @param RejectLevelEl Balance level el to calculate RejectLevel at which further consumption should be rejected
     */
	public void setRejectLevelEl(String rejectLevelEl) {
		this.rejectLevelEl = rejectLevelEl;
	}

}