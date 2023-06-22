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
package org.meveo.model.catalog;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Counter template/definition
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ModuleItem
@Cacheable
@ObservableEntity
@ExportIdentifier({ "code" })
@Table(name = "cat_counter_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_counter_template_seq"), })
@NamedQueries({
        @NamedQuery(name = "counterTemplate.getNbrCounterWithNotService", query = "select count(*) from CounterTemplate c where c.id not in (select serv.counterTemplate from ServiceChargeTemplateUsage serv)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),

        @NamedQuery(name = "counterTemplate.getCounterWithNotService", query = "from CounterTemplate c where c.id not in (select serv.counterTemplate from ServiceChargeTemplateUsage serv) ") })
public class CounterTemplate extends EnableBusinessEntity {

    private static final long serialVersionUID = -1246995971618884001L;

    /**
     * Counter type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "counter_type", nullable = false)
    @NotNull
    private CounterTypeEnum counterType = CounterTypeEnum.USAGE;

    /**
     * Expression to determine calendar code
     */
    @Column(name = "calendar_code_el", length = 2000)
    @Size(max = 2000)
    private String calendarCodeEl;

    /**
     * Calendar for counter period calculation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    /**
     * Initial counter value
     */
    @Column(name = "level_num", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal ceiling;

    /**
     * Unit description
     */
    @Column(name = "unity_description", length = 20)
    @Size(max = 20)
    private String unityDescription;

    /**
     * On what account type counter is grouped on
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "counter_level", nullable = false)
    @NotNull
    private CounterTemplateLevel counterLevel = CounterTemplateLevel.UA;

    /**
     * Expression to calculate the initial counter value
     */
    @Column(name = "ceiling_expression_el", length = 2000)
    @Size(max = 2000)
    private String ceilingExpressionEl;

    /**
     * Comma separated levels at which notification should be fired
     */
    @Column(name = "notification_levels", length = 70)
    @Size(max = 70)
    private String notificationLevels;

    /**
     * Check if is it an accumulator account.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_accumulator")
    private Boolean accumulator = Boolean.FALSE;

    /**
     * The type field can be "Multi-value" if the accumulator is true
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "accumulator_type")
    private AccumulatorCounterTypeEnum accumulatorType;

    /**
     * An EL expression that returns a boolean that tells us if we accumulate or not.
     */
    @Column(name = "filter_el", length = 2000)
    @Size(max = 2000)
    private String filterEl;
    /**
     * An EL expression that Returns a string that is an identifier for what we count
     */
    @Column(name = "key_el", length = 2000)
    @Size(max = 2000)
    private String keyEl;

    /**
     * An EL expression that returns a number (BigDecimal) that contains the quantity we count
     */
    @Column(name = "value_el", length = 2000)
    @Size(max = 2000)
    private String valueEl;
    
    /**
     * The field can be disable/enable accumulator counter automatic application
     */
    @Type(type = "numeric_boolean")
    @Column(name = "managed_byapp")
    private boolean managedByApp = true;

    public CounterTypeEnum getCounterType() {
        return counterType;
    }

    public void setCounterType(CounterTypeEnum counterType) {
        this.counterType = counterType;
    }

    public String getCalendarCodeEl() {
        return calendarCodeEl;
    }

    public void setCalendarCodeEl(String calendarCodeEl) {
        this.calendarCodeEl = calendarCodeEl;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public BigDecimal getCeiling() {
        return ceiling;
    }

    public void setCeiling(BigDecimal ceiling) {
        this.ceiling = ceiling;
    }

    public String getUnityDescription() {
        return unityDescription;
    }

    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    public CounterTemplateLevel getCounterLevel() {
        return counterLevel;
    }

    public void setCounterLevel(CounterTemplateLevel counterLevel) {
        this.counterLevel = counterLevel;
    }

    public String getCeilingExpressionEl() {
        return ceilingExpressionEl;
    }

    public void setCeilingExpressionEl(String ceilingExpressionEl) {
        this.ceilingExpressionEl = ceilingExpressionEl;
    }

    public String getNotificationLevels() {
        return notificationLevels;
    }

    public void setNotificationLevels(String notificationLevels) {
        this.notificationLevels = notificationLevels;
    }

    public Boolean getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(Boolean accumulator) {
        this.accumulator = accumulator;
    }

    /**
     * Gets the accumulator type multiple or single
     * @return an accumulator counter type enum.
     */
    public AccumulatorCounterTypeEnum getAccumulatorType() {
        return accumulatorType;
    }

    /**
     * Sets the accumulator counter type.
     * @param accumulatorType AccumulatorCounterTypeEnum
     */
    public void setAccumulatorType(AccumulatorCounterTypeEnum accumulatorType) {
        this.accumulatorType = accumulatorType;
    }

    /**
     * Gets the EL filter
     * @return the EL Filter
     */
    public String getFilterEl() {
        return filterEl;
    }

    /**
     * Sets the EL filter
     * @param filterEl
     */
    public void setFilterEl(String filterEl) {
        this.filterEl = filterEl;
    }

    /**
     * Gets the EL key expression
     * @return the EL key expression
     */
    public String getKeyEl() {
        return keyEl;
    }

    /**
     * Sets EL key expression
     * @param keyEl El key expression
     */
    public void setKeyEl(String keyEl) {
        this.keyEl = keyEl;
    }

    /**
     * Gets the EL value expression
     * @return EL value expression
     */
    public String getValueEl() {
        return valueEl;
    }

    /**
     * Sets EL value expression
     * @param valueEl EL value expression
     */
    public void setValueEl(String valueEl) {
        this.valueEl = valueEl;
    }

	public boolean isManagedByApp() {
		return managedByApp;
	}

	public void setManagedByApp(boolean managedByApp) {
		this.managedByApp = managedByApp;
	}
    
    
}