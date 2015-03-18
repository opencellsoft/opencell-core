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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

@Stateless
public class OneShotChargeInstanceService extends BusinessService<OneShotChargeInstance> {

	@Inject
	private WalletService walletService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	
	@Inject
	private WalletOperationService walletOperationService;
	
    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider; 

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

		log.debug("instanciate a oneshot for code {} on subscription {}",chargeTemplate.getCode(),subscription.getCode());
		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(chargeTemplate.getCode(),
				chargeTemplate.getDescription(), effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargeTemplate);
		oneShotChargeInstance.setStatus(InstanceStatusEnum.INACTIVE);

		if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
			log.debug("set the termination service instance to {}",serviceInstance.getId());
			oneShotChargeInstance.setTerminationServiceInstance(serviceInstance);
		} else {
			log.debug("set the subscription service instance to {}",serviceInstance.getId());
			oneShotChargeInstance.setSubscriptionServiceInstance(serviceInstance);
		}

		oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
		List<WalletTemplate> walletTemplates = null;
		if (isSubscriptionCharge) {
			ServiceChargeTemplateSubscription recChTmplServ = serviceInstance.getServiceTemplate()
					.getServiceChargeTemplateSubscriptionByChargeCode(chargeTemplate.getCode());
			walletTemplates = recChTmplServ.getWalletTemplates();
			log.debug("retrieve wallet templates from subscription service charge templates : {}",walletTemplates);
		} else {
			ServiceChargeTemplateTermination recChTmplServ = serviceInstance.getServiceTemplate()
					.getServiceChargeTemplateTerminationByChargeCode(chargeTemplate.getCode());
			walletTemplates = recChTmplServ.getWalletTemplates();
			log.debug("retrieve wallet templates from termination service charge templates : {}",walletTemplates);
		}
		log.debug("by default we set the charge instance as being postpaid");
		oneShotChargeInstance.setPrepaid(false);
		if (walletTemplates != null && walletTemplates.size() > 0) {
			log.debug("found {} wallets",walletTemplates.size());
			for (WalletTemplate walletTemplate : walletTemplates) {
				log.debug("walletTemplate {}",walletTemplate.getCode());
				if(walletTemplate.getWalletType()==BillingWalletTypeEnum.PREPAID){
					log.debug("this wallet is prepaid, we set the charge instance itself as being prepaid");
					oneShotChargeInstance.setPrepaid(true);

				}
				WalletInstance walletInstance=walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(),
						walletTemplate, serviceInstance.getAuditable().getCreator(),
						serviceInstance.getProvider());
				log.debug("add the wallet instance {} to the chargeInstance {}",walletInstance.getId(),oneShotChargeInstance.getId());
				oneShotChargeInstance.getWalletInstances().add(walletInstance);
			}
		} else {
			log.debug("as the charge is postpaid, we add the principal wallet");
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
				amoutWithoutTx2, quantity, criteria1, criteria2, criteria3, creator,true);
	}
	
	public OneShotChargeInstance oneShotChargeApplication(Subscription subscription, OneShotChargeTemplate chargetemplate,
			String walletCode, Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2,
			BigDecimal quantity, String criteria1, String criteria2, String criteria3, User creator,boolean applyCharge)
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

		if(applyCharge){
			walletOperationService.oneShotWalletOperation(subscription, oneShotChargeInstance, quantity, effetDate,
				creator);
		}
		return oneShotChargeInstance;
	}

	public void oneShotChargeApplication(Subscription subscription, OneShotChargeInstance oneShotChargeInstance,
			Date effetDate, BigDecimal quantity, User creator) throws BusinessException {
		oneShotChargeApplication(getEntityManager(), subscription, oneShotChargeInstance, effetDate, quantity, creator);
	}

	public void oneShotChargeApplication(EntityManager em, Subscription subscription,
			OneShotChargeInstance oneShotChargeInstance, Date effetDate, BigDecimal quantity, User creator)
			throws BusinessException {
		walletOperationService.oneShotWalletOperation(em, subscription, oneShotChargeInstance, quantity, effetDate,
				creator);
	}

	@SuppressWarnings("unchecked")
	public List<OneShotChargeInstance> findOneShotChargeInstancesBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void matchPrepaidWallet(WalletInstance wallet,String matchingChargeCode,User currentUser) throws BusinessException {
		// get the id of the last OPEN walletOperation
		Long maxWalletId = getEntityManager().createNamedQuery("WalletOperation.getMaxOpenId",Long.class)
				.setParameter("wallet", wallet).getSingleResult();
		BigDecimal balanceNoTax = getEntityManager().createNamedQuery("WalletOperation.getBalanceNoTaxUntilId",BigDecimal.class)
				.setParameter("wallet", wallet).setParameter("maxId", maxWalletId).getSingleResult();
		BigDecimal balanceWithTax = getEntityManager().createNamedQuery("WalletOperation.getBalanceWithTaxUntilId",BigDecimal.class)
				.setParameter("wallet", wallet).setParameter("maxId", maxWalletId).getSingleResult();
		Subscription subscription=null;
		if(balanceNoTax==null){
			return;
		}
		for(Subscription sub : wallet.getUserAccount().getSubscriptions()){
			if(sub.isActive()){
				subscription=sub;
				break;
			}
		}
		if(subscription==null){
			throw new BusinessException("NO_ACTIVE_SUBSCRIPTION");
		}
		OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(
					matchingChargeCode, wallet.getProvider());
		if (oneShotChargeTemplate == null) {
			throw new BusinessException("Charge template "+matchingChargeCode+" not found for provider"+wallet.getProvider());
		}
		log.debug("create matching charge instance with amountWithoutTax {}, amountWithTax {}",balanceNoTax, balanceWithTax);
		OneShotChargeInstance matchingCharge=oneShotChargeApplication(subscription,
					(OneShotChargeTemplate) oneShotChargeTemplate,
					wallet.getCode(), new Date(), balanceNoTax, balanceWithTax,
					BigDecimal.ONE, null, null,
					null, currentUser,false);
		if(matchingCharge==null){
			throw new BusinessException("Cannot find or create matching charge instance for code "+matchingChargeCode);
		}		
		log.debug("matchingCharge amount withoutTax {}",matchingCharge.getAmountWithoutTax());
		WalletOperation op=walletOperationService.oneShotWalletOperation(getEntityManager(),subscription, matchingCharge, BigDecimal.ONE, new Date(),
				currentUser);
		op.setStatus(WalletOperationStatusEnum.TREATED);
		OneShotChargeInstance compensationCharge=oneShotChargeApplication(subscription,
				(OneShotChargeTemplate) oneShotChargeTemplate,
				wallet.getCode(), new Date(), balanceNoTax.negate(), balanceWithTax.negate(),
				BigDecimal.ONE, null, null,
				null, currentUser,false);
		if(compensationCharge==null){
			throw new BusinessException("Cannot find or create compensating charge instance for code "
					+matchingChargeCode);
		}
		int updatedOps =getEntityManager().createNamedQuery("WalletOperation.setTreatedStatusUntilId")
				.setParameter("wallet", wallet).setParameter("maxId", maxWalletId).executeUpdate();
		log.debug("set to TREATED {} wallet ops on wallet {}",updatedOps,wallet.getId());
		walletOperationService.oneShotWalletOperation(getEntityManager(),subscription, compensationCharge, BigDecimal.ONE, new Date(),
				currentUser);
		//we check that balance is unchanged
		//
		BigDecimal cacheBalance=walletCacheContainerProvider.getBalance(wallet.getId());
		if(cacheBalance.compareTo(balanceWithTax)!=0){
			log.error("balances in prepaid matching process do not match cache={}, compensated={}"
					,cacheBalance,balanceWithTax);
			throw new BusinessException("MATCHING_ERROR");
		}
	}

}
