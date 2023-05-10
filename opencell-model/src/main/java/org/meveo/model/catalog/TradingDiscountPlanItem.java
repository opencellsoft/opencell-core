package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingCurrency;

@Entity
@Table(name = "cat_trading_discount_plan_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_trading_discount_plan_item_seq"), }) 
public class TradingDiscountPlanItem extends AuditableEntity {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "transactional_discount_value", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	private BigDecimal transactionalDiscountValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency_id", nullable = false)
	private TradingCurrency tradingCurrency;
	
	@Column(name = "rate")
	private BigDecimal rate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_item_id")
	private DiscountPlanItem discountPlanItem;
	
	public TradingDiscountPlanItem() {
    
	}

    public TradingDiscountPlanItem(TradingDiscountPlanItem copy) {
        this.transactionalDiscountValue = copy.transactionalDiscountValue;
        this.tradingCurrency = copy.tradingCurrency;
        this.rate = copy.rate;
        this.discountPlanItem = copy.discountPlanItem;
    }

	/**
	 * @return the transactionalDiscountValue
	 */
	public BigDecimal getTransactionalDiscountValue() {
		return transactionalDiscountValue;
	}

	/**
	 * @param transactionalDiscountValue the transactionalDiscountValue to set
	 */
	public void setTransactionalDiscountValue(BigDecimal transactionalDiscountValue) {
		this.transactionalDiscountValue = transactionalDiscountValue;
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
	 * @return the discountPlanItem
	 */
	public DiscountPlanItem getDiscountPlanItem() {
		return discountPlanItem;
	}

	/**
	 * @param discountPlanItem the discountPlanItem to set
	 */
	public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
		this.discountPlanItem = discountPlanItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(discountPlanItem, rate, tradingCurrency, transactionalDiscountValue);
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
		TradingDiscountPlanItem other = (TradingDiscountPlanItem) obj;
		return Objects.equals(discountPlanItem, other.discountPlanItem) && Objects.equals(rate, other.rate)
				&& Objects.equals(tradingCurrency, other.tradingCurrency)
				&& Objects.equals(transactionalDiscountValue, other.transactionalDiscountValue);
	}

}
