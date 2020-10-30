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

import javax.persistence.*;

/**
 * Represents a calendar that operates on two calendars joining them by union or intersection. Union will return the greatest matched period while intersect will return the
 * smallest matched period.
 * 
 * Example: given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return
 * monday and friday as previous and next calendar days and intersection calendar will return 8 and 15 as previous and next calendar days
 * 
 * @author Andrius Karpavicius
 * 
 */
@Entity
@DiscriminatorValue("JOIN")
public class CalendarJoin extends Calendar {


    public enum CalendarJoinTypeEnum {
        UNION, INTERSECT, APPEND;

        public String getLabel() {
            return "CalendarJoinTypeEnum." + this.name();
        }
    }

    private static final long serialVersionUID = 1L;

    @Column(name = "join_type")
    @Enumerated(EnumType.STRING)
    private CalendarJoinTypeEnum joinType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "join_cal_1_id")
    private Calendar joinCalendar1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "join_cal_2_id")
    private Calendar joinCalendar2;

    @Transient
    private Date lastCalendar1date;

    public CalendarJoinTypeEnum getJoinType() {
        return joinType;
    }

    public void setJoinType(CalendarJoinTypeEnum joinType) {
        this.joinType = joinType;
    }

    public Calendar getJoinCalendar1() {
        return joinCalendar1;
    }

    public void setJoinCalendar1(Calendar joinCalendar1) {
        this.joinCalendar1 = joinCalendar1;
    }

    public Calendar getJoinCalendar2() {
        return joinCalendar2;
    }

    public void setJoinCalendar2(Calendar joinCalendar2) {
        this.joinCalendar2 = joinCalendar2;
    }

    /**
     * Determines a next calendar date joining next calendar date result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return friday as
     * next calendar days and intersection calendar will return 15 as next calendar days
     * 
     * @param date Date to check
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {
        initChildCalendarsDate();

        if(joinType.equals(CalendarJoinTypeEnum.APPEND)){
            Date firstCalendarDate = joinCalendar1.nextCalendarDate(date, getInitDate());

            if(firstCalendarDate != null){
                lastCalendar1date = firstCalendarDate;
                return firstCalendarDate;
            }else {
                Date secondDate = joinCalendar2.nextCalendarDate(date, lastCalendar1date);
                return secondDate;
            }
        }
    	

        Date date1 = joinCalendar1.nextCalendarDate(date);
        Date date2 = joinCalendar2.nextCalendarDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }
        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;
            } else if (date1 != null && date2 == null) {
                return date1;
            } else if (date1.after(date2)) {
                return date1;
            } else {
                return date2;
            }
            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;
            } else if (date1.before(date2)) {
                return date1;
            } else {
                return date2;
            }
        }

        return null;
    }

    /**
     * Determines a previous calendar date joining previous calendar date result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return monday as
     * previous calendar days and intersection calendar will return 8 as previous calendar days
     * 
     * @param date Date to check
     * @return Previous calendar date.
     */
    public Date previousCalendarDate(Date date) {
    	
    	initChildCalendarsDate();
    	
        Date date1 = joinCalendar1.previousCalendarDate(date);
        Date date2 = joinCalendar2.previousCalendarDate(date);
        
        if(joinType == CalendarJoinTypeEnum.APPEND &&joinCalendar1 instanceof CalendarPeriod && joinCalendar2 instanceof CalendarPeriod) {
    		Date endOfPreviousDate = ((CalendarPeriod)joinCalendar1).getLimitOfNextDate();
    		if(endOfPreviousDate!=null) {
				joinCalendar2.setInitDate(endOfPreviousDate);
        		date2 = joinCalendar2.previousCalendarDate(date);
    		}
    	}

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;
            } else if (date1 != null && date2 == null) {
                return date1;
            } else if (date1.before(date2)) {
                return date1;
            } else {
                return date2;
            }
            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;
            } else if (date1.after(date2)) {
                return date1;
            } else {
                return date2;
            }
        } else if (joinType == CalendarJoinTypeEnum.APPEND) {
        	if (date1 != null) {
        		return date1;
        	} else {
        		return date2;
        	}
        }
        return null;
    }

	private void initChildCalendarsDate() {
		if(joinCalendar1.getInitDate()==null) {
    		joinCalendar1.setInitDate(getInitDate());
    	}
    	if(joinCalendar2.getInitDate()==null) {
    		joinCalendar2.setInitDate(getInitDate());
    	}
	}

    /**
     * Determines a previous period end date by joining previousPeriodEndDate result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return friday as
     * previous period end date and intersection calendar will return 15 as period end date
     * 
     * @param date Date to check
     * @return Previous period end date calendar date.
     */
    @Override
    public Date previousPeriodEndDate(Date date) {

    	initChildCalendarsDate();
        Date date1 = joinCalendar1.previousPeriodEndDate(date);
        Date date2 = joinCalendar2.previousPeriodEndDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;
            } else if (date1 != null && date2 == null) {
                return date1;
            } else if (date1.after(date2)) {
                return date1;
            } else {
                return date2;
            }
            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;
            } else if (date1.before(date2)) {
                return date1;
            } else {
                return date2;
            }
        } else if (joinType == CalendarJoinTypeEnum.APPEND) {
        	if (date1 != null) {
        		return date1;
        	} else {
        		return date2;
        	}
        }

        return null;
    }

    /**
     * Determines a next period start date joining nextPeriodStartDate result from two calendars. Result depends on a join type:
     * 
     * given one calendar as a weekday calendar with interval monday - friday and another calendar of as hour calendar with interval 8 - 15. A union calendar will return monday as
     * next period start date and intersection calendar will return 8 as next period start date
     * 
     * @param date Date to check
     * @return Next period start date.
     */
    @Override
    public Date nextPeriodStartDate(Date date) {

    	initChildCalendarsDate();
        Date date1 = joinCalendar1.nextPeriodStartDate(date);
        Date date2 = joinCalendar2.nextPeriodStartDate(date);

        if (date1 == null && date2 == null) {
            return null;
        }

        // Get the farthest date
        if (joinType == CalendarJoinTypeEnum.UNION) {
            if (date1 == null && date2 != null) {
                return date2;
            } else if (date1 != null && date2 == null) {
                return date1;
            } else if (date1.before(date2)) {
                return date1;
            } else {
                return date2;
            }
            // Get the closest date
        } else if (joinType == CalendarJoinTypeEnum.INTERSECT) {
            if (date1 == null || date2 == null) {
                return null;
            } else if (date1.after(date2)) {
                return date1;
            } else {
                return date2;
            }
        } else if (joinType == CalendarJoinTypeEnum.APPEND) {
        	if (date1 != null) {
        		return date1;
        	} else {
        		return date2;
        	}
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Date truncateDateTime(Date dateToTruncate) {
        Date date1 = joinCalendar1.truncateDateTime(dateToTruncate);
        Date date2 = joinCalendar2.truncateDateTime(dateToTruncate);

        // Pick which one has greatest time granularity
        if (date1.getSeconds() > date2.getSeconds() || date1.getMinutes() > date2.getMinutes() || date1.getHours() > date2.getHours() || date1.getDate() > date2.getDate()) {
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * Splits a single JOIN calendar type into two: INTERSECT and UNION
     */
    public String getCalendarTypeWSubtypes() {
        return joinType.name();
    }

    @Override
    public boolean isInitializationRequired() {
        return joinCalendar1.isInitializationRequired() || joinCalendar2.isInitializationRequired();
    }
}