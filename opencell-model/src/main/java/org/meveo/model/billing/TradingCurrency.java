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
package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Currency;

/**
 * Currency enabled in application
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "currency.currencyCode" })
@Table(name = "billing_trading_currency")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_trading_currency_seq"), })
@NamedQueries({ @NamedQuery(name = "TradingCurrency.getByCode", query = "from TradingCurrency tr where tr.currency.currencyCode = :tradingCurrencyCode) ", hints = {
        @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class TradingCurrency extends EnableEntity {
    private static final long serialVersionUID = 1L;

    /**
     * Currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    /**
     * Description. Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "pr_description", length = 255)
    @Size(max = 255)
    private String prDescription;

    /**
     * Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "pr_currency_to_this", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal prCurrencyToThis;

    /**
     * Currency code
     */
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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TradingCurrency)) {
            return false;
        }

        TradingCurrency other = (TradingCurrency) obj;
        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;

        } else if (currency.getId().equals(other.getCurrency().getId())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("TradingCurrency [currency=%s, id=%s]", currency, id);
    }
}
