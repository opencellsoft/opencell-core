/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.payments;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_DUNNING_PLAN_TRANSITION", uniqueConstraints = @UniqueConstraint(columnNames = {
		"DUNNING_LEVEL_FROM", "DUNNING_LEVEL_TO", "DUNNING_PLAN_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DUNNING_PLAN_TRANSITION_SEQ")
public class DunningPlanTransition extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "DUNNING_LEVEL_FROM")
	private DunningLevelEnum dunningLevelFrom;

	@Enumerated(EnumType.STRING)
	@Column(name = "DUNNING_LEVEL_TO")
	private DunningLevelEnum dunningLevelTo;

	@Column(name = "DELAY_BEFORE_PROCESS")
	private Integer delayBeforeProcess;

	@Column(name = "THRESHOLD_AMOUNT", precision = 23, scale = 12)
	private BigDecimal thresholdAmount;

	@Column(name = "WAIT_DURATION")
	private Integer waitDuration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DUNNING_PLAN_ID")
	private DunningPlan dunningPlan;
	
	@Column(name = "CONDITION_EL", length = 1000)
	@Size(max = 1000)
	private String conditionEl;

	public DunningLevelEnum getDunningLevelFrom() {
		return dunningLevelFrom;
	}

	public void setDunningLevelFrom(DunningLevelEnum dunningLevelFrom) {
		this.dunningLevelFrom = dunningLevelFrom;
	}

	public DunningLevelEnum getDunningLevelTo() {
		return dunningLevelTo;
	}

	public void setDunningLevelTo(DunningLevelEnum dunningLevelTo) {
		this.dunningLevelTo = dunningLevelTo;
	}

	public BigDecimal getThresholdAmount() {
		return thresholdAmount;
	}

	public void setThresholdAmount(BigDecimal thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}

	public Integer getDelayBeforeProcess() {
		return delayBeforeProcess;
	}

	public void setDelayBeforeProcess(Integer delayBeforeProcess) {
		this.delayBeforeProcess = delayBeforeProcess;
	}

	public Integer getWaitDuration() {
		return waitDuration;
	}

	public void setWaitDuration(Integer waitDuration) {
		this.waitDuration = waitDuration;
	}

	public DunningPlan getDunningPlan() {
		return dunningPlan;
	}

	public void setDunningPlan(DunningPlan dunningPlan) {
		this.dunningPlan = dunningPlan;
	}
	
	public String getConditionEl() {
		return conditionEl;
	}

	public void setConditionEl(String conditionEl) {
		this.conditionEl = conditionEl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		DunningPlanTransition other = (DunningPlanTransition) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
