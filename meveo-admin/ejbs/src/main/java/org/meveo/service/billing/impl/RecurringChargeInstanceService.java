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
import java.util.ArrayList;
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
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class RecurringChargeInstanceService extends BusinessService<RecurringChargeInstance> {

	@Inject
	private WalletService walletService;

	@Inject
	private WalletOperationService chargeApplicationService;

	// @Inject
	// private RecurringChargeTemplateServiceLocal
	// recurringChargeTemplateService;

	public ChargeInstance findByCodeAndService(String code, Long subscriptionId) {
		ChargeInstance chargeInstance = null;
		try {
			log.debug("start of find {} by code (code={}) ..", "ChargeInstance", code);
			QueryBuilder qb = new QueryBuilder(ChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
			chargeInstance = (ChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();
			log.debug("end of find {} by code (code={}). Result found={}.", new Object[] { "ChargeInstance", code,
					chargeInstance != null });

		} catch (NoResultException nre) {
			log.warn("findByCodeAndService : no charges have been found");
		} catch (Exception e) {
			log.error("findByCodeAndService error={} ", e.getMessage());
		}
		return chargeInstance;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findIdsByStatus(InstanceStatusEnum status, Date maxChargeDate) {
		List<Long> ids = new ArrayList<Long>();
		try {
			log.debug("start of find RecurringChargeInstance --IDS---  by status {} and date {}", status, maxChargeDate);
			
			QueryBuilder qb = new QueryBuilder("SELECT c.id FROM "+RecurringChargeInstance.class.getName()+" c");
			qb.addCriterion("c.status", "=", status, true);
			qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate);
			ids = qb.getQuery(getEntityManager()).getResultList();
			log.debug("end of find {} by status (status={}). Result size found={}.",
					new Object[] { "RecurringChargeInstance", status,
					ids != null ? ids.size() : 0 });

		} catch (Exception e) {
			log.error("findIdsByStatus error={} ", e.getMessage());
		}
		return ids;
	}
	
	@SuppressWarnings("unchecked")
	public List<RecurringChargeInstance> findByStatus(InstanceStatusEnum status, Date maxChargeDate) {
		List<RecurringChargeInstance> recurringChargeInstances = new ArrayList<RecurringChargeInstance>();
		try {
			log.debug("start of find RecurringChargeInstance by status {} and date {}", status, maxChargeDate);
			QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
			qb.addCriterion("c.status", "=", status, true);
			qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate);
			recurringChargeInstances = qb.getQuery(getEntityManager()).getResultList();
			log.debug("end of find {} by status (status={}). Result size found={}.",
					new Object[] { "RecurringChargeInstance", status,
							recurringChargeInstances != null ? recurringChargeInstances.size() : 0 });

		} catch (Exception e) {
			log.error("findByStatus error={} ", e.getMessage());
		}
		return recurringChargeInstances;
	}

	public Long recurringChargeApplication(Subscription subscription, RecurringChargeTemplate chargetemplate,
			Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, Integer quantity, String criteria1,
			String criteria2, String criteria3, User creator) throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}
		RecurringChargeInstance recurringChargeInstance = new RecurringChargeInstance(chargetemplate.getCode(),
				chargetemplate.getDescription(), effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
				chargetemplate, null);
		recurringChargeInstance.setCriteria1(criteria1);
		recurringChargeInstance.setCriteria2(criteria2);
		recurringChargeInstance.setCriteria3(criteria3);
		recurringChargeInstance.setCountry(subscription.getUserAccount().getBillingAccount().getTradingCountry());
		recurringChargeInstance.setCurrency(subscription.getUserAccount().getBillingAccount().getCustomerAccount()
				.getTradingCurrency());
		// TODO : should choose wallet from GUI
		recurringChargeInstance.setPrepaid(false);
		recurringChargeInstance.getWalletInstances().add(subscription.getUserAccount().getWallet());

		create(recurringChargeInstance, creator, chargetemplate.getProvider());

		chargeApplicationService.recurringWalletOperation(subscription, recurringChargeInstance, quantity, effetDate,
				creator);
		return recurringChargeInstance.getId();
	}

	public void recurringChargeApplication(RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		recurringChargeApplication(getEntityManager(), chargeInstance, creator);
	}

	public void recurringChargeApplication(EntityManager em, RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		chargeApplicationService.chargeSubscription(em, chargeInstance, creator);
	}

	@SuppressWarnings("unchecked")
	public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

	public RecurringChargeInstance recurringChargeInstanciation(ServiceInstance serviceInst,
			RecurringChargeTemplate recurringChargeTemplate, Date subscriptionDate, Seller seller, User creator)
			throws BusinessException {

		if (serviceInst == null) {
			throw new BusinessException("service instance does not exist.");
		}

		if (serviceInst.getStatus() == InstanceStatusEnum.CANCELED
				|| serviceInst.getStatus() == InstanceStatusEnum.TERMINATED
				|| serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
			throw new BusinessException("service instance is " + serviceInst.getStatus() + ". code="
					+ serviceInst.getCode());
		}
		String chargeCode = recurringChargeTemplate.getCode();

		RecurringChargeInstance chargeInst = (RecurringChargeInstance) findByCodeAndService(chargeCode,
				serviceInst.getId());

		if (chargeInst != null) {
			throw new BusinessException("charge instance code already exists. code=" + chargeCode);
		}
		log.debug("create chargeInstance for charge {}",chargeCode);
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
		chargeInstance.setInvoicingCalendar(serviceInst.getInvoicingCalendar());
		chargeInstance.setSeller(seller);
		chargeInstance.setCountry(serviceInst.getSubscription().getUserAccount().getBillingAccount()
				.getTradingCountry());
		chargeInstance.setCurrency(serviceInst.getSubscription().getUserAccount().getBillingAccount()
				.getCustomerAccount().getTradingCurrency());
		chargeInstance.updateAudit(getCurrentUser());

		ServiceChargeTemplateRecurring recChTmplServ = serviceInst.getServiceTemplate()
				.getServiceRecurringChargeByChargeCode(chargeCode);
		getEntityManager().merge(recChTmplServ);
		List<WalletTemplate> walletTemplates = recChTmplServ.getWalletTemplates();

		if (walletTemplates != null && walletTemplates.size() > 0) {
			log.debug("associate {} walletsInstance",walletTemplates.size());
			for (WalletTemplate walletTemplate : walletTemplates) {
				if (walletTemplate == null){
					log.debug("walletTemplate is null, we continue");
					continue;
				}
				if (walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
					log.debug("one walletTemplate is prepaid, we set the chargeInstance as being prepaid");
					chargeInstance.setPrepaid(true);
				}
				
				WalletInstance walletInstance = walletService.getWalletInstance(serviceInst.getSubscription().getUserAccount(), walletTemplate,
						serviceInst.getAuditable().getCreator(), serviceInst.getProvider());
				log.debug("add the wallet instance {} to the chargeInstance {}",walletInstance.getId(),chargeInstance.getId());
				chargeInstance.getWalletInstances().add(walletInstance);
			}
		} else {
			log.debug("we set the chargeInstance as being postpaid and associate it to the principal wallet");
			chargeInstance.setPrepaid(false);
			chargeInstance.getWalletInstances().add(serviceInst.getSubscription().getUserAccount().getWallet());
		}

		create(chargeInstance, creator, recurringChargeTemplate.getProvider());
		return chargeInstance;
	}

	public void recurringChargeDeactivation(long recurringChargeInstanId, Date terminationDate, User updater)
			throws BusinessException {
		recurringChargeDeactivation(getEntityManager(), recurringChargeInstanId, terminationDate, updater);
	}

	public void recurringChargeDeactivation(EntityManager em, long recurringChargeInstanId, Date terminationDate,
			User updater) throws BusinessException {

		RecurringChargeInstance recurringChargeInstance = findById(recurringChargeInstanId, true);

		log.debug("recurringChargeDeactivation : recurringChargeInstanceId={},ChargeApplications size={}",
				recurringChargeInstance.getId(), recurringChargeInstance.getWalletOperations().size());

		recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);

		// chargeApplicationService.cancelChargeApplications(recurringChargeInstanId,
		// null, updater);

		update(recurringChargeInstance, updater);

	}

	public void recurringChargeReactivation(ServiceInstance serviceInst, Subscription subscription,
			Date subscriptionDate, User creator) throws BusinessException {
		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
				|| subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
			throw new BusinessException("subscription is " + subscription.getStatus());
		}
		if (serviceInst.getStatus() == InstanceStatusEnum.TERMINATED
				|| serviceInst.getStatus() == InstanceStatusEnum.CANCELED
				|| serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
			throw new BusinessException("service instance is " + subscription.getStatus() + ". service Code="
					+ serviceInst.getCode() + ",subscription Code" + subscription.getCode());
		}
		for (RecurringChargeInstance recurringChargeInstance : serviceInst.getRecurringChargeInstances()) {
			recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
			// recurringChargeInstance.setSubscriptionDate(subscriptionDate);
			recurringChargeInstance.setTerminationDate(null);
			recurringChargeInstance.setChargeDate(subscriptionDate);
			update(recurringChargeInstance);
		}

	}

}
