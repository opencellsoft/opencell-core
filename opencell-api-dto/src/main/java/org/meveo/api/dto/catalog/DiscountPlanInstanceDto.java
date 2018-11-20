package org.meveo.api.dto.catalog;

import java.util.Date;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;
import org.meveo.model.catalog.DiscountPlanInstance;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class DiscountPlanInstanceDto extends BaseEntityDto {

	private static final long serialVersionUID = 3302140811850985823L;

	/**
	 * The discount plan code.
	 */
	private String discountPlan;

	/**
	 * The billingAccount code.
	 */
	private String billingAccount;

	/**
	 * Effectivity start date
	 */
	private Date startDate;

	/**
	 * Effectivity end date
	 */
	private Date endDate;

	/**
	 * 
	 * Length of effectivity. If start date is not null and end date is null, we use
	 * the defaultDuration from the discount plan. If start date is null, and
	 * defaultDuration is not null, defaultDuration is ignored.
	 */
	private Integer defaultDuration;

	/**
	 * Unit of duration
	 */
	private DurationPeriodUnitEnum durationUnit = DurationPeriodUnitEnum.DAY;

	/**
	 * Default constructor.
	 */
	public DiscountPlanInstanceDto() {
		
	}
	
	/**
	 * Initialized from {@link DiscountPlanInstance} entity.
	 * @param e the discount plan instance entity
	 */
	public DiscountPlanInstanceDto(DiscountPlanInstance e) {
		discountPlan = e.getDiscountPlan().getCode();
		billingAccount = e.getBillingAccount().getCode();
		startDate = e.getStartDate();
		endDate = e.getEndDate();
		defaultDuration = e.getDefaultDuration();
		durationUnit = e.getDurationUnit();
	}

	/**
	 * Gets the discount plan code
	 * @return code
	 */
	public String getDiscountPlan() {
		return discountPlan;
	}

	/**
	 * Sets the discount plan code
	 * @param discountPlan code
	 */
	public void setDiscountPlan(String discountPlan) {
		this.discountPlan = discountPlan;
	}

	/**
	 * Gets the billing account code
	 * @return code
	 */
	public String getBillingAccount() {
		return billingAccount;
	}

	/**
	 * Sets the billing account code
	 * @param billingAccount code
	 */
	public void setBillingAccount(String billingAccount) {
		this.billingAccount = billingAccount;
	}

	/**
	 * Gets the effective start date.
	 * @return date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the effective start date.
	 * @param startDate date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the effective end date.
	 * @return date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the effective end date.
	 * @param startDate date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the default duration. Use in combination with durationUnit.
	 * @return how long this discount is active
	 */
	public Integer getDefaultDuration() {
		return defaultDuration;
	}

	/**
	 * Sets the default duration. Use in combination with durationUnit.
	 * @param defaultDuration how long this discount is active
	 */
	public void setDefaultDuration(Integer defaultDuration) {
		this.defaultDuration = defaultDuration;
	}

	/**
	 * Gets the period of duration.
	 * @return unit of duration
	 */
	public DurationPeriodUnitEnum getDurationUnit() {
		return durationUnit;
	}

	/**
	 * Sets the period of duration.
	 * @param durationUnit unit
	 */
	public void setDurationUnit(DurationPeriodUnitEnum durationUnit) {
		this.durationUnit = durationUnit;
	}

}
