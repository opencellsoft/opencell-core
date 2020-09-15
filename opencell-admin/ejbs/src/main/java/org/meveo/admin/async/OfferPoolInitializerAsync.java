/**
 * 
 */
package org.meveo.admin.async;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.OfferPoolInitializerUnitJobBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class OfferPoolInitializerAsync {

    private static final String OFFER_AGENCIES_COUNTERS_QUERY =  "select sub.user_account_id, count(sub.id) \n"
            + "from billing_subscription sub \n"
            + "where sub.offer_id=:offerId and sub.status='ACTIVE' \n"
            + "and sub.subscription_date <= :counterEndDate  \n"
            + "and ( sub.termination_date is null or (sub.termination_date - INTERVAL ':termination_delay DAYS') <= :counterEndDate ) \n"
            + "group by sub.user_account_id";
    @Inject
    private Logger log;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private OfferPoolInitializerUnitJobBean offerPoolInitializerUnitJobBean;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BigInteger> offerIds, Date counterStartDate, Date counterEndDate, int terminationDelay, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start new thread to initialize pool counters of offerIds workSet with length={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;
            @SuppressWarnings("unchecked")
            List<Object[]> items = emWrapper.getEntityManager()
                    .createNativeQuery(OFFER_AGENCIES_COUNTERS_QUERY.replaceAll(":termination_delay", String.valueOf(terminationDelay)))
                    .setParameter("offerId", offerId.longValue())
                    .setParameter("counterEndDate", counterEndDate)
                    .getResultList();

            for (Object[] item : items) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0
                        && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                Long userAccountId = ((BigInteger) item[0]).longValue();
                BigDecimal countSubs = BigDecimal.valueOf(((BigInteger) item[1]).longValue());

                offerPoolInitializerUnitJobBean.execute(result, offerId.longValue(), userAccountId, countSubs, counterStartDate);
            }
        }

        return new AsyncResult<>("OK");
    }

}
