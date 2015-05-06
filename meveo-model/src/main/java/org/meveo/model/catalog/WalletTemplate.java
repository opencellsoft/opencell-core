/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.BillingWalletTypeEnum;

@Entity
@ObservableEntity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_WALLET_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_WALLET_TEMPLATE_SEQ")
public class WalletTemplate extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	public static final String PRINCIPAL = "PRINCIPAL";

	@Column(name = "WALLET_TYPE")
	@Enumerated(EnumType.STRING)
	private BillingWalletTypeEnum walletType;

	@Column(name = "CONSUMPTION_ALERT_SET")
	private boolean consumptionAlertSet;

	@Column(name = "FAST_RATING_LEVEL")
	private int fastRatingLevel;
	
    @Column(name = "LOW_BALANCE_LEVEL", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal lowBalanceLevel;
    

	public BillingWalletTypeEnum getWalletType() {
		return walletType;
	}

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

    public BigDecimal getLowBalanceLevel() {
        return lowBalanceLevel;
    }

    public void setLowBalanceLevel(BigDecimal lowBalanceLevel) {
        this.lowBalanceLevel = lowBalanceLevel;
    }

}
