package org.meveo.admin.job;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
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
    private Event<Serializable> rejectededEdrProducer;

    @EJB
    private UnitUsageRatingJobBean unitUsageRatingJobBean;

    // @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, User currentUser, Long edrId) {
        log.debug("Running for user={}, edrId={}", currentUser, edrId);

        EDR edr = null;
        try {
            edr = edrService.findById(edrId, currentUser.getProvider());
            if (edr == null) {
                return;
            }
            usageRatingService.ratePostpaidUsage(edr, currentUser);
            
            if (edr.getStatus() == EDRStatusEnum.RATED) {
                edr = edrService.updateNoCheck(edr);
                result.registerSucces();
            } else {
                edr = edrService.updateNoCheck(edr);
                rejectededEdrProducer.fire(edr);
                result.registerError(edr.getId(), edr.getRejectReason());
                result.addReport("EdrId : " + edr.getId() + " RejectReason : " + edr.getRejectReason());
            }
        } catch (BusinessException e) {
            if (!(e instanceof InsufficientBalanceException)) {
                log.error("Failed to unit usage rate for {}", edrId, e);
            }
            unitUsageRatingJobBean.registerFailedEdr(result, edr, e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerFailedEdr(JobExecutionResultImpl result, EDR edr, Exception e) {

        edr = edrService.updateNoCheck(edr);
        rejectededEdrProducer.fire(edr);
        result.registerError(edr.getId(), e != null ? e.getMessage() : edr.getRejectReason());
        result.addReport("EdrId : " + edr.getId() + " RejectReason : " + (e != null ? e.getMessage() : edr.getRejectReason()));
    }
}