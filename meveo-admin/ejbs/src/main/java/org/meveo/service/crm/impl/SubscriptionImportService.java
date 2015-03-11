package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.CustomField;
import org.meveo.model.jaxb.subscription.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.medina.impl.AccessService;
import org.slf4j.Logger;

@Stateless
public class SubscriptionImportService {

	@Inject
	private Logger log;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private AccessService accessService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private UserAccountService userAccountService;

	private ParamBean paramBean = ParamBean.getInstance();

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int importSubscription(CheckedSubscription checkSubscription,
			org.meveo.model.jaxb.subscription.Subscription jaxbSubscription, String fileName, User currentUser, int i)
			throws BusinessException, SubscriptionServiceException, ImportIgnoredException {

		Provider provider = currentUser.getProvider();

		OfferTemplate offerTemplate = null;
		try {
			offerTemplate = offerTemplateService.findByCode(jaxbSubscription.getOfferCode().toUpperCase(), provider);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		checkSubscription.offerTemplate = offerTemplate;

		UserAccount userAccount = null;
		try {
			userAccount = userAccountService.findByCode(jaxbSubscription.getUserAccountId(), provider);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		if (userAccount == null) {
			throw new BusinessException("UserAccount does not exists.");
		}

		checkSubscription.userAccount = userAccount;

		try {
			checkSubscription.subscription = subscriptionService.findByCode(jaxbSubscription.getCode(), provider);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		Subscription subscription = checkSubscription.subscription;
		if (subscription != null) {
			if (!"ACTIVE".equals(jaxbSubscription.getStatus().getValue())) {
				if (!provider.getCode().equals(subscription.getProvider().getCode())) {
					throw new BusinessException("Conflict subscription.provider and file.provider.");
				}

				SubscriptionTerminationReason subscriptionTerminationType = null;
				try {
					subscriptionTerminationType = subscriptionTerminationReasonService.findByCodeReason(
							jaxbSubscription.getStatus().getReason(), provider);
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				if (subscriptionTerminationType == null) {
					throw new BusinessException("subscriptionTerminationType not found for codeReason:"
							+ jaxbSubscription.getStatus().getReason());
				}

				subscriptionService.terminateSubscription(
						subscription,
						DateUtils.parseDateWithPattern(jaxbSubscription.getStatus().getDate(),
								paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")),
						subscriptionTerminationType, currentUser);
				log.info("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
						+ jaxbSubscription.getCode() + ", status:Terminated");

				return 0;
			} else {
				throw new ImportIgnoredException();
			}
		}

		subscription = new Subscription();
		subscription.setOffer(checkSubscription.offerTemplate);
		subscription.setCode(jaxbSubscription.getCode());
		subscription.setDescription(jaxbSubscription.getDescription());
		if (jaxbSubscription.getCustomFields() != null && jaxbSubscription.getCustomFields().getCustomField() != null
				&& jaxbSubscription.getCustomFields().getCustomField().size() > 0) {
			for (CustomField customField : jaxbSubscription.getCustomFields().getCustomField()) {
				// check if cft exists
				if (customFieldTemplateService.findByCodeAndAccountLevel(customField.getCode(), AccountLevelEnum.SUB,
						provider) == null) {
					log.warn("CustomFieldTemplate with code={} does not exists.", customField.getCode());
					continue;
				}
				CustomFieldInstance cfi = new CustomFieldInstance();
				cfi.setSubscription(subscription);
				cfi.setActive(true);
				cfi.setCode(customField.getCode());
				cfi.setDateValue(customField.getDateValue());
				cfi.setDescription(customField.getDescription());
				cfi.setDoubleValue(customField.getDoubleValue());
				cfi.setLongValue(customField.getLongValue());
				cfi.setProvider(provider);
				cfi.setStringValue(customField.getStringValue());
				Auditable auditable = new Auditable();
				auditable.setCreated(new Date());
				auditable.setCreator(currentUser);
				cfi.setAuditable(auditable);
				subscription.getCustomFields().put(cfi.getCode(), cfi);
			}
		}
		subscription.setSubscriptionDate(DateUtils.parseDateWithPattern(jaxbSubscription.getSubscriptionDate(),
				paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
		subscription.setEndAgrementDate(DateUtils.parseDateWithPattern(jaxbSubscription.getEndAgreementDate(),
				paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
		subscription.setStatusDate(DateUtils.parseDateWithPattern(jaxbSubscription.getStatus().getDate(),
				paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
		subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
		subscription.setUserAccount(checkSubscription.userAccount);
		subscriptionService.create(subscription, currentUser, provider);

		log.info("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:" + jaxbSubscription.getCode()
				+ ", status:Created");

		for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : checkSubscription.serviceInsts) {
			try {
				ServiceTemplate serviceTemplate = null;
				ServiceInstance serviceInstance = new ServiceInstance();
				serviceTemplate = serviceTemplateService.findByCode(serviceInst.getCode().toUpperCase(), provider);
				serviceInstance.setCode(serviceTemplate.getCode());
				serviceInstance.setDescription(serviceTemplate.getDescription());
				serviceInstance.setServiceTemplate(serviceTemplate);
				serviceInstance.setSubscription(subscription);
				serviceInstance.setSubscriptionDate(DateUtils.parseDateWithPattern(serviceInst.getSubscriptionDate(),
						paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
				BigDecimal quantity = BigDecimal.ONE;

				if (serviceInst.getQuantity() != null && serviceInst.getQuantity().trim().length() != 0) {
					quantity = new BigDecimal(serviceInst.getQuantity().trim());
				}

				log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
						+ jaxbSubscription.getCode() + ", quantity:" + quantity);
				serviceInstance.setQuantity(quantity);
				serviceInstance.setProvider(provider);
				serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);

				subscription.getServiceInstances().add(serviceInstance);

				if (serviceInst.getRecurringCharges() != null) {
					if (serviceInstance.getRecurringChargeInstances() != null) {
						for (RecurringChargeInstance recurringChargeInstance : serviceInstance
								.getRecurringChargeInstances()) {
							log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
									+ jaxbSubscription.getCode() + ", recurringChargeInstance:"
									+ recurringChargeInstance.getCode());

							if (serviceInst.getRecurringCharges().getAmountWithoutTax() != null) {
								recurringChargeInstance.setAmountWithoutTax(new BigDecimal(serviceInst
										.getRecurringCharges().getAmountWithoutTax().replace(',', '.')).setScale(
										BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
										+ jaxbSubscription.getCode() + ", recurringChargeInstance.setAmountWithoutTax:"
										+ serviceInst.getRecurringCharges().getAmountWithoutTax());
							}

							if (serviceInst.getRecurringCharges().getAmountWithoutTax() != null) {
								recurringChargeInstance.setAmountWithTax(new BigDecimal(serviceInst
										.getRecurringCharges().getAmountWithTax().replace(',', '.')).setScale(
										BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
										+ jaxbSubscription.getCode() + ", recurringChargeInstance.setAmount2:"
										+ serviceInst.getRecurringCharges().getAmountWithTax());
							}

							recurringChargeInstance.setCriteria1(serviceInst.getRecurringCharges().getC1());
							recurringChargeInstance.setCriteria2(serviceInst.getRecurringCharges().getC2());
							recurringChargeInstance.setCriteria3(serviceInst.getRecurringCharges().getC3());
						}
					}
				}

				if (serviceInst.getOneshotCharges() != null) {
					if (serviceInstance.getSubscriptionChargeInstances() != null) {
						for (ChargeInstance subscriptionChargeInstance : serviceInstance
								.getSubscriptionChargeInstances()) {
							if (serviceInst.getOneshotCharges().getAmountWithoutTax() != null) {
								subscriptionChargeInstance.setAmountWithoutTax(new BigDecimal(serviceInst
										.getOneshotCharges().getAmountWithoutTax().replace(',', '.')).setScale(
										BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
										+ jaxbSubscription.getCode()
										+ ", subscriptionChargeInstance.setAmountWithoutTax:"
										+ serviceInst.getOneshotCharges().getAmountWithoutTax());
							}

							if (serviceInst.getOneshotCharges().getAmountWithoutTax() != null) {
								subscriptionChargeInstance.setAmountWithTax(new BigDecimal(serviceInst
										.getOneshotCharges().getAmountWithTax().replace(',', '.')).setScale(
										BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
								log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:"
										+ jaxbSubscription.getCode() + ", subscriptionChargeInstance.setAmount2:"
										+ serviceInst.getOneshotCharges().getAmountWithTax());
							}

							subscriptionChargeInstance.setCriteria1(serviceInst.getOneshotCharges().getC1());
							subscriptionChargeInstance.setCriteria2(serviceInst.getOneshotCharges().getC2());
							subscriptionChargeInstance.setCriteria3(serviceInst.getOneshotCharges().getC3());
						}
					}
				}

				subscription.updateAudit(currentUser);

				serviceInstanceService.serviceActivation(serviceInstance, null, null, currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new SubscriptionServiceException(jaxbSubscription, serviceInst, e.getMessage());
			}

			log.info("File:" + fileName + ", typeEntity:ServiceInstance, index:" + i + ", code:"
					+ serviceInst.getCode() + ", status:Actived");
		}

		log.info("accessPoints.size=" + checkSubscription.accessPoints.size());

		for (Access jaxbAccessPoint : checkSubscription.accessPoints) {
			org.meveo.model.mediation.Access access = new org.meveo.model.mediation.Access();
			access.setSubscription(subscription);
			access.setAccessUserId(jaxbAccessPoint.getAccessUserId());
			access.setStartDate(DateUtils.parseDateWithPattern(jaxbAccessPoint.getStartDate(),
					paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy")));
			access.setEndDate(DateUtils.parseDateWithPattern(jaxbAccessPoint.getEndDate(),
					paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy")));
			accessService.create(access, currentUser, provider);
			log.info("File:" + fileName + ", typeEntity:access, index:" + i + ", AccessUserId:"
					+ access.getAccessUserId());
		}

		return 1;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void activateServices(EntityManager em, CheckedSubscription checkSubscription,
			org.meveo.model.jaxb.subscription.Subscription subscrip, User currentUser)
			throws SubscriptionServiceException {
		if (checkSubscription.subscription != null && checkSubscription.subscription.getServiceInstances().size() > 0) {
			for (ServiceInstance serviceInstance : checkSubscription.subscription.getServiceInstances()) {
				try {
					serviceInstanceService.serviceActivation(em, serviceInstance, null, null, currentUser);
				} catch (Exception e) {
					log.error(e.getMessage());
					throw new SubscriptionServiceException(subscrip, null, e.getMessage());
				}
			}
		}
	}

}
