package org.meveo.admin.job;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRProcessingStatus;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitUsageRatingJobBean {

    @Inject
    private Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    @Rejected
    private Event<Serializable> rejectededEdrProducer;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, EDR edr) throws BusinessException {

        log.debug("Processing EDR {}", edr.getId());

        try {

            usageRatingService.ratePostpaidUsage(edr);

            if (edr.getRatingRejectionReason() == null) {
                result.registerSucces();
            } else {
                throw new BusinessException(edr.getRatingRejectionReason());
            }
        } catch (BusinessException e) {
            if (!(e instanceof InsufficientBalanceException)) {
                log.error("Failed to unit usage rate for {}", edr.getId(), e);
            }
            throw e;
        }
    }

    /**
     * Mark EDR as rejected along with a rejection reason and fire a rejected EDR event
     * 
     * @param result Job execution result
     * @param edrId EDR identifier
     * @param e
     * @throws BusinessException
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerFailedEdr(JobExecutionResultImpl result, Long edrId, Exception e) throws BusinessException {

        String rejectReason = StringUtils.truncate(e.getMessage(), 255, true);
        EDRProcessingStatus edrStatus = new EDRProcessingStatus(edrId, EDRStatusEnum.REJECTED, rejectReason);
        edrService.getEntityManager().persist(edrStatus);

        EDR edr = edrService.findById(edrId);

        rejectededEdrProducer.fire(edr);

        // Adding as a line, so
        StringBuilder aLine = new StringBuilder("Edr Id : ").append(edr.getId()).append(" RejectReason : ").append(rejectReason).append(" eventDate:").append(edr.getEventDate())
            .append("originBatch:").append(edr.getOriginBatch()).append("originRecord:").append(edr.getOriginRecord()).append("quantity:").append(edr.getQuantity())
            .append("subscription:").append(edr.getSubscription().getCode()).append("access:").append(edr.getAccessCode()).append("parameter1:").append(edr.getParameter1())
            .append("parameter2:").append(edr.getParameter2()).append("parameter3:").append(edr.getParameter3()).append("parameter4:").append(edr.getParameter4());
        result.registerError(aLine.toString());
    }
}