package org.meveo.admin.job;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.event.qualifier.Rejected;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.EDR;
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
    Event<Serializable> rejectededEdrProducer;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, User currentUser, Long edrId) {
    	log.debug("Running for user={}, edrId={}", currentUser, edrId);

    	try {
            EDR edr = edrService.findById(edrId);
            usageRatingService.ratePostpaidUsage(edr, currentUser);
            
            edrService.updateNoCheck(edr);

            if (edr.getStatus() == EDRStatusEnum.RATED) {
                result.registerSucces();
            } else {
                rejectededEdrProducer.fire(edr);
                result.registerError(edrId, edr.getRejectReason());
            }
            result.registerSucces();
        } catch (Exception e) {
            log.error("Failed to unit usage rate for {}", edrId, e);
            rejectededEdrProducer.fire("" + edrId);
            result.registerError(edrId, e.getMessage());
        }
    }
}