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
 * @author Amine BEN AICHA
 */
@Stateless
public class OfferPoolInitializerAsync {
    
    private static final String OFFER_AGENCIES_COUNTERS_QUERY = "select agency.code, count(t.sub_id)\n"
            + "from account_entity agency\n"
            + "join billing_user_account ua on agency.id = ua.id\n"
            + "join billing_billing_account ba on ua.billing_account_id = ba.id\n"
            + "join \n"
            + "(   select sub.id as sub_id,\n"
            + "    sub.user_account_id as agency_id,\n"
            + "    case\n"
            + "        when sub.cardWithExemption is not null and sub.cardWithExemption=true then sub.exemptionEndDate\n"
            + "        else sub.subscription_date\n"
            + "    end as activated_at\n"
            + "    from billing_service_instance si\n"
            + "    join (  select s.id,\n"
            + "                s.user_account_id,\n"
            + "                (cast(s.cf_values as json)#>>'{cardWithExemption, 0, string}')\\:\\:boolean as cardWithExemption,\n"
            + "                (cast(s.cf_values as json)#>>'{exemptionEndDate, 0, date}')\\:\\:timestamp as exemptionEndDate,\n"
            + "                s.subscription_date\n"
            + "            from billing_subscription s\n"
            + "            where s.offer_id = :offerId \n"
            + "            and s.status = 'ACTIVE'\n"
            + "            and ( s.termination_date is null \n"
            + "                  or (\n"
            + "                        (cast(s.cf_values as json)#>>'{dateTerminated, 0, date}')\\:\\:timestamp is not null\n"
            + "                        and\n"
            + "                        (cast(s.cf_values as json)#>>'{dateTerminated, 0, date}')\\:\\:timestamp >= :counterStartDate \n"
            + "                     )\n"
            + "                )\n"
            + "          ) sub on si.subscription_id = sub.id\n"
            + "    join account_entity agency on sub.user_account_id = agency.id\n"
            + "    where si.code like '%_REC_%'\n"
            + "    and si.status = 'ACTIVE'\n"
            + ") t on (t.agency_id = agency.id and t.activated_at <= :counterEndDate )\n"
            + "where ba.status = 'ACTIVE'\n"
            + "group by agency.code";

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
    @SuppressWarnings("unchecked")
    public Future<String> launchAndForget(List<BigInteger> offerIds, Date executionDate, Date counterStartDate, Date counterEndDate, JobExecutionResultImpl result,
            MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start new thread to initialize pool counters of offerIds workSet with length={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;

            List<Object[]> items = emWrapper.getEntityManager().createNativeQuery(OFFER_AGENCIES_COUNTERS_QUERY)
                .setParameter("offerId", offerId.longValue())
                .setParameter("counterStartDate", counterStartDate)
                .setParameter("counterEndDate", counterEndDate).getResultList();

            for (Object[] item : items) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                String userAccountCode = (String) item[0];
                BigInteger countSubs = (BigInteger) item[1];

                offerPoolInitializerUnitJobBean.execute(result, offerId, userAccountCode, countSubs, executionDate, counterStartDate);
            }
        }

        return new AsyncResult<>("OK");
    }

}
