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
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.service.base.local.BusinessServiceLocal;

/**
 * @author R.AITYAAZZA
 * 
 */
@Local
public interface OneShotChargeInstanceServiceLocal extends BusinessServiceLocal<OneShotChargeInstance> {

    public OneShotChargeInstance findByCodeAndSubsription(String code, Long subscriptionId);

    public OneShotChargeInstance oneShotChargeInstanciation(Subscription subscription, ServiceInstance serviceCode,
            OneShotChargeTemplate chargeTemplate, Date effetDate, BigDecimal amoutWithoutTax,
            BigDecimal amoutWithoutTx2, Integer quantity, User creator) throws BusinessException;

    public Long oneShotChargeApplication(Subscription subscription, OneShotChargeTemplate chargetemplate,
            Date effetDate, BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, Integer quantity, String criteria1,
            String criteria2, String criteria3, User creator) throws BusinessException;

    public void oneShotChargeApplication(Subscription subscription, OneShotChargeInstance oneShotChargeInstance,
            Date effetDate, Integer quantity, User creator) throws BusinessException;

    public List<OneShotChargeInstance> findOneShotChargeInstancesBySubscriptionId(Long subscriptionId);
}
