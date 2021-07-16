package org.meveo.admin.job;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

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

/**
 * @author Mounir BOUKAYOUA
 * @author Amine BEN AICHA
 */
@Stateless
public class OfferPoolInitializerJobBean extends BaseJobBean {

    private static final String DATE_PATTERN = "MM/yyyy";

    private static final String OFFERS_TO_INITILIZE = "select offer.id\n"
            + "from cat_offer_template offer\n"
            + "where offer.business_offer_model_id is not null\n"
            + "and cast(offer.cf_values as json)#>>'{sharingLevel, 0, string}'='OF'";

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private Logger log;

    @Inject
    private OfferPoolInitializerAsync offerPoolInitializerAsync;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running Job with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        Date executionDate = new Date();
        Date counterStartDate = getCounterStartDate(jobInstance);
        Date counterEndDate = getCounterEndDate(counterStartDate);

        try {
            List<BigInteger> offerIds = emWrapper.getEntityManager().createNativeQuery(OFFERS_TO_INITILIZE).getResultList();

            log.info("Total of sahred offers that pools should be initialized: {}", offerIds.size());
            result.setNbItemsToProcess(offerIds.size());

            SubListCreator<BigInteger> subListCreator = new SubListCreator<>(offerIds, nbRuns.intValue());
            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            while (subListCreator.isHasNext()) {
                futures.add(offerPoolInitializerAsync.launchAndForget(subListCreator.getNextWorkSet(), executionDate, counterStartDate, counterEndDate, result, lastCurrentUser));
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
                    log.error("Failed to execute async method", cause);
                }
            }

            if (result.getNbItemsProcessedWithError() == 0) {
                result.addReport("OfferPoolInitializer has been executed for the month: " + DateUtils.formatDateWithPattern(counterEndDate, DATE_PATTERN));
            }
            result.setDone(true);

        } catch (Exception e) {
            log.error("Failed to initialize offers shared pools ", e);
            result.registerError(e.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    /**
     * Get start date from param
     *
     * @param jobInstance
     * @return
     */
    private Date getCounterStartDate(JobInstance jobInstance) {
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Get end date from param
     * 
     * @param date
     * @return
     */
    private Date getCounterEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        // Set last day of month
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        cal.set(year, month, lastDay, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

}
