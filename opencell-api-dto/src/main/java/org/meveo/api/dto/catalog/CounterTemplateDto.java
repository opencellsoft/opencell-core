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

package org.meveo.api.dto.catalog;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTypeEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The Class CounterTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CounterTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterTemplateDto extends EnableBusinessDto implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 2587489734648000805L;

    /**
     * The calendar.
     */
    @XmlAttribute(required = true)
    private String calendar;

    /**
     * The unity.
     */
    private String unity;

    /**
     * The type.
     */
    private CounterTypeEnum type;

    /**
     * The ceiling.
     */
    private BigDecimal ceiling;

    /**
     * The counter level.
     */
    private CounterTemplateLevel counterLevel;

    /**
     * The ceiling expression el.
     */
    private String ceilingExpressionEl;

    /**
     * The notification levels.
     */
    private String notificationLevels;

    /**
     * Is an accumulator counter
     */
    private Boolean accumulator;

    /**
     * Instantiates a new counter template dto.
     */
    public CounterTemplateDto() {
    }

    /**
     * Instantiates a new counter template dto.
     *
     * @param counterTemplate the CounterTemplate entity
     */
    public CounterTemplateDto(final CounterTemplate counterTemplate) {
        super(counterTemplate);
        unity = counterTemplate.getUnityDescription();
        type = counterTemplate.getCounterType();
        ceiling = counterTemplate.getCeiling();
        calendar = counterTemplate.getCalendar().getCode();
        counterLevel = counterTemplate.getCounterLevel();
        ceilingExpressionEl = counterTemplate.getCeilingExpressionEl();
        notificationLevels = counterTemplate.getNotificationLevels();
        accumulator = counterTemplate.getAccumulator();
    }

    /**
     * Gets the unity.
     *
     * @return the unity
     */
    public String getUnity() {
        return unity;
    }

    /**
     * Sets the unity.
     *
     * @param unity the new unity
     */
    public void setUnity(String unity) {
        this.unity = unity;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public CounterTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(CounterTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the ceiling.
     *
     * @return the ceiling
     */
    public BigDecimal getCeiling() {
        return ceiling;
    }

    /**
     * Sets the ceiling.
     *
     * @param ceiling the new ceiling
     */
    public void setCeiling(BigDecimal ceiling) {
        this.ceiling = ceiling;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public String getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets the counter level.
     *
     * @return the counter level
     */
    public CounterTemplateLevel getCounterLevel() {
        return counterLevel;
    }

    /**
     * Sets the counter level.
     *
     * @param counterLevel the new counter level
     */
    public void setCounterLevel(CounterTemplateLevel counterLevel) {
        this.counterLevel = counterLevel;
    }

    /**
     * Gets the ceiling expression el.
     *
     * @return the ceiling expression el
     */
    public String getCeilingExpressionEl() {
        return ceilingExpressionEl;
    }

    /**
     * Sets the ceiling expression el.
     *
     * @param ceilingExpressionEl the new ceiling expression el
     */
    public void setCeilingExpressionEl(String ceilingExpressionEl) {
        this.ceilingExpressionEl = ceilingExpressionEl;
    }

    /**
     * Gets the notification levels.
     *
     * @return the notification levels
     */
    public String getNotificationLevels() {
        return notificationLevels;
    }

    /**
     * Sets the notification levels.
     *
     * @param notificationLevels the new notification levels
     */
    public void setNotificationLevels(String notificationLevels) {
        this.notificationLevels = notificationLevels;
    }

    /**
     * Check if is an accumulator counter.
     *
     * @return true if is an accumulator counter false otherwise
     */
    public Boolean getAccumulator() {
        return accumulator;
    }

    /**
     * Sets accumulator counter flag.
     *
     * @param accumulator accumulator counter flag
     */
    public void setAccumulator(Boolean accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof CounterTemplateDto)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CounterTemplateDto other = (CounterTemplateDto) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format(
            "CounterTemplateDto [code=%s, description=%s, calendar=%s, unity=%s, type=%s, ceiling=%s, disabled=%s, counterLevel=%s, ceilingExpressionEl=%s, notificationLevels=%s]",
            getCode(), getDescription(), calendar, unity, type, ceiling, isDisabled(), counterLevel, ceilingExpressionEl, notificationLevels);
    }    
}