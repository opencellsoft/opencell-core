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
package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "CAT_SERV_USAGE_CHARGE_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_SERV_USAGECHRG_TEMPLT_SEQ")
public class ServiceChargeTemplateUsage extends ServiceChargeTemplate<UsageChargeTemplate> {

	private static final long serialVersionUID = -6881449392209666474L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTER_TEMPLATE_ID")
	private CounterTemplate counterTemplate;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_SERV_USAGE_WALLET_TEMPLATE", joinColumns = @JoinColumn(name = "SERVICE_USAGE_TEMPLT_ID"), inverseJoinColumns = @JoinColumn(name = "WALLET_TEMPLATE_ID"))
	@OrderColumn(name = "INDX")
	private List<WalletTemplate> walletTemplates;

	public CounterTemplate getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(CounterTemplate counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	public List<WalletTemplate> getWalletTemplates() {
		return walletTemplates;
	}

	public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
		this.walletTemplates = walletTemplates;
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
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ServiceChargeTemplateUsage other = (ServiceChargeTemplateUsage) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
