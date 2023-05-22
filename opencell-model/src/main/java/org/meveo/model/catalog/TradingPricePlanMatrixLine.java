package org.meveo.model.catalog;

import java.math.BigDecimal;

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
@Table(name = "cpq_trading_price_plan_matrix_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_trading_price_plan_matrix_line_sq") })
@NamedQueries({
	@NamedQuery(name = "TradingPricePlanMatrixLine.enableOrDisable", query = "UPDATE TradingPricePlanMatrixLine set useForBillingAccounts =:enable where id in (:ids)") 
})
public class TradingPricePlanMatrixLine  extends AuditableEntity {

    private static final long serialVersionUID = -8640286801032633017L;

	@Column(name = "trading_value", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal tradingValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
	private TradingCurrency tradingCurrency;

    @Column(name = "rate", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal rate;

	@Type(type = "numeric_boolean")
    @Column(name = "use_for_billing_accounts")
	private boolean useForBillingAccounts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_plan_matrix_line_id")
	private PricePlanMatrixLine pricePlanMatrixLine;
    
	public TradingPricePlanMatrixLine() {
        super();
    }

    public TradingPricePlanMatrixLine(BigDecimal tradingValue, TradingCurrency tradingCurrency, BigDecimal rate, boolean useForBillingAccounts, PricePlanMatrixLine pricePlanMatrixLine) {
        super();
        this.tradingValue = tradingValue;
        this.tradingCurrency = tradingCurrency;
        this.rate = rate;
        this.useForBillingAccounts = useForBillingAccounts;
        this.pricePlanMatrixLine = pricePlanMatrixLine;
    }

    /**
	 * @return the tradingValue
	 */
	public BigDecimal getTradingValue() {
		return tradingValue;
	}

	/**
	 * @param tradingValue the tradingValue to set
	 */
	public void setTradingValue(BigDecimal tradingValue) {
		this.tradingValue = tradingValue;
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
	 * @return the pricePlanMatrixLine
	 */
	public PricePlanMatrixLine getPricePlanMatrixLine() {
		return pricePlanMatrixLine;
	}

	/**
	 * @param pricePlanMatrixLine the pricePlanMatrixLine to set
	 */
	public void setPricePlanMatrixLine(PricePlanMatrixLine pricePlanMatrixLine) {
		this.pricePlanMatrixLine = pricePlanMatrixLine;
	}
	
}
