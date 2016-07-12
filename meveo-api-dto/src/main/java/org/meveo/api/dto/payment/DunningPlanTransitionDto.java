package org.meveo.api.dto.payment;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlanTransition;


@XmlType(name = "DunningPlanTransition")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningPlanTransitionDto extends BaseDto {
	private static final long serialVersionUID = 8309866046667741458L; 
	@XmlElement(required = true)
	private DunningLevelEnum dunningLevelFrom;
	@XmlElement(required = true)
	private DunningLevelEnum dunningLevelTo;
	@XmlElement(required = true)
	private Integer delayBeforeProcess;
	@XmlElement(required = true)
	private BigDecimal thresholdAmount; 
	@XmlElement(required = true)
	private Integer waitDuration;
    @XmlElement(required = true)
    private String dunningPlan;
	private String conditionEl;
	
	public DunningPlanTransitionDto(){
	}
	public DunningPlanTransitionDto(DunningPlanTransition dunningPlanTransition) { 
		this.dunningLevelFrom=dunningPlanTransition.getDunningLevelFrom();
		this.dunningLevelTo=dunningPlanTransition.getDunningLevelTo(); 
		this.delayBeforeProcess=dunningPlanTransition.getDelayBeforeProcess(); 
		this.thresholdAmount=dunningPlanTransition.getThresholdAmount(); 
		this.waitDuration=dunningPlanTransition.getWaitDuration();  
		this.conditionEl=dunningPlanTransition.getConditionEl(); 
	 	
	}
	public DunningLevelEnum getDunningLevelFrom() {
		return dunningLevelFrom;
	}
	public void setDunningLevelFrom(DunningLevelEnum dunningLevelFrom) {
		this.dunningLevelFrom = dunningLevelFrom;
	}
	public DunningLevelEnum getDunningLevelTo() {
		return dunningLevelTo;
	}
	public void setDunningLevelTo(DunningLevelEnum dunningLevelTo) {
		this.dunningLevelTo = dunningLevelTo;
	}
	public Integer getDelayBeforeProcess() {
		return delayBeforeProcess;
	}
	public void setDelayBeforeProcess(Integer delayBeforeProcess) {
		this.delayBeforeProcess = delayBeforeProcess;
	}
	public BigDecimal getThresholdAmount() {
		return thresholdAmount;
	}
	public void setThresholdAmount(BigDecimal thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}
	public Integer getWaitDuration() {
		return waitDuration;
	}
	public void setWaitDuration(Integer waitDuration) {
		this.waitDuration = waitDuration;
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

