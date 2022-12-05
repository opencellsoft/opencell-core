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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Currency;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

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
@NamedQueries({ @NamedQuery(name = "TradingCurrency.getByCode", query = "from TradingCurrency tr where tr.currency.currencyCode = :tradingCurrencyCode ", hints = {
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
    @Column(name = "pr_description")
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

    @Column(name = "symbol")
    @Size(max = 255)
    private String symbol;

    @Column(name = "decimal_places")
    private Integer decimalPlaces;

    @Column(name = "current_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal currentRate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "current_rate_from_date")
    private Date currentRateFromDate;

    @Column(name = "current_rate_updater")
    private String currentRateUpdater;

    @OneToMany(mappedBy = "tradingCurrency", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ExchangeRate> exchangeRates = new ArrayList<>();

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
    
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
        return currency != null ? currency.getCurrencyCode() : null;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public BigDecimal getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(BigDecimal currentRate) {
        this.currentRate = currentRate;
    }

    public Date getCurrentRateFromDate() {
        return currentRateFromDate;
    }

    public void setCurrentRateFromDate(Date currentRateFromDate) {
        this.currentRateFromDate = currentRateFromDate;
    }

    public String getCurrentRateUpdater() {
        return currentRateUpdater;
    }

    public void setCurrentRateUpdater(String currentRateUpdater) {
        this.currentRateUpdater = currentRateUpdater;
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

    public ExchangeRate getExchangeRate(Date invoiceDate) {

        List<ExchangeRate> ratesList = getExchangeRates();
        if (ratesList == null || ratesList.isEmpty()) {
            return null;
        }
        return ratesList.stream().sorted(Comparator.comparing(ExchangeRate::getFromDate).reversed()).filter(rate -> (
                (rate.getFromDate().toInstant().equals(invoiceDate.toInstant())) || rate.getFromDate().toInstant().isBefore(invoiceDate.toInstant()))).findFirst().orElse(null);
    }
}
