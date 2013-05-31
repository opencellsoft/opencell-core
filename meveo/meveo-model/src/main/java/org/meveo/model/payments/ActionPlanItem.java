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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_ACTION_PLAN_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_ACTION_PLAN_ITEM_SEQ")
public class ActionPlanItem extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "DUNNING_LEVEL")
	private DunningLevelEnum dunningLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE")
	private DunningActionTypeEnum actionType;

	@Column(name = "ITEM_ORDER")
	private Integer itemOrder;

	@Column(name = "THRESHOLD_AMOUNT", precision = 23, scale = 12)
	private BigDecimal thresholdAmount;

	@Column(name = "CHARGE_AMOUNT")
	private BigDecimal chargeAmount;

	@Column(name = "LETTER_TEMPLATE")
	private String letterTemplate;

	@ManyToOne
	@JoinColumn(name = "DUNNING_PLAN_ID")
	private DunningPlan dunningPlan;

	public DunningLevelEnum getDunningLevel() {
		return dunningLevel;
	}

	public void setDunningLevel(DunningLevelEnum dunningLevel) {
		this.dunningLevel = dunningLevel;
	}

	public DunningActionTypeEnum getActionType() {
		return actionType;
	}

	public void setActionType(DunningActionTypeEnum actionType) {
		this.actionType = actionType;
	}

	public BigDecimal getThresholdAmount() {
		return thresholdAmount;
	}

	public void setThresholdAmount(BigDecimal thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}

	public String getLetterTemplate() {
		return letterTemplate;
	}

	public void setLetterTemplate(String letterTemplate) {
		this.letterTemplate = letterTemplate;
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
		ActionPlanItem other = (ActionPlanItem) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public void setItemOrder(Integer itemOrder) {
		this.itemOrder = itemOrder;
	}

	public Integer getItemOrder() {
		return itemOrder;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

}
