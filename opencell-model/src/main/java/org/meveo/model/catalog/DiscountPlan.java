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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.cpq.Product;

/**
 * Discount plan
 * 
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "DiscountPlan")
@Table(name = "cat_discount_plan", uniqueConstraints = { @UniqueConstraint(columnNames = { "code" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "cat_discount_plan_seq"), })
@NamedQueries({
		@NamedQuery(name = "discountPlan.getExpired", query = "select d.id from DiscountPlan d where d.endDate is not null and d.endDate<=:date and d.status in (:statuses)") })

public class DiscountPlan extends EnableBusinessCFEntity implements ISearchable {

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

	/**
	 * DP type
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "discount_plan_type", length = 50)
	private DiscountPlanTypeEnum discountPlanType;


	/**
	 * Status of the discount plan:
	 * <p>
	 * DRAFT “Draft”: The discount plan is being configured and is waiting for validation
	 * ACTIVE “Active”: the discount plan is available and can be set used
	 * IN_USE “In use”: the discount plan is currently in use / it has been applied at least once
	 * EXPIRED “Expired”: the discount plan has expired. Either because the end of validity date has been reach, or the used quantity has is equal to initial quantity.
	 * <p>
	 * Initialized with DRAFT
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@NotNull
	private DiscountPlanStatusEnum status = DiscountPlanStatusEnum.DRAFT;

	/**
	 * Datetime of last status update
	 * Automatically filed at creation and status update
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date")
	private Date statusDate;

	/**
	 * The initial available quantity for the discount plan.
	 * Default value is 0 = infinite.
	 * For types QUOTE, INVOICE, INVOICE_LINE, the value is forced to 0.
	 */
	@Column(name = "initial_quantity")
	private Long initialQuantity = 0L;

	/**
	 * How many times the discount plan has been used.
	 * Initialized to 0.
	 * If intialQuantity is not 0, then reaching the initialQuantity expires the discount plan.
	 * The value is incremented every time the discountPlan is instantiated on any Billing Account, Subscription, or ProductInstance
	 */
	@Column(name = "used_quantity")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long usedQuantity = 0L;

	/**
	 * How many times the discount can be applied on a given entity (BillingAccount, Subscription, Product Instance).
	 * Default value is 0 = infinite.
	 * Useful for one-time discounts.
	 * See DiscountPlanInstance below for more details.
	 * This has no real meaning for discounts applied to invoices or invoice lines.
	 */
	@Column(name = "application_limit", columnDefinition = "bigint default 0", nullable = false)
	private Long applicationLimit = 0L;

	/**
	 * A boolean EL that must evaluate to true to allow the discount plan to be applied.
	 * It will have access to the variables:
	 * entity: the entity on which we want to apply the discount
	 * discountPlan: the discount plan itself
	 */
	@Column(name = "application_filter_el")
	private String applicationFilterEL;

	/**
	 * A list of entities (CustomerCategory, Offer, Product, Article).
	 * Only instances (Customer/BillingAccount, Subscription, ProductInstance, InvoiceLine) of these entities can have the discount applied to them.
	 */

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "cat_discount_applicable_entity", joinColumns = { @JoinColumn(name = "disount_plan_id") })
	@AttributeOverrides(value = { @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 255)),
			@AttributeOverride(name = "entityClass", column = @Column(name = "entity_class", nullable = false, length = 255)) })
	private List<ApplicableEntity> applicableEntities;

	/**
	 * A list of discounts plans that cannot be active at the same time on an entity instance.
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JoinColumn(name = "discount_plan_id")
	private List<DiscountPlan> incompatibleDiscountPlans;

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
	

	/**
	 * Expression to determine if discount applies
	 */
	@Column(name = "expression_el", length = 2000)
	@Size(max = 2000)
	private String expressionEl;

	public DiscountPlan() {}
	
	public DiscountPlan(DiscountPlan dp) {

		this.setActive(dp.isActive());
		this.setStartDate(dp.getStartDate());
		this.setEndDate(dp.getEndDate());
		this.setDefaultDuration(dp.getDefaultDuration());
		this.setDurationUnit(dp.getDurationUnit());
		this.setUUIDIfNull();
		this.setCode(dp.getCode());
		this.setMaxDuration(dp.getMaxDuration());
		this.setDescription(dp.getDescription());
		this.setMinDuration(dp.getMinDuration());
		this.setCfValues(dp.getCfValues());
		this.setCfAccumulatedValues(dp.getCfAccumulatedValues());
		this.setDiscountPlanItems(new ArrayList<>(dp.getDiscountPlanItems()));
	}
	
	public boolean isValid() {
		return (startDate == null || endDate == null || startDate.before(endDate));
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

	@Override
	public String toString() {
		return "DiscountPlan [minDuration=" + minDuration + ", maxDuration=" + maxDuration + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", defaultDuration=" + defaultDuration + ", durationUnit=" + durationUnit
				+ "]";
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

	/**
	 * @return the discountPlanType
	 */
	public DiscountPlanTypeEnum getDiscountPlanType() {
		return discountPlanType;
	}

	/**
	 * @param discountPlanType the discountPlanType to set
	 */
	public void setDiscountPlanType(DiscountPlanTypeEnum discountPlanType) {
		this.discountPlanType = discountPlanType;
	}

	public DiscountPlanStatusEnum getStatus() {
		return status;
	}

	public void setStatus(DiscountPlanStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Long getInitialQuantity() {
		if (initialQuantity == null) {
			initialQuantity = 0L;
		}
		return initialQuantity;
	}

	public void setInitialQuantity(Long initialQuantity) {
		this.initialQuantity = initialQuantity;
	}

	public Long getUsedQuantity() {
		if (usedQuantity == null) {
			usedQuantity = 0L;
		}
		return usedQuantity;
	}

	public void setUsedQuantity(Long usedQuantity) {
		this.usedQuantity = usedQuantity;
	}

	public Long getApplicationLimit() {
		if (applicationLimit == null) {
			applicationLimit = 0L;
		}
		return applicationLimit;
	}

	public void setApplicationLimit(Long applicationLimit) {
		this.applicationLimit = applicationLimit;
	}

	public String getApplicationFilterEL() {
		return applicationFilterEL;
	}

	public void setApplicationFilterEL(String applicationFilterEL) {
		this.applicationFilterEL = applicationFilterEL;
	}

	public List<ApplicableEntity> getApplicableEntities() {
		return applicableEntities;
	}

	public void setApplicableEntities(List<ApplicableEntity> applicableEntities) {
		this.applicableEntities = applicableEntities;
	}

	public List<DiscountPlan> getIncompatibleDiscountPlans() {
		return incompatibleDiscountPlans;
	}

	public void setIncompatibleDiscountPlans(List<DiscountPlan> incompatibleDiscountPlans) {
		this.incompatibleDiscountPlans = incompatibleDiscountPlans;
	}

	/**
	 * @return the expressionEl
	 */
	public String getExpressionEl() {
		return expressionEl;
	}

	/**
	 * @param expressionEl the expressionEl to set
	 */
	public void setExpressionEl(String expressionEl) {
		this.expressionEl = expressionEl;
	}
	
    /**
     * Check if a date is within this Discount's effective date. Exclusive of the endDate. If startDate is null, it returns true. If startDate is not null and endDate is null,
     * endDate is computed from the given duration.
     *
     * @param date the given date
     * @return returns true if this DiscountItem is to be applied
     */
    public boolean isEffective(Date date) {
        if (startDate == null && endDate == null) {
            return true;
        }
        if (startDate != null && endDate == null) {
            return date.compareTo(startDate) >= 0;
        }
        if (startDate == null) {
            return date.before(endDate);
        }
        return (date.compareTo(startDate) >= 0) && (date.before(endDate));
    }



}