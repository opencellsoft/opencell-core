package org.meveo.api.dto.payment;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningActionTypeEnum;
import org.meveo.model.payments.DunningLevelEnum;


@XmlType(name = "ActionPlanItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionPlanItemDto extends BaseDto {
	private static final long serialVersionUID = 8309866046667741458L;  
	
	@XmlElement(required = true)
	private DunningLevelEnum dunningLevel;
	@XmlElement(required = true)
	private DunningActionTypeEnum actionType;
	@XmlElement(required = true)
	private Integer itemOrder;
	@XmlElement(required = true)
	private BigDecimal thresholdAmount;
	@XmlElement(required = true)
	private BigDecimal chargeAmount;
	private String letterTemplate;
	@XmlElement(required = true)
	private String dunningPlan;
	private String conditionEl;
	
	public ActionPlanItemDto(){
	}
	public ActionPlanItemDto(ActionPlanItem actionPlanItem) { 
		this.dunningLevel=actionPlanItem.getDunningLevel();
		this.actionType=actionPlanItem.getActionType(); 
		this.itemOrder=actionPlanItem.getItemOrder(); 
		this.thresholdAmount=actionPlanItem.getThresholdAmount(); 
		this.chargeAmount=actionPlanItem.getChargeAmount();  
		this.letterTemplate=actionPlanItem.getLetterTemplate(); 
		this.dunningPlan=actionPlanItem.getDunningPlan().getCode();
		this.conditionEl=actionPlanItem.getConditionEl();
	 	
	}
	public DunningLevelEnum getDunningLevel() {
		return dunningLevel;
	}
	public void setDunningLevel(DunningLevelEnum dunningLevel) {
		this.dunningLevel = dunningLevel;
	}
	public DunningActionTypeEnum getActionType() {
		return actionType;
	}
	public void setActionType(DunningActionTypeEnum actionType) {
		this.actionType = actionType;
	}
	public Integer getItemOrder() {
		return itemOrder;
	}
	public void setItemOrder(Integer itemOrder) {
		this.itemOrder = itemOrder;
	}
	public BigDecimal getThresholdAmount() {
		return thresholdAmount;
	}
	public void setThresholdAmount(BigDecimal thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}
	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}
	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	public String getLetterTemplate() {
		return letterTemplate;
	}
	public void setLetterTemplate(String letterTemplate) {
		this.letterTemplate = letterTemplate;
	}
	public String getDunningPlan() {
		return dunningPlan;
	}
	public void setDunningPlan(String dunningPlan) {
		this.dunningPlan = dunningPlan;
	}
	public String getConditionEl() {
		return conditionEl;
	}
	public void setConditionEl(String conditionEl) {
		this.conditionEl = conditionEl;
	}
	
	

}

