package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "CommercialRuleHeaderDTO")
@XmlType(name = "CommercialRuleHeaderDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommercialRuleHeaderDTO extends BusinessEntityDto{

    
	/**
	 * 
	 */
	private static final long serialVersionUID = 2921006853398452396L;
	

    @Schema(description = "rule type", example = "possible value are : PRE_REQUISITE, INCOMPATIBILITY, REPLACEMENT")
	protected RuleTypeEnum ruleType;
    @Schema(description = "rule expression language")
	protected String ruleEl;
    @Schema(description = "code offer of template")
	protected String offerCode;  
    @Schema(description = "code product")
	protected String productCode;
    @Schema(description = "product version")
	protected Integer productVersion;
    @Schema(description = "code of attribute")
	protected String attributeCode;
    @Schema(description = "code tag")
	protected String tagCode;
    @Schema(description = "code of grouped attribute")
	protected String groupedAttributeCode;
    @Schema(description = "target attribute value")
	protected String targetAttributeValue; 
    @Schema(description = "is commercial rule header is the target, default value is True")
	protected Boolean isTarget=Boolean.TRUE;
    @Schema(description = "is commercial rule disabled, default value is false")
	protected Boolean disabled=Boolean.FALSE;
    @Schema(description = "list of commercial rule item")
	protected List<CommercialRuleItemDTO> commercialRuleItems=new ArrayList<CommercialRuleItemDTO>();
	 
	
	
	
	
	
	
	
	public CommercialRuleHeaderDTO() {
		super();
	}
	public CommercialRuleHeaderDTO(CommercialRuleHeader commercialRuleHeader) {
		super();
		this.code=commercialRuleHeader.getCode();
		this.description=commercialRuleHeader.getDescription();
		this.ruleType = commercialRuleHeader.getRuleType();
		this.ruleEl = commercialRuleHeader.getRuleEl();
		this.offerCode = commercialRuleHeader.getTargetOfferTemplate()!=null?commercialRuleHeader.getTargetOfferTemplate().getCode():null;
		this.productCode = commercialRuleHeader.getTargetProduct()!=null?commercialRuleHeader.getTargetProduct().getCode():null;
		this.productVersion = commercialRuleHeader.getTargetProductVersion()!=null?commercialRuleHeader.getTargetProductVersion().getCurrentVersion():null;
		this.attributeCode = commercialRuleHeader.getTargetAttribute()!=null?commercialRuleHeader.getTargetAttribute().getCode():null;
		this.tagCode = commercialRuleHeader.getTargetTag()!=null?commercialRuleHeader.getTargetTag().getCode():null;
		this.groupedAttributeCode = commercialRuleHeader.getTargetGroupedAttributes()!=null?commercialRuleHeader.getTargetGroupedAttributes().getCode():null;
		this.targetAttributeValue = commercialRuleHeader.getTargetAttributeValue();  
		if(commercialRuleHeader.getCommercialRuleItems()!= null && !commercialRuleHeader.getCommercialRuleItems().isEmpty()) {
    		commercialRuleItems = commercialRuleHeader.getCommercialRuleItems().stream().map(d -> {
    			final CommercialRuleItemDTO ruleItem = new CommercialRuleItemDTO(d);
    			return ruleItem;
    		}).collect(Collectors.toList());
    	}
		this.disabled=commercialRuleHeader.isDisabled();
		
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
	/**
	 * @return the targetAttributeValue
	 */
	public String getTargetAttributeValue() {
		return targetAttributeValue;
	}
	/**
	 * @param targetAttributeValue the targetAttributeValue to set
	 */
	public void setTargetAttributeValue(String targetAttributeValue) {
		this.targetAttributeValue = targetAttributeValue;
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
	/**
	 * @return the ruleEl
	 */
	public String getRuleEl() {
		return ruleEl;
	}
	/**
	 * @param ruleEl the ruleEl to set
	 */
	public void setRuleEl(String ruleEl) {
		this.ruleEl = ruleEl;
	}
	/**
	 * @return the isTarget
	 */
	public Boolean getIsTarget() {
		return isTarget;
	}
	/**
	 * @param isTarget the isTarget to set
	 */
	public void setIsTarget(Boolean isTarget) {
		this.isTarget = isTarget;
	}
	/**
	 * @return the disabled
	 */
	public Boolean getDisabled() {
		return disabled;
	}
	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	
	

	
    
}
