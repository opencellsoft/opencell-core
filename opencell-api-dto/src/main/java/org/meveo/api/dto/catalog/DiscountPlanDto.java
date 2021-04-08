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

package org.meveo.api.dto.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ApplicableEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.ApplicableEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class DiscountPlanDto.
 *
 * @author anasseh
 */
@XmlRootElement(name = "DiscountPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanDto extends EnableBusinessDto {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Effective start date
	 */
	@Schema(description = "Effective start date")
	private Date startDate;

	/**
	 * Effective end date
	 */
	@Schema(description = "Effective end date")
	private Date endDate;

	/**
	 * Length of effectivity.
	 * If start date is not null and end date is null, we use the defaultDuration from the discount plan.
	 * If start date is null, and defaultDuration is not null, defaultDuration is ignored.
	 */
	@Schema(description = "Length of effectivity."
							+ "<br/> If start date is not null and end date is null, we use the defaultDuration from the discount plan."
							+ "<br />If start date is null, and defaultDuration is not null, defaultDuration is ignored.")
	private Integer defaultDuration;

	/**
	 * Unit of duration
	 */
	@Schema(description = "Unit of duration", example = "possible value are : MONTH, DAY")
	private DurationPeriodUnitEnum durationUnit;

	/**
	 * The custom fields.
	 */
	@XmlElement(required = false)
	@Schema(description = "The custom fields associated to discount plan")
	private CustomFieldsDto customFields;

	@XmlElementWrapper(name = "discountPlanItems")
	@XmlElement(name = "discountPlanItem")
	@Schema(description = "list of discount plan item", example = "discountPlanItem[]")
	private List<DiscountPlanItemDto> discountPlanItems;


    /** expressionEl */
	@Schema(description = "expression language")
    private String expressionEl;

	/**
	 * Type of the discount plan. Defines on which entity the discount plan can be applied.
	 */
	@Schema(description = "Type of the discount plan. Defines on which entity the discount plan can be applied", example = "possible value are : QUOTE, OFFER, PRODUCT, INVOICE, INVOICE_LINE, PROMO_CODE")
	private DiscountPlanTypeEnum discountPlanType;

	/**
	 * Status of the discount plan:
	 * DRAFT “Draft”: The discount plan is being configured and is waiting for validation
	 * ACTIVE “Active”: the discount plan is available and can be set used
	 * IN_USE “In use”: the discount plan is currently in use / it has been applied at least once
	 * EXPIRED “Expired”: the discount plan has expired. Either because the end of validity date has been reach, or the used quantity has is equal to initial quantity.
	 * Initialized with DRAFT
	 */
	@Schema(description = "Status of the discount plan. The default value is DRAFT", example = "possible value are : DRAFT, ACTIVE, INACTIVE, IN_USE, EXPIRED")
	private DiscountPlanStatusEnum status = DiscountPlanStatusEnum.DRAFT;

	/**
	 * Datetime of last status update
	 * Automatically filed at creation and status update
	 */
	@Schema(description = "Datetime of last status update, Automatically filed at creation and status update")
	private Date statusDate;

	/**
	 * The initial available quantity for the discount plan.
	 * Default value is 0 = infinite.
	 * For types QUOTE, INVOICE, INVOICE_LINE, the value is forced to 0.
	 */
	@Schema(description = "The initial available quantity for the discount plan, For types QUOTE, INVOICE, INVOICE_LINE, the value is forced to 0")
	private Long initialQuantity = 0L;

	/**
	 * How many times the discount plan has been used.
	 * Initialized to 0.
	 * If intialQuantity is not 0, then reaching the initialQuantity expires the discount plan.
	 * The value is incremented every time the discountPlan is instantiated on any Billing Account, Subscription, or ProductInstance
	 */
	@Schema(description = "How many times the discount plan has been used"
			+ ".<br/> If intialQuantity is not 0, then reaching the initialQuantity expires the discount plan"
			+ ".<br />The value is incremented every time the discountPlan is instantiated on any Billing Account, Subscription, or ProductInstance")
	private Long usedQuantity = 0L;

	/**
	 * How many times the discount can be applied on a given entity (BillingAccount, Subscription, Product Instance).
	 * Default value is 0 = infinite.
	 * Useful for one-time discounts.
	 * See DiscountPlanInstance below for more details.
	 * This has no real meaning for discounts applied to invoices or invoice lines.
	 */
	@Schema(description = "How many times the discount can be applied on a given entity (BillingAccount, Subscription, Product Instance)."
			+ "<br />Default value is 0 = infinite."
			+ "<br/>Useful for one-time discounts.")
	private Long applicationLimit = 0L;

	/**
	 * A boolean EL that must evaluate to true to allow the discount plan to be applied.
	 * It will have access to the variables:
	 * entity: the entity on which we want to apply the discount
	 * discountPlan: the discount plan itself
	 */
	@Schema(description = "A boolean EL that must evaluate to true to allow the discount plan to be applied"
			+ "<br/>It will have access to the variables."
			+ "<br />entity: the entity on which we want to apply the discount")
	private String applicationFilterEL;

	/**
	 * A list of discounts plans that cannot be active at the same time on an entity instance.
	 */
	@Schema(description = "A list of discounts plans that cannot be active at the same time on an entity instance.")
	private List<DiscountPlanDto> incompatibleDiscountPlans;

	/**
	 * A list of entities (CustomerCategory, Offer, Product, Article).
	 * Only instances (Customer/BillingAccount, Subscription, ProductInstance, InvoiceLine) of these entities can have the discount applied to them.
	 */
	@Schema(description = "A list of entities (CustomerCategory, Offer, Product, Article).")
	private List<ApplicableEntityDto> applicableEntities;

	/**
	 * Instantiates a new DiscountPlanDto
	 */
	public DiscountPlanDto() {
		super();
	}

	/**
	 * Convert DiscountPlan JPA entity to DTO
	 *
	 * @param discountPlan         Entity to convert
	 * @param customFieldInstances the custom fields
	 */
	public DiscountPlanDto(DiscountPlan discountPlan, CustomFieldsDto customFieldInstances) {
		super(discountPlan);

		startDate = discountPlan.getStartDate();
		endDate = discountPlan.getEndDate();
		defaultDuration = discountPlan.getDefaultDuration();
		durationUnit = discountPlan.getDurationUnit();
		expressionEl = discountPlan.getExpressionEl();
		customFields = customFieldInstances;
		status = discountPlan.getStatus();
		statusDate = discountPlan.getStatusDate();
		initialQuantity = discountPlan.getInitialQuantity();
		usedQuantity = discountPlan.getUsedQuantity();
		applicationLimit = discountPlan.getApplicationLimit();
		applicationFilterEL = discountPlan.getApplicationFilterEL();
		if (discountPlan.getDiscountPlanaApplicableEntities() != null && discountPlan.getDiscountPlanaApplicableEntities().isEmpty()) {
			for (ApplicableEntity applicableEntity : discountPlan.getDiscountPlanaApplicableEntities()) {
				ApplicableEntityDto applicableEntityDto = new ApplicableEntityDto(applicableEntity);
				applicableEntities.add(applicableEntityDto);
			}
		}
		if (discountPlan.getIncompatibleDiscountPlans() != null && discountPlan.getIncompatibleDiscountPlans().isEmpty()) {
			List<DiscountPlanDto> discountPlansDto = new ArrayList<>();
			for (DiscountPlan dp : discountPlan.getIncompatibleDiscountPlans()) {
				discountPlansDto.add(new DiscountPlanDto(dp, null));
			}
			incompatibleDiscountPlans = discountPlansDto;
		}
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

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public List<DiscountPlanItemDto> getDiscountPlanItems() {
		return discountPlanItems;
	}

	public void setDiscountPlanItems(List<DiscountPlanItemDto> discountPlanItems) {
		this.discountPlanItems = discountPlanItems;
	}



	public String getExpressionEl() {
		return expressionEl;
	}

	public void setExpressionEl(String expressionEl) {
		this.expressionEl = expressionEl;
	}

	public static DiscountPlan copyFromDto(DiscountPlanDto source, DiscountPlan target) {
		if (source.getStartDate() != null) {
			target.setStartDate(source.getStartDate());
		}
		if (source.getEndDate() != null) {
			target.setEndDate(source.getEndDate());
		}
		if (source.getDurationUnit() != null) {
			target.setDurationUnit(source.getDurationUnit());
		}
		if (source.getDefaultDuration() != null) {
			target.setDefaultDuration(source.getDefaultDuration());
		}

		return target;
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

	public List<DiscountPlanDto> getIncompatibleDiscountPlans() {
		return incompatibleDiscountPlans;
	}

	public void setIncompatibleDiscountPlans(List<DiscountPlanDto> incompatibleDiscountPlans) {
		this.incompatibleDiscountPlans = incompatibleDiscountPlans;
	}

	public List<ApplicableEntityDto> getApplicableEntities() {
		return applicableEntities;
	}

	public void setApplicableEntities(List<ApplicableEntityDto> applicableEntities) {
		this.applicableEntities = applicableEntities;
	}

	public DiscountPlanTypeEnum getDiscountPlanType() {
		return discountPlanType;
	}

	public void setDiscountPlanType(DiscountPlanTypeEnum discountPlanType) {
		this.discountPlanType = discountPlanType;
	}
}