/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.model.mediation;

import java.util.ArrayList;
import java.util.List;

public enum TimePlanDaysEnum {
	SUNDAY(1,"timePlanDaysEnum.sunday"),
	MONDAY (2,"timePlanDaysEnum.monday"),
	TUESDAY(3,"timePlanDaysEnum.tuesday")
	,WEDNESDAY(4,"timePlanDaysEnum.wednesday")
	,THURSDAY(5,"timePlanDaysEnum.thursday")
	,FRIDAY(6,"timePlanDaysEnum.friday")
	,SATURDAY(7,"timePlanDaysEnum.saturday")
	,WORKINGDAYS(8,"timePlanDaysEnum.workingday")
	,WEEEKEND(9,"timePlanDaysEnum.weekend")
	,ALL(10,"timePlanDaysEnum.all");
	
	
	 private Integer id;
	    private String label;

	    TimePlanDaysEnum(Integer id, String label) {
	        this.id = id;
	        this.label = label;
	    }

	    public Integer getId() {
	        return id;
	    }

	    public String getLabel() {
	        return this.label;
	    }

	    public static TimePlanDaysEnum getValue(Integer id) {
	        if (id != null) {
	            for (TimePlanDaysEnum type : values()) {
	                if (id.equals(type.getId())) {
	                    return type;
	                }
	            }
	        }
	        return null;
	    }

	
	
	static int t[] = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4}; 
	TimePlanDaysEnum getDayOfWeek(int year /* between 1752 and 2200*/,int month /* between 1 and 12*/, int day){
		 if(month < 3){
			 year--;
		 }
		 return  values()[(year + year/4 - year/100 + year/400 + t[month-1] + day) % 7]; 
	}

	@SuppressWarnings("incomplete-switch")
	boolean isWeekDay(){
		boolean result=true;
		switch(this){
			case WORKINGDAYS: result=false;
			break;
			case WEEEKEND: result=false;
			break;
			case ALL: result=false;
			break;
		}
		return result;
	}

	List<TimePlanDaysEnum> getWeekDays(){
		List<TimePlanDaysEnum> result = new ArrayList<TimePlanDaysEnum>(); 
		switch(this){
			case WORKINGDAYS: 
				result.add(MONDAY);
				result.add(TUESDAY);
				result.add(WEDNESDAY);
				result.add(THURSDAY);
				result.add(FRIDAY);
			break;
			case WEEEKEND:
				result.add(SUNDAY);
				result.add(SATURDAY);
			break;
			case ALL: 
				result.add(SUNDAY);
				result.add(MONDAY);
				result.add(TUESDAY);
				result.add(WEDNESDAY);
				result.add(THURSDAY);
				result.add(FRIDAY);
				result.add(SATURDAY);
			break;
			default:
				result.add(this);
			break;
		}
		return result;
	}
}
