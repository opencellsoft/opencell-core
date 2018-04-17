package org.meveo.api.dto.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.RevenueRecognitionDelayUnitEnum;
import org.meveo.model.scripts.RevenueRecognitionEventEnum;

/**
 * The Class RevenueRecognitionRuleDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "RevenueRecognitionRule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDto {

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    @XmlAttribute
    private String description;

    /** The disabled. */
    private boolean disabled = false;

    /** The start delay. */
    private Integer startDelay;

    /** The start unit. */
    private RevenueRecognitionDelayUnitEnum startUnit;

    /** The start event. */
    private RevenueRecognitionEventEnum startEvent;

    /** The stop delay. */
    private Integer stopDelay;

    /** The stop unit. */
    private RevenueRecognitionDelayUnitEnum stopUnit;

    /** The stop event. */
    private RevenueRecognitionEventEnum stopEvent;

    /** The script. */
    private ScriptInstanceDto script;

    /**
     * Instantiates a new revenue recognition rule dto.
     */
    public RevenueRecognitionRuleDto() {

    }

    /**
     * Instantiates a new revenue recognition rule dto.
     *
     * @param revenueRecognitionRule the RevenueRecognitionRule entity
     */
    public RevenueRecognitionRuleDto(RevenueRecognitionRule revenueRecognitionRule) {
        code = revenueRecognitionRule.getCode();
        description = revenueRecognitionRule.getDescription();
        startDelay = revenueRecognitionRule.getStartDelay();
        startUnit = revenueRecognitionRule.getStartUnit();
        startEvent = revenueRecognitionRule.getStartEvent();
        stopDelay = revenueRecognitionRule.getStopDelay();
        stopUnit = revenueRecognitionRule.getStopUnit();
        stopEvent = revenueRecognitionRule.getStopEvent();

        this.setScript(new ScriptInstanceDto(revenueRecognitionRule.getScript()));
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if is disabled.
     *
     * @return true, if is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the new disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the start delay.
     *
     * @return the start delay
     */
    public Integer getStartDelay() {
        return startDelay;
    }

    /**
     * Sets the start delay.
     *
     * @param startDelay the new start delay
     */
    public void setStartDelay(Integer startDelay) {
        this.startDelay = startDelay;
    }

    /**
     * Gets the start unit.
     *
     * @return the start unit
     */
    public RevenueRecognitionDelayUnitEnum getStartUnit() {
        return startUnit;
    }

    /**
     * Sets the start unit.
     *
     * @param startUnit the new start unit
     */
    public void setStartUnit(RevenueRecognitionDelayUnitEnum startUnit) {
        this.startUnit = startUnit;
    }

    /**
     * Gets the start event.
     *
     * @return the start event
     */
    public RevenueRecognitionEventEnum getStartEvent() {
        return startEvent;
    }

    /**
     * Sets the start event.
     *
     * @param startEvent the new start event
     */
    public void setStartEvent(RevenueRecognitionEventEnum startEvent) {
        this.startEvent = startEvent;
    }

    /**
     * Gets the stop delay.
     *
     * @return the stop delay
     */
    public Integer getStopDelay() {
        return stopDelay;
    }

    /**
     * Sets the stop delay.
     *
     * @param stopDelay the new stop delay
     */
    public void setStopDelay(Integer stopDelay) {
        this.stopDelay = stopDelay;
    }

    /**
     * Gets the stop unit.
     *
     * @return the stop unit
     */
    public RevenueRecognitionDelayUnitEnum getStopUnit() {
        return stopUnit;
    }

    /**
     * Sets the stop unit.
     *
     * @param stopUnit the new stop unit
     */
    public void setStopUnit(RevenueRecognitionDelayUnitEnum stopUnit) {
        this.stopUnit = stopUnit;
    }

    /**
     * Gets the stop event.
     *
     * @return the stop event
     */
    public RevenueRecognitionEventEnum getStopEvent() {
        return stopEvent;
    }

    /**
     * Sets the stop event.
     *
     * @param stopEvent the new stop event
     */
    public void setStopEvent(RevenueRecognitionEventEnum stopEvent) {
        this.stopEvent = stopEvent;
    }


    /**
     * Gets the script.
     *
     * @return the script
     */
    public ScriptInstanceDto getScript() {
        return script;
    }

    /**
     * Sets the script.
     *
     * @param script the new script
     */
    public void setScript(ScriptInstanceDto script) {
        this.script = script;
    }
    
    @Override
    public String toString() {
        return String.format(
            "RevenueRecognitionRuleDto [code=%s, description=%s, disabled=%s, startDelay=%s, startUnit=%s, startEvent=%s, stopDelay=%s, stopUnit=%s, stopEvent=%s, script=%s]",
            code, description, disabled, startDelay, startUnit, startEvent, stopDelay, stopUnit, stopEvent, script);
    }
}