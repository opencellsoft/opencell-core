package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "CAT_DISCOUNT_PLAN", uniqueConstraints = { @UniqueConstraint(columnNames = { "EVENT_CODE", "PROVIDER_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DISCOUNT_PLAN_SEQ")
public class DiscountPlan extends AuditableEntity {

	private static final long serialVersionUID = -2762453947446654646L;

	@Column(name = "EVENT_CODE", length = 60, nullable = false)
	@Size(max = 60, min = 1)
	private String eventCode;

	@Column(name = "DESCRIPTION", nullable = true, length = 100)
	@Size(max = 100)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private DiscountPlanStatusEnum discountPlanStatusEnum;

	@Column(name = "MIN_DURATION")
	private int minDuration = 0;

	@Column(name = "MAX_DURATION")
	private int maxDuration = 99999;

	@OneToMany(mappedBy = "discountPlan")
	private List<DiscountPlanItem> discountPlanItems;

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DiscountPlanStatusEnum getDiscountPlanStatusEnum() {
		return discountPlanStatusEnum;
	}

	public void setDiscountPlanStatusEnum(DiscountPlanStatusEnum discountPlanStatusEnum) {
		this.discountPlanStatusEnum = discountPlanStatusEnum;
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

}
