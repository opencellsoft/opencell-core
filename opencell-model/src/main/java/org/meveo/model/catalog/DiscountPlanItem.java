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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;

/**
 * Discount plan item/details
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Entity
@Cacheable
@ExportIdentifier({ "discount_plan_id", "code" })
@Table(name = "cat_discount_plan_item", uniqueConstraints = { @UniqueConstraint(columnNames = { "discount_plan_id", "code" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_discount_plan_item_seq"), })
public class DiscountPlanItem extends EnableEntity {

    private static final long serialVersionUID = 4543503736567841084L;

    /**
     * Code
     */
    @Column(name = "code", length = 255, nullable = false)
    @Size(max = 255, min = 1)
    @NotNull
    private String code;

    /**
     * Parent discount plan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_id", nullable = false)
    @NotNull
    private DiscountPlan discountPlan;

    /**
     * Apply discount to a given invoice category. If not specified, discount will be applied to any invoice category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_category_id")
    private InvoiceCategory invoiceCategory;

    /**
     * Apply discount to a given invoice subcategory.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    /**
     * @deprecated As of version 5.0. No replacement.
     */
    @Deprecated // until further analysis
    @Column(name = "accounting_code", length = 255)
    @Size(max = 255)
    private String accountingCode;

    /**
     * Expression to determine if discount applies
     */
    @Column(name = "expression_el", length = 2000)
    @Size(max = 2000)
    private String expressionEl;

    /**
     * Expression to determine if discount applies - for Spark
     */
    @Column(name = "expression_el_sp", length = 2000)
    @Size(max = 2000)
    private String expressionElSpark;
   
    /**
     * The absolute or percentage discount amount.
     */
    @Column(name = "discount_value", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	private BigDecimal discountValue;
    
    /**
     * The absolute or percentage discount amount EL.
     */
    @Column(name = "discount_value_el", length = 2000)
    @Size(max = 2000)
	private String discountValueEL;
    
    /**
     * Expression to calculate discount percentage - for Spark
     */
    @Column(name = "discount_value_el_sp", length = 2000)
    @Size(max = 2000)
    private String discountValueElSpark;
    
    /**
     * Type of discount, default is percent.
     */
    @Enumerated(EnumType.STRING)
	@Column(name = "discount_plan_item_type", length = 25)
	private DiscountPlanItemTypeEnum discountPlanItemType = DiscountPlanItemTypeEnum.PERCENTAGE;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Expression to determine if discount applies
     */
    public String getExpressionEl() {
        return expressionEl;
    }

    /**
     * @param expressionEl Expression to determine if discount applies
     */
    public void setExpressionEl(String expressionEl) {
        this.expressionEl = expressionEl;
    }

    /**
     * @return Expression to determine if discount applies - for Spark
     */
    public String getExpressionElSpark() {
        return expressionElSpark;
    }

    /**
     * @param expressionElSpark Expression to determine if discount applies - for Spark
     */
    public void setExpressionElSpark(String expressionElSpark) {
        this.expressionElSpark = expressionElSpark;
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
    
    public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}

	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
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

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public String getDiscountValueElSpark() {
		return discountValueElSpark;
	}

	public void setDiscountValueElSpark(String discountValueElSpark) {
		this.discountValueElSpark = discountValueElSpark;
	}

	public void setDiscountValueEL(String discountValueEL) {
		this.discountValueEL = discountValueEL;
	}

	public String getDiscountValueEL() {
		return discountValueEL;
	}

}
