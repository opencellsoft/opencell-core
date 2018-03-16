package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.CacheKeyStr;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.jaxb.subscription.Access;
import org.meveo.model.jaxb.subscription.Charge;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.medina.impl.AccessService;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
public class SubscriptionImportService extends ImportService {

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private AccessService accessService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private Map<CacheKeyStr, OfferTemplate> offerMap = new HashedMap();

    private Map<CacheKeyStr, UserAccount> userAccountMap = new HashedMap();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int importSubscription(CheckedSubscription checkSubscription, org.meveo.model.jaxb.subscription.Subscription jaxbSubscription, String fileName, int i)
            throws BusinessException, SubscriptionServiceException, ImportIgnoredException {

        ParamBean paramBean = paramBeanFactory.getInstance();

        OfferTemplate offerTemplate = null;
        offerTemplate = offerMap.get(new CacheKeyStr(currentUser.getProviderCode(), jaxbSubscription.getOfferCode().toUpperCase()));
        if (offerTemplate == null) {

            try {
                offerTemplate = offerTemplateService.findByCode(jaxbSubscription.getOfferCode().toUpperCase(),
                    DateUtils.parseDateWithPattern(jaxbSubscription.getSubscriptionDate(), paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
                offerMap.put(new CacheKeyStr(currentUser.getProviderCode(), jaxbSubscription.getOfferCode().toUpperCase()), offerTemplate);
            } catch (Exception e) {
                log.warn("failed to find offerTemplate ", e);
            }
        }
        checkSubscription.offerTemplate = offerTemplate;

        UserAccount userAccount = checkSubscription.userAccount;
        if (userAccount == null) {
            userAccount = userAccountMap.get(new CacheKeyStr(currentUser.getProviderCode(), jaxbSubscription.getUserAccountId()));
            if (userAccount == null) {
                try {
                    userAccount = userAccountService.findByCode(jaxbSubscription.getUserAccountId());
                    userAccountMap.put(new CacheKeyStr(currentUser.getProviderCode(), jaxbSubscription.getUserAccountId()), userAccount);
                } catch (Exception e) {
                    log.error("failed to find userAccount", e);
                }
            }
            checkSubscription.userAccount = userAccount;
        }
        boolean ignoreCheck = jaxbSubscription.getIgnoreCheck() != null && jaxbSubscription.getIgnoreCheck().booleanValue();
        try {
            if (!ignoreCheck) {
                checkSubscription.subscription = subscriptionService.findByCode(jaxbSubscription.getCode());
            }
        } catch (Exception e) {
            log.error("failed to find checkSubscription", e);
        }

        Subscription subscription = checkSubscription.subscription;
        if (subscription != null) {
            if (!"ACTIVE".equals(jaxbSubscription.getStatus().getValue())) {

                SubscriptionTerminationReason subscriptionTerminationType = null;
                try {
                    subscriptionTerminationType = subscriptionTerminationReasonService.findByCodeReason(jaxbSubscription.getStatus().getReason());
                } catch (Exception e) {
                    log.error("failed to find subscriptionTerminationType ", e);
                }

                if (subscriptionTerminationType == null) {
                    throw new BusinessException("subscriptionTerminationType not found for codeReason:" + jaxbSubscription.getStatus().getReason());
                }

                subscriptionService.terminateSubscription(subscription,
                    DateUtils.parseDateWithPattern(jaxbSubscription.getStatus().getDate(), paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")),
                    subscriptionTerminationType, subscription.getOrderNumber());
                log.info("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:" + jaxbSubscription.getCode() + ", status:Terminated");

                return 0;
            } else {
                throw new ImportIgnoredException();
            }
        }

        subscription = new Subscription();
        subscription.setOffer(checkSubscription.offerTemplate);
        subscription.setCode(jaxbSubscription.getCode());
        subscription.setDescription(jaxbSubscription.getDescription());

        subscription.setSubscriptionDate(DateUtils.parseDateWithPattern(jaxbSubscription.getSubscriptionDate(), paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
        subscription.setEndAgreementDate(DateUtils.parseDateWithPattern(jaxbSubscription.getEndAgreementDate(), paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
        subscription.setStatusDate(DateUtils.parseDateWithPattern(jaxbSubscription.getStatus().getDate(), paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
        subscription.setUserAccount(checkSubscription.userAccount);
        subscriptionService.create(subscription);

        if (jaxbSubscription.getCustomFields() != null) {
            populateCustomFields(jaxbSubscription.getCustomFields().getCustomField(), subscription);
        }

        log.info("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:" + jaxbSubscription.getCode() + ", status:Created");

        for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : checkSubscription.serviceInsts) {
            try {
                ServiceTemplate serviceTemplate = null;
                ServiceInstance serviceInstance = new ServiceInstance();
                serviceTemplate = serviceTemplateService.findByCode(serviceInst.getCode().toUpperCase());
                if (serviceTemplate == null) {
                    continue;
                }
                serviceInstance.setCode(serviceTemplate.getCode());
                serviceInstance.setDescription(serviceTemplate.getDescription());
                serviceInstance.setServiceTemplate(serviceTemplate);
                serviceInstance.setSubscription(subscription);
                serviceInstance
                    .setSubscriptionDate(DateUtils.parseDateWithPattern(serviceInst.getSubscriptionDate(), paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy")));
                BigDecimal quantity = BigDecimal.ONE;

                if (serviceInst.getQuantity() != null && serviceInst.getQuantity().trim().length() != 0) {
                    quantity = new BigDecimal(serviceInst.getQuantity().trim());
                }

                log.debug("File:" + fileName + ", typeEntity:Subscription, index:" + i + ", code:" + jaxbSubscription.getCode() + ", quantity:" + quantity);
                serviceInstance.setQuantity(quantity);
                serviceInstanceService.serviceInstanciation(serviceInstance);

                subscription.getServiceInstances().add(serviceInstance);

                if (serviceInst.getRecurringCharges() != null && serviceInstance.getRecurringChargeInstances() != null) {
                    updateCharges(serviceInstance.getRecurringChargeInstances(), serviceInst.getRecurringCharges());
                }
                if (serviceInst.getSubscriptionCharges() != null && serviceInstance.getSubscriptionChargeInstances() != null) {
                    updateCharges(serviceInstance.getSubscriptionChargeInstances(), serviceInst.getSubscriptionCharges());
                }
                if (serviceInst.getTerminationCharges() != null && serviceInstance.getTerminationChargeInstances() != null) {
                    updateCharges(serviceInstance.getTerminationChargeInstances(), serviceInst.getTerminationCharges());
                }
                if (serviceInst.getUsageCharges() != null && serviceInstance.getUsageChargeInstances() != null) {
                    updateCharges(serviceInstance.getUsageChargeInstances(), serviceInst.getUsageCharges());
                }

                subscription.updateAudit(currentUser);

                serviceInstanceService.serviceActivation(serviceInstance, null, null);
            } catch (Exception e) {
                log.error("failed to importSubscription", e);
                throw new SubscriptionServiceException(jaxbSubscription, serviceInst, e.getMessage());
            }

            log.info("File:" + fileName + ", typeEntity:ServiceInstance, index:" + i + ", code:" + serviceInst.getCode() + ", status:Actived");
        }

        log.info("accessPoints.size=" + checkSubscription.accessPoints.size());

        for (Access jaxbAccessPoint : checkSubscription.accessPoints) {
            org.meveo.model.mediation.Access access = new org.meveo.model.mediation.Access();
            access.setSubscription(subscription);
            access.setAccessUserId(jaxbAccessPoint.getAccessUserId());
            access.setStartDate(DateUtils.parseDateWithPattern(jaxbAccessPoint.getStartDate(), paramBean.getDateFormat()));
            access.setEndDate(DateUtils.parseDateWithPattern(jaxbAccessPoint.getEndDate(), paramBean.getDateFormat()));

            // if (jaxbAccessPoint.getCustomFields() != null) {
            // populateCustomFields(AccountLevelEnum.ACC, jaxbAccessPoint.getCustomFields().getCustomField(), subscription, "access");
            // }

            accessService.create(access);
            log.info("File:" + fileName + ", typeEntity:access, index:" + i + ", AccessUserId:" + access.getAccessUserId());
        }

        return 1;
    }

    private void updateCharges(List<? extends ChargeInstance> chargeInstances, List<Charge> charges) {
        for (ChargeInstance chargeInstance : chargeInstances) {
            Charge charge = null;
            for (Charge chargeToTest : charges) {
                if (chargeToTest.getCode().equals(chargeInstance.getCode())) {
                    charge = chargeToTest;
                    break;
                }
                if (charge != null) {
                    if (charge.getAmountWithoutTax() != null) {
                        chargeInstance.setAmountWithoutTax(new BigDecimal(charge.getAmountWithoutTax().replace(',', '.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
                        log.debug("set charge :" + charge.getCode() + ", amountWithoutTax:" + chargeInstance.getAmountWithoutTax());
                    }
                    if (charge.getAmountWithTax() != null) {
                        chargeInstance.setAmountWithTax(new BigDecimal(charge.getAmountWithTax().replace(',', '.')).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
                        log.debug("set charge :" + charge.getCode() + ", amountWithTax:" + chargeInstance.getAmountWithoutTax());
                    }
                    chargeInstance.setCriteria1(charge.getC1());
                    chargeInstance.setCriteria2(charge.getC2());
                    chargeInstance.setCriteria3(charge.getC3());
                }
            }
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void activateServices(CheckedSubscription checkSubscription, org.meveo.model.jaxb.subscription.Subscription subscrip) throws SubscriptionServiceException {
        if (checkSubscription.subscription != null && checkSubscription.subscription.getServiceInstances().size() > 0) {
            for (ServiceInstance serviceInstance : checkSubscription.subscription.getServiceInstances()) {
                try {
                    serviceInstanceService.serviceActivation(serviceInstance, null, null);
                } catch (Exception e) {
                    log.error("failed to activate service", e);
                    throw new SubscriptionServiceException(subscrip, null, e.getMessage());
                }
            }
        }
    }

}
