/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.DateUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ApplicationChgStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.Wallet;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.local.ChargeApplicationServiceLocal;
import org.meveo.service.billing.local.ChargeInstanceServiceLocal;
import org.meveo.service.billing.local.OneShotChargeInstanceServiceLocal;
import org.meveo.service.billing.local.RatedTransactionServiceLocal;
import org.meveo.service.billing.local.RecurringChargeInstanceServiceLocal;
import org.meveo.service.billing.local.ServiceInstanceServiceLocal;
import org.meveo.service.billing.local.SubscriptionServiceLocal;
import org.meveo.service.catalog.local.ServiceTemplateServiceLocal;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;

/**
 * Service instance service implementation.
 * 
 * @author R.AITYAAZZA
 */
@Stateless
@Name("serviceInstanceService")
@AutoCreate
public class ServiceInstanceService extends BusinessService<ServiceInstance> implements ServiceInstanceServiceLocal {

    @In
    private SubscriptionServiceLocal subscriptionService;

    @In
    private ServiceTemplateServiceLocal serviceTemplateService;

    @In
    private RecurringChargeInstanceServiceLocal recurringChargeInstanceService;

    @In
    private ChargeInstanceServiceLocal<ChargeInstance> chargeInstanceService;

    @In
    private OneShotChargeInstanceServiceLocal oneShotChargeInstanceService;

    @In
    private ChargeApplicationServiceLocal chargeApplicationService;
    
    @In
    private CustomerAccountServiceLocal customerAccountService;
    
    @In
    private RatedTransactionServiceLocal ratedTransactionService;

    public ServiceInstance findByCodeAndSubscription(String code, String subscriptionCode) {
        ServiceInstance chargeInstance = null;
        try {
            log.debug("start of find {0} by code (code={1}) ..", "ServiceInstance", code);
            QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.subscription.code", "=", subscriptionCode, true);
            chargeInstance = (ServiceInstance) qb.getQuery(em).getSingleResult();
            log.debug("end of find {0} by code (code={1}). Result found={2}.", "ServiceInstance", code,
                    chargeInstance != null);
        } catch (NoResultException nre) {
            log.debug("findByCodeAndSubscription : aucun service n'a ete trouve");
        } catch (Exception e) {
            log.error("findByCodeAndSubscription error=#0 ", e.getMessage());
        }

        return chargeInstance;
    }

    public void serviceInstanciation(ServiceInstance serviceInstance, User creator)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
    	serviceInstanciation(serviceInstance, creator, null,null);
    }
    public void serviceInstanciation(ServiceInstance serviceInstance, User creator, BigDecimal subscriptionAmount, 
    		BigDecimal terminationAmount)
    throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        log.debug("serviceInstanciation serviceID=#0", serviceInstance.getId());

        String serviceCode = serviceInstance.getServiceTemplate().getCode();
        Subscription subscription = subscriptionService.findByCode(serviceInstance.getSubscription().getCode());
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("subscription does not exist. code="
                    + serviceInstance.getSubscription().getCode());
        }
        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
                || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new IncorrectSusbcriptionException("subscription is not active");
        }
        ServiceInstance serviceInst = findByCodeAndSubscription(serviceCode, subscription.getCode());
        if (serviceInst != null) {
            throw new IncorrectServiceInstanceException("service instance already created. service Code="
                    + serviceInstance.getCode() + ",subscription Code" + subscription.getCode());
        }

        if (serviceInstance.getSubscriptionDate()==null){
            serviceInstance.setSubscriptionDate(new Date());
        }
        
        serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
        serviceInstance.setStatusDate(new Date());
        serviceInstance.setCode(serviceCode);
        create(serviceInstance, creator, subscription.getProvider());
        ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();
        for (RecurringChargeTemplate recurringChargeTemplate : serviceTemplate.getRecurringCharges()) {
            chargeInstanceService.recurringChargeInstanciation(serviceInstance, recurringChargeTemplate.getCode(),
                    serviceInstance.getSubscriptionDate(), creator);
        }

        for (OneShotChargeTemplate subscriptionChargeTemplate : serviceTemplate.getSubscriptionCharges()) {
            oneShotChargeInstanceService.oneShotChargeInstanciation(serviceInstance.getSubscription(), serviceInstance,
                    subscriptionChargeTemplate, serviceInstance.getSubscriptionDate(), subscriptionAmount, null, 1, creator);
        }

        for (OneShotChargeTemplate terminationChargeTemplate : serviceTemplate.getTerminationCharges()) {
            oneShotChargeInstanceService.oneShotChargeInstanciation(serviceInstance.getSubscription(), serviceInstance,
                    terminationChargeTemplate, serviceInstance.getSubscriptionDate(), terminationAmount, null, 1, creator);
        }
    }

    public void serviceActivation(ServiceInstance serviceInstance, BigDecimal amountWithoutTax,
            BigDecimal amountWithoutTax2, User creator) throws IncorrectSusbcriptionException,
            IncorrectServiceInstanceException, BusinessException {
        Subscription subscription = serviceInstance.getSubscription();

        // String serviceCode = serviceInstance.getCode();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("subscription does not exist. code="
                    + serviceInstance.getSubscription().getCode());
        }
        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED
                || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new IncorrectServiceInstanceException("subscription is " + subscription.getStatus());
        }

        if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE
                || serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
            throw new IncorrectServiceInstanceException("serviceInstance is " + subscription.getStatus());
        }

        if (serviceInstance.getSubscriptionDate()==null){
            serviceInstance.setSubscriptionDate(new Date());
        }
        
        int agreementMonthTerm = 0;
        // activate recurring charges
        log.debug("serviceActivation:serviceInstance.getRecurrringChargeInstances.size=#0", serviceInstance
                .getRecurringChargeInstances().size());
        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
        	
            // application of subscription prorata
            recurringChargeInstanceService.recurringChargeApplication(recurringChargeInstance, creator);
            recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            recurringChargeInstance.setStatusDate(new Date());
            recurringChargeInstanceService.update(recurringChargeInstance);
            if (recurringChargeInstance.getRecurringChargeTemplate().getDurationTermInMonth() != null) {
                if (recurringChargeInstance.getRecurringChargeTemplate().getDurationTermInMonth() > agreementMonthTerm) {
                    agreementMonthTerm = recurringChargeInstance.getRecurringChargeTemplate().getDurationTermInMonth();
                }
            }

        }

        // set end Agreement Date
        Date serviceEngAgreementDate = null;
        if (agreementMonthTerm > 0) {
            serviceEngAgreementDate = DateUtils.addMonthsToDate(subscription.getSubscriptionDate(), agreementMonthTerm);
        }

        if ((serviceEngAgreementDate == null)) {
            serviceInstance.setEndAgrementDate(subscription.getEndAgrementDate());
        } else {
            serviceInstance.setEndAgrementDate(serviceEngAgreementDate);
        }

        // apply subscription charges
        log.debug("serviceActivation:serviceInstance.getSubscriptionChargeInstances.size=#0", serviceInstance
                .getSubscriptionChargeInstances().size());
        for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getSubscriptionChargeInstances()) {
            oneShotChargeInstanceService.oneShotChargeApplication(subscription, oneShotChargeInstance, serviceInstance
                    .getSubscriptionDate(), serviceInstance.getQuantity(), creator);
            oneShotChargeInstance.setStatus(InstanceStatusEnum.CLOSED);
            oneShotChargeInstance.setStatusDate(new Date());
            oneShotChargeInstanceService.update(oneShotChargeInstance);
        }

        serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
        serviceInstance.setStatusDate(new Date());
        update(serviceInstance, creator);

    }

    public void terminateService(ServiceInstance serviceInstance, Date terminationDate,
            SubscriptionTerminationReason terminationReason, User user) throws IncorrectSusbcriptionException,
            IncorrectServiceInstanceException, BusinessException {
        terminateService(serviceInstance, terminationDate, terminationReason.isApplyAgreement(), terminationReason
                .isApplyReimbursment(), terminationReason.isApplyTerminationCharges(), user);
        serviceInstance.setSubscriptionTerminationReason(terminationReason);
        update(serviceInstance, user);
    }

    public void terminateService(ServiceInstance serviceInstance, Date terminationDate, boolean applyAgreement,
            boolean applyReimbursment, boolean applyTerminationCharges, User user)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        log.info("terminateService terminationDate=#0,serviceInstanceId=#1", terminationDate, serviceInstance.getId());
        if (terminationDate == null) {
            terminationDate = new Date();
        }

        String serviceCode = serviceInstance.getCode();
        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
                    + serviceInstance.getCode());
        }
        if (serviceInstance.getStatus() == InstanceStatusEnum.INACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is inactive. service Code=" + serviceCode
                    + ",subscription Code" + subscription.getCode());
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            Date chargeDate = recurringChargeInstance.getChargeDate();
            Date nextChargeDate = recurringChargeInstance.getNextChargeDate();
            Date storedNextChargeDate = recurringChargeInstance.getNextChargeDate();

            if (recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvance()) {
                nextChargeDate = recurringChargeInstance.getChargeDate();
            }

            if (applyAgreement) {
                Date endAgrementDate = serviceInstance.getEndAgrementDate();
                if (endAgrementDate != null && terminationDate.before(endAgrementDate)) {
                    if (endAgrementDate.after(nextChargeDate)) {
                        chargeApplicationService.applyChargeAgreement(recurringChargeInstance, recurringChargeInstance
                                .getRecurringChargeTemplate(), user);
                    }

                }
            }
            if (applyReimbursment) {
                Date endAgrementDate = recurringChargeInstance.getServiceInstance().getEndAgrementDate();
                if (applyAgreement && endAgrementDate != null && terminationDate.before(endAgrementDate)) {
                    if (endAgrementDate.before(nextChargeDate)) {
                        recurringChargeInstance.setTerminationDate(endAgrementDate);
                        chargeApplicationService.applyReimbursment(recurringChargeInstance, user);
                    }

                } else if (terminationDate.before(nextChargeDate)) {
                    recurringChargeInstance.setTerminationDate(terminationDate);
                    chargeApplicationService.applyReimbursment(recurringChargeInstance, user);
                }

            }

            recurringChargeInstance.setChargeDate(chargeDate);
            recurringChargeInstance.setNextChargeDate(storedNextChargeDate);
            recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
            recurringChargeInstance.setStatusDate(new Date());
            chargeInstanceService.update(recurringChargeInstance);

        }
        if (applyTerminationCharges) {
            for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getTerminationChargeInstances()) {
                oneShotChargeInstanceService.oneShotChargeApplication(subscription, oneShotChargeInstance,
                        terminationDate, serviceInstance.getQuantity(), user);
            }
        }

        serviceInstance.setTerminationDate(terminationDate);
        serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
        serviceInstance.setStatusDate(new Date());
        update(serviceInstance, user);
        boolean termineSubscription = true;
        for (ServiceInstance srv : subscription.getServiceInstances()) {
            if (srv.getStatus() != InstanceStatusEnum.TERMINATED) {
                termineSubscription = false;
            }
        }
        if (termineSubscription) {
            subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
            subscription.setStatusDate(new Date());
            subscription.setTerminationDate(new Date());
            subscriptionService.update(subscription);
        }
        CustomerAccount customerAccount=serviceInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount();
        if(customerAccountService.isAllServiceInstancesTerminated(customerAccount)){
        	for(BillingAccount ba : customerAccount.getBillingAccounts()){
        		for(UserAccount ua:ba.getUsersAccounts()){
        			Wallet wallet=ua.getWallet();
        			for(RatedTransaction rt:wallet.getRatedTransactions()){
        				rt.setDoNotTriggerInvoicing(false);
        				ratedTransactionService.update(rt);
        			}
        		}
        	}
        }
        
    }

    public void updateTerminationMode(ServiceInstance serviceInstance, Date terminationDate, User user)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        log.info("updateTerminationMode terminationDate=#0,serviceInstanceId=#1", terminationDate, serviceInstance
                .getId());

        SubscriptionTerminationReason newReason = serviceInstance.getSubscriptionTerminationReason();

        log
                .info(
                        "updateTerminationMode terminationDate=#0,serviceInstanceId=#1,newApplyReimbursment=#2,newApplyAgreement=#3,newApplyTerminationCharges=#4",
                        terminationDate, serviceInstance.getId(), newReason.isApplyReimbursment(), newReason
                                .isApplyAgreement(), newReason.isApplyTerminationCharges());

        String serviceCode = serviceInstance.getCode();
        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
                    + serviceInstance.getCode());
        }

        if (serviceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
            throw new IncorrectServiceInstanceException("service instance is not terminated. service Code="
                    + serviceCode + ",subscription Code" + subscription.getCode());
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {

            chargeApplicationService.cancelChargeApplications(recurringChargeInstance.getId(),
                    ChargeApplicationModeEnum.AGREEMENT, user);

            chargeApplicationService.cancelChargeApplications(recurringChargeInstance.getId(),
                    ChargeApplicationModeEnum.REIMBURSMENT, user);

        }
        for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getTerminationChargeInstances()) {
            chargeApplicationService.cancelOneShotChargeApplications(oneShotChargeInstance,
                    OneShotChargeTemplateTypeEnum.TERMINATION, user);
        }

        terminateService(serviceInstance, terminationDate, newReason.isApplyAgreement(), newReason
                .isApplyReimbursment(), newReason.isApplyTerminationCharges(), user);

    }

    public void serviceSusupension(ServiceInstance serviceInstance, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        String serviceCode = serviceInstance.getCode();

        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
                    + serviceCode);
        }

        if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is not active. service Code=" + serviceCode
                    + ",subscription Code" + subscription.getCode());
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                chargeInstanceService.recurringChargeDeactivation(recurringChargeInstance.getId(), terminationDate,
                        updater);
            }

        }
        serviceInstance.setStatus(InstanceStatusEnum.SUSPENDED);
        serviceInstance.setStatusDate(new Date());
        serviceInstance.setTerminationDate(terminationDate);
        update(serviceInstance, updater);
    }

    public void serviceReactivation(ServiceInstance serviceInstance, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        String serviceCode = serviceInstance.getCode();
        Date subscriptionDate = new Date();

        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
                    + serviceInstance.getCode());
        }
        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceCode);
        if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is already active. service Code="
                    + serviceCode + ",subscription Code" + subscription.getCode());
        }

        serviceInstance.setStatus(InstanceStatusEnum.ACTIVE);
        serviceInstance.setStatusDate(new Date());
        serviceInstance.setSubscriptionDate(subscriptionDate);
        serviceInstance.setDescription(serviceTemplate.getDescription());
        serviceInstance.setTerminationDate(null);

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            if (recurringChargeInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
                chargeInstanceService.recurringChargeReactivation(serviceInstance, subscription.getCode(),
                        subscriptionDate, updater);
            }

        }

        update(serviceInstance, updater);
    }

    public void cancelService(ServiceInstance serviceInstance, User updater) throws IncorrectServiceInstanceException,
            BusinessException {

        String serviceCode = serviceInstance.getCode();
        String subscriptionCode = serviceInstance.getSubscription().getCode();

        if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is not active. service Code=" + serviceCode
                    + ",subscription Code" + subscriptionCode);
        }
        List<ChargeInstance> chargeInstances = new ArrayList<ChargeInstance>();
        chargeInstances.addAll(serviceInstance.getRecurringChargeInstances());
        chargeInstances.addAll(serviceInstance.getSubscriptionChargeInstances());
        chargeInstances.addAll(serviceInstance.getTerminationChargeInstances());
        for (ChargeInstance chargeInstance : chargeInstances) {
                chargeInstanceService.chargeInstanceCancellation(chargeInstance.getId(), updater);
            
            for (ChargeApplication chargeApplication : chargeInstance.getChargeApplications()) {
                if (chargeApplication.getStatus() != ApplicationChgStatusEnum.TREATED) {
                    chargeApplication.setStatus(ApplicationChgStatusEnum.CANCELED);
                    chargeApplication.setStatusDate(new Date());

                }
                for (RatedTransaction ratedTransaction : chargeApplication.getRatedTransactions()) {
                    if (ratedTransaction.getBillingRun() == null
                            || (ratedTransaction.getBillingRun() != null && ratedTransaction.getBillingRun()
                                    .getStatus() == BillingRunStatusEnum.CANCELED)) {
                        ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
                        chargeApplication.setStatus(ApplicationChgStatusEnum.CANCELED);
                        chargeApplication.setStatusDate(new Date());
                    }
                }
            }
        }
        serviceInstance.setStatus(InstanceStatusEnum.CANCELED);
        serviceInstance.setStatusDate(new Date());
        update(serviceInstance, updater);
    }

    public void serviceTermination(ServiceInstance serviceInstance, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        String serviceCode = serviceInstance.getCode();
        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
                    + serviceInstance.getCode());
        }
        if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is not active. service Code=" + serviceCode
                    + ",subscription Code" + subscription.getCode());
        }

        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                chargeInstanceService.recurringChargeDeactivation(recurringChargeInstance.getId(), terminationDate,
                        updater);
            }
            recurringChargeInstance.setTerminationDate(terminationDate);
            chargeApplicationService.chargeTermination(recurringChargeInstance, updater);

        }

        for (OneShotChargeInstance oneShotChargeInstance : serviceInstance.getTerminationChargeInstances()) {
            oneShotChargeInstanceService.oneShotChargeApplication(subscription, oneShotChargeInstance, terminationDate,
                    serviceInstance.getQuantity(), updater);
        }

        serviceInstance.setTerminationDate(terminationDate);
        serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
        serviceInstance.setStatusDate(new Date());
        update(serviceInstance, updater);
    }

    public void serviceCancellation(ServiceInstance serviceInstance, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {

        String serviceCode = serviceInstance.getCode();
        String subscriptionCode = serviceInstance.getSubscription().getCode();
        Subscription subscription = serviceInstance.getSubscription();
        if (subscription == null) {
            throw new IncorrectSusbcriptionException("service Instance does not have subscrption . serviceCode="
                    + serviceInstance.getCode());
        }
        if (serviceInstance.getStatus() != InstanceStatusEnum.ACTIVE) {
            throw new IncorrectServiceInstanceException("service instance is not active. service Code=" + serviceCode
                    + ",subscription Code" + subscriptionCode);
        }
        for (RecurringChargeInstance recurringChargeInstance : serviceInstance.getRecurringChargeInstances()) {
            if (recurringChargeInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                chargeInstanceService.recurringChargeDeactivation(recurringChargeInstance.getId(), terminationDate,
                        updater);
            }
            recurringChargeInstance.setTerminationDate(terminationDate);
            chargeApplicationService.chargeTermination(recurringChargeInstance, updater);
        }
        serviceInstance.setTerminationDate(terminationDate);
        serviceInstance.setStatus(InstanceStatusEnum.TERMINATED);
        serviceInstance.setStatusDate(new Date());
        update(serviceInstance, updater);
    }

}
