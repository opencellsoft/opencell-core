package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.trade.CommercialRuleItem;

@XmlRootElement(name = "CommercialRuleItemDTO")
@XmlType(name = "CommercialRuleItemDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommercialRuleItemDTO extends BaseEntityDto{

    
	/**
	 * 
	 */
	private static final long serialVersionUID = 2921006853398452396L;
	
	private OperatorEnum operator = OperatorEnum.ET;
	private String ruleItemEl;
	
	private List<CommercialRuleLineDTO> commercialRuleLines=new ArrayList<CommercialRuleLineDTO>();
	
	
	
	public CommercialRuleItemDTO(CommercialRuleItem commercialRuleItem) {
		super();
		this.operator=commercialRuleItem.getOperator();
		this.ruleItemEl=commercialRuleItem.getRuleItemEl();
		if(commercialRuleItem.getCommercialRuleLines()!= null && !commercialRuleItem.getCommercialRuleLines().isEmpty()) {
			commercialRuleLines = commercialRuleItem.getCommercialRuleLines().stream().map(d -> {
    			final CommercialRuleLineDTO line = new CommercialRuleLineDTO(d);
    			return line;
    		}).collect(Collectors.toList());
    	}
		
	}
	

	public CommercialRuleItemDTO() {
		super();
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


	/**
	 * @return the commercialRuleLines
	 */
	public List<CommercialRuleLineDTO> getCommercialRuleLines() {
		return commercialRuleLines;
	}

	/**
	 * @param commercialRuleLines the commercialRuleLines to set
	 */
	public void setCommercialRuleLines(List<CommercialRuleLineDTO> commercialRuleLines) {
		this.commercialRuleLines = commercialRuleLines;
	}
	

	

	
    
}
