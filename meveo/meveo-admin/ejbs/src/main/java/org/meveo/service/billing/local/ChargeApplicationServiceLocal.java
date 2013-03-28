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

import java.util.Date;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.service.base.local.ProviderBusinessServiceLocal;

/**
 * @author R.AITYAAZZA
 * 
 */
@Local
public interface ChargeApplicationServiceLocal extends ProviderBusinessServiceLocal<ChargeApplication> {

    public void oneShotChargeApplication(Subscription subscription, OneShotChargeInstance chargeInstance,
            Integer quantity, Date applicationDate, User creator) throws BusinessException;

    public void recurringChargeApplication(Subscription subscription, RecurringChargeInstance chargeInstance,
            Integer quantity, Date applicationDate, User creator) throws BusinessException;

    // apply subscription prorata then reccuring charges
    public void chargeSubscription(RecurringChargeInstance chargeInstance, User creator) throws BusinessException;

    // apply all recurring charges in the durationCalendar period of the
    // serviceInstance
    public void applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
            RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException;

    public void applyChargeAgreement(RecurringChargeInstance chargeInstance,
            RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException;

    public void applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
            RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException;

    public void applyReimbursment(RecurringChargeInstance chargeInstance, User creator) throws BusinessException;

    public void cancelChargeApplications(Long chargeInstanceId, ChargeApplicationModeEnum mode, User updater)
            throws BusinessException;

    public void cancelOneShotChargeApplications(OneShotChargeInstance chargeInstance,
            OneShotChargeTemplateTypeEnum templateTypeEnum, User updater) throws BusinessException;

    public void chargeTermination(RecurringChargeInstance chargeInstance, User creator) throws BusinessException;

}
