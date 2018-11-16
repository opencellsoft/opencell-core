package org.meveo.api.dto.catalog;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;

/**
 * The Class DiscountPlanDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DiscountPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** Effective start date */
    private Date startDate;
    
    /** Effective end date */
	private Date endDate;
	
	/**
	 * Length of effectivity. 
	 * If start date is not null and end date is null, we use the defaultDuration from the discount plan.
	 * If start date is null, and defaultDuration is not null, defaultDuration is ignored. 
	 */
	private Integer defaultDuration;
	
	/** Unit of duration */
	private DurationPeriodUnitEnum durationUnit;

    /**
     * Instantiates a new DiscountPlanDto
     */
    public DiscountPlanDto() {
        super();
    }

    /**
     * Convert DiscountPlan JPA entity to DTO
     * 
     * @param discountPlan Entity to convert
     */
    public DiscountPlanDto(DiscountPlan discountPlan) {
        super(discountPlan);
    }

    @Override
	public String toString() {
		return "DiscountPlanDto [startDate=" + startDate + ", endDate=" + endDate + ", defaultDuration="
				+ defaultDuration + ", durationUnit=" + durationUnit + "]";
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getDefaultDuration() {
		return defaultDuration;
	}

	public void setDefaultDuration(Integer defaultDuration) {
		this.defaultDuration = defaultDuration;
	}

	public DurationPeriodUnitEnum getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(DurationPeriodUnitEnum durationUnit) {
		this.durationUnit = durationUnit;
	}
}