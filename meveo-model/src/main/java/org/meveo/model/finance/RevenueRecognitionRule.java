package org.meveo.model.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.RevenueRecognitionDelayUnitEnum;
import org.meveo.model.scripts.RevenueRecognitionEventEnum;
import org.meveo.model.scripts.RevenueRecognitionScriptEntity;

@Entity
@Table(name = "AR_REVENUE_RECOG_RULE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_REVENUE_RECOG_RULE_SEQ")
public class RevenueRecognitionRule extends BusinessEntity {

	private static final long serialVersionUID = 7793758853731725829L;

	@ManyToOne
	@JoinColumn(name = "SCRIPT_INSTANCE_ID")
	private RevenueRecognitionScriptEntity script;

	@Column(name = "START_DELAY")
	private Integer startDelay = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "START_UNIT")
	private RevenueRecognitionDelayUnitEnum startUnit;

	@Enumerated(EnumType.STRING)
	@Column(name = "START_EVENT")
	private RevenueRecognitionEventEnum startEvent;

	@Column(name = "STOP_DELAY")
	private Integer stopDelay = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "STOP_UNIT")
	private RevenueRecognitionDelayUnitEnum stopUnit;

	@Enumerated(EnumType.STRING)
	@Column(name = "STOP_EVENT")
	private RevenueRecognitionEventEnum stopEvent;

	public RevenueRecognitionScriptEntity getScript() {
		return script;
	}

	public void setScript(RevenueRecognitionScriptEntity script) {
		this.script = script;
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

}
