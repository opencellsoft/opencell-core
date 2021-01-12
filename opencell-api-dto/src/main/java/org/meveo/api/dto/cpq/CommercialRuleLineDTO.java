package org.meveo.api.dto.cpq;

import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.trade.CommercialRuleLine;

@XmlRootElement(name = "CommercialRuleLineDTO")
@XmlType(name = "CommercialRuleLineDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommercialRuleLineDTO extends BaseEntityDto{


	/**
	 * 
	 */
	private static final long serialVersionUID = -386310082819716271L;
	private String offerCode;  
	private String productCode;
	private Integer productVersion;
	private String attributeCode;
	private String groupedAttributeCode;
	private String attributeValue;
	private String tagCode;
	private int operator;
	public CommercialRuleLineDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public CommercialRuleLineDTO(CommercialRuleLine commercialRuleLine) {
		super();   
		this.operator = commercialRuleLine.getOperator();
		this.offerCode = commercialRuleLine.getSourceOfferTemplate()!=null?commercialRuleLine.getSourceOfferTemplate().getCode():null;
		this.productCode = commercialRuleLine.getSourceProduct()!=null?commercialRuleLine.getSourceProduct().getCode():null;
		this.productVersion = commercialRuleLine.getSourceProductVersion()!=null?commercialRuleLine.getSourceProductVersion().getCurrentVersion():null;
		this.attributeCode = commercialRuleLine.getTargetAttribute()!=null?commercialRuleLine.getTargetAttribute().getCode():null;
		this.tagCode = commercialRuleLine.getTargetTag()!=null?commercialRuleLine.getTargetTag().getCode():null;
		this.groupedAttributeCode = commercialRuleLine.getTargetGroupedAttributes()!=null?commercialRuleLine.getTargetGroupedAttributes().getCode():null;
		this.attributeValue = commercialRuleLine.getSourceAttributeValue();
		
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
	 * @return the operator
	 */
	public int getOperator() {
		return operator;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(int operator) {
		this.operator = operator;
	}
	/**
	 * @return the tagCode
	 */
	public String getTagCode() {
		return tagCode;
	}
	/**
	 * @param tagCode the tagCode to set
	 */
	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}
	
	
    
}
