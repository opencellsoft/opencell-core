package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.mediation.MediationsettingService;
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
public class ReratingService extends PersistenceService<WalletOperation> implements Serializable {

    private static final long serialVersionUID = -1786938564004811233L;

    /** Logger. */
    @Inject
    private Logger log;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private OneShotRatingService oneShotRatingService;

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    private EdrService edrService;

    @Inject
    private RatedTransactionService ratedTransactionService;
    

    @Inject
    private UsageRatingService usageRatingService;
    
    @Inject
    private MediationsettingService mediationsettingService;

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
                    methodCallingUtils.callMethodInNewTx(()->reRateInNoTx(woIdsToRerate, false));
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

    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
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
    @SuppressWarnings("unchecked")
    public void reRate(Long operationToRerateId, boolean useSamePricePlan) throws BusinessException, RatingException {

        WalletOperation operationToRerate = getEntityManager().find(WalletOperation.class, operationToRerateId);
        if (operationToRerate.getStatus() != WalletOperationStatusEnum.TO_RERATE) {
            return;
        }
        // Change related OPEN or REJECTED Rated transaction status to CANCELED
        RatedTransaction ratedTransaction = operationToRerate.getRatedTransaction();
        if (ratedTransaction != null && (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN || ratedTransaction.getStatus() == RatedTransactionStatusEnum.REJECTED)) {
            ratedTransaction.changeStatus(RatedTransactionStatusEnum.RERATED);
        }

        // Check triggered EDRS and check status/rerate each of them
        List<EDR> tEdrs = getEntityManager().createNamedQuery("EDR.getByWO")
                .setParameter("WO_IDS", List.of(operationToRerate.getId()))
                .getResultList();
        tEdrs = tEdrs.stream().filter(e -> e.getStatus() != EDRStatusEnum.CANCELLED).collect(Collectors.toList());
        WalletOperation newWO = null;
        
		// when WO has Discounted WO and these discounted WO has TriggeredEDR
	    List<WalletOperation> discountWos=walletOperationService.findByDiscountedWo(operationToRerateId);
		if(CollectionUtils.isNotEmpty(discountWos) && operationToRerate.getEdr() != null){
			operationToRerate.setStatus(WalletOperationStatusEnum.CANCELED);
			operationToRerate.getEdr().setStatus(EDRStatusEnum.OPEN);
			edrService.update(operationToRerate.getEdr());
			getEntityManager().createNamedQuery("EDR.deleteByWO")
						.setParameter("WO_IDS", List.of(operationToRerate.getId()))
					.executeUpdate();
			return;
		}
	    

        // To manage case when 1 WO have more than 1 T.EDR
        Map<Long, WalletOperation> oldWOAndNewWO = new HashMap<>();
        boolean isEdrTreated = false;
        if (CollectionUtils.isNotEmpty(tEdrs)) {

            // For each EDR that was triggered by WO
            for (EDR edr : tEdrs) {

                // Find WOs that triggered EDR has produced
                List<WalletOperation> triggerWalletOperations = walletOperationService.getEntityManager().createQuery("from WalletOperation o where o.edr.id=:edrId").setParameter("edrId", edr.getId()).getResultList();
                if(CollectionUtils.isNotEmpty(triggerWalletOperations) && edr.getStatus() == EDRStatusEnum.RATED) {
                    WalletOperation triggerWalletOperation =  triggerWalletOperations.get(0);
                    if(triggerWalletOperation.getRatedTransaction() != null && triggerWalletOperation.getRatedTransaction().getStatus() == RatedTransactionStatusEnum.BILLED && triggerWalletOperation.getRatedTransaction().getInvoiceLine().getStatus() == InvoiceLineStatusEnum.BILLED && 
                            operationToRerate.getRatedTransaction() != null && (operationToRerate.getRatedTransaction().getStatus() != RatedTransactionStatusEnum.BILLED || operationToRerate.getRatedTransaction().getInvoiceLine().getStatus() != InvoiceLineStatusEnum.BILLED )) {
                        operationToRerate.setStatus(WalletOperationStatusEnum.F_TO_RERATE);
                        operationToRerate.setRejectReason("Wallet operation [id=" + operationToRerate.getId() + "] cannot be rerated because triggered EDR [id=" + edr.getId() + "] is already billed.");
                        walletOperationService.update(operationToRerate);
                        operationToRerate.getRatedTransaction().setStatus(RatedTransactionStatusEnum.CANCELED);
                        ratedTransactionService.update(operationToRerate.getRatedTransaction());
                        isEdrTreated = true;
                        continue;
                        // Cancel WO that was produced by a triggered EDR 
                    }else {
                        triggerWalletOperation.setStatus(WalletOperationStatusEnum.CANCELED);
                        triggerWalletOperation.setRejectReason("Origin wallet operation [id= "+operationToRerateId+"}] has been rerated");
                    }
                    walletOperationService.update(triggerWalletOperation);
                    
                }
                // if edr status is one of REJECTED, CANCELLED or OPEN => Triggered EDRs are simply CANCELLED with reason "Origin wallet operation [id={{id}}] has been rerated", and original WO is rerated
                if (edr.getStatus() == EDRStatusEnum.REJECTED || edr.getStatus() == EDRStatusEnum.CANCELLED || edr.getStatus() == EDRStatusEnum.OPEN) {
                    newWO = createNewWO(oldWOAndNewWO, operationToRerate, useSamePricePlan);

                    edr.setStatus(EDRStatusEnum.CANCELLED);
                    edr.setRejectReason("Origin wallet operation [id=" + operationToRerate.getId() + "] has been rerated");
                    operationToRerate.setStatus(WalletOperationStatusEnum.RERATED);

                    walletOperationService.update(operationToRerate);

                    if (newWO==null) {
                        // that mean no newWO are created, this is the case when we have 1 WO with more than 1 T.EDR, we skip creating of T.EDR
                        continue;
                    }

                    // Create new T.EDRs
                    List<EDR> newTEdrs = oneShotRatingService.instantiateTriggeredEDRs(newWO, edr, false, true);
                    Optional.ofNullable(newTEdrs).orElse(Collections.emptyList())
                            .forEach(newEdr -> edrService.create(newEdr));

                } else if (edr.getStatus() == EDRStatusEnum.RATED &&
                        operationToRerate.getRatedTransaction() != null && (operationToRerate.getRatedTransaction().getStatus() != RatedTransactionStatusEnum.BILLED || operationToRerate.getRatedTransaction().getInvoiceLine().getStatus() != InvoiceLineStatusEnum.BILLED)) {
                    newWO = createNewWO(oldWOAndNewWO, operationToRerate, useSamePricePlan);

                    // Triggered EDRs, their WOs and RTs are CANCELLED with reason "Origin wallet operation [id={{id}}] has been rerated", and original WO is rerated
                    edr.setStatus(EDRStatusEnum.CANCELLED);
                    edr.setRejectReason("Origin wallet operation [id=" + operationToRerate.getId() + "] has been rerated");
                    operationToRerate.setStatus(WalletOperationStatusEnum.RERATED);
                    operationToRerate.getRatedTransaction().setStatus(RatedTransactionStatusEnum.CANCELED);
                    operationToRerate.getRatedTransaction().setRejectReason("Origin wallet operation [id=" + operationToRerate.getId() + "] has been rerated");

                    // Find rated transaction linked to T.EDR
                    RatedTransaction linkedRT = ratedTransactionService.findByEDR(edr.getId());
                    if(linkedRT != null) {
                        linkedRT.setStatus(RatedTransactionStatusEnum.CANCELED);
                        linkedRT.setRejectReason("Origin wallet operation [id=" + operationToRerate.getId() + "] has been rerated");
                        ratedTransactionService.update(linkedRT);
                    }
                    walletOperationService.update(operationToRerate);
                    ratedTransactionService.update(operationToRerate.getRatedTransaction());
                    if (newWO==null) {
                        // that mean no newWO are created, this is the cas when we have 1 WO woth more than 1 T.EDR, we skip creating of T.EDR
                        continue;
                    }
                    // Create new T.EDRs
                    List<EDR> edrs = usageRatingService.instantiateTriggeredEDRs(newWO, operationToRerate.getEdr(), false, false);
                    for (EDR e : edrs) {
                        e.setWalletOperation(newWO);
                        e.setEventKey(mediationsettingService.getEventKeyFromEdrVersionRule(e));
                        e.setEventVersion(edr.getEventVersion() != null ? edr.getEventVersion() : null);
                        edrService.create(e);
                    }

                } else if (edr.getStatus() == EDRStatusEnum.RATED &&
                        operationToRerate.getRatedTransaction() != null && operationToRerate.getRatedTransaction().getStatus() == RatedTransactionStatusEnum.BILLED && operationToRerate.getRatedTransaction().getInvoiceLine().getStatus() == InvoiceLineStatusEnum.BILLED) {
                    // Triggered EDRs, their WOs and RTs are untouched, original WO fails to rerate F_TO_RERATE with reason in error message below
                    operationToRerate.setStatus(WalletOperationStatusEnum.F_TO_RERATE);
                    operationToRerate.setRejectReason("Wallet operation [id=" + operationToRerate.getId() + "] cannot be rerated because triggered EDR [id=" + edr.getId() + "] is already billed.");
                    walletOperationService.update(operationToRerate);

                    return;
                }else {
                    newWO = createNewWO(oldWOAndNewWO, operationToRerate, useSamePricePlan);
                    edr.setStatus(EDRStatusEnum.CANCELLED);
                    edr.setRejectReason("Origin wallet operation [id=" + operationToRerate.getId() + "] has been rerated");
                    if (newWO==null) {
                        // that mean no newWO are created, this is the cas when we have 1 WO woth more than 1 T.EDR, we skip creating of T.EDR
                        continue;
                    }
                    List<EDR> edrs = usageRatingService.instantiateTriggeredEDRs(newWO, operationToRerate.getEdr(), false, false);
                    for (EDR e : edrs) {
                        e.setWalletOperation(newWO);
                        e.setEventKey(mediationsettingService.getEventKeyFromEdrVersionRule(e));
                        e.setEventVersion(edr.getEventVersion() != null ? edr.getEventVersion() : null);
                        edrService.create(e);
                    }
                    walletOperationService.update(operationToRerate);
                }
            }

            getEntityManager().flush();

        } else {
            rateNewWO(operationToRerate, useSamePricePlan);
        }
        if(!isEdrTreated)
            operationToRerate.setStatus(WalletOperationStatusEnum.RERATED);
        operationToRerate.setUpdated(new Date());
        operationToRerate.setReratedWalletOperation(newWO);
        walletOperationService.update(operationToRerate);

    }

    private WalletOperation createNewWO(Map<Long, WalletOperation> oldWOAndNewWO, WalletOperation operationToRerate, boolean useSamePricePlan) {
        WalletOperation newWO;
        if (oldWOAndNewWO.get(operationToRerate.getId()) == null) {
            newWO = rateNewWO(operationToRerate, useSamePricePlan);
            oldWOAndNewWO.put(operationToRerate.getId(), newWO);
            return newWO;
        }
        return null;
    }

    private WalletOperation rateNewWO(WalletOperation oldWO, boolean useSamePricePlan) {
    	RatingResult ratingResult = oneShotRatingService.rateRatedWalletOperation(oldWO, useSamePricePlan);
    	for(WalletOperation wo : ratingResult.getWalletOperations()) {
    		create(wo);
    	} 
        return ratingResult.getWalletOperations().stream().filter(e -> e.getDiscountValue()==null).findFirst().orElse(null);
    }

}