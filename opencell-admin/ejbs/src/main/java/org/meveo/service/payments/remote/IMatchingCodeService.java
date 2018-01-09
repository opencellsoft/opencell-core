/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
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
 * @since 28.11.2010
 */
public interface IMatchingCodeService extends IPersistenceService<MatchingCode> {

    /**
     * match account operations.
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param operationIds list of operation id
     * @param operationIdForPartialMatching operation id for partila matching
     * @param user user
     * @return matching return object.
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException no all operation unmatched exception
     * @throws UnbalanceAmountException un balance amount exception.
     * @throws Exception exception.
     */
	MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds,
			Long operationIdForPartialMatching,
			User user) throws BusinessException,
			NoAllOperationUnmatchedException, UnbalanceAmountException, Exception;

	/**
	 * @param customerAccountId customer account id
	 * @param customerAccountCode customer account code
	 * @param operationIds list of operation id
	 * @param operationIdForPartialMatching operation id for partila matching
	 * @param matchingTypeEnum matching type enum
	 * @param user user
	 * @return matching return object.
	 * @throws BusinessException business exception
	 * @throws NoAllOperationUnmatchedException no all operation unmatched exception
	 * @throws UnbalanceAmountException unbalance amount exception.
	 * @throws Exception exception.
	 */
	MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds,
			Long operationIdForPartialMatching, MatchingTypeEnum matchingTypeEnum,
			User user) throws BusinessException,
			NoAllOperationUnmatchedException, UnbalanceAmountException, Exception;

	/**
	 * Remove machingCode.
	 * 
	 * @param idMatchingCode matching code
	 * @throws BusinessException business exception.
	 */
	void unmatching(Long idMatchingCode) throws BusinessException;
}