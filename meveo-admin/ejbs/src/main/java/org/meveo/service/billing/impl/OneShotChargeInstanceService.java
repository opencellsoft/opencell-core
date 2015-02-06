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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class OneShotChargeInstanceService extends BusinessService<OneShotChargeInstance> {

	@Inject
	private WalletService walletService;

	@Inject
	private WalletOperationService chargeApplicationService;

	public OneShotChargeInstance findByCodeAndSubsription(String code, Long subscriptionId) {
		OneShotChargeInstance oneShotChargeInstance = null;
		try {
			log.debug("start of find {} by code (code={}, subscriptionId={}) ..", new Object[] {
					"OneShotChargeInstance", code, subscriptionId });
			QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
			oneShotChargeInstance = (OneShotChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();
			log.debug("end of find {} by code (code={}, subscriptionId={}). Result found={}.", new Object[] {
					"OneShotChargeInstance", code, subscriptionId, oneShotChargeInstance != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubsription : aucune charge ponctuelle n'a ete trouvee");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return oneShotChargeInstance;
	}

	public OneShotChargeInstance oneShotChargeInstanciation(Subscription subscription, ServiceInstance serviceInstance,
			OneShotChargeTemplate chargeTemplate, Date effetDate, BigDecimal amoutWithoutTax,
			BigDecimal amoutWithoutTx2, Integer quantity, User creator, boolean isSubscriptionCharge)
			throws BusinessException {
		return oneShotChargeInstanciation(getEntityManager(), subscription, serviceInstance, chargeTemplate, effetDate,
				amoutWithoutTax, amoutWithoutTx2, quantity, creator, isSubscriptionCharge);
	}

	public OneShotChargeInstance oneShotChargeInstanciation(EntityManager em, Subscription subscription,
			ServiceInstance serviceInstance, OneShotChargeTemplate chargeTemplate, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, Integer quantity, User creator,
			boolean isSubscriptionCharge) throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}

		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(chargeTemplate.getCode(),
				chargeTemplate.getDescription(), effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargeTemplate);
		oneShotChargeInstance.setStatus(InstanceStatusEnum.INACTIVE);

		if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
			oneShotChargeInstance.setTerminationServiceInstance(serviceInstance);
		} else {
			oneShotChargeInstance.setSubscriptionServiceInstance(serviceInstance);
		}

		oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
		List<WalletTemplate> walletTemplates = null;
		if (isSubscriptionCharge) {
			ServiceChargeTemplateSubscription recChTmplServ = serviceInstance.getServiceTemplate()
					.getServiceChargeTemplateSubscriptionByChargeCode(chargeTemplate.getCode());
			walletTemplates = recChTmplServ.getWalletTemplates();
		} else {
			ServiceChargeTemplateTermination recChTmplServ = serviceInstance.getServiceTemplate()
					.getServiceChargeTemplateTerminationByChargeCode(chargeTemplate.getCode());
			walletTemplates = recChTmplServ.getWalletTemplates();
		}
		if (walletTemplates != null && walletTemplates.size() > 0) {
			for (WalletTemplate walletTemplate : walletTemplates) {
				if(walletTemplate.getWalletType()==BillingWalletTypeEnum.PREPAID){
					oneShotChargeInstance.setPrepaid(true);
				}
				oneShotChargeInstance.getWalletInstances().add(
						walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(),
								walletTemplate, serviceInstance.getAuditable().getCreator(),
								serviceInstance.getProvider()));
			}
		} else {
			oneShotChargeInstance.setPrepaid(false);
			oneShotChargeInstance.getWalletInstances().add(
					serviceInstance.getSubscription().getUserAccount().getWallet());
		}
		create(oneShotChargeInstance, creator, chargeTemplate.getProvider());

		return oneShotChargeInstance;
	}

	// apply a oneShotCharge on the postpaid wallet
	public OneShotChargeInstance oneShotChargeApplication(Subscription subscription, OneShotChargeTemplate chargetemplate,
			Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, BigDecimal quantity, String criteria1,
			String criteria2, String criteria3, User creator) throws BusinessException {
		return oneShotChargeApplication(subscription, chargetemplate, null, effetDate, amoutWithoutTax,
				amoutWithoutTx2, quantity, criteria1, criteria2, criteria3, creator);
	}

	public OneShotChargeInstance oneShotChargeApplication(Subscription subscription, OneShotChargeTemplate chargetemplate,
			String walletCode, Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			BigDecimal quantity, String criteria1, String criteria2, String criteria3, User creator)
			throws BusinessException {

		if (quantity == null) {
			quantity = BigDecimal.ONE;
		}

		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(chargetemplate.getCode(),
				chargetemplate.getDescription(), effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargetemplate);
		oneShotChargeInstance.setCriteria1(criteria1);
		oneShotChargeInstance.setCriteria2(criteria2);
		oneShotChargeInstance.setCriteria3(criteria3);
		if (walletCode == null) {
			oneShotChargeInstance.setPrepaid(false);
			oneShotChargeInstance.getWalletInstances().add(subscription.getUserAccount().getWallet());
		} else {
			WalletInstance wallet = subscription.getUserAccount().getWalletInstance(walletCode);
			oneShotChargeInstance.getWalletInstances().add(wallet);
			if(wallet.getWalletTemplate().getWalletType()==BillingWalletTypeEnum.PREPAID){
				oneShotChargeInstance.setPrepaid(true);
			}
		}

		create(oneShotChargeInstance, creator, chargetemplate.getProvider());

		chargeApplicationService.oneShotWalletOperation(subscription, oneShotChargeInstance, quantity, effetDate,
				creator);

		return oneShotChargeInstance;
	}

	public void oneShotChargeApplication(Subscription subscription, OneShotChargeInstance oneShotChargeInstance,
			Date effetDate, BigDecimal quantity, User creator) throws BusinessException {
		oneShotChargeApplication(getEntityManager(), subscription, oneShotChargeInstance, effetDate, quantity, creator);
	}

	public void oneShotChargeApplication(EntityManager em, Subscription subscription,
			OneShotChargeInstance oneShotChargeInstance, Date effetDate, BigDecimal quantity, User creator)
			throws BusinessException {
		chargeApplicationService.oneShotWalletOperation(em, subscription, oneShotChargeInstance, quantity, effetDate,
				creator);
	}

	@SuppressWarnings("unchecked")
	public List<OneShotChargeInstance> findOneShotChargeInstancesBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

}
