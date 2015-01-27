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
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class OneShotChargeInstanceService extends
		BusinessService<OneShotChargeInstance> {

	@Inject
	private UserAccountService userAccountService;
	
	@Inject
	private WalletOperationService chargeApplicationService;

	public OneShotChargeInstance findByCodeAndSubsription(String code,
			Long subscriptionId) {
		OneShotChargeInstance oneShotChargeInstance = null;
		try {
			log.debug(
					"start of find {} by code (code={}, subscriptionId={}) ..",
					new Object[] { "OneShotChargeInstance", code,
							subscriptionId });
			QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
			oneShotChargeInstance = (OneShotChargeInstance) qb.getQuery(
					getEntityManager()).getSingleResult();
			log.debug(
					"end of find {} by code (code={}, subscriptionId={}). Result found={}.",
					new Object[] { "OneShotChargeInstance", code,
							subscriptionId, oneShotChargeInstance != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubsription : aucune charge ponctuelle n'a ete trouvee");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return oneShotChargeInstance;
	}

	public OneShotChargeInstance oneShotChargeInstanciation(
			Subscription subscription, ServiceInstance serviceInstance,
			OneShotChargeTemplate chargeTemplate, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			Integer quantity, Seller seller, User creator)
			throws BusinessException {
		return oneShotChargeInstanciation(getEntityManager(), subscription,
				serviceInstance, chargeTemplate, effetDate, amoutWithoutTax,
				amoutWithoutTx2, quantity, seller, creator);
	}

	public OneShotChargeInstance oneShotChargeInstanciation(EntityManager em,
			Subscription subscription, ServiceInstance serviceInstance,
			OneShotChargeTemplate chargeTemplate, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			Integer quantity, Seller seller, User creator)
			throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}

		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(
				chargeTemplate.getCode(), chargeTemplate.getDescription(),
				effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargeTemplate, seller);
		oneShotChargeInstance.setStatus(InstanceStatusEnum.INACTIVE);

		if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
			oneShotChargeInstance
					.setTerminationServiceInstance(serviceInstance);
		} else {
			oneShotChargeInstance
					.setSubscriptionServiceInstance(serviceInstance);
		}

		oneShotChargeInstance.setChargeDate(serviceInstance
				.getSubscriptionDate());
		ServiceChargeTemplateRecurring recChTmplServ = serviceInstance.getServiceTemplate().getServiceRecurringChargeByChargeCode(chargeTemplate.getCode());
		List<WalletTemplate> walletTemplates = recChTmplServ.getWalletTemplates();
		if(walletTemplates!=null && walletTemplates.size()>0){
			for(WalletTemplate walletTemplate:walletTemplates){
				oneShotChargeInstance.getWalletInstances().add(userAccountService.getWalletInstance(serviceInstance.getSubscription()
						.getUserAccount(),walletTemplate,serviceInstance.getAuditable().getCreator(),serviceInstance.getProvider()));
			}
		} else {
			oneShotChargeInstance.getWalletInstances().add(serviceInstance.getSubscription()
				.getUserAccount().getWallet());
		}
		create(oneShotChargeInstance, creator, chargeTemplate.getProvider());

		return oneShotChargeInstance;
	}


	//apply a oneShotCharge on the postpaid wallet
	public Long oneShotChargeApplication(Subscription subscription,
			OneShotChargeTemplate chargetemplate, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			Integer quantity, String criteria1, String criteria2,
			String criteria3, Seller seller, User creator)
			throws BusinessException {
		return oneShotChargeApplication( subscription,
				 chargetemplate, null,  effetDate,
				 amoutWithoutTax,  amoutWithoutTx2,
				 quantity,  criteria1,  criteria2,
				 criteria3,  seller,  creator);
	}

	public Long oneShotChargeApplication(Subscription subscription,
			OneShotChargeTemplate chargetemplate,String walletCode, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			Integer quantity, String criteria1, String criteria2,
			String criteria3, Seller seller, User creator)
			throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}
		/*
		 * OneShotChargeInstance oneshotCharge =
		 * findByCodeAndSubsription(chargeCode, subscription.getId()); if
		 * (oneshotCharge != null) { throw new BusinessException("this one shot
		 * charge instance already exists for this subscription. code=" +
		 * oneshotCharge.getCode() + ",subscriptionCode=" +
		 * subscription.getCode()); } OneShotChargeTemplate chargetemplate =
		 * oneShotChargeTemplateService.findByCode(chargeCode);
		 */

		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(
				chargetemplate.getCode(), chargetemplate.getDescription(),
				effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargetemplate, seller);
		oneShotChargeInstance.setCriteria1(criteria1);
		oneShotChargeInstance.setCriteria2(criteria2);
		oneShotChargeInstance.setCriteria3(criteria3);
		if(walletCode==null){
			oneShotChargeInstance.getWalletInstances().add(subscription
					.getUserAccount().getWallet());
		} else {
			oneShotChargeInstance.getWalletInstances().add(subscription.getUserAccount().getWalletInstance(walletCode));

		}
	
		create(oneShotChargeInstance, creator, chargetemplate.getProvider());

		chargeApplicationService.oneShotWalletOperation(subscription,
				oneShotChargeInstance, quantity, effetDate, creator);

		return oneShotChargeInstance.getId();
	}

	public void oneShotChargeApplication(Subscription subscription,
			OneShotChargeInstance oneShotChargeInstance, Date effetDate,
			Integer quantity, User creator) throws BusinessException {
		oneShotChargeApplication(getEntityManager(), subscription,
				oneShotChargeInstance, effetDate, quantity, creator);
	}

	public void oneShotChargeApplication(EntityManager em,
			Subscription subscription,
			OneShotChargeInstance oneShotChargeInstance, Date effetDate,
			Integer quantity, User creator) throws BusinessException {
		chargeApplicationService.oneShotWalletOperation(em, subscription,
				oneShotChargeInstance, quantity, effetDate, creator);
	}

	@SuppressWarnings("unchecked")
	public List<OneShotChargeInstance> findOneShotChargeInstancesBySubscriptionId(
			Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

}
