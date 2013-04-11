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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.service.base.PersistenceService;

@Stateless @LocalBean
public class CounterInstanceService extends PersistenceService<CounterInstance> {

	@EJB
	UserAccountService userAccountService;
	
	public CounterInstance counterInstanciation(UserAccount userAccount,CounterTemplate counterTemplate, User creator)  throws BusinessException{
		CounterInstance result=null;
		if(userAccount==null){
			throw new BusinessException("userAccount is null");
		}
		if(counterTemplate==null){
			throw new BusinessException("counterTemplate is null");
		}
		if(creator==null){
			throw new BusinessException("creator is null");
		}
		//we instanciate the counter only if there is no existing instance for the same template
		if(!userAccount.getCounters().containsKey(counterTemplate.getCode())){
			result=new CounterInstance();
			result.setCounterTemplate(counterTemplate);
			result.setUserAccount(userAccount);
			create(result, creator,userAccount.getProvider());	
			userAccount.getCounters().put(counterTemplate.getCode(), result);
			userAccountService.update(userAccount);
		}
		return result;
	}
}
