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
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.AccountImportHisto;
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
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

@Stateless
public class ExportAccountsJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    @Inject
    private Logger log;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;
        
    BillingAccounts billingAccounts;
    ParamBean param = ParamBean.getInstance();

    int nbBillingAccounts;

    int nbUserAccounts;
    AccountImportHisto accountImportHisto;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String parameter) {
        
        String exportDir = param.getProperty("providers.rootDir", "./opencelldata/") + File.separator + appProvider.getCode() + File.separator + "exports" + File.separator + "accounts"
                + File.separator;
        log.info("exportDir=" + exportDir);
        File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = sdf.format(new Date());
        List<org.meveo.model.billing.BillingAccount> bas = billingAccountService.list();
        billingAccounts = billingAccountsToDto(bas, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"));
        try {
            JAXBUtils.marshaller(billingAccounts, new File(dir + File.separator + "ACCOUNT_" + timestamp + ".xml"));
        } catch (JAXBException e) {
            log.error("Failed to export accounts job", e);
        }

    }

    private BillingAccounts billingAccountsToDto(List<org.meveo.model.billing.BillingAccount> bas, String dateFormat) {
        BillingAccounts dto = new BillingAccounts();
        for (org.meveo.model.billing.BillingAccount ba : bas) {
            BillingAccount billingAcc = billingAccountToDto(ba, dateFormat);
            dto.getBillingAccount().add(billingAcc);
        }
        return dto;
    }

    private BillingAccount billingAccountToDto(org.meveo.model.billing.BillingAccount ba, String dateFormat) {
        BillingAccount dto = new BillingAccount();
        if (ba != null) {
            if (dateFormat == null) {
                dateFormat = "yyyy-MM-dd";
            }
            dto.setSubscriptionDate(ba.getSubscriptionDate() == null ? null : DateUtils.formatDateWithPattern(ba.getSubscriptionDate(), dateFormat));
            dto.setDescription(ba.getDescription());
            dto.setExternalRef1(ba.getExternalRef1());
            dto.setExternalRef2(ba.getExternalRef2());
            dto.setName(new Name(ba.getName()));
            dto.setAddress(new Address(ba.getAddress()));
            dto.setElectronicBilling(ba.getElectronicBilling() == null ? null : ba.getElectronicBilling() + "");
            dto.setEmail(ba.getEmail() == null ? null : ba.getEmail());
            dto.setTradingCountryCode(ba.getTradingCountry() == null ? null : ba.getTradingCountry().getCountryCode());
            dto.setTradingLanguageCode(ba.getTradingLanguage() == null ? null : ba.getTradingLanguage().getLanguageCode());
            dto.setCustomFields(CustomFields.toDTO(customFieldInstanceService.getCustomFieldInstances(ba)));
            dto.setUserAccounts(userAccountsToDto(ba.getUsersAccounts(), dateFormat));
            dto.setCode(ba.getCode() == null ? null : ba.getCode());
            dto.setCustomerAccountId(ba.getCustomerAccount().getCode());
            dto.setBillingCycle(ba.getBillingCycle() == null ? null : ba.getBillingCycle().getCode());
        }

        return dto;
    }

    private UserAccounts userAccountsToDto(List<org.meveo.model.billing.UserAccount> usersAccounts, String dateFormat) {
        UserAccounts dto = new UserAccounts();
        for (org.meveo.model.billing.UserAccount userAcc : usersAccounts) {
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
            dto.setName(new Name(ua.getName()));
            dto.setAddress(new Address(ua.getAddress()));
            dto.setCustomFields(CustomFields.toDTO(customFieldInstanceService.getCustomFieldInstances(ua)));
            dto.setCode(ua.getCode());
        }

        return dto;
    }
}