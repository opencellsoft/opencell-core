/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.BillingWalletTypeEnum;

@Entity
@Cacheable
@ObservableEntity
@ExportIdentifier({ "code"})
@Table(name = "cat_wallet_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "cat_wallet_template_seq"), })
public class WalletTemplate extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	public static final String PRINCIPAL = "PRINCIPAL";

	@Column(name = "wallet_type")
	@Enumerated(EnumType.STRING)
	private BillingWalletTypeEnum walletType;

	@Type(type="numeric_boolean")
    @Column(name = "consumption_alert_set")
	private boolean consumptionAlertSet;

	@Column(name = "fast_rating_level")
	private int fastRatingLevel;
	
    @Column(name = "low_balance_level", precision = NB_PRECISION, scale = NB_DECIMALS)
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
