package org.meveo.admin.job;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.async.OfferPoolInitializerAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class OfferPoolInitializerJobBean extends BaseJobBean {

    private static final String DATE_PATTERN = "MM/yyyy";

    private static final String OFFER_INIT_COUNT_QUERY = "select count(t.*) from " +
            "(select sub.user_account_id, offer.id offerId, count(sub.id) \n"
            + "from billing_subscription sub \n"
            + "inner join cat_offer_template offer on sub.offer_id=offer.id \n"
            + "where cast(offer.cf_values as json)#>>'{sharingLevel, 0, string}' = 'OF' \n"
            + "and sub.status='ACTIVE' \n"
            + "and sub.subscription_date <= :counterEndDate  \n"
            + "and sub.termination_date is null \n"
            + "group by sub.user_account_id, offerId \n"
            + "having count(sub.id) > 0) t";

    private static final String OFFERS_TO_INITILIZE = "select distinct offer.id \n"
            + "from billing_subscription sub \n"
            + "inner join cat_offer_template offer on sub.offer_id = offer.id \n"
            + "where cast(offer.cf_values as json)#>>'{sharingLevel, 0, string}' = 'OF' \n"
            + "and sub.status = 'ACTIVE' \n"
            + "and sub.subscription_date <= :counterEndDate  \n"
            + "and sub.termination_date is null \n"
            + "group by sub.user_account_id, offer.id \n"
            + "having count(sub.id) > 0";
    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private Logger log;

    @Inject
    private OfferPoolInitializerAsync offerPoolInitializerAsync;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running Job with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        Date counterEndDate = getCounterEndDate(jobInstance);

        try {
            BigInteger offerCountersNbr = (BigInteger) emWrapper.getEntityManager()
                    .createNativeQuery(OFFER_INIT_COUNT_QUERY)
                    .setParameter("counterEndDate", counterEndDate)
                    .getSingleResult();

            log.info("Total of offers/agencies counters to be initialized: {}", offerCountersNbr.longValue());
            result.setNbItemsToProcess(offerCountersNbr.longValue());

            @SuppressWarnings("unchecked")
            List<BigInteger> offerIds = emWrapper.getEntityManager().createNativeQuery(OFFERS_TO_INITILIZE)
                    .setParameter("counterEndDate", counterEndDate)
                    .getResultList();

            log.info("Total of sahred offers that pools should be initialized: {}", offerIds.size());

            SubListCreator<BigInteger> subListCreator = new SubListCreator<>(offerIds, nbRuns.intValue());
            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            while (subListCreator.isHasNext()) {
                futures.add(offerPoolInitializerAsync.launchAndForget(subListCreator.getNextWorkSet(), counterEndDate, result, lastCurrentUser));
                try {
                    Thread.sleep(waitingMillis.longValue());

                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            result.addReport("OfferPoolInitializer has been executed for the month: " + DateUtils.formatDateWithPattern(counterEndDate, DATE_PATTERN));
            result.setDone(true);

        } catch (Exception e) {
            log.error("Failed to initialize offers shared pools ", e);
        }
    }

    /**
     * Get date from param
     *
     * @param jobInstance
     * @return
     */
    private Date getCounterEndDate(JobInstance jobInstance) {
        Date date;

        String dateParam = (String) this.getParamOrCFValue(jobInstance, "DATE", "");
        if (!StringUtils.isBlank(dateParam)) {
            date = DateUtils.parseDateWithPattern(dateParam, DATE_PATTERN);
            if (date == null) {
                throw new BusinessException("The date format is incorrect, it must respect the format: " + DATE_PATTERN);
            }
        } else {
            date = new Date();
        }
        // Set last day of month
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

        return cal.getTime();
    }
}
