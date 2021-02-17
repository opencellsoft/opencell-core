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

import java.util.Date;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Calendar
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cat_calendar", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cal_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "cat_calendar_seq"), })
public abstract class Calendar extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Calendar type
     */
    @Column(name = "cal_type", insertable = false, updatable = false)
    @Size(max = 31)
    private String calendarType;

    /**
     * Calendar initialization date - expression to determine a value for calendar initialization date
     */
    @Column(name = "init_date_el", length = 2000)
    @Size(max = 2000)
    private String initDateEL;

    /**
     * Calendar initialization date - expression to determine a value for calendar initialization date for Spark
     */
    @Column(name = "init_date_el_sp", length = 2000)
    @Size(max = 2000)
    private String initDateELSpark;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    /**
     * Calendar initialization date
     */
    @Transient
    private Date initDate;

    /**
     * Get the period end date for a given date
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public abstract Date nextCalendarDate(Date date);

    /**
     * Get the period start date for a given date
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public abstract Date previousCalendarDate(Date date);

    /**
     * Get the previous period end date
     * 
     * @param date Current date
     * @return The previous period end date
     */
    public abstract Date previousPeriodEndDate(Date date);

    /**
     * Get the next period start date
     * 
     * @param date Current date
     * @return The next period start date
     */
    public abstract Date nextPeriodStartDate(Date date);

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public String getCalendarType() {
        return calendarType;
    }

    /**
     * Get calendar type by optionally detalizing into a more detailed subtype. Currently applies to JOIN type calendar with subtypes INTERSECT and UNION.
     * 
     * @return calendar type.
     */
    public String getCalendarTypeWSubtypes() {
        return calendarType;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date startDate) {
        this.initDate = startDate;
    }

    /**
     * Truncate day and time portion of a date as calendar considers it to be required.<br>
     * Note: default implementation does not truncate anything
     * 
     * @param dateToTruncate Date to be truncated
     * @return Truncated date
     */
    public Date truncateDateTime(Date dateToTruncate) {
        return dateToTruncate;
    }

    /**
     * @return Calendar initialization date - expression to determine a value for calendar initialization date
     */
    public String getInitDateEL() {
        return initDateEL;
    }

    /**
     * @param initDateEL Calendar initialization date - expression to determine a value for calendar initialization date
     */
    public void setInitDateEL(String initDateEL) {
        this.initDateEL = initDateEL;
    }

    /**
     * @return Calendar initialization date - expression to determine a value for calendar initialization date for Spark
     */
    public String getInitDateELSpark() {
        return initDateELSpark;
    }

    /**
     * @param initDateELSpark Calendar initialization date - expression to determine a value for calendar initialization date for Spark
     */
    public void setInitDateELSpark(String initDateELSpark) {
        this.initDateELSpark = initDateELSpark;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    /**
     * Is calendar initialization with a starting date required to determine calendar dates
     *
     * @return True it setInitDate() method should be called before nextCalendarDate(), previousCalendarDate(), nextPeriodStarDate(), previousPeriodEndDate()
     */
    public boolean isInitializationRequired() {
        return false;
    }

    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.description);
        } else {
            return this.description;
        }
    }

    protected Date nextCalendarDate(Date date, Date initDate) {
        setInitDate(initDate);
        return nextCalendarDate(date);
    }
}