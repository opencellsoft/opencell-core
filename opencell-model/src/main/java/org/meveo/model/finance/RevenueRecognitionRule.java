package org.meveo.model.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.RevenueRecognitionDelayUnitEnum;
import org.meveo.model.scripts.RevenueRecognitionEventEnum;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@Table(name = "ar_revenue_recog_rule", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "ar_revenue_recog_rule_seq"), })
public class RevenueRecognitionRule extends BusinessEntity {

	private static final long serialVersionUID = 7793758853731725829L;

	@ManyToOne
	@JoinColumn(name = "script_instance_id")
	private ScriptInstance script;

	@Column(name = "start_delay")
	private Integer startDelay = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "start_unit")
	private RevenueRecognitionDelayUnitEnum startUnit;

	@Enumerated(EnumType.STRING)
	@Column(name = "start_event")
	private RevenueRecognitionEventEnum startEvent;

	@Column(name = "stop_delay")
	private Integer stopDelay = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "stop_unit")
	private RevenueRecognitionDelayUnitEnum stopUnit;

	@Enumerated(EnumType.STRING)
	@Column(name = "stop_event")
	private RevenueRecognitionEventEnum stopEvent;

	public ScriptInstance getScript() {
		return script;
	}

	public void setScript(ScriptInstance script) {
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
