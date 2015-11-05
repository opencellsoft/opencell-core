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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.qualifier.Rejected;
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
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;

@Stateless
public class RecurringChargeInstanceService extends BusinessService<RecurringChargeInstance> {

	@Inject
	private WalletService walletService;
	
	@Inject
	private WalletOperationService walletOperationService;
	

	@Inject
	@Rejected
	Event<Serializable> rejectededChargeProducer;

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
			log.error("findByCodeAndService error={} ", e);
		}
		return chargeInstance;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findIdsByStatus(InstanceStatusEnum status, Date maxChargeDate,Provider currentProvider) {
		List<Long> ids = new ArrayList<Long>();
		try {
			log.debug("start of find RecurringChargeInstance --IDS---  by status {} and date {}", status, maxChargeDate);
			log.debug("find by provider {}",currentProvider.getCode());
			
			QueryBuilder qb = new QueryBuilder("SELECT c.id FROM "+RecurringChargeInstance.class.getName()+" c");
			qb.addCriterionEnum("c.status", status);
			qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate);
			qb.addCriterionEntity("c.provider", currentProvider);
			ids = qb.getQuery(getEntityManager()).getResultList();
			log.debug("end of find {} by status (status={}). Result size found={}.",
					new Object[] { "RecurringChargeInstance", status,
					(ids != null ? ids.size() : "NULL") });
		} catch (Exception e) {
			log.error("findIdsByStatus error={} ", e.getMessage(),e);
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
			log.error("findByStatus error={} ", e);
		}
		return recurringChargeInstances;
	}

	@SuppressWarnings("unchecked")
	public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c", Arrays.asList("chargeTemplate"), null);
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

	public int applyRecurringCharge(Long chargeInstanceId, Date maxDate,User user) throws BusinessException {
		int MaxRecurringRatingHistory=Integer.parseInt(ParamBean.getInstance().getProperty("rating.recurringMaxRetry", "100"));
		int nbRating=0;
		try {

			RecurringChargeInstance activeRecurringChargeInstance = findById(chargeInstanceId, user.getProvider());

			RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance
					.getRecurringChargeTemplate();
			if (recurringChargeTemplate.getCalendar() == null) {
				// FIXME : should not stop the method execution
				rejectededChargeProducer.fire(recurringChargeTemplate);
				log.error("Recurring charge template has no calendar: code="
						+ recurringChargeTemplate.getCode());
				throw new BusinessException("Recurring charge template has no calendar: code="
						+ recurringChargeTemplate.getCode());
			}

			Date applicationDate = null;
			//if (recurringChargeTemplate.getApplyInAdvance()) {
				applicationDate = activeRecurringChargeInstance.getNextChargeDate();
			//} else {
			//	applicationDate = activeRecurringChargeInstance.getChargeDate();
			//}

			while (nbRating<MaxRecurringRatingHistory && (applicationDate.getTime() <= maxDate.getTime())) {
				nbRating++;
				log.info("applicationDate={}", applicationDate);
				applicationDate = DateUtils.setTimeToZero(applicationDate);
				if (!recurringChargeTemplate.getApplyInAdvance()) {
					walletOperationService
							.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate, user);
				} else {
					walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate, user);
				}
				log.debug("nextChargeDate {}, chargeDate {}.",activeRecurringChargeInstance.getChargeDate(),activeRecurringChargeInstance.getNextChargeDate());
				//if (recurringChargeTemplate.getApplyInAdvance()) {
					applicationDate = activeRecurringChargeInstance.getNextChargeDate();
				//} else {
				//	applicationDate = activeRecurringChargeInstance.getChargeDate();
				//}
			} 
			if(nbRating>0){
				activeRecurringChargeInstance.updateAudit(user);
				updateNoCheck(activeRecurringChargeInstance);
			}
		} catch (Exception e) {
            rejectededChargeProducer.fire("RecurringCharge " + chargeInstanceId);
            throw new BusinessException(e);
        }
		return nbRating;
	}

}
