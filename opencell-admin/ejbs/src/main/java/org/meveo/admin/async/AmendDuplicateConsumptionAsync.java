/**
 * 
 */
package org.meveo.admin.async;

import org.meveo.admin.job.AmendDuplicateConsumptionUnitJobBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

import javax.ejb.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class AmendDuplicateConsumptionAsync {

    private static final String DUPLICATED_WO_TO_AMEND_QUERY = "select sub.user_account_id, sub.id as sub_id, wo.parameter_1, to_char(wo.operation_date, 'MM-yyyy'), wo.id as wo_id \n" +
            "from billing_wallet_operation wo inner join billing_subscription sub on wo.subscription_id = sub.id \n" +
            "where wo.offer_id = :offerId\n" +
            " and (wo.code like 'CH_M2M_USG_%_IN' and wo.code not like '%FREE%' and wo.parameter_1 not like '%_NUM_SPE') \n" +
            " and (wo.status='CANCELED' and wo.parameter_extra='DUPLICATE WO') \n" +
            " and (wo.parameter_2 = 'DEDUCTED_FROM_POOL' or wo.counter_id is not null) \n" +
            " and wo.parameter_3 != 'AMENDED_FROM_POOL' \n" +
            "order by 1, 2, 3, 4";

    private static final String COUNTER_OVERAGE_WO_QUERY = "select wo.id from WalletOperation wo \n" +
            "left join wo.ratedTransaction rt \n" +
            "where wo.subscription.id=:subId \n" +
            " and wo.code=:overChargeCode \n" +
            " and (wo.status = 'OPEN' or (wo.status = 'TREATED' and rt.status = 'OPEN')) \n" +
            " and (wo.operationDate between :startMonth and :endMonth) \n" +
            "order by wo.id";

    private static final String POOL_OVERAGE_WO_QUERY = "select wo.id from WalletOperation wo \n" +
            "left join wo.ratedTransaction rt \n" +
            "inner join wo.subscription sub \n" +
            "where wo.code like 'POOL%_USG_OVER' " +
            " and wo.parameter1=:chargeType \n" +
            " and wo.offerTemplate.id=:offerId \n" +
            " and sub.userAccount.id=:agencyId \n" +
            " and (wo.status = 'OPEN' or (wo.status = 'TREATED' and rt.status = 'OPEN')) \n" +
            " and (wo.operationDate between :startMonth and :endMonth) \n" +
            "order by wo.id";



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
    private AmendDuplicateConsumptionUnitJobBean amendDuplicateConsumptionUnitJobBean;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BigInteger> offerIds, JobExecutionResultImpl result, MeveoUser lastCurrentUser, boolean statsActivated) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start amend new group of canceled duplicated WO thread to process. WorkSet of offers={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;
            @SuppressWarnings("unchecked")
            List<Object[]> canceledWoInList = emWrapper.getEntityManager().createNativeQuery(DUPLICATED_WO_TO_AMEND_QUERY)
                    .setParameter("offerId", offerId.longValue())
                    .getResultList();

            log.info("Start amend canceled duplicated WO for offerId={}. nbr of WO={}", offerId, canceledWoInList.size());

            OfferTemplate offer = offerTemplateService.findById(offerId.longValue());
            boolean isSharedPool = "OF".equals(offer.getCfValue("sharingLevel"));

            List<Long> overageWOList = null;

            Long previousAgenceId = null;
            Long previousSubId = null;
            String previousChargeType = null;
            String previousDateConso = null;

            for (Object[] row : canceledWoInList) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                Long agenceId = ((BigInteger) row[0]).longValue();
                Long subId = ((BigInteger) row[1]).longValue();
                String chargeType = (String) row[2];
                String dateConso = (String) row[3];
                Long walletOperationId = ((BigInteger) row[4]).longValue();

                if (isSharedPool &&
                        (!agenceId.equals(previousAgenceId) || !chargeType.equals(previousChargeType) || !dateConso.equals(previousDateConso))) {
                    overageWOList = getOverageWalletOperationList(isSharedPool, offerId.longValue(), agenceId, null, chargeType, dateConso);
                }
                if (!isSharedPool &&
                        (!subId.equals(previousSubId) || !chargeType.equals(previousChargeType) || !dateConso.equals(previousDateConso))) {
                    overageWOList = getOverageWalletOperationList(isSharedPool, offerId.longValue(), null, subId, chargeType, dateConso);
                }
                amendDuplicateConsumptionUnitJobBean.execute(result, walletOperationId, overageWOList, statsActivated);

                previousAgenceId = agenceId;
                previousSubId = subId;
                previousChargeType = chargeType;
                previousDateConso = dateConso;
            }
        }

        return new AsyncResult<>("OK");
    }

    private List<Long> getOverageWalletOperationList(boolean isSharedPool, Long offerId, Long agencyId,
                                                                Long subId, String chargeType, String dateConso) {
        long start = System.currentTimeMillis();

        String[] monthYear = dateConso.split("-");
        int month = Integer.parseInt(monthYear[0]) - 1;
        int year = Integer.parseInt(monthYear[1]);

        Calendar calendar = Calendar.getInstance();
        // start month of usage date
        calendar.set(year, month, 1, 0, 0, 0);
        Date startMonth = calendar.getTime();
        // end month of usage date
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, lastDayOfMonth, 23, 59, 59);
        Date endMonth = calendar.getTime();

        List<Long> resultList;
        if (isSharedPool) {
            resultList = emWrapper.getEntityManager().createQuery(POOL_OVERAGE_WO_QUERY, Long.class)
                    .setParameter("chargeType", chargeType)
                    .setParameter("offerId", offerId)
                    .setParameter("agencyId", agencyId)
                    .setParameter("startMonth", startMonth)
                    .setParameter("endMonth", endMonth)
                    .getResultList();

        } else {
            resultList = emWrapper.getEntityManager().createQuery(COUNTER_OVERAGE_WO_QUERY, Long.class)
                    .setParameter("subId", subId)
                    .setParameter("overChargeCode", "CH_M2M_USG_" + chargeType + "_OVER")
                    .setParameter("startMonth", startMonth)
                    .setParameter("endMonth", endMonth)
                    .getResultList();
        }
        log.info("> ADCAsync > " + offerId + " > getOverageWalletOperationList >"+ (System.currentTimeMillis()-start));
        return resultList;
    }
}
