package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Discount plan item/details
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 **/
@Entity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "DISCOUNT_PLAN_ITEM", inheritCFValuesFrom = { "discountPlan" })
@ExportIdentifier({ "discount_plan_id", "code" })
@Table(name = "cat_discount_plan_item", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "discount_plan_id", "code" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "cat_discount_plan_item_seq"), })
public class DiscountPlanItem extends EnableEntity implements ICustomFieldEntity {

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
	 * Apply discount to a given invoice category. If not specified, discount will
	 * be applied to any invoice category.
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
     * Unique identifier UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    protected String uuid = UUID.randomUUID().toString();

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    protected CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    protected CustomFieldValues cfAccumulatedValues;

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
	 * @param expressionEl
	 *            Expression to determine if discount applies
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
	 * @param expressionElSpark
	 *            Expression to determine if discount applies - for Spark
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

    @Override
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid Unique identifier
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    /**
     * Change UUID value. Return old value
     * 
     * @return Old UUID value
     */
    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
    	return new ICustomFieldEntity[] { discountPlan };
    }

}
