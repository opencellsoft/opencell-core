/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
