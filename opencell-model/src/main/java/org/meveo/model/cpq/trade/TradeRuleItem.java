package org.meveo.model.cpq.trade;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.offer.CommercialOffer;

/**
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay
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
	 * offer commercial code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_commercial_offer_id", referencedColumnName = "id")
	private CommercialOffer ruleCommercialCode;
	
	/**
	 * rule operator
	 */
	@Column(name = "operator", nullable = false)
	@NotNull
	private OperatorEnum operator = OperatorEnum.ET;
	
	/**
	 * Expression language
	 */
	@Column(name = "rule_item_el")
	@Lob
	private String ruleItemEl;

	/**
	 * @return the ruleCommercialCode
	 */
	public CommercialOffer getRuleCommercialCode() {
		return ruleCommercialCode;
	}

	/**
	 * @param ruleCommercialCode the ruleCommercialCode to set
	 */
	public void setRuleCommercialCode(CommercialOffer ruleCommercialCode) {
		this.ruleCommercialCode = ruleCommercialCode;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(operator, ruleCommercialCode, ruleItemEl);
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
		return operator == other.operator && Objects.equals(ruleCommercialCode, other.ruleCommercialCode)
				&& Objects.equals(ruleItemEl, other.ruleItemEl);
	}
	
	

}
