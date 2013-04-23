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
package org.meveo.model.catalog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.billing.InvoiceSubcategoryCountry;

@Entity
@Table(name = "CAT_SERV_USAGE_CHARGE_TEMPLATE")
public class ServiceUsageChargeTemplate extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6881449392209666474L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_TEMPLATE_ID")
	private ServiceTemplate serviceTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARGE_TEMPLATE_ID")
	private UsageChargeTemplate chargeTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTER_TEMPLATE_ID")
	private CounterTemplate counterTemplate;


	//TODO do we need that ?
	/*
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WALLET_TEMPLATE_ID")
	private WalletTemplate walletTemplate;	
	
    @Enumerated(EnumType.STRING)
    @Column(name = "CREDIT_DEBIT_FLAG")
    private OperationTypeEnum type;
    
    @Column(name = "UNITY_COUNTER_MULTIPLICATOR")
	private BigDecimal unityCounterMultiplicator = BigDecimal.ONE;
	*/


	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public UsageChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}

	public void setChargeTemplate(UsageChargeTemplate chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}

	public CounterTemplate getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(CounterTemplate counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceUsageChargeTemplate other = (ServiceUsageChargeTemplate) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}


}
