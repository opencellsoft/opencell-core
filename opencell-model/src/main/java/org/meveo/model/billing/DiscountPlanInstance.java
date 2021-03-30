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

package org.meveo.model.billing;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.*;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Instance of {@link DiscountPlan}. It basically just contains the effectivity date per BA.
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Table(name = "billing_discount_plan_instance")
@CustomFieldEntity(cftCodePrefix = "DiscountPlanInstance", inheritCFValuesFrom = { "discountPlan" })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_discount_plan_instance_seq"), })
@NamedQueries({
        @NamedQuery(name = "discountPlanInstance.getExpired", query = "select d.id from DiscountPlanInstance d where d.endDate is not null and d.endDate<=:date and d.status in (:statuses)"),
        @NamedQuery(name = "discountPlanInstance.getToActive", query = "select d.id from DiscountPlanInstance d where (d.startDate is not null and d.startDate<=:date or d.startDate is null) and (d.endDate is not null and d.endDate<:date or d.endDate is null) and d.status in (:statuses)") })

public class DiscountPlanInstance extends BaseEntity implements ICustomFieldEntity {

    private static final long serialVersionUID = -3794502716655922498L;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_id", nullable = false, updatable = false)
    private DiscountPlan discountPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

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

    /**
     * Status of this specific discount plan instance
     * APPLIED: the discount plan has be applied to entity but effective start date is not reached yet
     * ACTIVE: the discount plan is active on the entity (start date has been reached)
     * IN_USE: the discount plan has already been used once for the entity.
     * EXPIRED: the discount plan is no longer active
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private DiscountPlanInstanceStatusEnum status;

    /**
     * Datetime of last status change.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date statusDate;

    /**
     * How many times the discount has been used.
     */
    @Column(name = "application_count")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long applicationCount;

    public boolean isValid() {
        return (startDate == null || endDate == null || startDate.before(endDate));
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

    public void copyEffectivityDates(DiscountPlan dp) {
        setStartDate(dp.getStartDate());
        setEndDate(dp.getEndDate());
    }

    public DiscountPlan getDiscountPlan() {
        return discountPlan;
    }

    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
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

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((billingAccount == null) ? 0 : billingAccount.hashCode());
        result = prime * result + ((discountPlan == null) ? 0 : discountPlan.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        DiscountPlanInstance other = (DiscountPlanInstance) obj;
        if (billingAccount == null) {
            if (other.billingAccount != null)
                return false;
        } else if (!billingAccount.equals(other.billingAccount))
            return false;
        if (discountPlan == null) {
            if (other.discountPlan != null)
                return false;
        } else if (!discountPlan.equals(other.discountPlan))
            return false;
        return true;
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
        return discountPlan != null ? new ICustomFieldEntity[] { discountPlan } : null;
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

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void assignEntityToDiscountPlanInstances(IDiscountable entity) {
        if (entity instanceof BillingAccount) {
            this.setBillingAccount((BillingAccount) entity);
        } else {
            this.setSubscription((Subscription) entity);
        }
    }

    public DiscountPlanInstanceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DiscountPlanInstanceStatusEnum status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Long applicationCount) {
        this.applicationCount = applicationCount;
    }

    public void setDiscountPlanInstanceStatus(DiscountPlan dp) {
        if (status != null && status.equals(DiscountPlanInstanceStatusEnum.EXPIRED)) {
            return;
        }
        Date now = new Date();
        Date start = dp.getStartDate();
        Date end = dp.getEndDate();
        if (startDate != null) {
            start = startDate;
        }
        if (endDate != null) {
            end = endDate;
        }
        if (start == null && end == null) {
            this.status = DiscountPlanInstanceStatusEnum.ACTIVE;
            return;
        }
        if (now.after(start) && now.before(end)) {
            this.status = DiscountPlanInstanceStatusEnum.ACTIVE;
            return;
        }
        if (now.before(start)) {
            this.status = DiscountPlanInstanceStatusEnum.APPLIED;
            return;
        }
        this.status = DiscountPlanInstanceStatusEnum.EXPIRED;
        this.statusDate = now;
    }

}
