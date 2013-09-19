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
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTemplate;

@Entity
@Table(name = "BILLING_COUNTER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_COUNTER_INSTANCE_SEQ")
public class CounterInstance extends BusinessEntity {
	private static final long serialVersionUID = -4924601467998738157L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTER_TEMPLATE_ID")
	private CounterTemplate counterTemplate;

	@ManyToOne
	@JoinColumn(name = "USER_ACCOUNT_ID")
	private UserAccount userAccount;

	@OneToMany(mappedBy = "counterInstance", fetch = FetchType.LAZY)
	private List<CounterPeriod> counterPeriods = new ArrayList<CounterPeriod>();

	public CounterTemplate getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(CounterTemplate counterTemplate) {
		this.counterTemplate = counterTemplate;
		if (counterTemplate != null) {
			this.code = counterTemplate.getCode();
			this.description = counterTemplate.getDescription();
		} else {
			this.code = null;
			this.description = null;
		}
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<CounterPeriod> getCounterPeriods() {
		return counterPeriods;
	}

	public void setCounterPeriods(List<CounterPeriod> counterPeriods) {
		this.counterPeriods = counterPeriods;
	}

}
