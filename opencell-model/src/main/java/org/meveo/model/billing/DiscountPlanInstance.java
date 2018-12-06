package org.meveo.model.billing;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Instance of {@link DiscountPlan}. It basically just contains the effectivity
 * date per BA.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@Entity
@ObservableEntity
@Table(name = "billing_discount_plan_instance")
@CustomFieldEntity(cftCodePrefix = "DISCOUNT_PLAN_INSTANCE", inheritCFValuesFrom = { "discountPlan" })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "billing_discount_plan_instance_seq"), })
public class DiscountPlanInstance extends BaseEntity implements ICustomFieldEntity {

	private static final long serialVersionUID = -3794502716655922498L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "discount_plan_id", nullable = false, updatable = false)
	private DiscountPlan discountPlan;

	@ManyToOne(optional = false)
	@JoinColumn(name = "billing_account_id", nullable = false, updatable = false)
	private BillingAccount billingAccount;

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
	 * @param uuid
	 *            Unique identifier
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
		return new ICustomFieldEntity[] { discountPlan };
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

}
