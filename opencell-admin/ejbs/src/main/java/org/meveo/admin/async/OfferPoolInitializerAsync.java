/**
 * 
 */
package org.meveo.admin.async;

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

//    // Old version
//    private static final String OFFER_AGENCIES_COUNTERS_QUERY = "select ua.code, count(sub.id) \n"
//            + "from billing_subscription sub \n"
//            + "join account_entity ua on sub.user_account_id = ua.id \n"
//            + "where sub.offer_id=:offerId \n"
//            + "and sub.status='ACTIVE' \n"
//            + "and sub.subscription_date <= :counterEndDate \n"
//            + "and (sub.termination_date is null or (sub.termination_date - INTERVAL ':termination_delay DAYS') >= :counterEndDate ) \n"
//            + "group by ua.code";
    
    private static final String OFFER_AGENCIES_COUNTERS_QUERY = "select t.agency_code, count(*)\n"
            + "from\n"
            + "(   select ua.code as agency_code,\n"
            + "    case\n"
            + "        when sub.cardWithExemption is not null and sub.cardWithExemption=true then sub.exemptionEndDate\n"
            + "        else sub.subscription_date\n"
            + "    end as activated_at\n"
            + "    from billing_service_instance si\n"
            + "    join (select id,\n"
            + "            (cast(cf_values as json)#>>'{cardWithExemption, 0, boolean}')\\:\\:boolean as cardWithExemption,\n"
            + "            (cast(cf_values as json)#>>'{exemptionEndDate, 0, date}')\\:\\:timestamp as exemptionEndDate,\n"
            + "            subscription_date,\n"
            + "            user_account_id,\n"
            + "            offer_id\n"
            + "          from billing_subscription\n"
            + "          where offer_id=:offerId \n"
            + "         ) sub\n"
            + "      on si.subscription_id=sub.id\n"
            + "    join account_entity ua on sub.user_account_id = ua.id\n"
            + "    where si.code like '%_SUBSCRIPTION'\n"
            + "    and si.status='ACTIVE'\n"
            + "    and sub.offer_id=:offerId \n"
            + ") t\n"
            + "where t.activated_at <= :counterEndDate \n"
            + "group by t.agency_code";

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
    public Future<String> launchAndForget(List<BigInteger> offerIds, Date counterStartDate, Date counterEndDate, JobExecutionResultImpl result,
            MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start new thread to initialize pool counters of offerIds workSet with length={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;
            @SuppressWarnings("unchecked")
            List<Object[]> items = emWrapper.getEntityManager().createNativeQuery(OFFER_AGENCIES_COUNTERS_QUERY)
                .setParameter("offerId", offerId.longValue()).setParameter("counterEndDate", counterEndDate).getResultList();

            for (Object[] item : items) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                String userAccountCode = (String) item[0];
                BigInteger countSubs = (BigInteger) item[1];

                offerPoolInitializerUnitJobBean.execute(result, offerId, userAccountCode, countSubs, counterStartDate);
            }
        }

        return new AsyncResult<>("OK");
    }

}
