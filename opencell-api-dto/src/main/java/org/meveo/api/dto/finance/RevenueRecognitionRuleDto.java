/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
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
public class RevenueRecognitionRuleDto extends EnableBusinessDto {

    private static final long serialVersionUID = 6795287686998653729L;

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
     * Convert revenue recognition rule entity to DTO
     *
     * @param revenueRecognitionRule Entity to convert
     */
    public RevenueRecognitionRuleDto(RevenueRecognitionRule revenueRecognitionRule) {
        super(revenueRecognitionRule);

        startDelay = revenueRecognitionRule.getStartDelay();
        startUnit = revenueRecognitionRule.getStartUnit();
        startEvent = revenueRecognitionRule.getStartEvent();
        stopDelay = revenueRecognitionRule.getStopDelay();
        stopUnit = revenueRecognitionRule.getStopUnit();
        stopEvent = revenueRecognitionRule.getStopEvent();

        this.setScript(new ScriptInstanceDto(revenueRecognitionRule.getScript()));
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
            code, description, isDisabled(), startDelay, startUnit, startEvent, stopDelay, stopUnit, stopEvent, script);
    }
}