package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicableDueDateDelayDto extends BaseDto {

	private static final long serialVersionUID = 1L;

	private DueDateDelayLevelEnum level;
	private boolean custom;
	private DueDateDelayReferenceDateEnum referenceDate;
	private int numberOfDays;
	private String dueDateDelayEL;

	public DueDateDelayLevelEnum getLevel() {
		return level;
	}

	public void setLevel(DueDateDelayLevelEnum level) {
		this.level = level;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public DueDateDelayReferenceDateEnum getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(DueDateDelayReferenceDateEnum referenceDate) {
		this.referenceDate = referenceDate;
	}

	public int getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

	public String getDueDateDelayEL() {
		return dueDateDelayEL;
	}

	public void setDueDateDelayEL(String dueDateDelayEL) {
		this.dueDateDelayEL = dueDateDelayEL;
	}
}
