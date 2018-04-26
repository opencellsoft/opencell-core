package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.RatingStatus;
import org.meveo.model.billing.RatingStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitRecurringRatingJobBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    protected Logger log;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long ID_activeRecurringChargeInstance, Date maxDate) {
        long startDate = System.currentTimeMillis();
        log.debug("Running with activeRecurringChargeInstanceID={}", ID_activeRecurringChargeInstance);
        try {
            RatingStatus ratingStatus = recurringChargeInstanceService.applyRecurringCharge(ID_activeRecurringChargeInstance, maxDate);
            if (ratingStatus.getNbRating() == 1) {
                result.registerSucces();
            } else if (ratingStatus.getNbRating() > 1) {
                result.registerWarning(ID_activeRecurringChargeInstance + " rated " + ratingStatus.getNbRating() + " times");
            } else {
                if(ratingStatus.getStatus() != RatingStatusEnum.NOT_RATED_FALSE_FILTER) {
                    result.registerWarning(ID_activeRecurringChargeInstance + " not rated");
                }
            }
            log.debug("After registerWarning:" + (System.currentTimeMillis() - startDate));
        } catch (BusinessException e) {
            result.registerError(ID_activeRecurringChargeInstance, e.getMessage());
        }
        log.debug("end executed!");
    }
}
