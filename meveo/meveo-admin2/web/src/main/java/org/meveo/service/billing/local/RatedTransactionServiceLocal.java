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

import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.local.IPersistenceService;

/**
 * @author R.AITYAAZZA
 * @created 16 déc. 10
 */
@Local
public interface RatedTransactionServiceLocal extends IPersistenceService<RatedTransaction> {

    public List<RatedTransaction> getRatedTransactionsInvoiced(UserAccount userAccount);

    public List<RatedTransaction> getRatedTransactionsNoInvoiced(UserAccount userAccount);

    public ConsumptionDTO getConsumption(String subscriptionCode, String infoType, Integer billingCycle, boolean sumarizeConsumption) throws IncorrectSusbcriptionException;
}