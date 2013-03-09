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
package org.meveo.service.payments.local;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.IPersistenceService;

/**
 * AccountOperation service interface.
 * 
 * @author Ignas
 * @created 2009.10.15
 */
@Local
public interface AccountOperationServiceLocal extends IPersistenceService<AccountOperation> {
    /**
     * Selects records fro db according criteriors: start date, end date,
     * aperation code
     * 
     * @param startDate
     *            Start date
     * @param endDate
     *            End date
     * 
     * @param operationCode
     *            Operation code
     */
    public List<AccountOperation> getAccountOperations(Date date, String operationCode, Provider provider);
    public AccountOperation getAccountOperation(BigDecimal amount, CustomerAccount customerAccount,String transactionType, Provider provider) ;

}
