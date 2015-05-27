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
package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Currency;

@Entity
@ObservableEntity
@ExportIdentifier({ "currency.currencyCode", "provider" })
@Cacheable
@Table(name = "BILLING_TRADING_CURRENCY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_TRADING_CURRENCY_SEQ")
public class TradingCurrency extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCY_ID")
	private Currency currency;

	@Column(name = "PR_DESCRIPTION", length = 100)
	private String prDescription;

	@Column(name = "PR_CURRENCY_TO_THIS", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal prCurrencyToThis;

	@Transient
	String currencyCode;

	public BigDecimal getPrCurrencyToThis() {
		return prCurrencyToThis;
	}

	public void setPrCurrencyToThis(BigDecimal prCurrencyToThis) {
		this.prCurrencyToThis = prCurrencyToThis;
	}

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getCurrencyCode() {
		return currency.getCurrencyCode();
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	public boolean equals(Object other){
		if(other==null || !(other instanceof TradingCurrency)){
			return false;
		}
		TradingCurrency o = (TradingCurrency) other;
		return getId()!=null &&getId().equals(o.getId());
//		return (o.currency!=null) && o.currency.equals(this.currency);
	}

    @Override
    public String toString() {
        return String.format("TradingCurrency [currency=%s, id=%s]", currency, id);
    }
}
