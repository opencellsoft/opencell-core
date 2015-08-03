package org.meveo.model.crm.wrapper;

import java.io.Serializable;
import java.util.Date;

public class DateWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date dateValue;
	private String label;

	public DateWrapper() {
	}

	public DateWrapper(String label, Date dateValue) {
		this(dateValue);
		this.label = label;
	}

	public DateWrapper(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.dateValue.toString();
	}

}
