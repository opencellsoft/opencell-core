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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Fixed Date entry for Fixed calendar
 * 
 * @author Mohammed Amine TAZI
 */
@Entity
@Cacheable
@Table(name = "cat_fixed_date")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_fixed_date_seq"), })
public class FixedDate extends BaseEntity {

    private static final long serialVersionUID = 1L;


    @Column(name = "fixed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fixedDate;

    @ManyToOne
    @JoinColumn(name="cat_calendar_id", nullable=false,insertable = true,updatable = true)
    private CalendarFixed calendarFixed;
    
    
    public void setFixedDate(Date fixedDate) {
		this.fixedDate = fixedDate;
	}
    
    public Date getFixedDate() {
		return fixedDate;
	}
    
    public void setCalendarFixed(CalendarFixed calendarFixed) {
		this.calendarFixed = calendarFixed;
	}
    
    public CalendarFixed getCalendarFixed() {
		return calendarFixed;
	}
    
    public FixedDate() {
        super();
    }

    public FixedDate(Date fixedDate) {
        super();
        this.fixedDate = fixedDate;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof FixedDate)) {
            return false;
        }

        FixedDate other = (FixedDate) obj;
        if(this.getFixedDate().equals(other.getFixedDate())) {
        	return true;
        } else {
        	return false;
        }
    }
    
    
}
