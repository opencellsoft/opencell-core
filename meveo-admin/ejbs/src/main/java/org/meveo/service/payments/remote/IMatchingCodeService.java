/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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