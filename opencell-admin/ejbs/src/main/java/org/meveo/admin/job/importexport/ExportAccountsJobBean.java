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

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.Address;
import org.meveo.model.jaxb.account.BillingAccount;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.Name;
import org.meveo.model.jaxb.account.UserAccount;
import org.meveo.model.jaxb.account.UserAccounts;
import org.meveo.model.jaxb.customer.CustomFields;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobSpeedEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ExportAccountsJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    @Inject
    private Logger log;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;

    private BillingAccounts billingAccounts;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String parameter) {
        ParamBean param = paramBeanFactory.getInstance();
        String exportDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "accounts" + File.separator;
        log.info("exportDir=" + exportDir);
        File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = sdf.format(new Date());
        List<org.meveo.model.billing.BillingAccount> bas = billingAccountService.list();
        billingAccounts = billingAccountsToDto(bas, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance());
        int nbItems = billingAccounts.getBillingAccount() != null ? billingAccounts.getBillingAccount().size() : 0;
        result.setNbItemsToProcess(nbItems);
        try {
            JAXBUtils.marshaller(billingAccounts, new File(dir + File.separator + "ACCOUNT_" + timestamp + ".xml"));
            result.setNbItemsCorrectlyProcessed(nbItems);
            logResult();
        } catch (JAXBException e) {
            log.error("Failed to export accounts job", e);
            result.setReport(e.getMessage());
            result.setNbItemsProcessedWithError(nbItems);
        }

    }

    private void logResult() {
        if (billingAccounts.getBillingAccount() != null) {
            int nbItems;
            for (BillingAccount billingAccount : billingAccounts.getBillingAccount()) {
                nbItems = billingAccount.getUserAccounts() != null && billingAccount.getUserAccounts().getUserAccount() != null ? billingAccount.getUserAccounts().getUserAccount().size() : 0;
                log.info("Number of processed userAccounts for the billingAccount {} in ExportAccountsJob is : {}", billingAccount.getCode(), nbItems);
            }
        }
    }

    private BillingAccounts billingAccountsToDto(List<org.meveo.model.billing.BillingAccount> bas, String dateFormat, JobInstance jobInstance) {
        BillingAccounts dto = new BillingAccounts();
        int i = 0;
        int checkJobStatusEveryNr = jobInstance.getJobSpeed().getCheckNb();
        
        for (org.meveo.model.billing.BillingAccount ba : bas) {
            if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
                break;
            }
            BillingAccount billingAcc = billingAccountToDto(ba, dateFormat, jobInstance.getId());
            dto.getBillingAccount().add(billingAcc);
            i++;
        }
        return dto;
    }

    private BillingAccount billingAccountToDto(org.meveo.model.billing.BillingAccount ba, String dateFormat, Long jobInstanceId) {
        BillingAccount dto = new BillingAccount();
        if (ba != null) {
            if (dateFormat == null) {
                dateFormat = "yyyy-MM-dd";
            }
            dto.setSubscriptionDate(ba.getSubscriptionDate() == null ? null : DateUtils.formatDateWithPattern(ba.getSubscriptionDate(), dateFormat));
            dto.setDescription(ba.getDescription());
            dto.setExternalRef1(ba.getExternalRef1());
            dto.setExternalRef2(ba.getExternalRef2());
            if (ba.getName() != null) {
                dto.setName(new Name(ba.getName()));
            }
            if (ba.getAddress() != null) {
                dto.setAddress(new Address(ba.getAddress()));
            }
            dto.setElectronicBilling(ba.getElectronicBilling() + "");
            if(ba.getContactInformation() != null) {
                dto.setEmail(ba.getContactInformation().getEmail() == null ? null : ba.getContactInformation().getEmail());
            }
            dto.setTradingCountryCode(ba.getTradingCountry() == null ? null : ba.getTradingCountry().getCountryCode());
            dto.setTradingLanguageCode(ba.getTradingLanguage() == null ? null : ba.getTradingLanguage().getLanguageCode());
            if (ba.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(ba.getCfValues().getValuesByCode()));
            }
            dto.setUserAccounts(userAccountsToDto(ba.getUsersAccounts(), dateFormat, jobInstanceId));
            dto.setCode(ba.getCode() == null ? null : ba.getCode());
            dto.setCustomerAccountId(ba.getCustomerAccount().getCode());
            dto.setBillingCycle(ba.getBillingCycle() == null ? null : ba.getBillingCycle().getCode());
        }

        return dto;
    }

    private UserAccounts userAccountsToDto(List<org.meveo.model.billing.UserAccount> usersAccounts, String dateFormat, Long jobInstanceId) {
        UserAccounts dto = new UserAccounts();
        int i = 0;
        for (org.meveo.model.billing.UserAccount userAcc : usersAccounts) {
            if (i % JobSpeedEnum.NORMAL.getCheckNb() == 0 && !jobExecutionService.isShouldJobContinue(jobInstanceId)) {
                break;
            }
            dto.getUserAccount().add(userAccountToDto(userAcc, dateFormat));
            i++;
        }
        return dto;
    }

    private UserAccount userAccountToDto(org.meveo.model.billing.UserAccount ua, String dateFormat) {
        UserAccount dto = new UserAccount();
        if (ua != null) {
            dto.setSubscriptionDate(DateUtils.formatDateWithPattern(ua.getSubscriptionDate(), dateFormat));
            dto.setDescription(ua.getDescription());
            dto.setExternalRef1(ua.getExternalRef1());
            dto.setExternalRef2(ua.getExternalRef2());
            if (ua.getName() != null) {
                dto.setName(new Name(ua.getName()));
            }
            if (ua.getAddress() != null) {
                dto.setAddress(new Address(ua.getAddress()));
            }
            if (ua.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(ua.getCfValues().getValuesByCode()));
            }
            dto.setCode(ua.getCode());
        }

        return dto;
    }
}