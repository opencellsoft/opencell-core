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
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.service.base.local.BusinessServiceLocal;

/**
 * @author R.AITYAAZZA
 * 
 */
@Local
public interface ChargeInstanceServiceLocal<P extends ChargeInstance> extends BusinessServiceLocal<P> {
    public P findByCodeAndService(String code, Long subscriptionId);

    public void recurringChargeInstanciation(ServiceInstance serviceInst, String chargeCode, Date subscriptionDate,
            User creator) throws BusinessException;

    public void recurringChargeDeactivation(long recurringChargeInstanId, Date terminationDate, User updater)
            throws BusinessException;

    public void recurringChargeReactivation(ServiceInstance serviceInst, String subscriptionCode,
            Date subscriptionDate, User creator) throws BusinessException;

    public void chargeInstanceCancellation(long chargeInstanceId, User updater) throws BusinessException;

}
