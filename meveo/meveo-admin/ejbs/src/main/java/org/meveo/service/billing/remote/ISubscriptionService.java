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
package org.meveo.service.billing.remote;

import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementAlreadyExistsException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;

/**
 * @author R.AITYAAZZA
 * 
 */
public interface ISubscriptionService {

    public void createSubscription(String userAccountCode, Subscription subscription, User creator)
            throws ElementAlreadyExistsException, UnknownAccountException;

    public void updateSubscription(Subscription subscription, User updater) throws BusinessException;

    public Subscription subscriptionDetails(String subscriptionCode);

    public List<OneShotChargeInstance> SubscriptionChargeList(String subscriptionCode)
            throws IncorrectSusbcriptionException;

    public ServiceInstance SubscriptionServiceDetail(String subscriptionCode, String serviceCode)
            throws IncorrectSusbcriptionException;

    public void subscriptionTermination(String subscriptionCode, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void subscriptionCancellation(String subscriptionCode, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void subscriptionSuspension(String subscriptionCode, Date suspensionDate, User updater) 
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;
    
    public void subscriptionReactivation(String subscriptionCode, Date activationDate, User updater)
            throws IncorrectSusbcriptionException, ElementNotResiliatedOrCanceledException, IncorrectServiceInstanceException, BusinessException;

    public void subscriptionOffer(String subscriptionCode, String offerCode, Date subscriptionDate, User updater)
            throws IncorrectSusbcriptionException, BusinessException;

    public void terminateSubscription(String subscriptionCode, Date terminationDate, boolean applyAgreement,
            boolean applyReimbursment, boolean applyTerminationCharges, User user)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;
    
    public void terminateSubscription(String subscriptionCode, Date terminationDate, SubscriptionTerminationReason terminationReason, User user)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;
}
