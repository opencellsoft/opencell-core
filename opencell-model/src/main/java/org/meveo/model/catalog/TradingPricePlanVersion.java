package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingCurrency;

@Entity
@Table(name = "cpq_trading_price_plan_version")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_trading_price_plan_version_seq") })
@NamedQueries({
		@NamedQuery(name = "TradingPricePlanVersion.getByPricePlanVersionAndCurrency", query = "SELECT tppv from TradingPricePlanVersion tppv where tppv.pricePlanMatrixVersion =:ppmv and tppv.tradingCurrency = :tradingCurrency") 
})
public class TradingPricePlanVersion extends AuditableEntity {
	
	private static final long serialVersionUID = 6114895863558742249L;

	@Column(name = "trading_price")
	private BigDecimal tradingPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency_id", nullable = false)
	private TradingCurrency tradingCurrency;
	
	@Column(name = "rate")
	private BigDecimal rate;
	
	@Type(type = "numeric_boolean")
	@Column(name = "use_for_billing_accounts")
	private boolean useForBillingAccounts;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_plan_matrix_version_id")
	private PricePlanMatrixVersion pricePlanMatrixVersion;
	
	public TradingPricePlanVersion() {
    
	}

    public TradingPricePlanVersion(TradingPricePlanVersion copy) {
        this.tradingPrice = copy.tradingPrice;
        this.tradingCurrency = copy.tradingCurrency;
        this.rate = copy.rate;
        this.useForBillingAccounts = copy.useForBillingAccounts;
        this.pricePlanMatrixVersion = copy.pricePlanMatrixVersion;
    }

	/**
	 * @return the tradingPrice
	 */
	public BigDecimal getTradingPrice() {
		return tradingPrice;
	}

	/**
	 * @param tradingPrice the tradingPrice to set
	 */
	public void setTradingPrice(BigDecimal tradingPrice) {
		this.tradingPrice = tradingPrice;
	}

	/**
	 * @return the tradingCurrency
	 */
	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}

	/**
	 * @param tradingCurrency the tradingCurrency to set
	 */
	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	/**
	 * @return the useForBillingAccounts
	 */
	public boolean isUseForBillingAccounts() {
		return useForBillingAccounts;
	}

	/**
	 * @param useForBillingAccounts the useForBillingAccounts to set
	 */
	public void setUseForBillingAccounts(boolean useForBillingAccounts) {
		this.useForBillingAccounts = useForBillingAccounts;
	}

	/**
	 * @return the pricePlanMatrixVersion
	 */
	public PricePlanMatrixVersion getPricePlanMatrixVersion() {
		return pricePlanMatrixVersion;
	}

	/**
	 * @param pricePlanMatrixVersion the pricePlanMatrixVersion to set
	 */
	public void setPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
		this.pricePlanMatrixVersion = pricePlanMatrixVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(tradingPrice, pricePlanMatrixVersion, rate, tradingCurrency, useForBillingAccounts);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradingPricePlanVersion other = (TradingPricePlanVersion) obj;
		return Objects.equals(tradingPrice, other.tradingPrice)
				&& Objects.equals(pricePlanMatrixVersion, other.pricePlanMatrixVersion)
				&& Objects.equals(rate, other.rate) && Objects.equals(tradingCurrency, other.tradingCurrency)
				&& useForBillingAccounts == other.useForBillingAccounts;
	}

	
	
}
