/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;

@Stateless @LocalBean
public class RatedTransactionService extends PersistenceService<RatedTransaction> {

    @EJB
    private SubscriptionService subscriptionService;

    @SuppressWarnings("unchecked")
    public List<RatedTransaction> getRatedTransactionsInvoiced(UserAccount userAccount) {
        if (userAccount == null || userAccount.getWallet() == null) {
            return null;
        }
        return (List<RatedTransaction>) em
                .createQuery(
                        "from "
                                + RatedTransaction.class.getSimpleName()
                                + " where wallet=:wallet and invoice is not null order by usageDate desc")
                .setParameter("wallet", userAccount.getWallet()).getResultList();
    }


    @SuppressWarnings("unchecked")
    public ConsumptionDTO getConsumption(Subscription subscription, String infoType,
            Integer billingCycle, boolean sumarizeConsumption)
            throws IncorrectSusbcriptionException {

        Date lastBilledDate = null;
        ConsumptionDTO consumptionDTO = new ConsumptionDTO();

        // If billing has been run already, use last billing date plus a day as
        // filtering FROM value
        // Otherwise leave it null, so it wont be included in a query
        if (subscription.getUserAccount().getBillingAccount().getBillingRun() != null) {
            lastBilledDate = subscription.getUserAccount().getBillingAccount().getBillingRun()
                    .getEndDate();

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(lastBilledDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            lastBilledDate = calendar.getTime();

        }

        if (sumarizeConsumption) {

            QueryBuilder qb = new QueryBuilder("select sum(amount1WithTax), sum(usageAmount) from "
                    + RatedTransaction.class.getSimpleName());
            qb.addCriterionEntity("subscription", subscription);
            qb.addCriterion("subUsageCode1", "=", infoType, false);
            qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
            String baseSql = qb.getSqlString();

            // Summarize invoiced transactions
            String sql = baseSql + " and status='BILLED'";

            Query query = em.createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            Object[] results = (Object[]) query.getSingleResult();

            consumptionDTO.setAmountCharged((BigDecimal) results[0]);
            consumptionDTO.setConsumptionCharged(((Long) results[1]).intValue());

            // Summarize not invoiced transactions
            sql = baseSql + " and status<>'BILLED'";

            query = em.createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            results = (Object[]) query.getSingleResult();

            consumptionDTO.setAmountUncharged((BigDecimal) results[0]);
            consumptionDTO.setConsumptionUncharged(((Long) results[1]).intValue());

        } else {

            QueryBuilder qb = new QueryBuilder(
                    "select sum(amount1WithTax), sum(usageAmount), groupingId, case when status='BILLED' then 'true' else 'false' end from "
                            + RatedTransaction.class.getSimpleName());
            qb.addCriterionEntity("subscription", subscription);
            qb.addCriterion("subUsageCode1", "=", infoType, false);
            qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
            qb.addSql("groupingId is not null");
            String sql = qb.getSqlString()
                    + " group by groupingId, case when status='BILLED' then 'true' else 'false' end";

            Query query = em.createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            List<Object[]> results = (List<Object[]>) query.getResultList();

            for (Object[] result : results) {

                BigDecimal amount = (BigDecimal) result[0];
                int consumption = ((Long) result[1]).intValue();
                int groupId = (Integer) result[2];
                boolean charged = Boolean.parseBoolean((String) result[3]);
                //boolean roaming = RatedTransaction.translateGroupIdToRoaming(groupId);
                //boolean upload = RatedTransaction.translateGroupIdToUpload(groupId);

                if (charged) {

                    //if (!roaming && !upload) {
                        consumptionDTO.setIncomingNationalConsumptionCharged(consumption);
                    //} else if (roaming && !upload) {
                    //    consumptionDTO.setIncomingRoamingConsumptionCharged(consumption);
                    //} else if (!roaming && upload) {
                    //    consumptionDTO.setOutgoingNationalConsumptionCharged(consumption);
                    //} else {
                    //    consumptionDTO.setOutgoingRoamingConsumptionCharged(consumption);
                    //}

                    consumptionDTO.setConsumptionCharged(consumptionDTO.getConsumptionCharged()
                            + consumption);
                    consumptionDTO.setAmountCharged(consumptionDTO.getAmountCharged().add(amount));

                } else {
                    //if (!roaming && !upload) {
                        consumptionDTO.setIncomingNationalConsumptionUncharged(consumption);
                    //} else if (roaming && !upload) {
                    //    consumptionDTO.setIncomingRoamingConsumptionUncharged(consumption);
                    //} else if (!roaming && upload) {
                    //    consumptionDTO.setOutgoingNationalConsumptionUncharged(consumption);
                    //} else {
                     //   consumptionDTO.setOutgoingRoamingConsumptionUncharged(consumption);
                    //}
                    consumptionDTO.setConsumptionUncharged(consumptionDTO.getConsumptionUncharged()
                            + consumption);
                    consumptionDTO.setAmountUncharged(consumptionDTO.getAmountUncharged().add(
                            amount));
                }
            }
        }

        return consumptionDTO;

    }
}