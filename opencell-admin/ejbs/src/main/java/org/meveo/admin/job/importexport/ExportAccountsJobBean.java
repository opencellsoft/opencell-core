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
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
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
        billingAccounts = billingAccountsToDto(bas, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance().getId());
        try {
            JAXBUtils.marshaller(billingAccounts, new File(dir + File.separator + "ACCOUNT_" + timestamp + ".xml"));
        } catch (JAXBException e) {
            log.error("Failed to export accounts job", e);
        }

    }

    private BillingAccounts billingAccountsToDto(List<org.meveo.model.billing.BillingAccount> bas, String dateFormat, Long jobInstanceId) {
        BillingAccounts dto = new BillingAccounts();
        for (org.meveo.model.billing.BillingAccount ba : bas) {
            if (!jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            BillingAccount billingAcc = billingAccountToDto(ba, dateFormat, jobInstanceId);
            dto.getBillingAccount().add(billingAcc);
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
            dto.setElectronicBilling(ba.getElectronicBilling() == null ? null : ba.getElectronicBilling() + "");
            dto.setEmail(ba.getEmail() == null ? null : ba.getEmail());
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
        for (org.meveo.model.billing.UserAccount userAcc : usersAccounts) {
            if (!jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            dto.getUserAccount().add(userAccountToDto(userAcc, dateFormat));
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