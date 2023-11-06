package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;

/**
 * Service to handle wallet operation re-rating when service parameters or tariffs change.
 * 
 * Rerating can be initiated for a particular service instance, or an offer + service template + charge type combination, specifying a date to rerate from. An option is provided to choose to rerate only unbilled or all
 * wallet operations.
 * 
 * 
 * <pre>
 *

One shot or usage charge:
Select Wallet operations with operation date>="rerate from date".
 Mark Wallet operations to rerate:
  For each BILLED wallet operation:
    - create a refund - an identical and negative wallet operation with status OPEN. A field refundsWalletOperation points to an original Wallet operation.
    - change wallet operation status TO_RERATE
  For each NOT billed wallet operation:
    - change related Rated transaction (unbilled) status to CANCELED
    - change wallet operation status to TO_RERATE
 Rerate wallet operations with status "TO_RERATE" (same as running a rerating job)
  Cancel Rated transaction if not canceled yet
    - if Wallet operation has a BILLED rated transaction, wallet operation status will be changed to TREATED (as if rerating was not possible - definatelly some anomoly)
    - if Wallet operation has a rated transaction with status OPEN (and REJECTED - dont know why??) - change rated transaction status to CANCELED
  Create an identical Wallet operation with status OPEN and calculate its amount with a usual rating logic
  Change the status of the wallet operation from TO_RERATE to RERATED and add a link to a new wallet operation (field reratedWalletOperation)


A final outcome for One shot and Usage charges:
For invoiced Wallet Operations:
    Invoiced original WO:
       status=RERATED, reratedWO = newWO
    Refunded WO:
       status=OPEN, refundsWO =originalWO, negative amount
    New/rerated WO
       status=OPEN
For not-invoiced Wallet Operations:
    Not-Invoiced original WO:
       status=RERATED, reratedWO = newWO. Rated transactions are set to status CANCELED
    New/rerated WO
       status=OPEN



Recurring:
Determine unique charge instances and min(startDate) (will be refered to as fromDate) and max(endDate) (will be refered to as toDate) for the wallet operations with end date>="rerate from date"
 For each chargeInstance
   - Change NOT billed wallet operation status to CANCELED for wallet operations with startDate>= fromDate
   - Refund already BILLED wallet operations with startDate>= fromDate by creating an identical wallet operation with a negated amount and status OPEN
   - reset chargedToDate to a fromDate
   - rate charge up to toDate as usual rating

Final outcome for recurring charges:
For invoiced Wallet Operations:
    Invoiced original WO:
      no changes
    Refunded WO:
       status=OPEN, refundsWO =originalWO, negative amount
    New/rerated WOs
       status=OPEN, no relation to original WO.
For not-invoiced Wallet Operations:
    Not-Invoiced original WO:
       status=CANCELED. Rated transactions are set to status CANCELED
    New/rerated WOs
       status=OPEN, no relation to original WO.
 * </pre>
 * 
 * </pre>
 * 
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class ReratingService extends RatingService implements Serializable {

    private static final long serialVersionUID = -1786938564004811233L;

    /** Logger. */
    @Inject
    private Logger log;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    private EdrService edrService;

    /**
     * Re-rate service instance charges
     * 
     * @param serviceInstance Service instance to re-rate
     * @param fromDate Date to re-rate from
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     */
    public void rerate(ServiceInstance serviceInstance, Date fromDate, boolean rerateInvoiced) {

        log.info("Will re-rate service instance {} from date {}", serviceInstance, DateUtils.formatAsDate(fromDate));

        // Re-rate each charge instance.
        // Another alternative, in case there are more than one charge of same type is to send offer, service template and a charge type instead of charge instance as parameter
        for (ChargeInstance chargeInstance : serviceInstance.getChargeInstances()) {
            rerate(null, null, null, chargeInstance, fromDate, rerateInvoiced, null);
        }
    }

    /**
     * Re-rate wallet operations of a given offer and service template. Start date might be earlier for recurring charges where given date falls within rating period boundry (wallet operation start/endDate)
     * 
     * @param reratingInfos Re-rating information
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     */
//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void rerate(Collection<ReratingInfo> reratingInfos, boolean rerateInvoiced) {

        for (ReratingInfo reratingInfo : reratingInfos) {
            rerate(reratingInfo.getOfferTemplateId(), reratingInfo.getServiceTemplateId(), reratingInfo.getChargeType(), null, reratingInfo.getFromDate(), rerateInvoiced, reratingInfo.getAdditionalCriteria());
        }
    }

    /**
     * Re-rate wallet operations of a given charge instance OR offer, service template and chargeType. Start date might be earlier for recurring charges where given date falls within rating period boundary (wallet
     * operation start/endDate)
     * 
     * @param offerTemplateId Offer template identifier
     * @param serviceTemplateId Service template identifier
     * @param chargeType Charge type
     * @param chargeInstance Charge instance. Either charge instance OR offer, serviceTemplate and chargeType must be provided
     * @param fromDate Date to re-rate from.
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     * @param additionalCriteria Additional Wallet operation filtering criteria with field name as a key. Custom field names are prefixed by "CF." value.
     */
    @SuppressWarnings("unchecked")
//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void rerate(Long offerTemplateId, Long serviceTemplateId, ChargeMainTypeEnum chargeType, ChargeInstance chargeInstance, Date fromDate, boolean rerateInvoiced, Map<String, Object> additionalCriteria) {

        boolean sameTx = true; // chargeInstance != null;
        chargeType = chargeInstance != null ? chargeInstance.getChargeMainType() : chargeType;

        EntityManager em = getEntityManager();

        // Additional Wallet operation filtering criteria with field name as a key. Custom field names are prefixed by "CF." value.
        // Right now only a single CF field is supported as additional filtering criteria
        String cfName = null;
        Object cfValue = null;
        if (additionalCriteria != null) {
            for (Entry<String, Object> additionalCriteriaEntry : additionalCriteria.entrySet()) {
                if (additionalCriteriaEntry.getKey().startsWith("CF.")) {
                    cfName = additionalCriteriaEntry.getKey().substring(3);
                    cfValue = additionalCriteriaEntry.getValue();
                }
            }
        }

        // One shot and usage type charges are rerated by marking WO with status "TO_RERATE" and then initiating rerating of the same WO record
        if (chargeType == ChargeMainTypeEnum.ONESHOT || chargeType == ChargeMainTypeEnum.USAGE) {

            TypedQuery<Long> query = null;
            if (chargeInstance != null) {
                query = em.createNamedQuery(
                    rerateInvoiced ? "WalletOperation.listWOIdsToRerateOneShotOrUsageChargeIncludingInvoicedByChargeInstance" : "WalletOperation.listWOIdsToRerateOneShotOrUsageChargeNotInvoicedByChargeInstance",
                    Long.class).setParameter("fromDate", fromDate).setParameter("chargeInstance", chargeInstance);

                // In case of using custom field as additional search criteria
            } else if (cfName != null && cfValue != null) {

                String sqlListWOIdsToRerateOneShotOrUsageChargeNotInvoicedByOfferAndServiceTemplateWithCF = "select wo.id from WalletOperation wo left join wo.ratedTransaction rt where (wo.ratedTransaction is null or rt.invoiceLine is null or rt.invoiceLine.status <> org.meveo.model.billing.InvoiceLineStatusEnum.BILLED) and wo.operationDate>=:fromDate and wo.status in ('OPEN', 'TREATED') and wo.chargeInstance.chargeType=:chargeType and wo.offerTemplate.id=:offer and wo.serviceInstance.serviceTemplate.id=:serviceTemplate ";
                String sqlListWOIdsToRerateOneShotOrUsageChargeIncludingInvoicedByOfferAndServiceTemplateWithCF = "select wo.id from WalletOperation wo where wo.operationDate>=:fromDate and wo.status in ('OPEN', 'TREATED') and wo.chargeInstance.chargeType=:chargeType and wo.offerTemplate.id=:offer and wo.serviceInstance.serviceTemplate.id=:serviceTemplate ";

                String sql = rerateInvoiced ? sqlListWOIdsToRerateOneShotOrUsageChargeIncludingInvoicedByOfferAndServiceTemplateWithCF : sqlListWOIdsToRerateOneShotOrUsageChargeNotInvoicedByOfferAndServiceTemplateWithCF;
                sql = sql + " and " + (cfValue instanceof Long ? "bigIntFromJson" : "varcharFromJson") + "(wo.cfValues," + cfName + ")=:cfValue";

                query = em.createQuery(sql, Long.class).setParameter("fromDate", fromDate).setParameter("chargeType", chargeType == ChargeMainTypeEnum.ONESHOT ? "O" : "U").setParameter("offer", offerTemplateId)
                    .setParameter("serviceTemplate", serviceTemplateId).setParameter("cfValue", cfValue);

            } else {
                query = em
                    .createNamedQuery(rerateInvoiced ? "WalletOperation.listWOIdsToRerateOneShotOrUsageChargeIncludingInvoicedByOfferAndServiceTemplate"
                            : "WalletOperation.listWOIdsToRerateOneShotOrUsageChargeNotInvoicedByOfferAndServiceTemplate",
                        Long.class)
                    .setParameter("fromDate", fromDate).setParameter("chargeType", chargeType == ChargeMainTypeEnum.ONESHOT ? "O" : "U").setParameter("offer", offerTemplateId)
                    .setParameter("serviceTemplate", serviceTemplateId);

            }

            List<Long> woIdsToRerate = query.getResultList();

            if (chargeInstance != null) {
                log.trace("Found {} Wallet operations to rerate for charge instance {} of {} type and from date {}", woIdsToRerate.size(), chargeInstance.getId(), chargeType, fromDate);
            } else {
                log.trace("Found {} Wallet operations to rerate for offer template {}, service template {}, charge type {} and from date {}", woIdsToRerate.size(), offerTemplateId, serviceTemplateId, chargeType,
                    fromDate);
            }

            if (woIdsToRerate.isEmpty()) {
                return;
            }
            int countToRerate = 0;
            if (sameTx) {
                countToRerate = walletOperationService.markToRerate(woIdsToRerate, rerateInvoiced);
            } else {
                countToRerate = walletOperationService.markToRerateInNewTx(woIdsToRerate, rerateInvoiced);
            }
            if (countToRerate > 0) {

                if (sameTx) {
                    reRate(woIdsToRerate, false);
                } else {
                    methodCallingUtils.callMethodInNewTx(() -> reRateInNoTx(woIdsToRerate, false));
                }
            }

            // Recurring charges will result in refunding and/or canceling WOs, retroceding recurringChargeInstance.chargedToDate value and recreating them again via a standard
            // recurring rating
            // process
        } else if (chargeType == ChargeMainTypeEnum.RECURRING) {

            Query query = null;
            if (chargeInstance != null) {
                query = em
                    .createNamedQuery(
                        rerateInvoiced ? "WalletOperation.listWOsInfoToRerateRecurringChargeIncludingInvoicedByChargeInstance" : "WalletOperation.listWOsInfoToRerateRecurringChargeNotInvoicedByChargeInstance")
                    .setParameter("fromDate", fromDate).setParameter("chargeInstance", chargeInstance);

            } else if (cfName != null && cfValue != null) {

                String sqlListWOsInfoToRerateRecurringChargeIncludingInvoicedByOfferAndServiceTemplateWithCF = "select wo.chargeInstance.id, min(wo.startDate), max(wo.endDate) from WalletOperation wo where wo.endDate>:fromDate and wo.status in ('OPEN', 'TREATED', 'TO_RERATE') and wo.chargeInstance.chargeType = 'R' and wo.offerTemplate.id=:offer and wo.serviceInstance.serviceTemplate.id=:serviceTemplate";
                String sqlListWOsInfoToRerateRecurringChargeNotInvoicedByOfferAndServiceTemplateWithCF = "select wo.chargeInstance.id, min(wo.startDate), max(wo.endDate) from WalletOperation wo left join wo.ratedTransaction rt where (wo.ratedTransaction is null or  rt.invoiceLine is null or rt.invoiceLine.status <> org.meveo.model.billing.InvoiceLineStatusEnum.BILLED) and wo.endDate>:fromDate and wo.status in ('OPEN', 'TREATED', 'TO_RERATE') and wo.chargeInstance.chargeType = 'R' and wo.offerTemplate.id=:offer and wo.serviceInstance.serviceTemplate.id=:serviceTemplate";

                String sql = rerateInvoiced ? sqlListWOsInfoToRerateRecurringChargeIncludingInvoicedByOfferAndServiceTemplateWithCF : sqlListWOsInfoToRerateRecurringChargeNotInvoicedByOfferAndServiceTemplateWithCF;
                sql = sql + " and " + (cfValue instanceof Long ? "bigIntFromJson" : "varcharFromJson") + "(wo.cfValues," + cfName + ")=:cfValue";

                sql = sql + " group by wo.chargeInstance.id";

                query = em.createQuery(sql).setParameter("fromDate", fromDate).setParameter("offer", offerTemplateId).setParameter("serviceTemplate", serviceTemplateId).setParameter("cfValue", cfValue);

            } else {

                query = em
                    .createNamedQuery(rerateInvoiced ? "WalletOperation.listWOsInfoToRerateRecurringChargeIncludingInvoicedByOfferAndServiceTemplate"
                            : "WalletOperation.listWOsInfoToRerateRecurringChargeNotInvoicedByOfferAndServiceTemplate")
                    .setParameter("fromDate", fromDate).setParameter("offer", offerTemplateId).setParameter("serviceTemplate", serviceTemplateId);
            }

            List<Object[]> rerateInfos = query.getResultList();

            if (chargeInstance != null) {
                log.trace("Found {} Wallet operations to rerate for charge instance {} of {} type and from date {}", rerateInfos.size(), chargeInstance.getId(), chargeType, fromDate);
            } else {
                log.trace("Found {} Wallet operations to rerate for offer template {}, service template {}, charge type {} and from date {}", rerateInfos.size(), offerTemplateId, serviceTemplateId, chargeType, fromDate);
            }

            for (Object[] rerateInfo : rerateInfos) {

                // Termination date (chargeToDateOnTermination) is before the max endDate - so will rate up to termination (chargeToDateOnTermination) date only
                // If from date is past the termination date, a reimbursement will be applied
                Date rerateFromDate = (Date) rerateInfo[1];
                Date rerateToDate = (Date) rerateInfo[2];

                RecurringChargeInstance recurringChargeInstance = (RecurringChargeInstance) chargeInstance;
                if (chargeInstance == null) {
                    recurringChargeInstance = recurringChargeInstanceService.findById((Long) rerateInfo[0]);
                }
                Date chargeToDateOnTermination = recurringChargeInstance.getChargeToDateOnTermination();

                if (chargeToDateOnTermination != null && rerateFromDate.compareTo(chargeToDateOnTermination) <= 0) {
                    rerateToDate = chargeToDateOnTermination;
                }

                if (sameTx) {
                    recurringChargeInstanceService.rerateRecurringCharge((Long) rerateInfo[0], rerateFromDate, rerateToDate, rerateInvoiced);
                } else {
                    recurringChargeInstanceService.rerateRecurringChargeInNewTx((Long) rerateInfo[0], rerateFromDate, rerateToDate, rerateInvoiced);
                }
            }
        }
    }

    /**
     * Re-rate wallet operations. Each wallet operation is rerated independently and marked as "failed to rerate" if error occurs.
     *
     * @param woIds Ids of wallet operations to be re-rated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation re-rating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private void reRateInNoTx(List<Long> woIds, boolean useSamePricePlan) throws BusinessException, RatingException {

        for (Long woId : woIds) {

            try {
                methodCallingUtils.callMethodInNewTx(() -> reRate(woId, useSamePricePlan));

            } catch (RatingException e) {
                log.trace("Failed to rerate Wallet operation {}: {}", woId, e.getRejectionReason());
                walletOperationService.markAsFailedToRerateInNewTx(woId, e);

            } catch (BusinessException e) {
                log.error("Failed to rerate Wallet operation {}: {}", woId, e.getMessage(), e);
                walletOperationService.markAsFailedToRerateInNewTx(woId, e);
            }
        }
    }

    /**
     * Re-rate wallet operations together. Each wallet operation is rerated and marked as "failed to rerate" if error occurs.
     *
     * @param woIds Ids of wallet operations to be re-rated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation re-rating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void reRate(List<Long> woIds, boolean useSamePricePlan) throws BusinessException, RatingException {

        for (Long woId : woIds) {

            try {
                reRate(woId, useSamePricePlan);

            } catch (RatingException e) {
                log.trace("Failed to rerate Wallet operation {}: {}", woId, e.getRejectionReason());
                throw e;

            } catch (BusinessException e) {
                log.error("Failed to rerate Wallet operation {}: {}", woId, e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Rerate wallet operation. Mark wallet operation as RERATED with a link to a newly created wallet operation with new amounts
     *
     * @param operationToRerateId wallet operation to be rerated
     * @param useSamePricePlan true if same price plan will be used
     * @throws BusinessException business exception
     * @throws RatingException Operation rerating failure due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void reRate(Long operationToRerateId, boolean useSamePricePlan) throws BusinessException, RatingException {

        WalletOperation operationToRerate = getEntityManager().find(WalletOperation.class, operationToRerateId);
        if (operationToRerate.getStatus() != WalletOperationStatusEnum.TO_RERATE) {
            return;
        }

        try {
            // Validate that WO can be rerated and cancel all related WO, dicount WO, triggered EDR, RTs
            String rejectReason = validateAndCancelDerivedWosEdrsAndRts(Arrays.asList(operationToRerate), null);

            if (rejectReason != null) {
                operationToRerate.changeStatus(WalletOperationStatusEnum.F_TO_RERATE);
                operationToRerate.setRejectReason(rejectReason);
                walletOperationService.update(operationToRerate);
                return;
            }

            // Rate a copy of wallet operation, rate it, link with previous WO and trigger EDRS

            rerateWalletOperationAndInstantiateTriggeredEDRs(operationToRerate, useSamePricePlan);

        } catch (Exception e) {
            methodCallingUtils.callMethodInNewTx(() -> {
                WalletOperation operationToRerateFailed = getEntityManager().find(WalletOperation.class, operationToRerateId);
                operationToRerateFailed.changeStatus(WalletOperationStatusEnum.F_TO_RERATE);
                operationToRerateFailed.setRejectReason(e.getMessage());
                walletOperationService.update(operationToRerateFailed);
            });
            throw e;
        }

    }

    /**
     * Validate if Wallet operations, derived from a given EDR, contain any billed Rated Transactions and if not - cancel all triggered EDRs, WO, discount WO, RTs and adjust Invoice line amounts and quantities<br/>
     * Will return TRUE if any Wallet Operation, derived from a given EDR, including its a discount wallet operation or if any RT downwards the hierarchy was NOT billed yet
     * 
     * @param edr EDR to check
     * @return Will return TRUE if any Wallet Operation, derived from a given EDR, including its discount wallet operation or if any RT downwards the hierarchy was NOT billed yet
     */
    public boolean validateAndCancelDerivedWosEdrsAndRts(EDR edr) {

        // check if wallet operation related to EDR is treated
        List<WalletOperation> wos = (List<WalletOperation>) getEntityManager()
            .createQuery("from WalletOperation wo where wo.edr.id=:edrId and  wo.status in ('TREATED', 'TO_RERATE', 'OPEN', 'SCHEDULED' )", WalletOperation.class).setParameter("edrId", edr.getId())
            .setHint("org.hibernate.readOnly", true).getResultList();
        if (wos.isEmpty()) {
            return true;
        }

        String cancelationReason = "Received new version EDR[id=" + edr.getId() + "]";
        String errorReason = validateAndCancelDerivedWosEdrsAndRts(wos, cancelationReason);
        if (errorReason == null) {
            List<Long> woIdsToUpdate = wos.stream().map(wo -> wo.getId()).collect(Collectors.toList());
            getEntityManager().createNamedQuery("WalletOperation.cancelWOs").setParameter("updatedDate", new Date()).setParameter("rejectReason", cancelationReason).setParameter("ids", woIdsToUpdate).executeUpdate();

            return true;
        }
        return false;
    }

    /**
     * Validate if Wallet operation can be rerated and if so, cancel all triggered EDRs, WO, discount WO, RTs and adjust Invoice line amounts and quantities<br/>
     * Wallet operation can not be rerated if its a discount wallet operation or if any RT downwards the hierarchy was billed already
     * 
     * @param operationToRerate Wallet operation to rerate
     * @param Cancelation reason to use. If not provided, a default reason "Origin wallet operation #XXX has been rerated" will be used.
     * @return A reason if rerating should be rejected
     */
    @SuppressWarnings("unchecked")
    private String validateAndCancelDerivedWosEdrsAndRts(List<WalletOperation> operationsToRerate, String cancelationReason) {

        // Contains all triggered EDRs that are not in status CANCELED
        List<Long> edrIdsToUpdate = new ArrayList<>();
        // Contains all triggered and discount WOs that are not in status CANCELED
        List<Long> woIdsToUpdate = new ArrayList<>();
        // Contains all triggered and discount RTs that are not in status CANCELED
        List<Long> rtIdsToUpdate = new ArrayList<>();

        List<Long> woIdsToCheck = new ArrayList<>();
        List<Long> rtIdsToCheck = new ArrayList<Long>();

        for (WalletOperation operationToRerate : operationsToRerate) {
            // Discount operations can not be rerated - need to rerate a parent WO
            if (operationToRerate.getDiscountPlanType() != null) {
                if (operationsToRerate.size() == 1) {
                    return "Discount type operations are not reratable";
                }

            } else {

                // Set cancelation reason if not provided from outside.
                if (cancelationReason == null) {
                    cancelationReason = "Origin wallet operation #" + operationToRerate.getId() + " has been rerated";
                }

                if (operationToRerate.getRatedTransaction() != null) {
                    rtIdsToCheck.add(operationToRerate.getRatedTransaction().getId());
                }

                woIdsToCheck.add(operationToRerate.getId());
            }
        }

        EntityManager em = getEntityManager();

        // Traverse down the WO/discountWO/EDR tree to gather a list of WOs, EDRs and RTs that resulted from an operation being rerated. Supports unlimited levels of hierarchy.

        while (!woIdsToCheck.isEmpty()) {
            // Find any discount WOs associated - Discount WOs with Canceled status are omitted
            List<Object[]> discountWOInfos = em.createNamedQuery("WalletOperation.discountWoSummaryForRerating").setParameter("woIds", woIdsToCheck).getResultList();
            for (Object[] discounWOInfo : discountWOInfos) {
                // Supplement WoIdsToCheck list with discount WO id for further processing to determine any triggered EDRs/WOs/RTs
                Long woId = ((BigInteger) discounWOInfo[0]).longValue();
//                WalletOperationStatusEnum woStatus = WalletOperationStatusEnum.valueOf((String) discounWOInfo[1]);
                woIdsToCheck.add(woId);

                // WOs will be marked as canceled
                woIdsToUpdate.add(woId);

                // If WO has RT
                if (discounWOInfo[2] != null) {
                    Long rtId = ((BigInteger) discounWOInfo[2]).longValue();
                    rtIdsToCheck.add(rtId);
                }
            }

            // Find any EDR and their WO, RT that were triggered - EDRs with Canceled status are omitted
            List<Object[]> edrInfos = em.createNamedQuery("EDR.triggeredEDRSummaryForRerating").setParameter("woIds", woIdsToCheck).getResultList();
            woIdsToCheck = new ArrayList<Long>();
            List<Long> edrIdsToCheck = new ArrayList<Long>();

            for (Object[] edrInfo : edrInfos) {
                Long edrId = ((BigInteger) edrInfo[0]).longValue();
                edrIdsToUpdate.add(edrId);
                EDRStatusEnum edrStatus = EDRStatusEnum.valueOf((String) edrInfo[1]);
                if (edrStatus != EDRStatusEnum.OPEN) { // EDR with CANCELED status was already omitted in SQL
                    edrIdsToCheck.add(edrId);
                }
            }

            if (!edrIdsToCheck.isEmpty()) {
                List<Object[]> woInfos = em.createNamedQuery("WalletOperation.woSummaryForRerating").setParameter("edrIds", edrIdsToCheck).getResultList();
                for (Object[] woInfo : woInfos) {
                    Long woId = ((BigInteger) woInfo[0]).longValue();
                    // WalletOperationStatusEnum woStatus = WalletOperationStatusEnum.valueOf((String) woInfo[1]);
                    // Check further for triggered EDRs/WOs/RTs for WOs in other than Canceled status // WO with status CANCELED was already omitted in SQL
                    woIdsToCheck.add(woId);
                    // Non-Canceled WOs will be marked as canceled
                    woIdsToUpdate.add(woId);
                    if (woInfo[2] != null) {
                        Long rtId = ((BigInteger) woInfo[2]).longValue();
                        rtIdsToCheck.add(rtId);
                    }
                }
            }
        }

        // Lookup all Rated transactions with invoice lines and check that they have not been billed yet.
        boolean isBilled = false;
        Long rtBilled = null;
        Map<Long, ILAdjustments> ilAdjustments = new HashMap<>();
        if (!rtIdsToCheck.isEmpty()) {
            List<Object[]> rtIlInfos = em.createNamedQuery("RatedTransaction.rtSummaryForRerating").setParameter("rtIds", rtIdsToCheck).getResultList();
            for (Object[] rtIlInfo : rtIlInfos) {
                Long rtId = ((BigInteger) rtIlInfo[0]).longValue();
                // RatedTransactionStatusEnum rtStatus = RatedTransactionStatusEnum.valueOf((String) rtIlInfo[1]);
                rtIdsToUpdate.add(rtId);
                if (rtIlInfo[6] != null) {
                    Long ilId = ((BigInteger) rtIlInfo[6]).longValue();
                    InvoiceLineStatusEnum ilStatus = InvoiceLineStatusEnum.valueOf((String) rtIlInfo[7]);
                    // RT was already invoiced - WO can not be rerated
                    if (ilStatus == InvoiceLineStatusEnum.BILLED) {
                        isBilled = true;
                        rtBilled = rtId;
                        break;

                        // IL was not billed yet, so IL amounts have to be deducted
                    } else if (ilStatus == InvoiceLineStatusEnum.OPEN) {

                        Long brId = ((BigInteger) rtIlInfo[8]).longValue();

                        BillingRun billingRun = em.find(BillingRun.class, brId);
                        boolean averageUnitAmounts = billingRun.getBillingCycle() != null && !billingRun.getBillingCycle().isDisableAggregation() && billingRun.getBillingCycle().isAggregateUnitAmounts();

                        // Updating IL by sql misses unit amount rounding
                        // MathContext mc = new MathContext(appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
                        // unitPrice = quantity.compareTo(ZERO) == 0 ? amountWithoutTax : amountWithoutTax.divide(quantity, mc);

                        ILAdjustments adjustment = new ILAdjustments((BigDecimal) rtIlInfo[2], (BigDecimal) rtIlInfo[3], (BigDecimal) rtIlInfo[4], (BigDecimal) rtIlInfo[5], averageUnitAmounts);

                        if (ilAdjustments.containsKey(ilId)) {
                            ilAdjustments.get(ilId).addAdjustment(adjustment);
                        } else {
                            ilAdjustments.put(ilId, adjustment);
                        }
                    }
                }
            }
        }

        // Some RT was already billed - mark everything as FAILED TO RERATE
        if (isBilled) {
            return "Wallet operation can not be rerated as Rated Transaction #" + rtBilled + " was billed aleady";
        }

        // -----------
        // Rerating should go on

        // Mark all triggered EDRs as CANCELED
        if (!edrIdsToUpdate.isEmpty()) {
            em.createNamedQuery("EDR.cancelEDRs").setParameter("updatedDate", new Date()).setParameter("rejectReason", cancelationReason).setParameter("ids", edrIdsToUpdate).executeUpdate();
        }

        // Mark all triggered and discount WOs as CANCELED
        if (!woIdsToUpdate.isEmpty()) {
            em.createNamedQuery("WalletOperation.cancelWOs").setParameter("updatedDate", new Date()).setParameter("rejectReason", cancelationReason).setParameter("ids", woIdsToUpdate).executeUpdate();
        }

        // Mark all main, triggered and discount RTs as CANCELED
        if (!rtIdsToUpdate.isEmpty()) {
            em.createNamedQuery("RatedTransaction.cancelRTs").setParameter("updatedDate", new Date()).setParameter("rejectReason", cancelationReason).setParameter("ids", rtIdsToUpdate).executeUpdate();
        }

        // Update Invoice lines - adjust amounts and quantity
        Date date = new Date();
        for (Entry<Long, ILAdjustments> ilInfo : ilAdjustments.entrySet()) {
            Long ilId = ilInfo.getKey();
            ILAdjustments ilAdjustment = ilInfo.getValue();
            em.createNamedQuery("InvoiceLine.updateByIncrementalModeWoutDates" + (ilAdjustment.isAverageUnitAmounts() ? "WithAverageUnitAmounts" : ""))
                .setParameter("deltaAmountWithoutTax", ilAdjustment.getAmountWithoutTax()).setParameter("deltaAmountWithTax", ilAdjustment.getAmountWithTax()).setParameter("deltaAmountTax", ilAdjustment.getAmountTax())
                .setParameter("deltaQuantity", ilAdjustment.getQuantity()).setParameter("id", ilId).setParameter("now", date).executeUpdate();
        }

        // No reason to reject rerating
        return null;
    }

    /**
     * Rate a copy of Wallet operation to rerate, preserving or not a priceplan. New Wallet operation will be associated with a rerated WalletOperation. OldWalletOperation.reratedWalletOperation = new WalletOperation
     * 
     * @param operationToRerate Operation to rerate
     * @param useSamePricePlan Shall a same price plan will be used, or a new one should be looked up again
     */
    private void rerateWalletOperationAndInstantiateTriggeredEDRs(WalletOperation operationToRerate, boolean useSamePricePlan) {

        RatingResult ratingResult = null;
        try {

            // Create a new Wallet operation based on a Wallet operation that is being rerated
            ratingResult = rateRatedWalletOperation(operationToRerate, useSamePricePlan);
            WalletOperation newWO = ratingResult.getWalletOperations().stream().filter(e -> e.getDiscountPlanType() == null).findFirst().orElse(null);

            operationToRerate.changeStatus(WalletOperationStatusEnum.RERATED);
            operationToRerate.setReratedWalletOperation(newWO);
            walletOperationService.update(operationToRerate);

            // Trigger EDRs
            for (WalletOperation walletOperation : ratingResult.getWalletOperations()) {
                List<EDR> triggeredEdrs = instantiateTriggeredEDRs(walletOperation, operationToRerate.getEdr(), false, true);
                ratingResult.addTriggeredEDRs(triggeredEdrs);
            }

            if (ratingResult.getTriggeredEDRs() != null) {
                for (EDR triggeredEdr : ratingResult.getTriggeredEDRs()) {
                    edrService.create(triggeredEdr);
                }
            }
            for (WalletOperation walletOperation : ratingResult.getWalletOperations()) {
                walletOperationService.chargeWalletOperation(walletOperation);
            }

        } catch (EJBTransactionRolledbackException e) {
            if (ratingResult != null) {
                revertCounterChanges(ratingResult.getCounterChanges());
            }
            throw e;

        } catch (Exception e) {
            if (ratingResult != null) {
                revertCounterChanges(ratingResult.getCounterChanges());
            }
            throw e;
        }
    }

    /**
     * A class to track InvoiceLine adjustments when rerating
     */
    private class ILAdjustments extends Amounts {

        private static final long serialVersionUID = -4541738602032087593L;

        /**
         * Quantity
         */
        private BigDecimal quantity;

        /**
         * Shall unit amounts be averaged
         */
        private boolean averageUnitAmounts;

        /**
         * Instantiate with given amounts and quantity
         * 
         * @param amountWithoutTax Amount without tax
         * @param amountWithTax Amount with tax
         * @param amountTax Tax amount
         * @param quantity Quantity
         * @param averageUnitAmounts Shall unit amounts be averaged
         */
        public ILAdjustments(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, BigDecimal quantity, boolean averageUnitAmounts) {
            super(amountWithoutTax != null ? amountWithoutTax.negate() : null, amountWithTax != null ? amountWithTax.negate() : null, amountTax != null ? amountTax.negate() : null);
            this.quantity = quantity.negate();
            this.averageUnitAmounts = averageUnitAmounts;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public boolean isAverageUnitAmounts() {
            return averageUnitAmounts;
        }

        /**
         * Add adjustment
         * 
         * @param adjustmentToAdd Adjustment to add
         */
        public void addAdjustment(ILAdjustments adjustmentToAdd) {
            if (adjustmentToAdd == null) {
                return;
            }
            super.addAmounts(adjustmentToAdd);
            this.quantity = this.quantity.add(adjustmentToAdd.getQuantity());
        }
    }
}