package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.enums.OperatorEnum;

@XmlRootElement(name = "CommercialRuleItemDTO")
@XmlType(name = "CommercialRuleItemDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommercialRuleItemDTO extends BaseEntityDto{

    
	/**
	 * 
	 */
	private static final long serialVersionUID = 2921006853398452396L;
	
	private OperatorEnum operator = OperatorEnum.AND;
	
	private List<CommercialRuleLineDTO> commercialRuleLines=new ArrayList<CommercialRuleLineDTO>();
	
	

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
