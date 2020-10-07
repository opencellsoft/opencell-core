package org.meveo.admin.job;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BaseEntity;
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
import java.math.RoundingMode;
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

    private static final String OFFER_NONE_OVER_WO_QUERY = "from WalletOperation wo \n" +
            "where wo.subscription.id=:subId\n" +
            " and wo.chargeInstance.code=:overChargeCode\n" +
            " and wo.status = 'CANCELED'" +
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

            // if canceledWO's counter is not null, it means that canceledWO belong to none shaed pool
            // otherwise it belongs to a shared pool
            if (canceledWO.getCounter() != null) {
                amendDuplicateWOFromCounter(canceledWO);

            } else {
                amendDuplicateWOFromSharedPool(canceledWO);
            }

            canceledWO.setParameter3("AMENDED_FROM_POOL");
            walletOperationService.update(canceledWO);

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to cancel the deduction from pool of a duplicated WalletOperationId={}: {}", canceledWOId, e);
            result.registerError("Error on duplicated processing WOId="+ canceledWOId + ": " +e.getMessage());
        }
    }

    private void amendDuplicateWOFromCounter(WalletOperation canceledWO) {

        List<WalletOperation> overWOList = emWrapper.getEntityManager().createQuery(OFFER_NONE_OVER_WO_QUERY, WalletOperation.class)
                .setParameter("subId", canceledWO.getSubscription().getId())
                .setParameter("overChargeCode", "CH_M2M_USG_" + canceledWO.getParameter1() + "_OVER")
                .getResultList();

        BigDecimal quantityToRestore;
        if (overWOList == null || overWOList.isEmpty()) {
            quantityToRestore = canceledWO.getQuantity();
        } else {
            quantityToRestore = adjustNonSharedPoolOverageWOs(canceledWO, overWOList);
        }
        CounterPeriod counterPeriod = counterPeriodService.getCounterPeriod(canceledWO.getCounter(), canceledWO.getOperationDate());
        counterPeriod.setValue(counterPeriod.getValue().add(quantityToRestore));
        counterPeriodService.update(counterPeriod);
    }

    @SuppressWarnings("unchecked")
    private BigDecimal adjustNonSharedPoolOverageWOs(WalletOperation canceledWO, List<WalletOperation> overageWOs) {
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
                overageWO.setQuantity(BigDecimal.ZERO);
                recomputeWOAmounts(overageWO);
                overageWO.setStatus(WalletOperationStatusEnum.CANCELED);
                overageWO.setParameterExtra("Canceled by AmendDuplicateConsumption");
                walletOperationService.update(overageWO);

            } else {
                // Update the last rated transaction
                BigDecimal rest = canceledQuantity.negate();
                ServiceTemplate serviceTemplate = canceledWO.getServiceInstance().getServiceTemplate();
                String overageUnit = (String) cfiService.getCFValue(serviceTemplate, "overageUnit");
                Double multiplier = ((Map<String, Double>) cfiService
                        .getInheritedCFValueByKey(appProvider, "CF_P_USAGE_UNITS", overageUnit))
                        .get("multiplier");
                BigDecimal newOverageQuantity = rest.divide(BigDecimal.valueOf(multiplier), BaseEntity.NB_DECIMALS, RoundingMode.HALF_EVEN);

                overageWO.setQuantity(newOverageQuantity);
                recomputeWOAmounts(overageWO);
                overageWO.setStatus(WalletOperationStatusEnum.OPEN);
                walletOperationService.update(overageWO);

                return BigDecimal.ZERO;
            }
        }

        return canceledQuantity;
    }

    public void recomputeWOAmounts(WalletOperation wo) {
        BigDecimal quantity = wo.getQuantity();
        wo.setAmountWithoutTax(wo.getUnitAmountWithoutTax().multiply(quantity));
        wo.setAmountTax(wo.getUnitAmountTax().multiply(quantity));
        wo.setAmountWithTax(wo.getAmountWithoutTax().add(wo.getAmountTax()));
    }

    @SuppressWarnings("unchecked")
    private void amendDuplicateWOFromSharedPool(WalletOperation canceledWO) {
        ServiceTemplate serviceTemplate = canceledWO.getServiceInstance().getServiceTemplate();
        BigDecimal qantityToRestore = canceledWO.getQuantity();

        // cancel the wo's associated Over WO
        String overageWOId = canceledWO.getParameter2().replace("DEDUCTED_FROM_POOL_", "");
        if (!StringUtils.isBlank(overageWOId)) {
            WalletOperation overageWO = walletOperationService.findById(Long.valueOf(overageWOId));
            if (overageWO != null) {
                overageWO.setStatus(WalletOperationStatusEnum.CANCELED);
                overageWO.setParameterExtra("Canceled by AmendDuplicateConsumptionJOB!");
                walletOperationService.update(overageWO);

                qantityToRestore = canceledWO.getQuantity().subtract(overageWO.getInputQuantity());

                // Cancel Over RT if already exists
                RatedTransaction overageRT = overageWO.getRatedTransaction();
                if (overageRT != null) {
                    overageRT = ratedTransactionService.refreshOrRetrieve(overageRT);
                    if (overageRT.getStatus() == RatedTransactionStatusEnum.OPEN) {
                        overageRT.setStatus(RatedTransactionStatusEnum.CANCELED);
                        overageRT.setParameterExtra("Canceled by AmendDuplicateConsumptionJOB!");
                        ratedTransactionService.update(overageRT);
                    }
                }
            }
        }

        // reset WO qt to its pool
        String agencyCode = canceledWO.getSubscription().getUserAccount().getCode();
        String agencyCounterKey = agencyCode + "_value";

        Map<String, Double> offerAgenciesCountersMap = (Map<String, Double>) cfiService
                .getCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, canceledWO.getOperationDate());

        if (offerAgenciesCountersMap == null || offerAgenciesCountersMap.get(agencyCounterKey) == null) {
            throw new IllegalStateException(String.format("Pool counter not yet initialized for operation date %tF " +
                    "ServiceTemplate=%s Agency=%s", canceledWO.getOperationDate(),serviceTemplate.getCode(),  agencyCode));
        }
        Double agencyCounter = offerAgenciesCountersMap.get(agencyCounterKey);
        offerAgenciesCountersMap.put(agencyCounterKey, agencyCounter + qantityToRestore.doubleValue());

        cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, offerAgenciesCountersMap, canceledWO.getOperationDate());
        serviceTemplateService.update(serviceTemplate);
    }
}