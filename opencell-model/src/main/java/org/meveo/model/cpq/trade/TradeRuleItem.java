
package org.meveo.model.cpq.trade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.OperatorEnum;

/**
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay
 *  @author Rachid.AIT
 *	@version 10.0
 */
@Entity
@Table(name = "cpq_trade_rule_item", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_trade_rule_item_seq"), })
public class TradeRuleItem extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6013887131795900749L;
	
	/**
	 * Trade rule header
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trade_rule_header_id")
	private TradeRuleHeader tradeRuleHeader;

	
	/**
	 * rule operator
	 */
	@Column(name = "operator", nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private OperatorEnum operator = OperatorEnum.ET;
	
	/**
	 * Expression language
	 */ 
	@Size(max = 2000)
    @Column(name = "rule_item_el", columnDefinition = "TEXT")
	private String ruleItemEl;

	/**
	 * @return the operator
	 */
	public OperatorEnum getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(OperatorEnum operator) {
		this.operator = operator;
	}

	/**
	 * @return the ruleItemEl
	 */
	public String getRuleItemEl() {
		return ruleItemEl;
	}

	/**
	 * @param ruleItemEl the ruleItemEl to set
	 */
	public void setRuleItemEl(String ruleItemEl) {
		this.ruleItemEl = ruleItemEl;
	}

	public TradeRuleHeader getTradeRuleHeader() {
		return tradeRuleHeader;
	}

	public void setTradeRuleHeader(TradeRuleHeader tradeRuleHeader) {
		this.tradeRuleHeader = tradeRuleHeader;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((tradeRuleHeader == null) ? 0 : tradeRuleHeader.hashCode());
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
		TradeRuleItem other = (TradeRuleItem) obj;
		if (operator != other.operator)
			return false;
		if (tradeRuleHeader == null) {
			if (other.tradeRuleHeader != null)
				return false;
		} else if (!tradeRuleHeader.equals(other.tradeRuleHeader))
			return false;
		return true;
	}


	
	

}
