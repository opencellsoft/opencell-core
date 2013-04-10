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
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.RecurringChargeTemplate;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless @LocalBean
public class RecurringChargeInstanceService extends ChargeInstanceService<RecurringChargeInstance> {

    @EJB
    private WalletOperationService chargeApplicationService;

	// @EJB
    // private RecurringChargeTemplateServiceLocal
    // recurringChargeTemplateService;

    @SuppressWarnings("unchecked")
    public List<RecurringChargeInstance> findByStatus(InstanceStatusEnum status,Date maxChargeDate) {
        List<RecurringChargeInstance> recurringChargeInstances = null;
        try {
            log.debug("start of find #0 by status (status=#1)) ..", "RecurringChargeInstance", status);
            QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
            qb.addCriterion("c.status", "=", status, true);
            qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate);
            recurringChargeInstances = qb.getQuery(em).getResultList();
            log.debug("end of find {0} by status (status={1}). Result size found={2}.", "RecurringChargeInstance",
                    status, recurringChargeInstances != null ? recurringChargeInstances.size() : 0);

        } catch (Exception e) {
            log.error("findByStatus error=#0 ", e.getMessage());
        }
        return recurringChargeInstances;
    }

    public Long recurringChargeApplication(Subscription subscription, RecurringChargeTemplate chargetemplate,
            Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, Integer quantity, String criteria1,
            String criteria2, String criteria3, User creator) throws BusinessException {

        if (quantity == null) {
            quantity = 1;
        }
        RecurringChargeInstance recurringChargeInstance = new RecurringChargeInstance(chargetemplate.getCode(),
                chargetemplate.getDescription(), effetDate, amoutWithoutTax, amoutWithoutTx2, subscription,
                chargetemplate, null);
        recurringChargeInstance.setCriteria1(criteria1);
        recurringChargeInstance.setCriteria2(criteria2);
        recurringChargeInstance.setCriteria3(criteria3);

        create(recurringChargeInstance, creator,chargetemplate.getProvider());

        chargeApplicationService.recurringWalletOperation(subscription, recurringChargeInstance, quantity, effetDate,
                creator);
        return recurringChargeInstance.getId();
    }

    public void recurringChargeApplication(RecurringChargeInstance chargeInstance, User creator)
            throws BusinessException {
        chargeApplicationService.chargeSubscription(chargeInstance, creator);
    }

    @SuppressWarnings("unchecked")
    public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(Long subscriptionId) {
        QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
        qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
        return qb.getQuery(em).getResultList();
    }

}
