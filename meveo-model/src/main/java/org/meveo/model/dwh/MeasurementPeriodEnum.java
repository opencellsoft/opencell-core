package org.meveo.model.dwh;

public enum MeasurementPeriodEnum {
	DAILY, WEEKLY, MONTHLY, YEARLY;

	public String getLabel() {
		return "enum.measurementperiod." + name();
	}

}
