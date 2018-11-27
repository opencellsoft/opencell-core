package org.meveo.model.catalog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.BillingAccount;

/**
 * Instance of {@link DiscountPlan}. It basically just contains the effectivity
 * date per BA.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */

@Entity
@ObservableEntity
@Table(name = "cat_discount_plan_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "cat_discount_plan_instance_seq"), })
public class DiscountPlanInstance extends BaseEntity {

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

}
