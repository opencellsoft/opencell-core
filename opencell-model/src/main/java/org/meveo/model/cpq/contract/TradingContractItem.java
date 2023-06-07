package org.meveo.model.cpq.contract;

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
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingCurrency;

@Entity
@Table(name = "cpq_trading_contract_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_trading_contract_item_seq"), })
@NamedQueries({
		@NamedQuery(name = "TradingContractItem.getByContractItemAndCurrency", query = "SELECT tci from TradingContractItem tci where tci.contractItem =:contractItem and tci.tradingCurrency = :tradingCurrency") 
})
public class TradingContractItem extends AuditableEntity {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "trading_value", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	private BigDecimal tradingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency_id", nullable = false)
	private TradingCurrency tradingCurrency;
	
	@Column(name = "rate")
	private BigDecimal rate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_item_id")
	private ContractItem contractItem;
	
	public TradingContractItem() {
    
	}
	
    public TradingContractItem(TradingContractItem copy) {
        this.tradingValue = copy.tradingValue;
        this.tradingCurrency = copy.tradingCurrency;
        this.rate = copy.rate;
        this.contractItem = copy.contractItem;
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
	 * @return the contractItem
	 */
	public ContractItem getContractItem() {
		return contractItem;
	}

	/**
	 * @param contractItem the contractItem to set
	 */
	public void setContractItem(ContractItem contractItem) {
		this.contractItem = contractItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(contractItem, rate, tradingCurrency, tradingValue);
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
		TradingContractItem other = (TradingContractItem) obj;
		return Objects.equals(contractItem, other.contractItem) && Objects.equals(rate, other.rate)
				&& Objects.equals(tradingCurrency, other.tradingCurrency)
				&& Objects.equals(tradingValue, other.tradingValue);
	}

}
