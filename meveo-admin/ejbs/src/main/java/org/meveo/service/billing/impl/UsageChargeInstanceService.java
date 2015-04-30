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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class UsageChargeInstanceService extends BusinessService<UsageChargeInstance> {

	@Inject
	private WalletService walletService;

	@Inject
	private CounterInstanceService counterInstanceService;

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

	public UsageChargeInstance usageChargeInstanciation(Subscription subscription, ServiceInstance serviceInstance,
			ServiceChargeTemplateUsage serviceUsageChargeTemplate, Date startDate, Seller seller, User creator)
			throws BusinessException {
		return usageChargeInstanciation(getEntityManager(), subscription, serviceInstance, serviceUsageChargeTemplate,
				startDate, seller, creator);
	}

	public UsageChargeInstance usageChargeInstanciation(EntityManager em, Subscription subscription,
			ServiceInstance serviceInstance, ServiceChargeTemplateUsage serviceUsageChargeTemplate, Date startDate,
			Seller seller, User creator) throws BusinessException {
		log.debug("instanciate usageCharge for code {} and subscription {}",
				serviceUsageChargeTemplate.getChargeTemplate().getCode(),subscription.getCode());
		UsageChargeInstance usageChargeInstance = new UsageChargeInstance();
		usageChargeInstance.setSubscription(subscription);
		usageChargeInstance.setChargeTemplate(serviceUsageChargeTemplate.getChargeTemplate());
		usageChargeInstance.setChargeDate(startDate);
		usageChargeInstance.setAmountWithoutTax(null);
		usageChargeInstance.setAmountWithTax(null);
		usageChargeInstance.setStatus(InstanceStatusEnum.INACTIVE);
		usageChargeInstance.setServiceInstance(serviceInstance);
		usageChargeInstance.setInvoicingCalendar(serviceInstance.getInvoicingCalendar());
		usageChargeInstance.setRatingUnitDescription(serviceUsageChargeTemplate.getChargeTemplate().getRatingUnitDescription());
		usageChargeInstance.setSeller(seller);
		usageChargeInstance.setCountry(subscription.getUserAccount().getBillingAccount().getTradingCountry());
		usageChargeInstance.setCurrency(subscription.getUserAccount().getBillingAccount().getCustomerAccount()
				.getTradingCurrency());
		List<WalletTemplate> walletTemplates = serviceUsageChargeTemplate.getWalletTemplates();
		log.debug("usage charge wallet templates {}, by default we set it to postpaid",walletTemplates);
		usageChargeInstance.setPrepaid(false);
		if (walletTemplates != null && walletTemplates.size() > 0) {
			log.debug("usage charge has {} wallet templates",walletTemplates.size());
			for (WalletTemplate walletTemplate : walletTemplates) {
				log.debug("wallet template {}",walletTemplate.getCode());
				if(walletTemplate.getWalletType()==BillingWalletTypeEnum.PREPAID){
					log.debug("it is a prepaid wallet so we set the charge itself has being prepaid");
					usageChargeInstance.setPrepaid(true);
				}
				WalletInstance walletInstance =walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(),
						walletTemplate, serviceInstance.getAuditable().getCreator(),
						serviceInstance.getProvider());
				log.debug("we add the waleltInstance {} to the charge instance {}",walletInstance.getId(),
						usageChargeInstance.getId());
				usageChargeInstance.getWalletInstances().add(walletInstance);
			}
		} else {
			log.debug("add postpaid walletInstance {}",serviceInstance.getSubscription().getUserAccount().getWallet());
			usageChargeInstance.getWalletInstances()
					.add(serviceInstance.getSubscription().getUserAccount().getWallet());
		}
		create(usageChargeInstance, creator, serviceInstance.getProvider());

		if (serviceUsageChargeTemplate.getCounterTemplate() != null) {
			CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance
					.getSubscription().getUserAccount(), serviceUsageChargeTemplate.getCounterTemplate(), creator);
			usageChargeInstance.setCounter(counterInstance);
			setProvider(creator.getProvider());
			update(usageChargeInstance, creator);
		}

		return usageChargeInstance;
	}

	public void activateUsageChargeInstance(UsageChargeInstance usageChargeInstance, User currentUser) {
		activateUsageChargeInstance(getEntityManager(), usageChargeInstance, currentUser);
	}

	public void activateUsageChargeInstance(EntityManager em, UsageChargeInstance usageChargeInstance, User currentUser) {
		usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
		setProvider(currentUser.getProvider());
		update(usageChargeInstance, currentUser);
		ratingCacheContainerProvider.updateUsageChargeInstanceInCache(usageChargeInstance);
	}

	public void terminateUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date terminationDate) {
		terminateUsageChargeInstance(usageChargeInstance, terminationDate, getCurrentUser());
	}

	public void terminateUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date terminationDate,
			User currentUser) {
		terminateUsageChargeInstance(getEntityManager(), usageChargeInstance, terminationDate, currentUser);
	}

	public void terminateUsageChargeInstance(EntityManager em, UsageChargeInstance usageChargeInstance,
			Date terminationDate, User currentUser) {
		usageChargeInstance.setTerminationDate(terminationDate);
		usageChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
		ratingCacheContainerProvider.updateUsageChargeInstanceInCache(usageChargeInstance);
		update(usageChargeInstance, currentUser);
	}

	public void suspendUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date suspensionDate) {
		usageChargeInstance.setTerminationDate(suspensionDate);
		usageChargeInstance.setStatus(InstanceStatusEnum.SUSPENDED);
		ratingCacheContainerProvider.updateUsageChargeInstanceInCache(usageChargeInstance);
		update(usageChargeInstance);
	}

	public void reactivateUsageChargeInstance(UsageChargeInstance usageChargeInstance, Date reactivationDate) {
		usageChargeInstance.setChargeDate(reactivationDate);
		usageChargeInstance.setTerminationDate(null);
		usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
		ratingCacheContainerProvider.updateUsageChargeInstanceInCache(usageChargeInstance);
		update(usageChargeInstance);
	}

	@SuppressWarnings("unchecked")
	public List<UsageChargeInstance> findUsageChargeInstanceBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(UsageChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

    /**
     * Get a list of prepaid and active usage charge instances to populate a cache
     * 
     * @return A list of prepaid and active usage charge instances
     */
    public List<UsageChargeInstance> getPrepaidUsageChargeInstancesForCache() {
        return getEntityManager().createNamedQuery("UsageChargeInstance.listPrepaidActive", UsageChargeInstance.class).getResultList();
    }
    
    /**
     * Get a list of all active usage charge instances to populate a cache
     * 
     * @return A list of prepaid and active usage charge instances
     */
    public List<UsageChargeInstance> getAllUsageChargeInstancesForCache() {
        return getEntityManager().createNamedQuery("UsageChargeInstance.listActive", UsageChargeInstance.class).getResultList();
    }
}