package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class ApplicableDueDateDelayDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicableDueDateDelayDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The level. */
    private DueDateDelayLevelEnum level;

    /** The custom. */
    private boolean custom;

    /** The reference date. */
    private DueDateDelayReferenceDateEnum referenceDate;

    /** The number of days. */
    private int numberOfDays;

    /** The due date delay EL. */
    private String dueDateDelayEL;

    /**
     * Gets the level.
     *
     * @return the level
     */
    public DueDateDelayLevelEnum getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(DueDateDelayLevelEnum level) {
        this.level = level;
    }

    /**
     * Checks if is custom.
     *
     * @return true, if is custom
     */
    public boolean isCustom() {
        return custom;
    }

    /**
     * Sets the custom.
     *
     * @param custom the new custom
     */
    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    /**
     * Gets the reference date.
     *
     * @return the reference date
     */
    public DueDateDelayReferenceDateEnum getReferenceDate() {
        return referenceDate;
    }

    /**
     * Sets the reference date.
     *
     * @param referenceDate the new reference date
     */
    public void setReferenceDate(DueDateDelayReferenceDateEnum referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * Gets the number of days.
     *
     * @return the number of days
     */
    public int getNumberOfDays() {
        return numberOfDays;
    }

    /**
     * Sets the number of days.
     *
     * @param numberOfDays the new number of days
     */
    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    /**
     * Gets the due date delay EL.
     *
     * @return the due date delay EL
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * Sets the due date delay EL.
     *
     * @param dueDateDelayEL the new due date delay EL
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }
}
