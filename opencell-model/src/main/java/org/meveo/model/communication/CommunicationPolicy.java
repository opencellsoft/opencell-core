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
package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Communication policy
 * 
 * @author Andrius Karpavicius
 */
@Embeddable
public class CommunicationPolicy {

    /**
     * Minimum delay between two messages
     */
    @Column(name = "delay_min")
    private Long delayMinBetween2messages;

    /**
     * Maximum number of messages per day
     */
    @Column(name = "nb_max_day")
    private Long NbMaxMessagePerDay;

    /**
     * Maximum number of messages per week
     */
    @Column(name = "nb_max_week")
    private Long NbMaxMessagePerWeek;

    /**
     * Maximum number of messages per month
     */
    @Column(name = "nb_max_month")
    private Long NbMaxMessagePerMonth;

    public Long getDelayMinBetween2messages() {
        return delayMinBetween2messages;
    }

    public void setDelayMinBetween2messages(Long delayMinBetween2messages) {
        this.delayMinBetween2messages = delayMinBetween2messages;
    }

    public Long getNbMaxMessagePerDay() {
        return NbMaxMessagePerDay;
    }

    public void setNbMaxMessagePerDay(Long nbMaxMessagePerDay) {
        NbMaxMessagePerDay = nbMaxMessagePerDay;
    }

    public Long getNbMaxMessagePerWeek() {
        return NbMaxMessagePerWeek;
    }

    public void setNbMaxMessagePerWeek(Long nbMaxMessagePerWeek) {
        NbMaxMessagePerWeek = nbMaxMessagePerWeek;
    }

    public Long getNbMaxMessagePerMonth() {
        return NbMaxMessagePerMonth;
    }

    public void setNbMaxMessagePerMonth(Long nbMaxMessagePerMonth) {
        NbMaxMessagePerMonth = nbMaxMessagePerMonth;
    }

}
