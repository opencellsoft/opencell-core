package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.subscription.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.medina.impl.AccessService;

@Stateless
public class SubscriptionImportService {

	@Inject
	private Logger log;
	

	@Inject
	SubscriptionService subscriptionService;

	@Inject
	OfferTemplateService offerTemplateService;

	@Inject
	UserAccountService userAccountService;

	@Inject
	SubscriptionTerminationReasonService subscriptionTerminationReasonService;

	@Inject
	ServiceTemplateService serviceTemplateService;

	@Inject
	ServiceInstanceService serviceInstanceService;
	
	
	@Inject
	AccessService accessService;
	

	ParamBean param = ParamBean.getInstance("meveo-admin.properties");
	
	//return 1 if a subscription has been created, 0 else
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int importSubscription(CheckedSubscription checkSubscription,Provider provider
			,org.meveo.model.jaxb.subscription.Subscription subscrip,String fileName,User userJob,int i) throws BusinessException, SubscriptionServiceException, ImportIgnoredException {
		
		Subscription subscription=checkSubscription.subscription;
		if (subscription != null) {
			if (!"ACTIVE".equals(subscrip.getStatus().getValue())) {
				if (!provider.getCode().equals(
						subscription.getProvider().getCode())) {
					throw new BusinessException("Conflict subscription.provider and file.provider");
				}
				SubscriptionTerminationReason subscriptionTerminationType = null;
				try {
					subscriptionTerminationType = subscriptionTerminationReasonService
							.findByCodeReason(subscrip.getStatus()
									.getReason(), provider.getCode());
				} catch (Exception e) {
				}
				if (subscriptionTerminationType == null) {
					throw new BusinessException("subscriptionTerminationType not found for codeReason:"
									+ subscrip.getStatus().getReason());
				}
				subscriptionService
							.terminateSubscription(
									subscription,
									DateUtils.parseDateWithPattern(
											subscrip.getStatus()
													.getDate(),
											param.getProperty("connectorCRM.dateFormat")),
									subscriptionTerminationType,
									userJob);
				log.info("file:" + fileName
							+ ", typeEntity:Subscription, index:" + i
							+ ", code:" + subscrip.getCode()
							+ ", status:Terminated");
				return 0;
			} else {
				throw new ImportIgnoredException();
			}
		}

		subscription = new Subscription();

		subscription.setOffer(checkSubscription.offerTemplate);
		subscription.setCode(subscrip.getCode());
		subscription.setDescription(subscrip.getDescription());
		subscription.setSubscriptionDate(DateUtils
				.parseDateWithPattern(subscrip.getSubscriptionDate(),
						param.getProperty("connectorCRM.dateFormat")));
		subscription.setEndAgrementDate(DateUtils.parseDateWithPattern(
				subscrip.getEndAgreementDate(),
				param.getProperty("connectorCRM.dateFormat")));
		subscription.setStatusDate(DateUtils.parseDateWithPattern(
				subscrip.getStatus().getDate(),
				param.getProperty("connectorCRM.dateFormat")));
		subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
		subscription.setUserAccount(checkSubscription.userAccount);
		subscriptionService.create(subscription, userJob, provider);
		
		log.info("file:" + fileName
				+ ", typeEntity:Subscription, index:" + i + ", code:"
				+ subscrip.getCode() + ", status:Created");
		for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : checkSubscription.serviceInsts) {
			try {
				ServiceTemplate serviceTemplate = null;
				ServiceInstance serviceInstance = new ServiceInstance();
				serviceTemplate = serviceTemplateService.findByCode(
						serviceInst.getCode().toUpperCase(), provider);
				serviceInstance.setCode(serviceTemplate.getCode());
				serviceInstance.setDescription(serviceTemplate
						.getDescription());
				serviceInstance.setServiceTemplate(serviceTemplate);
				serviceInstance.setSubscription(subscription);
				serviceInstance
						.setSubscriptionDate(DateUtils.parseDateWithPattern(
								serviceInst.getSubscriptionDate(),
								param.getProperty("connectorCRM.dateFormat")));
				int quantity = 1;
				if (serviceInst.getQuantity() != null
						&& serviceInst.getQuantity().trim().length() != 0) {
					quantity = Integer.parseInt(serviceInst
							.getQuantity().trim());
				}
				log.debug("file:" + fileName
						+ ", typeEntity:Subscription, index:" + i
						+ ", code:" + subscrip.getCode()
						+ ", quantity:" + quantity);
				serviceInstance.setQuantity(quantity);
				serviceInstance.setProvider(provider);
				serviceInstanceService.serviceInstanciation(
						serviceInstance, userJob);
				subscription.getServiceInstances().add(serviceInstance);
				if (serviceInst.getRecurringCharges() != null) {
					if (serviceInstance.getRecurringChargeInstances() != null) {
						for (RecurringChargeInstance recurringChargeInstance : serviceInstance
								.getRecurringChargeInstances()) {
							log.debug("file:"
									+ fileName
									+ ", typeEntity:Subscription, index:"
									+ i + ", code:"
									+ subscrip.getCode()
									+ ", recurringChargeInstance:"
									+ recurringChargeInstance.getCode());
							if (serviceInst.getRecurringCharges()
									.getAmountWithoutTax() != null) {
								recurringChargeInstance
										.setAmountWithoutTax(new BigDecimal(
												serviceInst
														.getRecurringCharges()
														.getAmountWithoutTax()
														.replace(',',
																'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("file:"
										+ fileName
										+ ", typeEntity:Subscription, index:"
										+ i
										+ ", code:"
										+ subscrip.getCode()
										+ ", recurringChargeInstance.setAmountWithoutTax:"
										+ serviceInst
												.getRecurringCharges()
												.getAmountWithoutTax());
							}
							if (serviceInst.getRecurringCharges()
									.getAmountWithoutTax() != null) {
								recurringChargeInstance
										.setAmountWithTax(new BigDecimal(
												serviceInst
														.getRecurringCharges()
														.getAmountWithTax()
														.replace(',',
																'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("file:"
										+ fileName
										+ ", typeEntity:Subscription, index:"
										+ i
										+ ", code:"
										+ subscrip.getCode()
										+ ", recurringChargeInstance.setAmount2:"
										+ serviceInst
												.getRecurringCharges()
												.getAmountWithTax());
							}
							recurringChargeInstance
									.setCriteria1(serviceInst
											.getRecurringCharges()
											.getC1());
							recurringChargeInstance
									.setCriteria2(serviceInst
											.getRecurringCharges()
											.getC2());
							recurringChargeInstance
									.setCriteria3(serviceInst
											.getRecurringCharges()
											.getC3());
						}
					}
				}

				if (serviceInst.getOneshotCharges() != null) {
					if (serviceInstance
							.getSubscriptionChargeInstances() != null) {
						for (ChargeInstance subscriptionChargeInstance : serviceInstance
								.getSubscriptionChargeInstances()) {
							if (serviceInst.getOneshotCharges()
									.getAmountWithoutTax() != null) {
								subscriptionChargeInstance
										.setAmountWithoutTax(new BigDecimal(
												serviceInst
														.getOneshotCharges()
														.getAmountWithoutTax()
														.replace(',',
																'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("file:"
										+ fileName
										+ ", typeEntity:Subscription, index:"
										+ i
										+ ", code:"
										+ subscrip.getCode()
										+ ", subscriptionChargeInstance.setAmountWithoutTax:"
										+ serviceInst
												.getOneshotCharges()
												.getAmountWithoutTax());
							}
							if (serviceInst.getOneshotCharges()
									.getAmountWithoutTax() != null) {
								subscriptionChargeInstance
										.setAmountWithTax(new BigDecimal(
												serviceInst
														.getOneshotCharges()
														.getAmountWithTax()
														.replace(',',
																'.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("file:"
										+ fileName
										+ ", typeEntity:Subscription, index:"
										+ i
										+ ", code:"
										+ subscrip.getCode()
										+ ", subscriptionChargeInstance.setAmount2:"
										+ serviceInst
												.getOneshotCharges()
												.getAmountWithTax());
							}
							subscriptionChargeInstance
									.setCriteria1(serviceInst
											.getOneshotCharges()
											.getC1());
							subscriptionChargeInstance
									.setCriteria2(serviceInst
											.getOneshotCharges()
											.getC2());
							subscriptionChargeInstance
									.setCriteria3(serviceInst
											.getOneshotCharges()
											.getC3());
						}
					}
				}

				subscriptionService.update(subscription, userJob);
				serviceInstanceService.serviceActivation(
						serviceInstance, null, null, userJob);
			} catch (Exception e) {
				throw new SubscriptionServiceException(subscrip, serviceInst,e.getMessage());
			}

			log.info("file:" + fileName
					+ ", typeEntity:ServiceInstance, index:" + i
					+ ", code:" + serviceInst.getCode()
					+ ", status:Actived");
		}
		log.info("accessPoints.size="+checkSubscription.accessPoints.size());
		for(Access accessPoint:checkSubscription.accessPoints){
			org.meveo.model.mediation.Access access=new org.meveo.model.mediation.Access();
			access.setSubscription(subscription);
			access.setAccessUserId(accessPoint.getAccessUserId());
			accessService.create(access,userJob,provider);
			log.info("file:" + fileName
					+ ", typeEntity:access, index:" + i
					+ ", AccessUserId:" + access.getAccessUserId());
		}
		return 1;
	}
	

}
