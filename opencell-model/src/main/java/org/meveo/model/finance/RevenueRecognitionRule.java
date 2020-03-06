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

package org.meveo.model.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.scripts.RevenueRecognitionDelayUnitEnum;
import org.meveo.model.scripts.RevenueRecognitionEventEnum;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@Table(name = "ar_revenue_recog_rule", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_revenue_recog_rule_seq"), })
public class RevenueRecognitionRule extends EnableBusinessEntity {

    private static final long serialVersionUID = 7793758853731725829L;

    @ManyToOne(fetch = FetchType.LAZY)
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
