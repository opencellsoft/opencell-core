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
package org.meveo.service.billing.local;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.RecurringChargeTemplate;

/**
 * @author R.AITYAAZZA
 * 
 */
@Local
public interface RecurringChargeInstanceServiceLocal extends ChargeInstanceServiceLocal<RecurringChargeInstance> {
    public List<RecurringChargeInstance> findByStatus(InstanceStatusEnum status,Date maxChargeDate);

    public Long recurringChargeApplication(Subscription subscription, RecurringChargeTemplate chargetemplate,
            Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, Integer quantity, String criteria1,
            String criteria2, String criteria3, User creator) throws BusinessException;

    public void recurringChargeApplication(RecurringChargeInstance chargeInstance, User creator)
            throws BusinessException;
    public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(Long subscriptionId);
}
