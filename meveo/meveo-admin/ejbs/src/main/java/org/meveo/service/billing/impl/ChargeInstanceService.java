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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
public class ChargeInstanceService<P extends ChargeInstance> extends BusinessService<P> {

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private ServiceInstanceService serviceInstanceService;
	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private ChargeApplicationService chargeApplicationService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@SuppressWarnings("unchecked")
	public P findByCodeAndService(String code, Long subscriptionId) {
		P chargeInstance = null;
		try {
			log.debug("start of find {0} by code (code={1}) ..", "OneShotChargeInstance", code);
			QueryBuilder qb = new QueryBuilder(ChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
			chargeInstance = (P) qb.getQuery(em).getSingleResult();
			log.debug("end of find {0} by code (code={1}). Result found={2}.", "OCCTemplate", code,
					chargeInstance != null);

		} catch (NoResultException nre) {
			log.debug("findByCodeAndService : aucune charge n'a ete trouvee");
		} catch (Exception e) {
			log.error("findByCodeAndService error=#0 ", e.getMessage());
		}
		return chargeInstance;
	}

	public void recurringChargeInstanciation(ServiceInstance serviceInst, String chargeCode,
			Date subscriptionDate, User creator) throws BusinessException {

		if (serviceInst == null) {
			throw new BusinessException("service instance does not exist.");
		}
		if (serviceInst.getStatus() == InstanceStatusEnum.CANCELED
				|| serviceInst.getStatus() == InstanceStatusEnum.TERMINATED
				|| serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
			throw new BusinessException("service instance is " + serviceInst.getStatus()
					+ ". code=" + serviceInst.getCode());
		}

		RecurringChargeInstance chargeInst = (RecurringChargeInstance) recurringChargeInstanceService
				.findByCodeAndService(chargeCode, serviceInst.getId());
		if (chargeInst != null) {
			throw new BusinessException("charge instance code already exists. code=" + chargeCode);
		}
		RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService
				.findByCode(chargeCode, serviceInst.getProvider());
		RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
		chargeInstance.setCode(chargeCode);
		chargeInstance.setDescription(recurringChargeTemplate.getDescription());
		chargeInstance.setStatus(InstanceStatusEnum.INACTIVE);
		chargeInstance.setChargeDate(subscriptionDate);
		chargeInstance.setSubscriptionDate(subscriptionDate);
		chargeInstance.setSubscription(serviceInst.getSubscription());
		chargeInstance.setChargeTemplate(recurringChargeTemplate);
		chargeInstance.setRecurringChargeTemplate(recurringChargeTemplate);
		chargeInstance.setServiceInstance(serviceInst);
		recurringChargeInstanceService.create(chargeInstance, creator,
				recurringChargeTemplate.getProvider());

	}

	public void recurringChargeDeactivation(long recurringChargeInstanId, Date terminationDate,
			User updater) throws BusinessException {

		RecurringChargeInstance recurringChargeInstance = recurringChargeInstanceService.findById(
				recurringChargeInstanId, true);

		log.debug(
				"recurringChargeDeactivation : recurringChargeInstanceId=#0,ChargeApplications size=#1",
				recurringChargeInstance.getId(), recurringChargeInstance.getChargeApplications()
						.size());

		recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);

		chargeApplicationService.cancelChargeApplications(recurringChargeInstanId, null, updater);

		recurringChargeInstanceService.update(recurringChargeInstance, updater);

	}

	@SuppressWarnings("unchecked")
	public void chargeInstanceCancellation(long chargeInstanceId, User updater)
			throws BusinessException {
		ChargeInstance chargeInstance = findById(chargeInstanceId, true);
		P p = (P) chargeInstance;
		p.setStatus(InstanceStatusEnum.CANCELED);
		chargeApplicationService.cancelChargeApplications(chargeInstanceId, null, updater);
		this.update(p, updater);

	}

	public void recurringChargeReactivation(ServiceInstance serviceInst, String subscriptionCode,
			Date subscriptionDate, User creator) throws BusinessException {
		Subscription subscription = subscriptionService.findByCode(subscriptionCode);
		if (subscription == null) {
			throw new BusinessException("subscription does not exist. code=" + subscriptionCode);
		}
		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
				|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
			throw new BusinessException("subscription is " + subscription.getStatus());
		}
		if (serviceInst.getStatus() == InstanceStatusEnum.TERMINATED
				|| serviceInst.getStatus() == InstanceStatusEnum.CANCELED
				|| serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
			throw new BusinessException("service instance is " + subscription.getStatus()
					+ ". service Code=" + serviceInst.getCode() + ",subscription Code"
					+ subscriptionCode);
		}
		for (RecurringChargeInstance recurringChargeInstance : serviceInst
				.getRecurringChargeInstances()) {
			recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
			recurringChargeInstance.setSubscriptionDate(subscriptionDate);
			recurringChargeInstance.setTerminationDate(null);
			// TODO: Why change the charge date ??
			recurringChargeInstance.setChargeDate(subscriptionDate);
			recurringChargeInstanceService.update(recurringChargeInstance);
		}

	}

}
