package org.meveocrm.admin.action.reporting;

import java.util.Calendar;
import java.util.Date;

public class ChartEntityModel {

	private Date minDate;

	private Date maxDate;

	public ChartEntityModel() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		minDate = cal.getTime();

		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		maxDate = cal.getTime();
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

}
