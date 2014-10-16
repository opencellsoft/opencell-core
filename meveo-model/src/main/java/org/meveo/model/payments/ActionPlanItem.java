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

	@ManyToOne(fetch = FetchType.LAZY)
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
