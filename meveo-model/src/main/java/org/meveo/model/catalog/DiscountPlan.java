package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "CAT_DISCOUNT_PLAN", uniqueConstraints = { @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DISCOUNT_PLAN_SEQ")
public class DiscountPlan extends AuditableEntity {

	private static final long serialVersionUID = -2762453947446654646L;

	@Column(name = "CODE", length = 60, nullable = false)
	@Size(max = 60, min = 1)
	private String code;

	@Column(name = "DESCRIPTION", nullable = true, length = 100)
	@Size(max = 100)
	private String description;

	@Column(name = "MIN_DURATION")
	private int minDuration = 0;

	@Column(name = "MAX_DURATION")
	private int maxDuration = 99999;

	@OneToMany(mappedBy = "discountPlan")
	private List<DiscountPlanItem> discountPlanItems;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "DiscountPlan [code=" + code + ", description=" + description + ", minDuration=" + minDuration
				+ ", maxDuration=" + maxDuration + ", discountPlanItems=" + discountPlanItems + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscountPlan other = (DiscountPlan) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
