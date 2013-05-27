package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTemplate;

@Entity
@Table(name = "BILLING_COUNTER")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "CAT_COUNTER_INSTANCE_SEQ")
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
