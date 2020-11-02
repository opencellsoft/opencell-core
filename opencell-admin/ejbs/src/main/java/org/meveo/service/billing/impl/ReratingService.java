package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;

/**
 * Service to handle wallet operation re-rating when service parameters or tariffs change
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class ReratingService implements Serializable {

    private static final long serialVersionUID = -1786938564004811233L;

    /** Logger. */
    @Inject
    private Logger log;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private RatingService ratingService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

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
            rerate(null, null, null, chargeInstance, fromDate, rerateInvoiced);
        }
    }

    /**
     * Re-rate wallet operations of a given offer and service template. Start date might be earlier for recurring charges where given date falls within rating period boundry
     * (wallet operation start/endDate)
     * 
     * @param reratingInfos Re-rating information
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     */
//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void rerate(List<ReratingInfo> reratingInfos, boolean rerateInvoiced) {

        for (ReratingInfo reratingInfo : reratingInfos) {
            rerate(reratingInfo.getOfferTemplateId(), reratingInfo.getServiceTemplateId(), reratingInfo.getChargeType(), null, reratingInfo.getFromDate(), rerateInvoiced);
        }
    }

    /**
     * Re-rate wallet operations of a given charge instance OR offer, service template and chargeType. Start date might be earlier for recurring charges where given date falls
     * within rating period boundary (wallet operation start/endDate)
     * 
     * @param offerTemplateId Offer template identifier
     * @param serviceTemplateId Service template identifier
     * @param chargeType Charge type
     * @param chargeInstance Charge instance. Either charge instance OR offer, serviceTemplate and chargeType must be provided
     * @param fromDate Date to re-rate from.
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     */
    @SuppressWarnings("unchecked")
//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void rerate(Long offerTemplateId, Long serviceTemplateId, ChargeMainTypeEnum chargeType, ChargeInstance chargeInstance, Date fromDate, boolean rerateInvoiced) {

        boolean sameTx = true; // chargeInstance != null;
        chargeType = chargeInstance != null ? chargeInstance.getChargeMainType() : chargeType;

        EntityManager em = getEntityManager();

        // One shot and usage type charges are rerated by marking WO with status "TO_RERATE" and then initiating rerating of the same WO record
        if (chargeType == ChargeMainTypeEnum.ONESHOT || chargeType == ChargeMainTypeEnum.USAGE) {

            TypedQuery<Long> query = null;
            if (chargeInstance != null) {
                query = em.createNamedQuery(
                    rerateInvoiced ? "WalletOperation.listWOIdsToRerateOneShotOrUsageChargeIncludingInvoicedByChargeInstance" : "WalletOperation.listWOIdsToRerateOneShotOrUsageChargeNotInvoicedByChargeInstance",
                    Long.class).setParameter("fromDate", fromDate).setParameter("chargeInstance", chargeInstance);

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
                    ratingService.reRate(woIdsToRerate, false);
                } else {
                    ratingService.reRateInNewTx(woIdsToRerate, false);
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
}