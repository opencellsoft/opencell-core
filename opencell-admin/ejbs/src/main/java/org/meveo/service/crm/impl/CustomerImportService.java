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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.*;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class CustomerImportService extends ImportService {

    @Inject
    private CreditCategoryService creditCategoryService;

    @Inject
    private SellerService sellerService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerBrandService customerBrandService;

    @Inject
    private CustomerCategoryService customerCategoryService;

    @Inject
    private TitleService titleService;

    @Inject
    private CountryService countryService;

    @Inject
    private CustomerAccountService customerAccountService;

    private Map<String, Title> map = new HashMap<>();

    private Map<String, TradingCurrency> tradingCurrencyMap = new HashMap<>();

    private Map<String, TradingLanguage> tradingLanguageMap = new HashMap<>();

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Customer createCustomer(org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust)
            throws BusinessException {

        Customer customer = null;

        if (customer == null) {
            customer = new Customer();
            customer.setCode(cust.getCode());
            customer.setDescription(cust.getDesCustomer());
            customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand()));
            customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory()));
            customer.setSeller(seller);

            org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
            String tilteCode = cust.getName().getTitle();
            Title existingTitle = map.get(tilteCode);
            if (existingTitle == null) {
                Title title = titleService.findByCode(tilteCode);
                map.put(tilteCode, title);
                name.setTitle(title);
            } else {
                name.setTitle(existingTitle);
            }

            name.setFirstName(cust.getName().getFirstName());
            name.setLastName(cust.getName().getLastName());
            customer.setName(name);

            customerService.create(customer);

            if (cust.getCustomFields() != null) {
                populateCustomFields(cust.getCustomFields().getCustomField(), customer);
            }
        }

        return customer;
    }

    /**
     * @param customer customer
     * @param seller seller
     * @param custAcc customer account
     * @param cust jaxb customer
     * @param sell jaxb seller
     * @return customer account.
     * @throws BusinessException business exception.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CustomerAccount createCustomerAccount(Customer customer, org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
                                                 org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell) throws BusinessException {

        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setCode(custAcc.getCode());
        customerAccount.setDescription(custAcc.getDescription());
        customerAccount.setDateDunningLevel(new Date());
        customerAccount.setDunningLevel(DunningLevelEnum.R0);
        customerAccount.setPassword(RandomStringUtils.randomAlphabetic(8));
        customerAccount.setDateStatus(new Date());
        customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);

        String paymentMethod = custAcc.getPaymentMethod();
        List<PaymentMethod> paymentMethods = customerAccount.getPaymentMethods();
        if (paymentMethod == null) {
            if (paymentMethods == null) {
                paymentMethods = new ArrayList<PaymentMethod>();
                customerAccount.setPaymentMethods(paymentMethods);
            }
            CheckPaymentMethod checkPaymentMethod = new CheckPaymentMethod();
            checkPaymentMethod.setPaymentType(PaymentMethodEnum.CHECK);
            checkPaymentMethod.setCustomerAccount(customerAccount);
            checkPaymentMethod.setPreferred(true);
            paymentMethods.add(checkPaymentMethod);
        }else { // Added by Mohamed Ali Hammal for the Xml importing job
            switch (paymentMethod) {
                case ("DIRECTDEBIT"): // Direct debit configuration
                    DDPaymentMethod DDpaymentMethod = new DDPaymentMethod();
                    DDpaymentMethod.setPaymentType(PaymentMethodEnum.DIRECTDEBIT);
                    DDpaymentMethod.setPreferred(true);
                    DDpaymentMethod.setAlias("SEPA");
                    DDpaymentMethod.setMandateIdentification(custAcc.getMandateIdentification());
                    try {
                        Date MandatDate = new SimpleDateFormat("yyyy-mm-dd").parse(custAcc.getMandateDate());
                        DDpaymentMethod.setMandateDate(MandatDate);
                    } catch(ParseException e) {
                        log.error("Error when parsing mandateDate", e);
                    }
                    BankCoordinates Bankcoordinates = new BankCoordinates(); // Bank coordinates
                    Bankcoordinates.setBankCode(custAcc.getBankCoordinates().getBankCode());
                    Bankcoordinates.setIban(custAcc.getBankCoordinates().getIBAN());
                    Bankcoordinates.setBankName(custAcc.getBankCoordinates().getBankName());
                    Bankcoordinates.setBic(custAcc.getBankCoordinates().getBIC());
                    Bankcoordinates.setAccountNumber(custAcc.getBankCoordinates().getAccountNumber());
                    DDpaymentMethod.setBankCoordinates(Bankcoordinates); // Add the bank coordinates too the payment method
                    DDpaymentMethod.setCustomerAccount(customerAccount);
                    DDpaymentMethod.setPreferred(true);
                    paymentMethods.add(DDpaymentMethod);
                    
                    break;
                default: // The default configuration is for the CHECK payment method
                    CheckPaymentMethod checkPaymentMethod = new CheckPaymentMethod();
                    checkPaymentMethod.setPaymentType(PaymentMethodEnum.CHECK);
                    checkPaymentMethod.setCustomerAccount(customerAccount);
                    checkPaymentMethod.setPreferred(true);
                    paymentMethods.add(checkPaymentMethod);
            }
        }

        Address address = new Address();
        if (custAcc.getAddress() != null) {
            address.setAddress1(custAcc.getAddress().getAddress1());
            address.setAddress2(custAcc.getAddress().getAddress2());
            address.setAddress3(custAcc.getAddress().getAddress3());
            address.setCity(custAcc.getAddress().getCity());
            address.setCountry(countryService.findByCode(custAcc.getAddress().getCountry()));
            address.setZipCode("" + custAcc.getAddress().getZipCode());
            address.setState(custAcc.getAddress().getState());
            customerAccount.setAddress(address);
        }

        ContactInformation contactInformation = new ContactInformation();
        contactInformation.setEmail(custAcc.getEmail());
        contactInformation.setPhone(custAcc.getTel1());
        contactInformation.setMobile(custAcc.getTel2());
        customerAccount.setContactInformation(contactInformation);
        if (!StringUtils.isBlank(custAcc.getCreditCategory())) {
            // customerAccount.setCreditCategory(creditCategoryService.findByCode(custAcc.getCreditCategory()));
        }
        customerAccount.setExternalRef1(custAcc.getExternalRef1());
        customerAccount.setExternalRef2(custAcc.getExternalRef2());

        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

        if (custAcc.getName() != null) {
            name.setFirstName(custAcc.getName().getFirstName());
            name.setLastName(custAcc.getName().getLastName());
            if (!StringUtils.isBlank(custAcc.getName().getTitle())) {
                // Title title = titleService.findByCode(custAcc.getName().getTitle().trim());

                String tilteCode = custAcc.getName().getTitle();
                Title existingTitle = map.get(tilteCode);
                if (existingTitle == null) {
                    Title title = titleService.findByCode(tilteCode);
                    map.put(tilteCode, title);
                    name.setTitle(title);
                } else {
                    name.setTitle(existingTitle);
                }
            }
            customerAccount.setName(name);
        }

        // customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode()));
        // customerAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(custAcc.getTradingLanguageCode()));

        TradingCurrency existingTradingCurrency = tradingCurrencyMap.get(custAcc.getTradingCurrencyCode());
        if (existingTradingCurrency == null) {
            TradingCurrency findByTradingCurrencyCode = tradingCurrencyService.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode());
            tradingCurrencyMap.put(custAcc.getTradingCurrencyCode(), findByTradingCurrencyCode);
            customerAccount.setTradingCurrency(findByTradingCurrencyCode);
        } else {
            customerAccount.setTradingCurrency(existingTradingCurrency);
        }

        String tradingLanguageCode = custAcc.getTradingLanguageCode();
        TradingLanguage tradingLanguage = tradingLanguageMap.get(tradingLanguageCode);
        if (tradingLanguage == null) {
            TradingLanguage findByTradingLanguageCode = tradingLanguageService.findByTradingLanguageCode(tradingLanguageCode);
            tradingLanguageMap.put(tradingLanguageCode, findByTradingLanguageCode);
            if (findByTradingLanguageCode != null) {
                customerAccount.setTradingLanguage(findByTradingLanguageCode);
            }
        } else {
            customerAccount.setTradingLanguage(tradingLanguage);
        }

        customerAccount.setCustomer(customer);
        customerAccountService.create(customerAccount);

        if (custAcc.getCustomFields() != null) {
            populateCustomFields(custAcc.getCustomFields().getCustomField(), customerAccount);
        }

        return customerAccount;
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Customer updateCustomer(Customer customer, org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust)
            throws BusinessException {

        customer.setDescription(cust.getDesCustomer());
        customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand()));
        customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory()));
        customer.setSeller(seller);

        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
        Title title = titleService.findByCode(cust.getName().getTitle());
        name.setTitle(title);
        name.setFirstName(cust.getName().getFirstName());
        name.setLastName(cust.getName().getLastName());
        customer.setName(name);

        customer = customerService.updateNoCheck(customer);

        if (cust.getCustomFields() != null) {
            populateCustomFields(cust.getCustomFields().getCustomField(), customer);
        }

        return customer;
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCustomerAccount(CustomerAccount customerAccount, Customer customer, Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
                                      org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell) throws BusinessException {

        customerAccount.setDescription(custAcc.getDescription());
        customerAccount.setDateDunningLevel(new Date());
        customerAccount.setDunningLevel(DunningLevelEnum.R0);
        customerAccount.setPassword(RandomStringUtils.randomAlphabetic(8));
        customerAccount.setDateStatus(new Date());
        customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);

        if (custAcc.getAddress() != null) {
            Address address = customerAccount.getAddress() == null ? new Address() : customerAccount.getAddress();

            address.setAddress1(custAcc.getAddress().getAddress1());
            address.setAddress2(custAcc.getAddress().getAddress2());
            address.setAddress3(custAcc.getAddress().getAddress3());
            address.setCity(custAcc.getAddress().getCity());
            address.setCountry(countryService.findByCode(custAcc.getAddress().getCountry()));
            address.setZipCode("" + custAcc.getAddress().getZipCode());
            address.setState(custAcc.getAddress().getState());
            customerAccount.setAddress(address);
        }

        ContactInformation contactInformation = customerAccount.getContactInformationNullSafe();
        contactInformation.setEmail(custAcc.getEmail());
        contactInformation.setPhone(custAcc.getTel1());
        contactInformation.setMobile(custAcc.getTel2());
        customerAccount.setContactInformation(contactInformation);

        if (!StringUtils.isBlank(custAcc.getCreditCategory())) {
            customerAccount.setCreditCategory(creditCategoryService.findByCode(custAcc.getCreditCategory()));
        }
        customerAccount.setExternalRef1(custAcc.getExternalRef1());
        customerAccount.setExternalRef2(custAcc.getExternalRef2());

        if (custAcc.getName() != null) {
            Name name = customerAccount.getName() == null ? new Name() : customerAccount.getName();
            name.setFirstName(custAcc.getName().getFirstName());
            name.setLastName(custAcc.getName().getLastName());
            if (!StringUtils.isBlank(custAcc.getName().getTitle())) {
                Title title = titleService.findByCode(custAcc.getName().getTitle().trim());
                name.setTitle(title);
            }
            customerAccount.setName(name);
        }

        customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode()));
        customerAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(custAcc.getTradingLanguageCode()));
        customerAccount.setCustomer(customer);
        customerAccount = customerAccountService.updateNoCheck(customerAccount);

        if (custAcc.getCustomFields() != null) {
            populateCustomFields(custAcc.getCustomFields().getCustomField(), customerAccount);
        }

    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSeller(org.meveo.model.admin.Seller seller) throws BusinessException {
        sellerService.updateNoCheck(seller);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createSeller(org.meveo.model.admin.Seller seller) throws BusinessException {
        sellerService.create(seller);
    }
}