package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cat_discount_plan_item", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "discount_plan_id", "code" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "cat_discount_plan_item_seq"), })
public class DiscountPlanItem extends EnableEntity {

	private static final long serialVersionUID = 4543503736567841084L;

	@Column(name = "code", length = 255, nullable = false)
	@Size(max = 255, min = 1)
	@NotNull
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discount_plan_id", nullable = false)
	@NotNull
	private DiscountPlan discountPlan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_category_id")
	private InvoiceCategory invoiceCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_sub_category_id")
	private InvoiceSubCategory invoiceSubCategory;

	@Column(name = "discount_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	@Min(0)
	@Max(100)
	private BigDecimal percent = new BigDecimal(0);

	@Column(name = "discount_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	private BigDecimal discountAmount = new BigDecimal(0);

	/**
	 * @deprecated As of version 5.0. No replacement.
	 */
	@Deprecated // until further analysis
	@Column(name = "accounting_code", length = 255)
	@Size(max = 255)
	private String accountingCode;

	@Column(name = "expression_el", length = 2000)
	@Size(max = 2000)
	private String expressionEl;

	@Column(name = "discount_percent_el", length = 2000)
	@Size(max = 2000)
	private String discountPercentEl;

	@Enumerated(EnumType.STRING)
	@Column(name = "discount_plan_item_type", length = 25)
	private DiscountPlanItemTypeEnum discountPlanItemType = DiscountPlanItemTypeEnum.PERCENTAGE;

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private Date endDate;

	/**
	 * Length of effectivity. If start date is not null and end date is null, we use
	 * the defaultDuration from the discount plan. If start date is null, and
	 * defaultDuration is not null, defaultDuration is ignored.
	 */
	@Column(name = "default_duration")
	private Integer defaultDuration;

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

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public InvoiceCategory getInvoiceCategory() {
		return invoiceCategory;
	}

	public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExpressionEl() {
		return expressionEl;
	}

	public void setExpressionEl(String expressionEl) {
		this.expressionEl = expressionEl;
	}

	@Override
	public int hashCode() {
		return 961 + (("DiscountPlanItem" + (code == null ? "" : code)).hashCode());
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof DiscountPlanItem)) {
			return false;
		}

		DiscountPlanItem other = (DiscountPlanItem) obj;
		if (id != null && other.getId() != null && id.equals(other.getId())) {
			return true;
		}
		if (code == null) {
			if (other.getCode() != null) {
				return false;
			}
		} else if (!code.equals(other.getCode())) {
			return false;
		}
		return true;
	}

	public String getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(String accountingCode) {
		this.accountingCode = accountingCode;
	}

	public String getDiscountPercentEl() {
		return discountPercentEl;
	}

	public void setDiscountPercentEl(String discountPercentEl) {
		this.discountPercentEl = discountPercentEl;
	}

	public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}

	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
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

	public boolean isValid() {
		return (startDate == null || endDate == null || startDate.before(endDate));
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

	/**
	 * Check if a date is within this Discount's effective date. Exclusive of the endDate.
	 * If startDate is null, it returns true.
	 * If startDate is not null and endDate is null, endDate is computed from the given duration.
	 * @param date the given date
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

}
