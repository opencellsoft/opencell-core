package org.meveo.api.dto.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.RevenueRecognitionDelayUnitEnum;
import org.meveo.model.scripts.RevenueRecognitionEventEnum;

@XmlRootElement(name = "RevenueRecognitionRule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDto extends EnableBusinessDto {

    private static final long serialVersionUID = 6795287686998653729L;

    private Integer startDelay;

    private RevenueRecognitionDelayUnitEnum startUnit;

    private RevenueRecognitionEventEnum startEvent;

    private Integer stopDelay;

    private RevenueRecognitionDelayUnitEnum stopUnit;

    private RevenueRecognitionEventEnum stopEvent;

    private ScriptInstanceDto script;

    public RevenueRecognitionRuleDto() {

    }

    public RevenueRecognitionRuleDto(RevenueRecognitionRule rrr) {
        super(rrr);

        startDelay = rrr.getStartDelay();
        startUnit = rrr.getStartUnit();
        startEvent = rrr.getStartEvent();
        stopDelay = rrr.getStopDelay();
        stopUnit = rrr.getStopUnit();
        stopEvent = rrr.getStopEvent();

        this.setScript(new ScriptInstanceDto(rrr.getScript()));
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

    @Override
    public String toString() {
        return String.format(
            "RevenueRecognitionRuleDto [code=%s, description=%s, disabled=%s, startDelay=%s, startUnit=%s, startEvent=%s, stopDelay=%s, stopUnit=%s, stopEvent=%s, script=%s]",
            code, description, isDisabled(), startDelay, startUnit, startEvent, stopDelay, stopUnit, stopEvent, script);
    }

    public ScriptInstanceDto getScript() {
        return script;
    }

    public void setScript(ScriptInstanceDto script) {
        this.script = script;
    }
}