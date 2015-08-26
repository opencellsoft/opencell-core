package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.dunning.UpgradeDunningLevel;
import org.meveo.admin.dunning.UpgradeDunningReturn;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.BayadDunningInputHistory;
import org.meveo.model.admin.DunningHistory;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.admin.impl.BayadDunningInputHistoryService;
import org.meveo.service.job.Job;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningHistoryService;
import org.meveo.service.payments.impl.DunningLOTService;
import org.meveo.service.payments.impl.DunningPlanService;

@Startup
@Singleton
public class DunningProcessJob extends Job {

    @Inject
    private DunningPlanService dunningPlanService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private UpgradeDunningLevel upgradeDunning;

    @Inject
    private DunningLOTService dunningLOTService;

    @Inject
    private DunningHistoryService dunningHistoryService;

    @Inject
    private BayadDunningInputHistoryService bayadDunningInputHistoryService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
        Provider provider = currentUser.getProvider();

        for (DunningPlan dunningPlan : dunningPlanService.getDunningPlans()) {
            int loadedCustomerAccounts = 0;
            int errorCustomerAccounts = 0;
            int updatedCustomerAccounts = 0;
            List<ActionDunning> listActionDunning = new ArrayList<ActionDunning>();
            List<OtherCreditAndCharge> listOCC = new ArrayList<OtherCreditAndCharge>();

            List<CustomerAccount> customerAccounts = customerAccountService.getCustomerAccounts(dunningPlan.getCreditCategory(), dunningPlan.getPaymentMethod(), dunningPlan
                .getProvider().getCode());
            log.info(String.format("Found %s CustomerAccounts to check", (customerAccounts == null ? "null" : customerAccounts.size())));

            for (CustomerAccount customerAccount : customerAccounts) {
                try {
                    log.info("Processing  customerAccounts code " + customerAccount.getCode());
                    loadedCustomerAccounts++;
                    BigDecimal balanceExigible = customerAccountService.customerAccountBalanceExigibleWithoutLitigation(customerAccount.getId(), null, new Date(),customerAccount.getProvider());
                    log.info("balanceExigible " + balanceExigible);

                    if (DowngradeDunningLevel(customerAccount, balanceExigible)) {
                        updatedCustomerAccounts++;
                    } else {
                        UpgradeDunningReturn upgradeDunningReturn = upgradeDunning.execute(customerAccount, balanceExigible, dunningPlan);
                        if (upgradeDunningReturn.isUpgraded()) {
                            updatedCustomerAccounts++;
                            listActionDunning.addAll(upgradeDunningReturn.getListActionDunning());
                            listOCC.addAll(upgradeDunningReturn.getListOCC());
                        }
                    }
                } catch (Exception e) {
                    errorCustomerAccounts++;
                    log.error("Failed to process dunning", e);
                }
            }

            DunningHistory dunningHistory = new DunningHistory();
            dunningHistory.setExecutionDate(new Date());
            dunningHistory.setLinesRead(loadedCustomerAccounts);
            dunningHistory.setLinesRejected(errorCustomerAccounts);
            dunningHistory.setLinesInserted(updatedCustomerAccounts);
            dunningHistory.setProvider(dunningPlan.getProvider());
            dunningHistoryService.create(dunningHistory);
            BayadDunningInputHistory bayadDunningInputHistory = createNewInputHistory(loadedCustomerAccounts, updatedCustomerAccounts, errorCustomerAccounts, new Date(),
                dunningPlan.getProvider());
            bayadDunningInputHistoryService.create(bayadDunningInputHistory);
            dunningLOTService.createDunningLOTAndCsvFile(listActionDunning, dunningHistory, provider);
        }
    }

    public boolean DowngradeDunningLevel(CustomerAccount customerAccount, BigDecimal balanceExigible) throws Exception {
        log.info("DowngradeDunningLevelStep ...");
        boolean isDowngradelevel = false;
        if (balanceExigible.compareTo(BigDecimal.ZERO) <= 0 && customerAccount.getDunningLevel() != DunningLevelEnum.R0) {
            customerAccount.setDunningLevel(DunningLevelEnum.R0);
            customerAccount.setDateDunningLevel(new Date());
            isDowngradelevel = true;
            customerAccountService.update(customerAccount);
            log.info("customerAccount code:" + customerAccount.getCode() + " updated to R0");
        }
        // attente besoin pour par exp : R3--> R2 avec actions

        return isDowngradelevel;
    }

    /**
     * Creates input history object, to save it to DB.
     */
    private BayadDunningInputHistory createNewInputHistory(int nbTicketsParsed, int nbTicketsSucceeded, int nbTicketsRejected, Date startDate, Provider provider) {
        BayadDunningInputHistory inputHistory = new BayadDunningInputHistory();
        inputHistory.setName(startDate.toString());
        inputHistory.setParsedTickets(nbTicketsParsed);
        inputHistory.setRejectedTickets(nbTicketsRejected);
        inputHistory.setSucceededTickets(nbTicketsSucceeded);
        inputHistory.setAnalysisStartDate(startDate);
        inputHistory.setAnalysisEndDate(new Date());
        inputHistory.setProvider(provider);
        return inputHistory;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }

}