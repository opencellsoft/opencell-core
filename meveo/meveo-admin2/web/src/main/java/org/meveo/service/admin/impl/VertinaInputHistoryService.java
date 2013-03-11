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
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.model.admin.VertinaInputHistory;
import org.meveo.model.billing.ApplicationChgStatusEnum;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.service.admin.local.VertinaInputHistoryServiceLocal;
import org.meveo.service.base.PersistenceService;

/**
 * Vertina input History service implementation.
 * 
 * @author Ignas
 * @created 2009.10.15
 */
@Stateless
@Name("vertinaInputHistoryService")
@AutoCreate
public class VertinaInputHistoryService extends PersistenceService<VertinaInputHistory> implements
        VertinaInputHistoryServiceLocal {

    /**
     * @see org.meveo.service.admin.local.VertinaInputHistoryServiceLocal#getChargeApplications(java.lang.Long,
     *      org.meveo.model.billing.ApplicationChgStatusEnum)
     */
    @SuppressWarnings("unchecked")
    public List<ChargeApplication> getChargeApplications(Long inputHistoryId, ApplicationChgStatusEnum status) {
        if (status == null) {
            Query q = em.createQuery("select ca from ChargeApplication ca where ca.inputHistoryId = :id");
            q.setParameter("id", inputHistoryId);
            return q.getResultList();
        } else {
            Query q = em
                    .createQuery("select ca from ChargeApplication ca where ca.inputHistoryId = :id and ca.status = :status");
            q.setParameter("id", inputHistoryId);
            q.setParameter("status", status);
            return q.getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<ChargeApplication> getTransactions(Long inputHistoryId, RatedTransactionStatusEnum status) {
        if (status == null) {
            Query q = em.createQuery("select rt from RatedTransaction rt where rt.inputHistoryId = :id");
            q.setParameter("id", inputHistoryId);
            return q.getResultList();
        } else {
            Query q = em
                    .createQuery("select rt from RatedTransaction rt where rt.inputHistoryId = :id and rt.status = :status");
            q.setParameter("id", inputHistoryId);
            q.setParameter("status", status);
            return q.getResultList();
        }
    }

}
