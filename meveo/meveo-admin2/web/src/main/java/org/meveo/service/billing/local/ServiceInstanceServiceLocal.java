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

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.service.base.local.BusinessServiceLocal;

/**
 * Service Instance service interface.
 * 
 */
@Local
public interface ServiceInstanceServiceLocal extends BusinessServiceLocal<ServiceInstance> {

    public ServiceInstance findByCodeAndSubscription(String code, String subscriptionCode);

    public void serviceInstanciation(ServiceInstance serviceInstance, User creator)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;
    

    public void serviceInstanciation(ServiceInstance serviceInstance, User creator, BigDecimal subscriptionAmount,BigDecimal terminationAmount)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void serviceActivation(ServiceInstance serviceInstance, BigDecimal amountWithoutTax,
            BigDecimal amountWithoutTax2, User creator) throws IncorrectSusbcriptionException,
            IncorrectServiceInstanceException, BusinessException;

    public void serviceSusupension(ServiceInstance serviceInstance, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void serviceReactivation(ServiceInstance serviceInstance, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void cancelService(ServiceInstance serviceInstance, User updater) throws IncorrectServiceInstanceException,
            BusinessException;

    public void terminateService(ServiceInstance serviceInstance, Date terminationDate, 
    		SubscriptionTerminationReason terminationReason, User user)
    		throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void terminateService(ServiceInstance serviceInstance, Date terminationDate, boolean applyAgreement,
            boolean applyReimbursment, boolean applyTerminationCharges, User user)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void updateTerminationMode(ServiceInstance serviceInstance, Date terminationDate, User user)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;
    
    public void serviceTermination(ServiceInstance serviceInstance, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

    public void serviceCancellation(ServiceInstance serviceInstance, Date terminationDate, User updater)
            throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException;

}