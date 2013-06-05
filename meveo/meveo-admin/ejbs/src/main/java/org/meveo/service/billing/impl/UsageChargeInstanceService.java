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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
@LocalBean
public class UsageChargeInstanceService extends BusinessService<UsageChargeInstance> {

	@EJB
	private WalletOperationService chargeApplicationService;

	@EJB
	UsageRatingService usageRatingService;

	@EJB
	private CounterInstanceService counterInstanceService;

	public UsageChargeInstance usageChargeInstanciation(Subscription subscription,
			ServiceInstance serviceInstance, ServiceUsageChargeTemplate serviceUsageChargeTemplate,
			Date startDate, Seller seller, User creator) throws BusinessException {

		UsageChargeInstance usageChargeInstance = new UsageChargeInstance();
		usageChargeInstance.setSubscription(subscription);
		usageChargeInstance.setChargeTemplate(serviceUsageChargeTemplate.getChargeTemplate());
		usageChargeInstance.setChargeDate(startDate);
		usageChargeInstance.setAmountWithoutTax(null);
		usageChargeInstance.setAmountWithTax(null);
		usageChargeInstance.setStatus(InstanceStatusEnum.INACTIVE);
		usageChargeInstance.setServiceInstance(serviceInstance);
		usageChargeInstance.setSeller(seller);

		create(usageChargeInstance, creator, serviceInstance.getProvider());
		if (serviceUsageChargeTemplate.getCounterTemplate() != null) {
			CounterInstance counterInstance = counterInstanceService.counterInstanciation(
					serviceInstance.getSubscription().getUserAccount(),
					serviceUsageChargeTemplate.getCounterTemplate(), creator);
			usageChargeInstance.setCounter(counterInstance);
			update(usageChargeInstance, creator);
		}
		return usageChargeInstance;
	}

	public void activateUsageChargeInstance(UsageChargeInstance usageChargeInstance) {
		usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
		update(usageChargeInstance);
		usageRatingService.updateCache(usageChargeInstance);
	}

	public void terminateUsageChargeInstance(UsageChargeInstance usageChargeInstance,
			Date terminationDate) {
		usageChargeInstance.setTerminationDate(terminationDate);
		usageChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
		usageRatingService.updateCache(usageChargeInstance);
		update(usageChargeInstance);
	}

	public void suspendUsageChargeInstance(UsageChargeInstance usageChargeInstance,
			Date suspensionDate) {
		usageChargeInstance.setTerminationDate(suspensionDate);
		usageChargeInstance.setStatus(InstanceStatusEnum.SUSPENDED);
		usageRatingService.updateCache(usageChargeInstance);
		update(usageChargeInstance);
	}

	public void reactivateUsageChargeInstance(UsageChargeInstance usageChargeInstance,
			Date reactivationDate) {
		usageChargeInstance.setChargeDate(reactivationDate);
		usageChargeInstance.setTerminationDate(null);
		usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
		usageRatingService.updateCache(usageChargeInstance);
		update(usageChargeInstance);
	}

	@SuppressWarnings("unchecked")
	public List<UsageChargeInstance> findUsageChargeInstanceBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(UsageChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

}
