/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
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
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.custom.CustomFieldValues;

import static javax.persistence.CascadeType.ALL;

/**
 * Discount plan item/details
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "DiscountPlanItem", inheritCFValuesFrom = { "discountPlan" })
@ExportIdentifier({ "discountPlan.code", "code" })
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
	@Column(name = "discount_plan_item_type", length = 50)
	private DiscountPlanItemTypeEnum discountPlanItemType = DiscountPlanItemTypeEnum.PERCENTAGE;

	/**
	 * Unique identifier UUID
	 */
	@Column(name = "uuid", nullable = false, updatable = false, length = 60)
	@Size(max = 60)
	@NotNull
	protected String uuid;

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

	@Column(name = "priorty")
	private Long priority;

	/**
	 * If true, then allows to negate the amount of affected invoice lines.
	 * If fase, then amount for the discount line produce by the discount plan item cannot exceed the amount of discounted lines.
	 * Default: false
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "allow_to_negate")
	@NotNull
	private boolean allowToNegate = false;

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

	/**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
    	if (uuid == null) {
    		uuid = UUID.randomUUID().toString();
    	}
    }
    
    @Override
    public String getUuid() {
    	setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
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
    


	/**
	 * @return the priority
	 */
	public Long getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public boolean isAllowToNegate() {
		return allowToNegate;
	}

	public void setAllowToNegate(boolean allowToNegate) {
		this.allowToNegate = allowToNegate;
	}
}
