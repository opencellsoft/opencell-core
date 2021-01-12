
package org.meveo.model.cpq.trade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.enums.OperatorEnum;

/**
 * @author Tarik FAKHOURI.
 *  @author Mbarek-Ay
 *  @author Rachid.AIT
 *	@version 10.0
 */
@Entity
@Table(name = "cpq_commercial_rule_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_commercial_rule_item_seq"), })
public class CommercialRuleItem extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6013887131795900749L;
	
	/**
	 * Trade rule header
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commercial_rule_header_id",referencedColumnName = "id")
	private CommercialRuleHeader commercialRuleHeader;

	
	
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
	
	
	@OneToMany(mappedBy = "commercialRuleItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CommercialRuleLine> commercialRuleLines = new ArrayList<>();


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

	/**
	 * @return the commercialRuleHeader
	 */
	public CommercialRuleHeader getCommercialRuleHeader() {
		return commercialRuleHeader;
	}

	/**
	 * @param commercialRuleHeader the commercialRuleHeader to set
	 */
	public void setCommercialRuleHeader(CommercialRuleHeader commercialRuleHeader) {
		this.commercialRuleHeader = commercialRuleHeader;
	}

	/**
	 * @return the commercialRuleLines
	 */
	public List<CommercialRuleLine> getCommercialRuleLines() {
		return commercialRuleLines;
	}

	/**
	 * @param commercialRuleLines the commercialRuleLines to set
	 */
	public void setCommercialRuleLines(List<CommercialRuleLine> commercialRuleLines) {
		this.commercialRuleLines = commercialRuleLines;
	}

	



	
	

}
