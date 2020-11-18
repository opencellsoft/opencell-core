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

    private static final String COUNTER_OVERAGE_WO_QUERY = "select wo from WalletOperation wo \n" +
            "left join wo.ratedTransaction rt \n" +
            "where wo.subscription.id=:subId \n" +
            " and wo.code=:overChargeCode \n" +
            " and (wo.status = 'OPEN' or (wo.status = 'TREATED' and rt.status = 'OPEN')) \n" +
            " and (wo.operationDate between :startMonth and :endMonth) \n" +
            "order by wo.id";

    private static final String POOL_OVERAGE_WO_QUERY = "select wo from WalletOperation wo \n" +
            "left join wo.ratedTransaction rt \n" +
            "where wo.code like 'POOL%_USG_OVER' " +
            " and wo.parameter1=:chargeType \n" +
            " and wo.offerTemplate=:offer \n" +
            " and wo.subscription.userAccount=:agency \n" +
            " and (wo.status = 'OPEN' or (wo.status = 'TREATED' and rt.status = 'OPEN')) \n" +
            " and (wo.operationDate between :startMonth and :endMonth) \n" +
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
    public void execute(JobExecutionResultImpl result, Long canceledWOId, String activateStats) throws BusinessException {
        log.info("Cancel consumption of a duplicated WOId={}", canceledWOId);
        AuditWOCancelation audit = null;
        try {
            boolean statsActivated = "TRUE".equalsIgnoreCase(activateStats);

            WalletOperation canceledWO = walletOperationService.findById(canceledWOId);
            //trace
            if (statsActivated) {
                audit = new AuditWOCancelation();
                audit.woId = canceledWO.getId();
                audit.subId = canceledWO.getSubscription().getId();
                audit.offerId = canceledWO.getOfferTemplate().getId();
                audit.originCanceledQT = canceledWO.getQuantity();

            }

            List<WalletOperation> overageWOList = getOverageWalletOperationList(canceledWO, audit);
            if (statsActivated) {
                audit.allOvers = overageWOList.size();
            }

            BigDecimal quantityToRestore;
            if (overageWOList.isEmpty()) {
                quantityToRestore = canceledWO.getQuantity();
                //trace
                if (statsActivated) {
                    audit.nbrOfOvers = 0;
                    audit.oversIds = "no_adjustement";
                    audit.originOversQT = BigDecimal.ZERO;
                    audit.newOversQT = BigDecimal.ZERO;
                }
            } else {
                quantityToRestore = adjustOverageWOQuantities(canceledWO, overageWOList, audit, statsActivated);
            }
            //trace
            if (statsActivated) {
                audit.restoredQT = quantityToRestore;
            }
            // restore the canceled QT which rest after adjusting Overage WO to counter or pool
            restoreQuantityToCounterOrPool(canceledWO, quantityToRestore, audit);

            // in case of WO deducted from counter
            // also reajuste canceled WO's quantity and put it to OPEN
            if (canceledWO.getCounter() != null) {
                canceledWO.setQuantity(canceledWO.getQuantity().subtract(quantityToRestore));
                canceledWO.setStatus(WalletOperationStatusEnum.OPEN);
                canceledWO.setParameter3("AMENDED_FROM_COUNTER");
            } else {
                canceledWO.setParameter3("AMENDED_FROM_POOL");
            }
            walletOperationService.update(canceledWO);

            //trace
            if (statsActivated) {
                audit.newQT = canceledWO.getQuantity();
                audit.trace();
            }

            result.registerSucces();
        } catch (Exception e) {
            log.error("Failed to cancel the deduction from pool of a duplicated WalletOperationId={}: {}", canceledWOId, e);
            result.registerError("Error on duplicated processing WOId="+ canceledWOId + ": " +e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<WalletOperation> getOverageWalletOperationList(WalletOperation canceledWO, AuditWOCancelation audit) {
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

        //trace
        if (audit != null) {
            audit.startMonth = startMonth;
            audit.endMonth = endMonth;
        }

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
    private void restoreQuantityToCounterOrPool(WalletOperation canceledWO, BigDecimal quantityToRestore, AuditWOCancelation audit) {
        if (quantityToRestore.compareTo(BigDecimal.ZERO) == 0) {
            // no quantity to restore to counter or pool
            return;
        }
        if (canceledWO.getCounter() != null) {
            CounterPeriod counterPeriod = counterPeriodService
                    .getCounterPeriod(canceledWO.getCounter(), canceledWO.getOperationDate());
            //trace
            audit.originCounterQT = counterPeriod.getValue();
            counterPeriod.setValue(counterPeriod.getValue().add(quantityToRestore));
            //trace
            audit.newCounterQT = counterPeriod.getValue();

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
            //trace
            audit.originCounterQT = BigDecimal.valueOf(agencyCounter);
            offerAgenciesCountersMap.put(agencyCounterKey, agencyCounter + quantityToRestore.doubleValue());

            cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, offerAgenciesCountersMap, canceledWO.getOperationDate());
            //trace
            audit.newCounterQT = BigDecimal.valueOf(offerAgenciesCountersMap.get(agencyCounterKey));

            serviceTemplateService.update(serviceTemplate);
        }
    }

    @SuppressWarnings("unchecked")
    private BigDecimal adjustOverageWOQuantities(WalletOperation canceledWO, List<WalletOperation> overageWOs,
                                                 AuditWOCancelation audit, boolean statsActivated) {
        BigDecimal canceledQuantity = canceledWO.getQuantity();

        int nbrOfOvers = 0;
        String oversIds = "adjustement";
        BigDecimal originOversQT = BigDecimal.ZERO;
        BigDecimal newOversQT = BigDecimal.ZERO;

        //if we are here, so overageWOs wouldn't be empty
        for (WalletOperation overageWO : overageWOs) {
            //trace
            BigDecimal originQT = overageWO.getInputQuantity();
            originOversQT = originOversQT.add(overageWO.getInputQuantity());
            nbrOfOvers++;
            oversIds = oversIds.concat(",").concat(overageWO.getId().toString());

            canceledQuantity = canceledQuantity.subtract(overageWO.getInputQuantity());

            // Cancel RT if already exists
            RatedTransaction ratedTransaction = overageWO.getRatedTransaction();
            if (ratedTransaction != null) {
                ratedTransaction = ratedTransactionService.refreshOrRetrieve(ratedTransaction);
                if (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN) {
                    ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
                    ratedTransaction.setParameterExtra("canceled by AmendDuplicateConsumption");
                    ratedTransactionService.update(ratedTransaction);
                }
            }
            if (canceledQuantity.compareTo(BigDecimal.ZERO) >= 0) {
                // the canceled Quantiy cover all overageWO quantity
                // so the whole Over WO should be canceled
                overageWO.setParameterExtra("AmendDuplicateConsumption_Canceled_" + bgToStr(originQT));
                overageWO.setStatus(WalletOperationStatusEnum.CANCELED);
                walletOperationService.update(overageWO);

                if (canceledQuantity.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }

            } else {
                // the canceled quantiy cover partially the overageWO quantity
                BigDecimal rest = canceledQuantity.negate();
                //trace
                newOversQT = newOversQT.add(rest);
                oversIds = oversIds.concat("_adj");

                ServiceTemplate serviceTemplate = canceledWO.getServiceInstance().getServiceTemplate();
                String overageUnit = (String) cfiService.getCFValue(serviceTemplate, "overageUnit");
                Double multiplier = ((Map<String, Double>) cfiService
                        .getInheritedCFValueByKey(appProvider, "CF_P_USAGE_UNITS", overageUnit))
                        .get("multiplier");
                BigDecimal newOverageQuantity = rest.divide(BigDecimal.valueOf(multiplier),
                        appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());

                overageWO.setQuantity(newOverageQuantity);
                overageWO.setInputQuantity(rest);
                recomputeWOAmounts(overageWO);
                overageWO.setParameterExtra("AmendDuplicateConsumption_Adjusted_" + bgToStr(originQT) + "_" + bgToStr(rest));
                overageWO.setStatus(WalletOperationStatusEnum.OPEN);
                overageWO.setRatedTransaction(null);
                walletOperationService.update(overageWO);

                // the whole canceled quantity is deducted from Overage WO
                // so we return ZERO
                if (statsActivated) {
                    audit.nbrOfOvers = nbrOfOvers;
                    audit.oversIds = oversIds;
                    audit.originOversQT = originOversQT;
                    audit.newOversQT = newOversQT;
                }
                return BigDecimal.ZERO;
            }
        }

        // return the rest of canceled quantity which still here
        // even we deducted it from all overage WOs which became all with zero
        if (statsActivated) {
            audit.nbrOfOvers = nbrOfOvers;
            audit.oversIds = oversIds;
            audit.originOversQT = originOversQT;
            audit.newOversQT = newOversQT;
        }
        return canceledQuantity;
    }

    private void recomputeWOAmounts(WalletOperation wo) {
        BigDecimal quantity = wo.getQuantity();
        wo.setAmountWithoutTax(wo.getUnitAmountWithoutTax().multiply(quantity));
        wo.setAmountTax(wo.getUnitAmountTax().multiply(quantity));
        wo.setAmountWithTax(wo.getAmountWithoutTax().add(wo.getAmountTax()));
    }

    private String bgToStr(BigDecimal bg) {
        return bg.setScale(0, BigDecimal.ROUND_UP).toString();
    }

    //trace
    private class AuditWOCancelation {

        Long woId;
        Long subId;
        BigDecimal originCanceledQT;
        BigDecimal newQT;
        BigDecimal restoredQT;
        Integer nbrOfOvers;
        String oversIds;
        BigDecimal originOversQT;
        BigDecimal newOversQT;
        BigDecimal originCounterQT;
        BigDecimal newCounterQT;
        Long offerId;
        Date startMonth;
        Date endMonth;
        Integer allOvers;

        void trace() {
            String sqlString = "INSERT INTO amend_stat(wo_id, sub_id, canceled_qt, new_qt, restored_qt, " +
                    "nbr_overs, overs_ids, overs_qt, overs_new_qt, time_op, cn_qt, cn_new_qt, offer_id," +
                    "start_month, end_month, all_overs, instant)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            walletOperationService.getEntityManager().createNativeQuery(sqlString)
                    .setParameter(1, woId)
                    .setParameter(2, subId)
                    .setParameter(3, originCanceledQT != null ? originCanceledQT : BigDecimal.ZERO)
                    .setParameter(4, newQT != null ? newQT : BigDecimal.ZERO)
                    .setParameter(5, restoredQT != null ? restoredQT : BigDecimal.ZERO)
                    .setParameter(6, nbrOfOvers)
                    .setParameter(7, oversIds)
                    .setParameter(8, originOversQT != null ? originOversQT : BigDecimal.ZERO)
                    .setParameter(9, newOversQT != null ? newOversQT : BigDecimal.ZERO)
                    .setParameter(10, new Date())
                    .setParameter(11, originCounterQT != null ? originCounterQT : BigDecimal.ZERO)
                    .setParameter(12, newCounterQT != null ? newCounterQT : BigDecimal.ZERO)
                    .setParameter(13, offerId)
                    .setParameter(14, startMonth)
                    .setParameter(15, endMonth)
                    .setParameter(16, allOvers)
                    .setParameter(17, String.valueOf(System.currentTimeMillis()))
                    .executeUpdate();
        }
    }
}