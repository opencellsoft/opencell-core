/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
