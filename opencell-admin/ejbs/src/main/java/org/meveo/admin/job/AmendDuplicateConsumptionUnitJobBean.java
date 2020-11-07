package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculate the overrun by Agency-Offer-ChargeType and create WalletOperation
 *
 * @author BEN AICHA Amine
 * @author BOUKAYOUA Mounir
 */
@Stateless
public class AmendDuplicateConsumptionUnitJobBean {

    private static final String CF_POOL_PER_OFFER_MAP = "POOL_PER_OFFER_MAP";

    private static final String COUNTER_OVERAGE_WO_QUERY = "from WalletOperation wo \n" +
            "where wo.subscription.id=:subId\n" +
            " and wo.chargeInstance.code=:overChargeCode\n" +
            " and (wo.status = 'OPEN' or wo.ratedTransaction.status = 'OPEN')\n" +
            " and (wo.operationDate between :startMonth and :endMonth)\n" +
            "order by wo.id";

    private static final String POOL_OVERAGE_WO_QUERY = "from WalletOperation wo \n" +
            "where wo.code like 'POOL%_USG_OVER' " +
            " and wo.parameter1=:chargeType \n" +
            " and wo.offerTemplate=:offer \n" +
            " and wo.subscription.userAccount=:agency \n" +
            " and (wo.status = 'OPEN' or wo.ratedTransaction.status = 'OPEN')\n" +
            " and (wo.operationDate between :startMonth and :endMonth)\n" +
            "order by wo.id";

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private Logger log;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private CustomFieldInstanceService cfiService;

    @Inject
    private CounterPeriodService counterPeriodService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @SuppressWarnings({"unchecked"})
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long canceledWOId) throws BusinessException {
        log.info("Cancel consumption of a duplicated WOId={}", canceledWOId);
        try {
            WalletOperation canceledWO = walletOperationService.findById(canceledWOId);

            List<WalletOperation> overageWOList = getOverageWalletOperationList(canceledWO);

            BigDecimal quantityToRestore;
            if (overageWOList == null || overageWOList.isEmpty()) {
                quantityToRestore = canceledWO.getQuantity();
            } else {
                quantityToRestore = adjustOverageWOQuantities(canceledWO, overageWOList);
            }
            restoreQuantityToCounterOrPool(canceledWO, quantityToRestore);

            // in case of WO deducted from counter
            // also reajuste canceled WO's quantity and put it to OPEN
            if (canceledWO.getCounter() != null) {
                canceledWO.setQuantity(canceledWO.getQuantity().subtract(quantityToRestore));
                canceledWO.setStatus(WalletOperationStatusEnum.OPEN);
            }

            canceledWO.setParameter3("AMENDED_FROM_POOL");
            walletOperationService.update(canceledWO);

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to cancel the deduction from pool of a duplicated WalletOperationId={}: {}", canceledWOId, e);
            result.registerError("Error on duplicated processing WOId="+ canceledWOId + ": " +e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<WalletOperation> getOverageWalletOperationList(WalletOperation canceledWO) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(canceledWO.getOperationDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        // start month of usage date
        calendar.set(year, month, 1, 0, 0, 0);
        Date startMonth = calendar.getTime();
        // end month of usage date
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, lastDayOfMonth, 23, 59, 59);
        Date endMonth = calendar.getTime();

        if (canceledWO.getCounter() != null) {
            return emWrapper.getEntityManager().createQuery(COUNTER_OVERAGE_WO_QUERY, WalletOperation.class)
                    .setParameter("subId", canceledWO.getSubscription().getId())
                    .setParameter("overChargeCode", "CH_M2M_USG_" + canceledWO.getParameter1() + "_OVER")
                    .setParameter("startMonth", startMonth)
                    .setParameter("endMonth", endMonth)
                    .getResultList();

        } else {
            return emWrapper.getEntityManager().createQuery(POOL_OVERAGE_WO_QUERY, WalletOperation.class)
                    .setParameter("chargeType", canceledWO.getParameter1())
                    .setParameter("offer", canceledWO.getOfferTemplate())
                    .setParameter("agency", canceledWO.getSubscription().getUserAccount())
                    .setParameter("startMonth", startMonth)
                    .setParameter("endMonth", endMonth)
                    .getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreQuantityToCounterOrPool(WalletOperation canceledWO, BigDecimal quantityToRestore) {
        if (quantityToRestore.compareTo(BigDecimal.ZERO) == 0) {
            // no quantity to restore to counter or pool
            return;
        }
        if (canceledWO.getCounter() != null) {
            CounterPeriod counterPeriod = counterPeriodService
                    .getCounterPeriod(canceledWO.getCounter(), canceledWO.getOperationDate());
            counterPeriod.setValue(counterPeriod.getValue().add(quantityToRestore));
            counterPeriodService.update(counterPeriod);

        } else {
            ServiceTemplate serviceTemplate = canceledWO.getServiceInstance().getServiceTemplate();
            String agencyCode = canceledWO.getSubscription().getUserAccount().getCode();
            String agencyCounterKey = agencyCode + "_value";
            Map<String, Double> offerAgenciesCountersMap = (Map<String, Double>) cfiService
                    .getCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, canceledWO.getOperationDate());

            if (offerAgenciesCountersMap == null || offerAgenciesCountersMap.get(agencyCounterKey) == null) {
                throw new IllegalStateException(String.format("Pool counter not yet initialized for operation date %tF " +
                        "ServiceTemplate=%s Agency=%s", canceledWO.getOperationDate(),serviceTemplate.getCode(),  agencyCode));
            }
            Double agencyCounter = offerAgenciesCountersMap.get(agencyCounterKey);
            offerAgenciesCountersMap.put(agencyCounterKey, agencyCounter + quantityToRestore.doubleValue());

            cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, offerAgenciesCountersMap, canceledWO.getOperationDate());
            serviceTemplateService.update(serviceTemplate);
        }
    }

    @SuppressWarnings("unchecked")
    private BigDecimal adjustOverageWOQuantities(WalletOperation canceledWO, List<WalletOperation> overageWOs) {
        BigDecimal canceledQuantity = canceledWO.getQuantity();
        for (WalletOperation overageWO : overageWOs) {
            canceledQuantity = canceledQuantity.subtract(overageWO.getInputQuantity());

            // Cancel RT if already exists
            RatedTransaction ratedTransaction = overageWO.getRatedTransaction();
            if (ratedTransaction != null) {
                ratedTransaction = ratedTransactionService.refreshOrRetrieve(ratedTransaction);
                if (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN) {
                    ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
                    ratedTransactionService.update(ratedTransaction);
                }
            }

            if (canceledQuantity.compareTo(BigDecimal.ZERO) >= 0) {
                // the canceled Quantiy cover all overageWO quantity
                overageWO.setQuantity(BigDecimal.ZERO);
                overageWO.setInputQuantity(BigDecimal.ZERO);
                initOverageWOStatus(overageWO);
                walletOperationService.update(overageWO);

            } else {
                // the canceled quantiy cover partially the overageWO quantity
                BigDecimal rest = canceledQuantity.negate();
                ServiceTemplate serviceTemplate = canceledWO.getServiceInstance().getServiceTemplate();
                String overageUnit = (String) cfiService.getCFValue(serviceTemplate, "overageUnit");
                Double multiplier = ((Map<String, Double>) cfiService
                        .getInheritedCFValueByKey(appProvider, "CF_P_USAGE_UNITS", overageUnit))
                        .get("multiplier");
                BigDecimal newOverageQuantity = rest.divide(BigDecimal.valueOf(multiplier), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());

                overageWO.setQuantity(newOverageQuantity);
                overageWO.setInputQuantity(rest);
                initOverageWOStatus(overageWO);
                walletOperationService.update(overageWO);

                // the whole canceled quantity is deducted from Overage WO
                // so we return ZERO
                return BigDecimal.ZERO;
            }
        }

        // return the rest of canceled quantity which still here
        // even we deducted it from all overage WOs which became all with zero
        return canceledQuantity;
    }

    private void initOverageWOStatus(WalletOperation overageWO) {
        recomputeWOAmounts(overageWO);
        overageWO.setParameterExtra("reajusted by AmendDuplicateConsumption");
        overageWO.setStatus(WalletOperationStatusEnum.OPEN);
        overageWO.setRatedTransaction(null);
    }

    public void recomputeWOAmounts(WalletOperation wo) {
        BigDecimal quantity = wo.getQuantity();
        wo.setAmountWithoutTax(wo.getUnitAmountWithoutTax().multiply(quantity));
        wo.setAmountTax(wo.getUnitAmountTax().multiply(quantity));
        wo.setAmountWithTax(wo.getAmountWithoutTax().add(wo.getAmountTax()));
    }
}