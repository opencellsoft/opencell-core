package org.meveo.api.dto.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.enums.RuleTypeEnum;

@XmlRootElement(name = "CommercialRuleDTO")
@XmlType(name = "CommercialRuleDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommercialRuleDTO extends BaseEntityDto{

    
	/**
	 * 
	 */
	private static final long serialVersionUID = 2921006853398452396L;
	
	private String code;
	private String label;
	private RuleTypeEnum ruleType;
	private EntityTypeEnum entityType;  
	private String entityCode;
	private String entityValue;
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the ruleType
	 */
	public RuleTypeEnum getRuleType() {
		return ruleType;
	}
	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(RuleTypeEnum ruleType) {
		this.ruleType = ruleType;
	}
	/**
	 * @return the entityType
	 */
	public EntityTypeEnum getEntityType() {
		return entityType;
	}
	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(EntityTypeEnum entityType) {
		this.entityType = entityType;
	}
	/**
	 * @return the entityCode
	 */
	public String getEntityCode() {
		return entityCode;
	}
	/**
	 * @param entityCode the entityCode to set
	 */
	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}
	/**
	 * @return the entityValue
	 */
	public String getEntityValue() {
		return entityValue;
	}
	/**
	 * @param entityValue the entityValue to set
	 */
	public void setEntityValue(String entityValue) {
		this.entityValue = entityValue;
	}

    
    
	
	
    
}
