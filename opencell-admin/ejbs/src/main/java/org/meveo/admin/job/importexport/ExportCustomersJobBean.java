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

package org.meveo.admin.job.importexport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.Address;
import org.meveo.model.jaxb.account.Name;
import org.meveo.model.jaxb.customer.CustomFields;
import org.meveo.model.jaxb.customer.CustomerAccount;
import org.meveo.model.jaxb.customer.CustomerAccounts;
import org.meveo.model.jaxb.customer.Customers;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ExportCustomersJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    @Inject
    private Logger log;

    @Inject
    private CustomerService customerService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private Sellers sellers;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String parameter) {
        String exportDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "customers" + File.separator;
        log.info("exportDir=" + exportDir);
        File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = sdf.format(new Date());
        List<Seller> sellersInDB = customerService.listSellersWithCustomers();
        sellers = new Sellers(sellersInDB);// ,param.getProperty("connectorCRM.dateFormat",
                                           // "yyyy-MM-dd"));
        int i = 0;
        for (org.meveo.model.jaxb.customer.Seller seller : sellers.getSeller()) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            List<Customer> customers = customerService.listBySellerCode(seller.getCode());
            seller.setCustomers(customersToDto(customers));
            jobExecutionService.decCounterElementsRemaining(result);
        }
        int nbItems = sellers.getSeller() != null ? sellers.getSeller().size() : 0;
        result.setNbItemsToProcess(nbItems);
        jobExecutionService.initCounterElementsRemaining(result, nbItems);
        try {
            JAXBUtils.marshaller(sellers, new File(dir + File.separator + "CUSTOMER_" + timestamp + ".xml"));
            result.setNbItemsCorrectlyProcessed(nbItems);
            logResult();
        } catch (JAXBException e) {
            log.error("Failed to export customers job", e);
            result.getErrors().add(e.getMessage());
            result.setReport(e.getMessage());
            result.setNbItemsProcessedWithError(nbItems);
        }

    }

    private void logResult() {
        if (sellers.getSeller() != null) {
            int nbItems;
            for (org.meveo.model.jaxb.customer.Seller seller : sellers.getSeller()) {
                nbItems = seller.getCustomers() != null && seller.getCustomers().getCustomer() != null ? seller.getCustomers().getCustomer().size() : 0;
                log.info("Number of processed customers for the seller {} in ExportCustomersJob is : {}", seller.getCode(), nbItems);
                if (nbItems > 0) {
                    for (org.meveo.model.jaxb.customer.Customer customer : seller.getCustomers().getCustomer()) {
                        nbItems = customer.getCustomerAccounts() != null && customer.getCustomerAccounts().getCustomerAccount() != null ? customer.getCustomerAccounts().getCustomerAccount().size() : 0;
                        log.info("Number of processed customerAccounts for the customer {} in ExportCustomersJob is : {}", customer.getCode(), nbItems);
                    }
                }
            }
        }
    }

    private Customers customersToDto(List<org.meveo.model.crm.Customer> customerList) {
        Customers dto = new Customers();
        if (customerList != null) {
            for (org.meveo.model.crm.Customer cust : customerList) {
                dto.getCustomer().add(customerToDto(cust));
            }
        }

        return dto;
    }

    private org.meveo.model.jaxb.customer.Customer customerToDto(org.meveo.model.crm.Customer cust) {
        org.meveo.model.jaxb.customer.Customer dto = new org.meveo.model.jaxb.customer.Customer();
        if (cust != null) {
            dto.setDesCustomer(cust.getDescription());
            dto.setCode(cust.getCode());
            dto.setCustomerCategory(cust.getCustomerCategory() == null ? "" : cust.getCustomerCategory().getCode());
            dto.setCustomerBrand(cust.getCustomerBrand() == null ? "" : cust.getCustomerBrand().getCode());
            if (cust.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(cust.getCfValues().getValuesByCode()));
            }
            if (cust.getAddress() != null) {
                dto.setAddress(new Address(cust.getAddress()));
            }
            if (cust.getName() != null) {
                dto.setName(new Name(cust.getName()));
            }
            dto.setCustomerAccounts(customerAccountsToDto(cust.getCustomerAccounts()));
        }
        return dto;
    }

    private CustomerAccounts customerAccountsToDto(List<org.meveo.model.payments.CustomerAccount> customerAccounts) {
        CustomerAccounts dto = new CustomerAccounts();
        if (customerAccounts != null) {
            for (org.meveo.model.payments.CustomerAccount ca : customerAccounts) {
                dto.getCustomerAccount().add(customerAccountToDto(ca));
            }
        }
        return dto;
    }

    private CustomerAccount customerAccountToDto(org.meveo.model.payments.CustomerAccount ca) {
        CustomerAccount dto = new CustomerAccount();
        if (ca != null) {
            dto.setCode(ca.getCode());
            dto.setDescription(ca.getDescription());
            dto.setExternalRef1(ca.getExternalRef1());
            dto.setExternalRef2(ca.getExternalRef2());
            if (ca.getName() != null) {
                dto.setName(new Name(ca.getName()));
            }
            if (ca.getAddress() != null) {
                dto.setAddress(new Address(ca.getAddress()));
            }
            dto.setTradingCurrencyCode(ca.getTradingCurrency() == null ? null : ca.getTradingCurrency().getCurrencyCode());
            dto.setTradingLanguageCode(ca.getTradingLanguage() == null ? null : ca.getTradingLanguage().getLanguageCode());
            if (ca.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(ca.getCfValues().getValuesByCode()));
            }
            dto.setCreditCategory(ca.getCreditCategory() == null ? null : ca.getCreditCategory().getCode());
            if (ca.getContactInformationNullSafe() != null) {
                dto.setEmail(ca.getContactInformationNullSafe().getEmail());
                dto.setTel1(ca.getContactInformationNullSafe().getPhone());
                dto.setTel2(ca.getContactInformationNullSafe().getMobile());
            }
        }
        return dto;
    }
}