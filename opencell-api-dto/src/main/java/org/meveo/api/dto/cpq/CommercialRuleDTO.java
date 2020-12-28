package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;

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
	private String offerCode;  
	private String productCode;
	private Integer productVersion;
	private String attributeCode;
	private String groupedAttributeCode;
	private String attributeValue;
	private List<CommercialRuleItemDTO> commercialRuleItems=new ArrayList<CommercialRuleItemDTO>();
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
	 * @return the offerCode
	 */
	public String getOfferCode() {
		return offerCode;
	}
	/**
	 * @param offerCode the offerCode to set
	 */
	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}
	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the productVersion
	 */
	public Integer getProductVersion() {
		return productVersion;
	}
	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(Integer productVersion) {
		this.productVersion = productVersion;
	}
	/**
	 * @return the attributeCode
	 */
	public String getAttributeCode() {
		return attributeCode;
	}
	/**
	 * @param attributeCode the attributeCode to set
	 */
	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}
	/**
	 * @return the groupedAttributeCode
	 */
	public String getGroupedAttributeCode() {
		return groupedAttributeCode;
	}
	/**
	 * @param groupedAttributeCode the groupedAttributeCode to set
	 */
	public void setGroupedAttributeCode(String groupedAttributeCode) {
		this.groupedAttributeCode = groupedAttributeCode;
	}
	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return attributeValue;
	}
	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	/**
	 * @return the commercialRuleItems
	 */
	public List<CommercialRuleItemDTO> getCommercialRuleItems() {
		return commercialRuleItems;
	}
	/**
	 * @param commercialRuleItems the commercialRuleItems to set
	 */
	public void setCommercialRuleItems(List<CommercialRuleItemDTO> commercialRuleItems) {
		this.commercialRuleItems = commercialRuleItems;
	}

	
    
}
