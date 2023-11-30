package org.meveo.admin.job;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationAggregationSettingsService;
import org.meveo.service.billing.impl.WalletOperationService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatedTransactionsAggregatedJobBean extends IteratorBasedJobBean<AggregatedWalletOperation> {

    private static final long serialVersionUID = 5722941915986153255L;

    @Inject
    private WalletOperationService walletOperationService;

    private WalletOperationAggregationSettings aggregationSettings;

    @Inject
    private WalletOperationAggregationSettingsService walletOperationAggregationSettingsService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    private Date invoicingDate;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::convertWosToAggregatedRt, null, null, null);

        invoicingDate = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of aggregated Wallet operations to convert to Rated transactions
     */
    private Optional<Iterator<AggregatedWalletOperation>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        EntityReferenceWrapper aggregationSettingsWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "woAggregationSettings", null);

        if (aggregationSettingsWrapper != null) {
            aggregationSettings = walletOperationAggregationSettingsService.findByCode(aggregationSettingsWrapper.getCode());
        }

        // Only aggregation is not supported here
        if (aggregationSettings == null) {
            return Optional.empty();
        }

        log.info("Remove wallet operations rated to 0");
        walletOperationService.removeZeroWalletOperation();

        invoicingDate = new Date();
        List<AggregatedWalletOperation> aggregatedWo = walletOperationService.listToInvoiceIdsWithGrouping(invoicingDate, aggregationSettings);

        checkAggregatedWoCurrency(aggregatedWo);
        return Optional.of(new SynchronizedIterator<>(aggregatedWo));
    }

    /**
     * Convert grouped Wallet operations to an aggregated Rated transaction
     * 
     * @param aggregatedWo Grouped Wallet operations
     * @param jobExecutionResult Job execution result
     */
    private void convertWosToAggregatedRt(AggregatedWalletOperation aggregatedWo, JobExecutionResultImpl jobExecutionResult) {
        try {
            log.debug("Aggregating WOs to RT {}", aggregatedWo);
            ratedTransactionService.createRatedTransaction(aggregatedWo, aggregationSettings, invoicingDate);
        } catch (Exception exception) {
            log.error("Error during processing aggregates : "
                    + aggregatedWo.getWalletOperationsIds() + " : " + exception.getCause().toString());
            throw new BusinessException("Error during processing aggregate "
                    + aggregatedWo.getWalletOperationsIds() + " : " + exception.getCause().toString());
        }
    }

    private void checkAggregatedWoCurrency(List<AggregatedWalletOperation> aggregatedWos) {
        TradingCurrency functionalCurrency = tradingCurrencyService
                .findByTradingCurrencyByCurrencyID(appProvider.getCurrency().getId()).orElse(null);
        for (AggregatedWalletOperation aggregatedWalletOperation : aggregatedWos) {
            verifyWOCurrency(aggregatedWalletOperation, functionalCurrency);
        }
    }

    private void verifyWOCurrency(AggregatedWalletOperation aggregatedWalletOperation, TradingCurrency functionalCurrency) {
        List<Object[]> walletOperationsTradingCurrency =
                walletOperationService.getWalletOperationsTradingCurrency(aggregatedWalletOperation.getWalletOperationsIds());
        if(walletOperationsTradingCurrency != null && !walletOperationsTradingCurrency.isEmpty()) {
            Long functionalCurrencyId = functionalCurrency != null ? functionalCurrency.getId() : null;
            Long firstTradingCurrencyID = (Long) walletOperationsTradingCurrency.get(0)[1];
            if(firstTradingCurrencyID == null) {
                firstTradingCurrencyID = functionalCurrencyId;
            }
            for (Object[] woAndTradingCurrency : walletOperationsTradingCurrency) {
                Long currencyId = woAndTradingCurrency[1] != null ? (Long) woAndTradingCurrency[1] : functionalCurrencyId;
                if(currencyId != firstTradingCurrencyID) {
                    throw new BusinessException("Aggregated wallet operations does not have the same trading currency, wallet operations ids : " + aggregatedWalletOperation.getId());
                }
            }
        }
    }
}