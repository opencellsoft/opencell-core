/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.math.BigDecimal;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;

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
        @NamedQuery(name = "counterTemplate.getNbrCounterWithNotService", query = "select count(*) from CounterTemplate c where c.id not in (select serv.counterTemplate from ServiceChargeTemplateUsage serv)"),

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

    public CounterTypeEnum getCounterType() {
        return counterType;
    }

    public void setCounterType(CounterTypeEnum counterType) {
        this.counterType = counterType;
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
}