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
package org.meveo.service.admin.local;

import java.util.List;

import javax.ejb.Local;

import org.meveo.model.admin.VertinaInputHistory;
import org.meveo.model.billing.ApplicationChgStatusEnum;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Vertina Input History service local interface.
 * 
 * @author Ignas
 * @created 2009.10.15
 */
@Local
public interface VertinaInputHistoryServiceLocal extends IPersistenceService<VertinaInputHistory> {

    /**
     * Loads charge applications for specific vertina batch history id.
     * 
     * @param inputHistoryId
     *            Vertina history id.
     * @param status
     *            Status. If its null - loads all charge applications.
     * @return List of charge applications
     */
    public List<ChargeApplication> getChargeApplications(Long inputHistoryId, ApplicationChgStatusEnum status);

    /**
     * @param inputHistoryId
     * @param status
     * @return
     */
    public List<ChargeApplication> getTransactions(Long inputHistoryId, RatedTransactionStatusEnum status);

}
