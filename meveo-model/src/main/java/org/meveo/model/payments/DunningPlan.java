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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "AR_DUNNING_PLAN", uniqueConstraints = @UniqueConstraint(columnNames = {
		"CREDIT_CATEGORY", "PAYMENT_METHOD", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DUNNING_PLAN_SEQ")
public class DunningPlan extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "CREDIT_CATEGORY")
	private CreditCategoryEnum creditCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_METHOD")
	private PaymentMethodEnum paymentMethod;

	@OneToMany(mappedBy = "dunningPlan",fetch = FetchType.LAZY)
	private List<DunningPlanTransition> transitions = new ArrayList<DunningPlanTransition>();

	@OneToMany(mappedBy = "dunningPlan", fetch = FetchType.LAZY)
	private List<ActionPlanItem> actions = new ArrayList<ActionPlanItem>();

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private DunningPlanStatusEnum status;

	public CreditCategoryEnum getCreditCategory() {
		return creditCategory;
	}

	public void setCreditCategory(CreditCategoryEnum creditCategory) {
		this.creditCategory = creditCategory;
	}

	public PaymentMethodEnum getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public List<DunningPlanTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<DunningPlanTransition> transitions) {
		this.transitions = transitions;
	}

	public List<ActionPlanItem> getActions() {
		return actions;
	}

	public void setActions(List<ActionPlanItem> actions) {
		this.actions = actions;
	}

	public DunningPlanStatusEnum getStatus() {
		return status;
	}

	public void setStatus(DunningPlanStatusEnum status) {
		this.status = status;
	}

}
