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
package org.meveo.service.payments.remote;

import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.admin.User;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.service.base.local.IPersistenceService;

/**
 * MatchingCode service local interface.
 * 
 * @author anasseh
 * @created 28.11.2010
 */
public interface IMatchingCodeService extends IPersistenceService<MatchingCode> {

	public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds,
			Long operationIdForPartialMatching,
			User user) throws BusinessException,
			NoAllOperationUnmatchedException, UnbalanceAmountException, Exception;

	public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds,
			Long operationIdForPartialMatching, MatchingTypeEnum matchingTypeEnum,
			User user) throws BusinessException,
			NoAllOperationUnmatchedException, UnbalanceAmountException, Exception;

	/**
	 * Remove machingCode
	 * 
	 * @param idMatchingCode
	 * @param user
	 * @throws BusinessException
	 */
	public void unmatching(Long idMatchingCode, User user) throws BusinessException;
}