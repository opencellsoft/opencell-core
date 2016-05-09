package org.meveo.api.dto.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.RevenueRecognitionDelayUnitEnum;
import org.meveo.model.scripts.RevenueRecognitionEventEnum;

@XmlRootElement(name = "RevenueRecognitionRule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDto {

	@XmlAttribute(required = true)
	private String code;
	private String description;
	private boolean disabled = false;
	private String scriptCode;
	private Integer startDelay;
	private RevenueRecognitionDelayUnitEnum startUnit;
	private RevenueRecognitionEventEnum startEvent;
	private Integer stopDelay;
	private RevenueRecognitionDelayUnitEnum stopUnit;
	private RevenueRecognitionEventEnum stopEvent;
	
	public RevenueRecognitionRuleDto(){
		
	}
	
	public RevenueRecognitionRuleDto(RevenueRecognitionRule rrr){
		code=rrr.getCode();
		description=rrr.getDescription();
		scriptCode=rrr.getScript().getCode();
		startDelay=rrr.getStartDelay();
		startUnit=rrr.getStartUnit();
		startEvent=rrr.getStartEvent();
		stopDelay=rrr.getStopDelay();
		stopUnit=rrr.getStopUnit();
		stopEvent=rrr.getStopEvent();
	}
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public Integer getStartDelay() {
		return startDelay;
	}

	public void setStartDelay(Integer startDelay) {
		this.startDelay = startDelay;
	}

	public RevenueRecognitionDelayUnitEnum getStartUnit() {
		return startUnit;
	}

	public void setStartUnit(RevenueRecognitionDelayUnitEnum startUnit) {
		this.startUnit = startUnit;
	}

	public RevenueRecognitionEventEnum getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(RevenueRecognitionEventEnum startEvent) {
		this.startEvent = startEvent;
	}

	public Integer getStopDelay() {
		return stopDelay;
	}

	public void setStopDelay(Integer stopDelay) {
		this.stopDelay = stopDelay;
	}

	public RevenueRecognitionDelayUnitEnum getStopUnit() {
		return stopUnit;
	}

	public void setStopUnit(RevenueRecognitionDelayUnitEnum stopUnit) {
		this.stopUnit = stopUnit;
	}

	public RevenueRecognitionEventEnum getStopEvent() {
		return stopEvent;
	}

	public void setStopEvent(RevenueRecognitionEventEnum stopEvent) {
		this.stopEvent = stopEvent;
	}
	
	public String toString(){
		return "RevenueRecognitionRuleDto [code="+code
				+",scriptCode="+scriptCode
				+",startDelay="+startDelay
				+",startUnit="+startUnit
				+",startEvent="+startEvent
				+",stopDelay="+stopDelay
				+",stopUnit="+stopUnit
				+",stopEvent="+stopEvent;
	}

}
