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
import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingWalletDetailDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.AccountServiceLocal;

/**
 * @author R.AITYAAZZA
 * 
 */
@Local
public interface UserAccountServiceLocal extends AccountServiceLocal<UserAccount> {

    public List<Subscription> subscriptionList(String code) throws BusinessException;

    public void createUserAccount(String billingAccountCode, UserAccount userAccount, User creator)
            throws AccountAlreadyExistsException;

    public void updateUserAccount(UserAccount userAccount, User updater) throws BusinessException;

    public UserAccount userAccountDetails(String code) throws BusinessException;

    public void userAccountTermination(String code, Date terminationDate, User updater) throws BusinessException;

    public void userAccountCancellation(String code, Date terminationDate, User updater) throws BusinessException;

    public void userAccountReactivation(String code, Date activationDate, User updater) throws BusinessException;

    public BillingWalletDetailDTO BillingWalletDetail(String code) throws BusinessException;

    public List<RatedTransaction> BillingRatedTransactionList(String code) throws BusinessException;
    public boolean isDuplicationExist(UserAccount userAccount);
}
