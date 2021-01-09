/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.crm.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.CacheKeyStr;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.SubscriptionImportHisto;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.jaxb.subscription.ErrorServiceInstance;
import org.meveo.model.jaxb.subscription.ErrorSubscription;
import org.meveo.model.jaxb.subscription.Subscription;
import org.meveo.model.jaxb.subscription.Subscriptions;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Title;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
public class AccountImportService extends ImportService {

    @Inject
    private WalletService walletService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private TitleService titleService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private SubscriptionImportService subscriptionImportService;

    @Inject
    private CountryService countryService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /** paramBeanFactory to instantiate adequate ParamBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    private Map<CacheKeyStr, Title> map = new HashMap<>();

    private Map<CacheKeyStr, TradingCountry> tradingCountryMap = new HashMap<>();

    private Map<CacheKeyStr, TradingLanguage> tradingLanguageMap = new HashMap<>();

    private Map<CacheKeyStr, BillingCycle> billingCycleMap = new HashMap<>();

    private Map<CacheKeyStr, CustomerAccount> customerAccountMap = new HashMap<>();

    private Subscriptions subscriptionsError;

    int nbSubscriptions;
    int nbSubscriptionsError;
    int nbSubscriptionsTerminated;
    int nbSubscriptionsIgnored;
    int nbSubscriptionsCreated;
    SubscriptionImportHisto subscriptionImportHisto;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public org.meveo.model.billing.BillingAccount importBillingAccount(org.meveo.model.jaxb.account.BillingAccount billAccount, CustomerAccount createdCustomerAccount)
            throws BusinessException, ImportWarningException {
        log.debug("create billingAccount found code:" + billAccount.getCode());

        org.meveo.model.billing.BillingAccount billingAccount = null;
        CustomerAccount customerAccount = createdCustomerAccount;
        BillingCycle billingCycle = null;

        billingCycle = billingCycleMap.get(new CacheKeyStr(currentUser.getProviderCode(), billAccount.getBillingCycle()));
        if (billingCycle == null) {
            billingCycle = billingCycleService.findByCode(billAccount.getBillingCycle());
            billingCycleMap.put(new CacheKeyStr(currentUser.getProviderCode(), billAccount.getBillingCycle()), billingCycle);
        }

        /**
         * try { billingCycle = billingCycleService.findByCode(billAccount.getBillingCycle()); } catch (Exception e) { log.warn("failed to find billingCycle", e); }
         */

        if (billingCycle == null) {
            throw new BusinessException("billingCycle not found " + billAccount.getBillingCycle());
        }

        if (customerAccount == null) {

            customerAccount = customerAccountMap.get(new CacheKeyStr(currentUser.getProviderCode(), billAccount.getCustomerAccountId()));

            if (customerAccount == null) {
                try {
                    customerAccount = customerAccountService.findByCode(billAccount.getCustomerAccountId());
                    if (customerAccount != null) {
                        Customer customer = customerAccount.getCustomer();
                        if (customer != null) {
                            customer.getSeller();
                        }
                    }

                    customerAccountMap.put(new CacheKeyStr(currentUser.getProviderCode(), billAccount.getCustomerAccountId()), customerAccount);
                } catch (Exception e) {
                    log.warn("failed to find customer account", e);
                }

                if (customerAccount == null) {
                    throw new BusinessException("Cannot find CustomerAccount");
                }
            }
        }

        billingAccountCheckError(billAccount);

        billingAccountCheckWarning(billAccount);

        billingAccount = new BillingAccount();
        billingAccount.setNextInvoiceDate(new Date());
        billingAccount.setBillingCycle(billingCycle);
        billingAccount.setCustomerAccount(customerAccount);
        billingAccount.setCode(billAccount.getCode());
        billingAccount.setSubscriptionDate(
            DateUtils.parseDateWithPattern(billAccount.getSubscriptionDate(), paramBeanFactory.getInstance().getProperty("connectorCRM.dateFormat", "yyyy-MM-dd")));
        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        billingAccount.setStatusDate(new Date());
        billingAccount.setDescription(billAccount.getDescription());

        Address address = new Address();
        if (billAccount.getAddress() != null) {
            address.setAddress1(billAccount.getAddress().getAddress1());
            address.setAddress2(billAccount.getAddress().getAddress2());
            address.setAddress3(billAccount.getAddress().getAddress3());
            address.setCity(billAccount.getAddress().getCity());
            address.setCountry(countryService.findByCode(billAccount.getAddress().getCountry()));
            address.setZipCode("" + billAccount.getAddress().getZipCode());
            address.setState(billAccount.getAddress().getState());
        }

        billingAccount.setAddress(address);
        billingAccount.setElectronicBilling("1".equalsIgnoreCase(billAccount.getElectronicBilling()));

        billingAccount.getContactInformationNullSafe().setEmail(billAccount.getEmail());
        billingAccount.setExternalRef1(billAccount.getExternalRef1());
        billingAccount.setExternalRef2(billAccount.getExternalRef2());
        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

        if (billAccount.getName() != null) {
            name.setFirstName(billAccount.getName().getFirstName());
            name.setLastName(billAccount.getName().getLastName());
            // name.setTitle(titleService.findByCode(billAccount.getName().getTitle().trim()));

            String tilteCode = billAccount.getName().getTitle();
            Title existingTitle = map.get(new CacheKeyStr(currentUser.getProviderCode(), tilteCode));
            if (existingTitle == null) {
                Title title = titleService.findByCode(tilteCode);
                map.put(new CacheKeyStr(currentUser.getProviderCode(), tilteCode), title);
                name.setTitle(title);
            } else {
                name.setTitle(existingTitle);
            }

            billingAccount.setName(name);
        }

        // billingAccount.setTradingCountry(tradingCountryService.findByCode(billAccount.getTradingCountryCode()));
        // billingAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(billAccount.getTradingLanguageCode()));

        TradingCountry tradingCountry = tradingCountryMap.get(new CacheKeyStr(currentUser.getProviderCode(), billAccount.getTradingCountryCode()));
        if (tradingCountry == null) {
            TradingCountry findByTradingCountryCode = tradingCountryService.findByCode(billAccount.getTradingCountryCode());
            tradingCountryMap.put(new CacheKeyStr(currentUser.getProviderCode(), billAccount.getTradingCountryCode()), findByTradingCountryCode);
            findByTradingCountryCode.getCountry().getCountryCode();
            billingAccount.setTradingCountry(findByTradingCountryCode);
        } else {
            billingAccount.setTradingCountry(tradingCountry);
        }

        String tradingLanguageCode = billAccount.getTradingLanguageCode();
        TradingLanguage tradingLanguage = tradingLanguageMap.get(new CacheKeyStr(currentUser.getProviderCode(), tradingLanguageCode));
        if (tradingLanguage == null) {
            TradingLanguage findByTradingLanguageCode = tradingLanguageService.findByTradingLanguageCode(tradingLanguageCode);
            findByTradingLanguageCode.getLanguage().getLanguageCode();
            tradingLanguageMap.put(new CacheKeyStr(currentUser.getProviderCode(), tradingLanguageCode), findByTradingLanguageCode);
            billingAccount.setTradingLanguage(findByTradingLanguageCode);
        } else {
            billingAccount.setTradingLanguage(tradingLanguage);
        }

        billingAccountService.create(billingAccount);

        if (billAccount.getCustomFields() != null) {
            populateCustomFields(billAccount.getCustomFields().getCustomField(), billingAccount);
        }

        return billingAccount;
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public org.meveo.model.billing.BillingAccount updateBillingAccount(org.meveo.model.jaxb.account.BillingAccount billingAccountDto)
            throws BusinessException, ImportWarningException {
        log.debug("update billingAccount found code:" + billingAccountDto.getCode());

        CustomerAccount customerAccount = null;
        BillingCycle billingCycle = null;

        try {
            billingCycle = billingCycleService.findByCode(billingAccountDto.getBillingCycle());
        } catch (Exception e) {
            log.warn("failed to find billingCycle", e);
        }

        if (billingCycle == null) {
            throw new BusinessException("Cannot find billingCycle with code=" + billingAccountDto.getBillingCycle());
        }

        try {
            customerAccount = customerAccountService.findByCode(billingAccountDto.getCustomerAccountId());
        } catch (Exception e) {
            log.warn("failed to find customerAccount", e);
        }

        if (customerAccount == null) {
            throw new BusinessException("Cannot find customerAccount with code=" + billingAccountDto.getCustomerAccountId());
        }

        billingAccountCheckError(billingAccountDto);

        billingAccountCheckWarning(billingAccountDto);

        org.meveo.model.billing.BillingAccount billingAccount = billingAccountService.findByCode(billingAccountDto.getCode());
        if (billingAccount == null) {
            throw new BusinessException("Cannot find billingAccount with code=" + billingAccountDto.getCode());
        }

        billingAccount.setNextInvoiceDate(new Date());
        billingAccount.setBillingCycle(billingCycle);
        billingAccount.setCustomerAccount(customerAccount);
        billingAccount.setSubscriptionDate(
            DateUtils.parseDateWithPattern(billingAccountDto.getSubscriptionDate(), paramBeanFactory.getInstance().getProperty("connectorCRM.dateFormat", "yyyy-MM-dd")));
        // billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        billingAccount.setStatusDate(new Date());
        billingAccount.setDescription(billingAccountDto.getDescription());

        Address address = new Address();
        if (billingAccountDto.getAddress() != null) {
            address.setAddress1(billingAccountDto.getAddress().getAddress1());
            address.setAddress2(billingAccountDto.getAddress().getAddress2());
            address.setAddress3(billingAccountDto.getAddress().getAddress3());
            address.setCity(billingAccountDto.getAddress().getCity());
            address.setCountry(countryService.findByCode(billingAccountDto.getAddress().getCountry()));
            address.setZipCode("" + billingAccountDto.getAddress().getZipCode());
            address.setState(billingAccountDto.getAddress().getState());
        }

        billingAccount.setAddress(address);
        billingAccount.setElectronicBilling("1".equalsIgnoreCase(billingAccountDto.getElectronicBilling()));
        billingAccount.getContactInformationNullSafe().setEmail(billingAccountDto.getEmail());
        billingAccount.setExternalRef1(billingAccountDto.getExternalRef1());
        billingAccount.setExternalRef2(billingAccountDto.getExternalRef2());
        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

        if (billingAccountDto.getName() != null) {
            name.setFirstName(billingAccountDto.getName().getFirstName());
            name.setLastName(billingAccountDto.getName().getLastName());
            name.setTitle(titleService.findByCode(billingAccountDto.getName().getTitle().trim()));
            billingAccount.setName(name);
        }

        billingAccount.setTradingCountry(tradingCountryService.findByCode(billingAccountDto.getTradingCountryCode()));
        billingAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(billingAccountDto.getTradingLanguageCode()));

        billingAccount = billingAccountService.updateNoCheck(billingAccount);

        if (billingAccountDto.getCustomFields() != null) {
            populateCustomFields(billingAccountDto.getCustomFields().getCustomField(), billingAccount);
        }

        return billingAccount;
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public UserAccount importUserAccount(org.meveo.model.billing.BillingAccount billingAccount, org.meveo.model.jaxb.account.BillingAccount billAccount,
            org.meveo.model.jaxb.account.UserAccount uAccount, Seller seller) throws BusinessException, ImportWarningException {
        userAccountCheckError(billAccount, uAccount);
        userAccountCheckWarning(billAccount, uAccount);
        UserAccount userAccount = new UserAccount();
        Address addressUA = new Address();

        if (uAccount.getAddress() != null) {
            addressUA.setAddress1(uAccount.getAddress().getAddress1());
            addressUA.setAddress2(uAccount.getAddress().getAddress2());
            addressUA.setAddress3(uAccount.getAddress().getAddress3());
            addressUA.setCity(uAccount.getAddress().getCity());
            addressUA.setCountry(countryService.findByCode(uAccount.getAddress().getCountry()));
            addressUA.setState(uAccount.getAddress().getState());
            addressUA.setZipCode("" + uAccount.getAddress().getZipCode());
        }

        userAccount.setAddress(addressUA);
        userAccount.setCode(uAccount.getCode());
        userAccount.setDescription(uAccount.getDescription());
        userAccount.setExternalRef1(uAccount.getExternalRef1());
        userAccount.setExternalRef2(uAccount.getExternalRef2());
        org.meveo.model.shared.Name nameUA = new org.meveo.model.shared.Name();

        if (uAccount.getName() != null) {
            nameUA.setFirstName(uAccount.getName().getFirstName());
            nameUA.setLastName(uAccount.getName().getLastName());
            // nameUA.setTitle(titleService.findByCode(uAccount.getName().getTitle().trim()));
            String tilteCode = billAccount.getName().getTitle();
            Title existingTitle = map.get(new CacheKeyStr(currentUser.getProviderCode(), tilteCode));
            if (existingTitle != null) {
                Title title = titleService.findByCode(tilteCode);
                map.put(new CacheKeyStr(currentUser.getProviderCode(), tilteCode), title);
                nameUA.setTitle(title);
            } else {
                nameUA.setTitle(existingTitle);
            }

            userAccount.setName(nameUA);
        }

        userAccount.setStatus(AccountStatusEnum.ACTIVE);
        userAccount.setStatusDate(new Date());


        userAccount.setBillingAccount(billingAccount);

        if (uAccount.getCustomFields() != null) {
            populateCustomFields(uAccount.getCustomFields().getCustomField(), userAccount);
        }

        userAccountService.create(userAccount);

        // create wallet
        WalletInstance wallet = new WalletInstance();
        wallet.setCode(WalletTemplate.PRINCIPAL);
        wallet.setCreated(new Date());
        wallet.setUserAccount(userAccount);
        userAccount.setWallet(wallet);
        walletService.create(wallet);

        Subscriptions subscriptions = uAccount.getSubscriptions();

        if (subscriptions != null) {
            List<Subscription> subscriptionList = subscriptions.getSubscription();
            int i = 0;
            for (Subscription jaxbSubscription : subscriptionList) {
                try {
                    CheckedSubscription checkSubscription = subscriptionCheckError(jaxbSubscription);
                    if (checkSubscription == null) {
                        continue;
                    }
                    checkSubscription.setUserAccount(userAccount);
                    checkSubscription.setSeller(seller);
                    nbSubscriptionsCreated += subscriptionImportService.importSubscription(checkSubscription, jaxbSubscription, "", i);
                } catch (ImportIgnoredException ie) {

                } catch (SubscriptionServiceException se) {

                } catch (Exception e) {

                }
            }

        }

        return userAccount;

    }

    private CheckedSubscription subscriptionCheckError(org.meveo.model.jaxb.subscription.Subscription jaxbSubscription) {
        CheckedSubscription checkSubscription = new CheckedSubscription();

        if (StringUtils.isBlank(jaxbSubscription.getCode())) {
            createSubscriptionError(jaxbSubscription, "Code is null.");
            return null;
        }

        if (StringUtils.isBlank(jaxbSubscription.getUserAccountId())) {
            createSubscriptionError(jaxbSubscription, "UserAccountId is null.");
            return null;
        }

        if (StringUtils.isBlank(jaxbSubscription.getOfferCode())) {
            createSubscriptionError(jaxbSubscription, "OfferCode is null.");
            return null;
        }

        if (StringUtils.isBlank(jaxbSubscription.getSubscriptionDate())) {
            createSubscriptionError(jaxbSubscription, "SubscriptionDate is null.");
            return null;
        }

        if (jaxbSubscription.getStatus() == null || StringUtils.isBlank(jaxbSubscription.getStatus().getValue())
                || ("ACTIVE" + "TERMINATED" + "CANCELED" + "SUSPENDED").indexOf(jaxbSubscription.getStatus().getValue()) == -1) {
            createSubscriptionError(jaxbSubscription, "Status is null, or not in { ACTIVE, TERMINATED, CANCELED, SUSPENDED }");

            return null;
        }
        /**
         * OfferTemplate offerTemplate = null;
         * 
         * offerTemplate = offerMap.get(jaxbSubscription.getOfferCode().toUpperCase()); if (offerTemplate == null) { try { offerTemplate =
         * offerTemplateService.findByCode(jaxbSubscription.getOfferCode().toUpperCase(), DateUtils.parseDateWithPattern(jaxbSubscription.getSubscriptionDate(),
         * paramBean.getProperty("connectorCRM.dateFormat", "dd/MM/yyyy"))); offerMap.put(jaxbSubscription.getOfferCode().toUpperCase(), offerTemplate); } catch (Exception e) {
         * log.warn("failed to find offerTemplate ",e); } }
         * 
         * if (offerTemplate == null) { createSubscriptionError(jaxbSubscription, "Cannot find OfferTemplate with code=" + jaxbSubscription.getOfferCode() + " / "+
         * jaxbSubscription.getSubscriptionDate()); return null; } checkSubscription.offerTemplate = offerTemplate;
         * 
         * UserAccount userAccount = null; userAccount = userAccountMap.get(jaxbSubscription.getUserAccountId()); if (userAccount == null) { try { userAccount =
         * userAccountService.findByCode(jaxbSubscription.getUserAccountId()); userAccountMap.put(jaxbSubscription.getUserAccountId(), userAccount); } catch (Exception e) {
         * log.error("error generated while getting user account",e); } }
         * 
         * if (userAccount == null) { createSubscriptionError(jaxbSubscription, "Cannot find UserAccount entity=" + jaxbSubscription.getUserAccountId()); return null; }
         * checkSubscription.userAccount = userAccount;
         * 
         * try { //checkSubscription.subscription = subscriptionService.findByCode(jaxbSubscription.getCode()); } catch (Exception e) { log.error("failed to find subscription",e);
         * }
         * 
         * if (!"ACTIVE".equals(jaxbSubscription.getStatus().getValue()) && checkSubscription.subscription == null) { createSubscriptionError(jaxbSubscription, "Cannot find
         * subscription with code=" + jaxbSubscription.getCode()); return null; }
         */
        if ("ACTIVE".equals(jaxbSubscription.getStatus().getValue())) {
            if (jaxbSubscription.getServices() == null || jaxbSubscription.getServices().getServiceInstance() == null
                    || jaxbSubscription.getServices().getServiceInstance().isEmpty()) {
                createSubscriptionError(jaxbSubscription, "Cannot create subscription without services");
                return null;
            }

            for (org.meveo.model.jaxb.subscription.ServiceInstance serviceInst : jaxbSubscription.getServices().getServiceInstance()) {
                if (serviceInstanceCheckError(jaxbSubscription, serviceInst)) {
                    return null;
                }

                checkSubscription.getServiceInstances().add(serviceInst);
            }

            if (jaxbSubscription.getAccesses() != null) {
                for (org.meveo.model.jaxb.subscription.Access jaxbAccess : jaxbSubscription.getAccesses().getAccess()) {
                    if (accessCheckError(jaxbSubscription, jaxbAccess)) {
                        return null;
                    }

                    checkSubscription.getAccessPoints().add(jaxbAccess);
                }
            }
        }

        return checkSubscription;
    }

    private boolean serviceInstanceCheckError(org.meveo.model.jaxb.subscription.Subscription subscrip, org.meveo.model.jaxb.subscription.ServiceInstance serviceInst) {

        if (StringUtils.isBlank(serviceInst.getCode())) {
            createServiceInstanceError(subscrip, serviceInst, "code is null");
            return true;
        }

        if (StringUtils.isBlank(serviceInst.getSubscriptionDate())) {
            createSubscriptionError(subscrip, "SubscriptionDate is null");
            return true;
        }

        return false;
    }

    private boolean accessCheckError(org.meveo.model.jaxb.subscription.Subscription subscrip, org.meveo.model.jaxb.subscription.Access access) {

        if (StringUtils.isBlank(access.getAccessUserId())) {
            createSubscriptionError(subscrip, "AccessUserId is null");
            return true;
        }

        return false;
    }

    private void createServiceInstanceError(org.meveo.model.jaxb.subscription.Subscription subscrip, org.meveo.model.jaxb.subscription.ServiceInstance serviceInst, String cause) {
        ErrorServiceInstance errorServiceInstance = new ErrorServiceInstance();
        errorServiceInstance.setCause(cause);
        errorServiceInstance.setCode(serviceInst.getCode());
        errorServiceInstance.setSubscriptionCode(subscrip.getCode());

        if (!subscriptionsError.getSubscription().contains(subscrip)) {
            subscriptionsError.getSubscription().add(subscrip);
        }

        if (subscriptionsError.getErrors() == null) {
            subscriptionsError.setErrors(new org.meveo.model.jaxb.subscription.Errors());
        }

        subscriptionsError.getErrors().getErrorServiceInstance().add(errorServiceInstance);
    }

    private void createSubscriptionError(org.meveo.model.jaxb.subscription.Subscription subscrip, String cause) {
        log.error(cause);

        String generateFullCrmReject = paramBeanFactory.getInstance().getProperty("connectorCRM.generateFullCrmReject", "true");
        ErrorSubscription errorSubscription = new ErrorSubscription();
        errorSubscription.setCause(cause);
        errorSubscription.setCode(subscrip.getCode());

        if (!subscriptionsError.getSubscription().contains(subscrip) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            subscriptionsError.getSubscription().add(subscrip);
        }

        if (subscriptionsError.getErrors() == null) {
            subscriptionsError.setErrors(new org.meveo.model.jaxb.subscription.Errors());
        }

        subscriptionsError.getErrors().getErrorSubscription().add(errorSubscription);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUserAccount(org.meveo.model.billing.BillingAccount billingAccount, org.meveo.model.jaxb.account.BillingAccount billAccount,
            org.meveo.model.jaxb.account.UserAccount userAccountDto) throws BusinessException, ImportWarningException {
        userAccountCheckError(billAccount, userAccountDto);
        userAccountCheckWarning(billAccount, userAccountDto);

        UserAccount userAccount = userAccountService.findByCode(userAccountDto.getCode());
        if (userAccount == null) {
            throw new BusinessException("Cannot find userAccount with code=" + userAccountDto.getCode());
        }

        userAccount.setBillingAccount(billingAccount);
        Address addressUA = new Address();

        if (userAccountDto.getAddress() != null) {
            addressUA.setAddress1(userAccountDto.getAddress().getAddress1());
            addressUA.setAddress2(userAccountDto.getAddress().getAddress2());
            addressUA.setAddress3(userAccountDto.getAddress().getAddress3());
            addressUA.setCity(userAccountDto.getAddress().getCity());
            addressUA.setCountry(countryService.findByCode(userAccountDto.getAddress().getCountry()));
            addressUA.setState(userAccountDto.getAddress().getState());
            addressUA.setZipCode("" + userAccountDto.getAddress().getZipCode());
        }

        userAccount.setAddress(addressUA);
        userAccount.setDescription(userAccountDto.getDescription());
        userAccount.setExternalRef1(userAccountDto.getExternalRef1());
        userAccount.setExternalRef2(userAccountDto.getExternalRef2());
        org.meveo.model.shared.Name nameUA = new org.meveo.model.shared.Name();

        if (userAccountDto.getName() != null) {
            nameUA.setFirstName(userAccountDto.getName().getFirstName());
            nameUA.setLastName(userAccountDto.getName().getLastName());
            nameUA.setTitle(titleService.findByCode(userAccountDto.getName().getTitle().trim()));
            userAccount.setName(nameUA);
        }

        // userAccount.setStatus(AccountStatusEnum.ACTIVE);
        userAccount.setStatusDate(new Date());

        userAccount = userAccountService.updateNoCheck(userAccount);

        if (userAccountDto.getCustomFields() != null) {
            populateCustomFields(userAccountDto.getCustomFields().getCustomField(), userAccount);
        }
    }

    private boolean billingAccountCheckError(org.meveo.model.jaxb.account.BillingAccount billAccount) throws BusinessException {
        /*
         * if (StringUtils.isBlank(billAccount.getExternalRef1())) { createBillingAccountError(billAccount, "ExternalRef1 is null"); return true; } if
         * (StringUtils.isBlank(billAccount.getBillingCycle())) { createBillingAccountError(billAccount, "BillingCycle is null"); return true; } if (billAccount.getName() == null)
         * { createBillingAccountError(billAccount, "Name is null"); return true; } if (StringUtils.isBlank(billAccount.getName().getTitle())) {
         * createBillingAccountError(billAccount, "Title is null"); return true; } if (StringUtils.isBlank(billAccount.getPaymentMethod()) || ("DIRECTDEBIT" + "CHECK" + "TIP" +
         * "WIRETRANSFER").indexOf(billAccount .getPaymentMethod()) == -1) { createBillingAccountError(billAccount,
         * "PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}" ); return true; }
         */
        if ("DIRECTDEBIT".equals(billAccount.getPaymentMethod())) {
            if (billAccount.getBankCoordinates() == null) {
                throw new BusinessException("BankCoordinates is null.");
            }

            if (StringUtils.isBlank(billAccount.getBankCoordinates().getAccountName())) {
                throw new BusinessException("BankCoordinates.AccountName is null.");
            }

            if (StringUtils.isBlank(billAccount.getBankCoordinates().getAccountNumber())) {
                throw new BusinessException("BankCoordinates.AccountNumber is null.");
            }

            if (StringUtils.isBlank(billAccount.getBankCoordinates().getBankCode())) {
                throw new BusinessException("BankCoordinates.BankCode is null.");
            }

            if (StringUtils.isBlank(billAccount.getBankCoordinates().getBranchCode())) {
                throw new BusinessException("BankCoordinates.BranchCode is null.");
            }
        }
        /*
         * if (billAccount.getAddress() == null || StringUtils.isBlank(billAccount.getAddress().getZipCode())) { createBillingAccountError(billAccount, "ZipCode is null"); return
         * true; } if (billAccount.getAddress() == null || StringUtils.isBlank(billAccount.getAddress().getCity())) { createBillingAccountError(billAccount, "City is null"); return
         * true; } if (billAccount.getAddress() == null || StringUtils.isBlank(billAccount.getAddress().getCountry())) { createBillingAccountError(billAccount, "Country is null");
         * return true; }
         */
        return false;
    }

    private boolean userAccountCheckError(org.meveo.model.jaxb.account.BillingAccount billAccount, org.meveo.model.jaxb.account.UserAccount uAccount) {
        /*
         * if (StringUtils.isBlank(uAccount.getExternalRef1())) { createUserAccountError(billAccount, uAccount, "ExternalRef1 is null"); return true; } if (uAccount.getName() ==
         * null) { createUserAccountError(billAccount, uAccount, "Name is null"); return true; } if (StringUtils.isBlank(uAccount.getName().getTitle())) {
         * createUserAccountError(billAccount, uAccount, "Title is null"); return true; } if (billAccount.getAddress() == null ||
         * StringUtils.isBlank(uAccount.getAddress().getZipCode())) { createUserAccountError(billAccount, uAccount, "ZipCode is null"); return true; } if (billAccount.getAddress()
         * == null || StringUtils.isBlank(uAccount.getAddress().getCity())) { createUserAccountError(billAccount, uAccount, "City is null"); return true; } if
         * (billAccount.getAddress() == null || StringUtils.isBlank(uAccount.getAddress().getCountry())) { createUserAccountError(billAccount, uAccount, "Country is null"); return
         * true; }
         */

        return false;
    }

    private void billingAccountCheckWarning(org.meveo.model.jaxb.account.BillingAccount billAccount) throws ImportWarningException {
        // if ("PRO".equals(customer.getCustomerCategory()) &&
        // StringUtils.isBlank(billAccount.getCompany())) {
        // createBillingAccountWarning(billAccount, "company is null");
        // isWarning = true;
        // }
        // if ("PART".equals(customer.getCustomerCategory()) &&
        // (billAccount.getName() == null ||
        // StringUtils.isBlank(billAccount.getName().getFirstname()))) {
        // createBillingAccountWarning(billAccount, "name is null");
        // isWarning = true;
        // }

        if ("TRUE".equalsIgnoreCase(billAccount.getElectronicBilling()) && StringUtils.isBlank(billAccount.getEmail())) {
            throw new ImportWarningException("Email is null");
        }

        if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod())) && billAccount.getBankCoordinates() == null) {
            throw new ImportWarningException("BankCoordinates is null");
        }

        if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod())) && billAccount.getBankCoordinates() != null
                && StringUtils.isBlank(billAccount.getBankCoordinates().getBranchCode())) {
            throw new ImportWarningException("BankCoordinates.BranchCode is null");
        }

        if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod())) && billAccount.getBankCoordinates() != null
                && StringUtils.isBlank(billAccount.getBankCoordinates().getAccountNumber())) {
            throw new ImportWarningException("BankCoordinates.AccountNumber is null");
        }

        if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod())) && billAccount.getBankCoordinates() != null
                && StringUtils.isBlank(billAccount.getBankCoordinates().getBankCode())) {
            throw new ImportWarningException("BankCoordinates.BankCode is null");
        }

        if (("DIRECTDEBIT".equalsIgnoreCase(billAccount.getPaymentMethod())) && billAccount.getBankCoordinates() != null
                && StringUtils.isBlank(billAccount.getBankCoordinates().getKey())) {
            throw new ImportWarningException("BankCoordinates.Key is null");
        }
    }

    private boolean userAccountCheckWarning(org.meveo.model.jaxb.account.BillingAccount billAccount, org.meveo.model.jaxb.account.UserAccount uAccount) {
        boolean isWarning = false;
        // if ("PRO".equals(customer.getCustomerCategory()) &&
        // StringUtils.isBlank(uAccount.getCompany())) {
        // createUserAccountWarning(billAccount, uAccount, "company is null");
        // isWarning = true;
        // }
        // if ("PART".equals(customer.getCustomerCategory()) &&
        // (uAccount.getName() == null ||
        // StringUtils.isBlank(uAccount.getName().getFirstname()))) {
        // createUserAccountWarning(billAccount, uAccount, "name is null");
        // isWarning = true;
        // }

        return isWarning;
    }
}
