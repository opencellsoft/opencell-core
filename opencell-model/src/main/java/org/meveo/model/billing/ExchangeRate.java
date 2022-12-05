package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.EnableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "exchange_rate")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "exchange_rate_seq"), })
@NamedQueries({
    @NamedQuery(name = "ExchangeRate.getAllTradingCurrencyWithCurrentRate",
            query = "SELECT s.id FROM ExchangeRate s WHERE (s.tradingCurrency.id, s.fromDate)" +
                    " IN (SELECT ex.tradingCurrency.id, MAX(ex.fromDate) FROM ExchangeRate ex WHERE ex.fromDate <=:sysDate GROUP BY ex.tradingCurrency.id)"),
    @NamedQuery(name = "ExchangeRate.findByfromDate", query = "SELECT ec FROM ExchangeRate ec WHERE ec.fromDate = :fromDate and ec.tradingCurrency.id = :tradingCurrencyId")
})
public class ExchangeRate extends EnableEntity {
    private static final long serialVersionUID = 1L;
    public static final int NB_DECIMALS = 6;
    /**
     * TradingCurrency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    @Column(name = "exchange_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal exchangeRate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_date")
    private Date fromDate;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "current_rate", nullable = false)
    private boolean isCurrentRate  = false;

    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public boolean isCurrentRate() {
        return isCurrentRate;
    }

    public void setCurrentRate(boolean isCurrentRate) {
        this.isCurrentRate = isCurrentRate;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ExchangeRate)) {
            return false;
        }

        ExchangeRate other = (ExchangeRate) obj;
        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;

        } else if (tradingCurrency.getId().equals(other.getTradingCurrency().getId())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ExchangeRate [tradingCurrency=%s, id=%s]", tradingCurrency, id);
    }
}
