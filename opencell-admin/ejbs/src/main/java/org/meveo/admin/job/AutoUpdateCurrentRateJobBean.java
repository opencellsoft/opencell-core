package org.meveo.admin.job;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.ExchangeRateService;

@Stateless
public class AutoUpdateCurrentRateJobBean extends BaseJobBean {


    @Inject
    private ExchangeRateService exchangeRateService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @EJB
    private AutoUpdateCurrentRateJobBean jobBean;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        checkFunctionalCurrency(jobExecutionResult); // US INTRD-8094

        List<Long> listExchangeRateId = exchangeRateService.getAllTradingCurrencyWithCurrentRate();

        for (Long idExchangeRate : listExchangeRateId) {
            try {
                jobBean.process(idExchangeRate, jobExecutionResult);
            } catch (Exception exception) {
                jobExecutionResult.addErrorReport(exception.getMessage());
            }
 
        }
        jobExecutionResult.setNbItemsCorrectlyProcessed(listExchangeRateId.size() - jobExecutionResult.getNbItemsProcessedWithError());
    }


    /**
     * Process exchange rate
     *
     * @param idExchangeRate   Exchange rate id
     * @param jobExecutionResult Job execution result
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process(Long idExchangeRate, JobExecutionResultImpl jobExecutionResult) {
        exchangeRateService.updateCurrentRateForTradingCurrency(idExchangeRate, appProvider.getCurrency());
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void checkFunctionalCurrency(JobExecutionResultImpl jobExecutionResult) {
        Boolean isCleanAppliedRateInvoice = (Boolean) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "cleanAppliedRateInvoice");
        tradingCurrencyService.checkFunctionalCurrency(isCleanAppliedRateInvoice != null && isCleanAppliedRateInvoice);
    }

}