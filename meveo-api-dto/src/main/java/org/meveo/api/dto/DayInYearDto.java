package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.MonthEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "DayInYear")
@XmlAccessorType(XmlAccessType.FIELD)
public class DayInYearDto implements Serializable {

	private static final long serialVersionUID = 7771829024178269036L;

	@XmlAttribute(required = true)
	private Integer day;

	@XmlAttribute(required = true)
	private MonthEnum month;

	public DayInYearDto() {

	}

	public DayInYearDto(DayInYear d) {
		day = d.getDay();

		if (d.getMonth() != null) {
			month = d.getMonth();
		}
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public MonthEnum getMonth() {
		return month;
	}

	public void setMonth(MonthEnum month) {
		this.month = month;
	}

	@Override
	public String toString() {
		return "DayInYearDto [day=" + day + ", month=" + month + "]";
	}

}
