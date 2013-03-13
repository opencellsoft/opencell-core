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

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.local.BillingCycleServiceLocal;

/**
 * BillingCycle service implementation.
 * 
 * @author Gediminas
 * @created 2010.05.14
 */
@Stateless
@Name("billingCycleService")
@JndiName("java:app/meveo-admin-ejb/BillingCycleService")
@AutoCreate
public class BillingCycleService extends PersistenceService<BillingCycle> implements BillingCycleServiceLocal {
    /**
     * Find BillingCycle by its billing cycle code.
     * 
     * @param billingCycleCode
     *            Billing Cycle Code
     * @return Billing cycle found or null.
     * @throws ElementNotFoundException
     */
    public BillingCycle findByBillingCycleCode(String billingCycleCode, Provider provider) {
        try {
            log.info("findByBillingCycleCode billingCycleCode=#0,provider=#1", billingCycleCode,
                    provider != null ? provider.getCode() : null);
            Query query = em
                    .createQuery("select b from BillingCycle b where b.code = :billingCycleCode and b.provider=:provider");
            query.setParameter("billingCycleCode", billingCycleCode);
            query.setParameter("provider", provider);
            return (BillingCycle) query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("findByBillingCycleCode billing cycle not found : billingCycleCode=#0,provider=#1",
                    billingCycleCode, provider != null ? provider.getCode() : null);
            return null;
        }
    }
}