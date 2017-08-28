/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.NumberUtil;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.script.revenue.RevenueRecognitionScriptService;

@Stateless
public class RecurringChargeInstanceService extends BusinessService<RecurringChargeInstance> {

	@Inject
	private WalletService walletService;
	
	@Inject
	private WalletOperationService walletOperationService;
	
	@Inject
	private RevenueRecognitionScriptService revenueRecognitionScriptService;

	@Inject
	@Rejected
	Event<Serializable> rejectededChargeProducer;

	public RecurringChargeInstance findByCodeAndService(String code, Long serviceInstanceId) {
	    RecurringChargeInstance chargeInstance = null;
		try {
			log.debug("start of find {} by code {} on service instance {}", "RecurringChargeInstance", code, serviceInstanceId);
			QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.serviceInstance.id", "=", serviceInstanceId, true);
			chargeInstance = (RecurringChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();
            log.debug("end of find {} by code (code={}). Result found={}.", "RecurringChargeInstance", code, chargeInstance != null);

		} catch (NoResultException nre) {
			log.warn("findByCodeAndService : no charges have been found");
		} catch (Exception e) {
			log.error("findByCodeAndService error={} ", e);
		}
		return chargeInstance;
	}

    public List<Long> findIdsByStatus(InstanceStatusEnum status, Date maxChargeDate) {
        List<Long> ids = new ArrayList<Long>();
        try {
            log.debug("start of find RecurringChargeInstance --IDS---  by status {} and date {}", status, maxChargeDate);

            QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
            qb.addCriterionEnum("c.status", status);
            qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate);
            ids = qb.getIdQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by status (status={}). Result size found={}.", new Object[] { "RecurringChargeInstance", status, (ids != null ? ids.size() : "NULL") });
        } catch (Exception e) {
            log.error("findIdsByStatus error={} ", e.getMessage(), e);
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
		QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c", Arrays.asList("chargeTemplate"));
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

	public RecurringChargeInstance recurringChargeInstanciation(ServiceInstance serviceInst,
			RecurringChargeTemplate recurringChargeTemplate, Date subscriptionDate, Seller seller, boolean isVirtual)
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

        if (!isVirtual) {
            RecurringChargeInstance chargeInst = (RecurringChargeInstance) findByCodeAndService(chargeCode, serviceInst.getId());
            if (chargeInst != null) {
                throw new BusinessException("charge instance code already exists. code=" + chargeCode + " service instance id " + serviceInst.getId());
            }
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
		chargeInstance.setOrderNumber(serviceInst.getOrderNumber());

		ServiceChargeTemplateRecurring recChTmplServ = serviceInst.getServiceTemplate()
				.getServiceRecurringChargeByChargeCode(chargeCode);
//		getEntityManager().merge(recChTmplServ); - does not make sence as merge result is what shoudl be used 
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
				
				WalletInstance walletInstance = walletService.getWalletInstance(serviceInst.getSubscription().getUserAccount(), walletTemplate, isVirtual);
				log.debug("add the wallet instance {} to the chargeInstance {}",walletInstance.getId(),chargeInstance.getId());
				chargeInstance.getWalletInstances().add(walletInstance);
			}
		} else {
			log.debug("we set the chargeInstance as being postpaid and associate it to the principal wallet");
			chargeInstance.setPrepaid(false);
			chargeInstance.getWalletInstances().add(serviceInst.getSubscription().getUserAccount().getWallet());
		}


        if (!isVirtual) {
            create(chargeInstance);
        }
        
		return chargeInstance;
	}

	public void recurringChargeDeactivation(long recurringChargeInstanId, Date terminationDate)
			throws BusinessException {

		RecurringChargeInstance recurringChargeInstance = findById(recurringChargeInstanId, true);

		log.debug("recurringChargeDeactivation : recurringChargeInstanceId={},ChargeApplications size={}",
				recurringChargeInstance.getId(), recurringChargeInstance.getWalletOperations().size());

		recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);

		// chargeApplicationService.cancelChargeApplications(recurringChargeInstanId,
		// null);

		update(recurringChargeInstance);

	}
	
	public void recurringChargeSuspension(long recurringChargeInstanId, Date terminationDate) throws BusinessException {

		RecurringChargeInstance recurringChargeInstance = findById(recurringChargeInstanId, true);

		log.debug("recurringChargeSuspension : recurringChargeInstanceId={},ChargeApplications size={}",
				recurringChargeInstance.getId(), recurringChargeInstance.getWalletOperations().size());

		recurringChargeInstance.setStatus(InstanceStatusEnum.SUSPENDED);
		update(recurringChargeInstance);

	}

	public void recurringChargeReactivation(ServiceInstance serviceInst, Subscription subscription,
			Date subscriptionDate) throws BusinessException {
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

	public int applyRecurringCharge(Long chargeInstanceId, Date maxDate) throws BusinessException {
		long startDate = System.currentTimeMillis();
		int MaxRecurringRatingHistory=Integer.parseInt(ParamBean.getInstance().getProperty("rating.recurringMaxRetry", "100"));
		int nbRating=0;
		
		try {
			RecurringChargeInstance activeRecurringChargeInstance = findById(chargeInstanceId);
			log.info("After findById:" + (System.currentTimeMillis() - startDate));
			
			if (!walletOperationService.isChargeMatch(activeRecurringChargeInstance, activeRecurringChargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
				log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", activeRecurringChargeInstance.getCode());
				return nbRating;
			}
			
			log.info("Before getRecurringChargeTemplate:" + (System.currentTimeMillis() - startDate));

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
				
			//If we recognize revenue we first delete all SCHEDULED wallet operations
			if(appProvider.isRecognizeRevenue()){
			  log.info("Before createNamedQuery:" + (System.currentTimeMillis() - startDate));
			  try {
				log.debug("delete scheduled charges applications on chargeInstance {}", chargeInstanceId);
				getEntityManager().createNamedQuery("WalletOperation.deleteScheduled")
				  .setParameter("chargeInstance", activeRecurringChargeInstance)
							.executeUpdate();
			  }catch (Exception e) {
				log.error("error while trying to delete scheduled charges applications on chargeInstance {}", chargeInstanceId, e);
			  }
			  
			  log.info("After createNamedQuery:" + (System.currentTimeMillis() - startDate));
			}

			while (applicationDate != null && nbRating<MaxRecurringRatingHistory && (applicationDate.getTime() <= maxDate.getTime())) {
				log.info("Inside applicationDate:" + (System.currentTimeMillis() - startDate));
				nbRating++;
				log.info("applicationDate={}", applicationDate);
				applicationDate = DateUtils.setTimeToZero(applicationDate);
				if (!recurringChargeTemplate.getApplyInAdvance()) {
					walletOperationService
							.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate);
				} else {
					walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate,false);
				}
				
				log.info("After applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
				
				log.debug("chargeDate {}, nextChargeDate {}, wo size {}",activeRecurringChargeInstance.getChargeDate()
						,activeRecurringChargeInstance.getNextChargeDate(),activeRecurringChargeInstance.getWalletOperations().size());
				//if (recurringChargeTemplate.getApplyInAdvance()) {
					applicationDate = activeRecurringChargeInstance.getNextChargeDate();
				//} else {
				//	applicationDate = activeRecurringChargeInstance.getChargeDate();
				//}
			} 
			if(nbRating>0){
				updateNoCheck(activeRecurringChargeInstance);
			}
			//If we recognize revenue we create SCHEDULED wallet op until the end of the contract
			if(appProvider.isRecognizeRevenue() && !activeRecurringChargeInstance.getPrepaid()){
				log.info("Inside isRecognizeRevenue:" + (System.currentTimeMillis() - startDate));
				Date endContractDate = activeRecurringChargeInstance.getSubscription().getEndAgreementDate();
				log.debug("apply scheduled charges until {}",endContractDate);
				if(endContractDate==null){
					log.error("error while trying to schedule revenue for chargeInstance {},"
							+ " the subscription has no end agreeement date",chargeInstanceId);
				} else {
					log.info("Before activeRecurringChargeInstance:" + (System.currentTimeMillis() - startDate));
					Date chargeDate = activeRecurringChargeInstance.getChargeDate();
					Date nextChargeDate  = activeRecurringChargeInstance.getNextChargeDate();
					while (applicationDate != null && applicationDate.getTime() <= endContractDate.getTime()) {
						log.info("Schedule applicationDate={}", applicationDate);
						applicationDate = DateUtils.setTimeToZero(applicationDate);
						if (!recurringChargeTemplate.getApplyInAdvance()) {
							walletOperationService
									.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate);
						} else {
							walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate,true);
						}
						log.debug("chargeDate {},nextChargeDate {},  wo size {}",activeRecurringChargeInstance.getChargeDate()
								,activeRecurringChargeInstance.getNextChargeDate(),activeRecurringChargeInstance.getWalletOperations().size());
						applicationDate = activeRecurringChargeInstance.getNextChargeDate();
								
					} 
					log.info("After activeRecurringChargeInstance:" + (System.currentTimeMillis() - startDate));
					activeRecurringChargeInstance.setChargeDate(chargeDate);
					activeRecurringChargeInstance.setNextChargeDate(nextChargeDate);
				}
				revenueRecognitionScriptService.createRevenueSchedule(activeRecurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule().getScript().getCode(), activeRecurringChargeInstance);
				log.info("After createRevenueSchedule:" + (System.currentTimeMillis() - startDate));
			}
			
		} catch (Exception e) {	
            rejectededChargeProducer.fire("RecurringCharge " + chargeInstanceId);
            throw new BusinessException(e);
        }
		return nbRating;
	}	

	/**
	 * Apply recurring charges between given dates to a user account for a Virtual operation. Does not create/update/persist any entity.
	 * 
	 * @param chargeInstance Recurring charge instance
     * @param quantity Quantity as calculated
     * @param fromDate Recurring charge application start
     * @param toDate Recurring charge application end

	 * @return Wallet operations
	 * @throws BusinessException
	 */
    public List<WalletOperation> applyRecurringChargeVirtual(RecurringChargeInstance chargeInstance, Date fromDate, Date toDate)
            throws BusinessException {

        log.debug("Apply recuring charges on Virtual operation. User account {}, offer {}, charge {}, quantity {}, date range {}-{}", chargeInstance.getUserAccount().getCode(),
            chargeInstance.getServiceInstance().getSubscription().getOffer().getCode(), chargeInstance.getRecurringChargeTemplate().getCode(), chargeInstance.getServiceInstance()
                .getQuantity(), fromDate, toDate);
        
		if (!walletOperationService.isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
			log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
			return null;
		}

        BigDecimal inputQuantity = chargeInstance.getServiceInstance().getQuantity();
        BigDecimal quantity = NumberUtil.getInChargeUnit(inputQuantity, chargeInstance.getRecurringChargeTemplate().getUnitMultiplicator(), chargeInstance.getRecurringChargeTemplate()
            .getUnitNbDecimal(), chargeInstance.getRecurringChargeTemplate().getRoundingMode());

        return walletOperationService.applyReccuringChargeVirtual(chargeInstance, inputQuantity, quantity, fromDate, toDate);

    }
}