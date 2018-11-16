package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Discount plan
 * 
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cat_discount_plan", uniqueConstraints = { @UniqueConstraint(columnNames = { "code" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "cat_discount_plan_seq"), })
public class DiscountPlan extends EnableBusinessEntity {

	private static final long serialVersionUID = -2762453947446654646L;

	/**
	 * Minimum duration. Deprecated in 5.3 for not use.
	 */
	@Deprecated
	@Column(name = "min_duration")
	private int minDuration = 0;

	/**
	 * Maximum duration. Deprecated in 5.3 for not use.
	 */
	@Deprecated
	@Column(name = "max_duration")
	private int maxDuration = 99999;

	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	/**
	 * Discount plan items. Must not be eager to reload in GUI.
	 */
	@OneToMany(mappedBy = "discountPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DiscountPlanItem> discountPlanItems;

	/**
	 * Effectivity start date
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;

	/**
	 * Effectivity end date
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private Date endDate;
	/**
	 * 
	 * Length of effectivity. If start date is not null and end date is null, we use
	 * the defaultDuration from the discount plan. If start date is null, and
	 * defaultDuration is not null, defaultDuration is ignored.
	 */
	@Column(name = "default_duration")
	private Integer defaultDuration;

	/**
	 * Unit of duration
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "duration_unit", length = 50)
	private DurationPeriodUnitEnum durationUnit = DurationPeriodUnitEnum.DAY;

	public enum DurationPeriodUnitEnum {
		/**
		 * Month: 2
		 */
		MONTH(Calendar.MONTH),

		/**
		 * Day: 5
		 */
		DAY(Calendar.DAY_OF_MONTH);

		int calendarField;

		DurationPeriodUnitEnum(int calendarField) {
			this.calendarField = calendarField;
		}

		public String getLabel() {
			return "DiscountPlanItem" + "." + this.name();
		}

		public int getCalendarField() {
			return calendarField;
		}
	}

	public int getMinDuration() {
		return minDuration;
	}

	public void setMinDuration(int minDuration) {
		this.minDuration = minDuration;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}

	public List<DiscountPlanItem> getDiscountPlanItems() {
		return discountPlanItems;
	}

	public void setDiscountPlanItems(List<DiscountPlanItem> discountPlanItems) {
		this.discountPlanItems = discountPlanItems;
	}

	public void addDiscountPlanItem(DiscountPlanItem di) {
		if (discountPlanItems == null) {
			discountPlanItems = new ArrayList<>();
		}

		discountPlanItems.add(di);
	}

	public boolean isValid() {
		return (startDate == null || endDate == null || startDate.before(endDate));
	}

	/**
	 * Check if a date is within this Discount's effective date. Exclusive of the
	 * endDate. If startDate is null, it returns true. If startDate is not null and
	 * endDate is null, endDate is computed from the given duration.
	 * 
	 * @param date
	 *            the given date
	 * @return returns true if this DiscountItem is to be applied
	 */
	public boolean isEffective(Date date) {
		if (startDate == null) {
			return true;
		}

		Date computedEndDate = endDate;
		if (endDate == null && defaultDuration != null && durationUnit != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(durationUnit.calendarField, defaultDuration);
			computedEndDate = cal.getTime();
		}

		if (computedEndDate == null && date.compareTo(startDate) > 0) {
			return true;
		}

		return (date.compareTo(startDate) >= 0) && (date.before(computedEndDate));
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format("DiscountPlan [%s, minDuration=%s, maxDuration=%s, discountPlanItems=%s]",
				super.toString(), minDuration, maxDuration,
				discountPlanItems != null ? discountPlanItems.subList(0, Math.min(discountPlanItems.size(), maxLen))
						: null);
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