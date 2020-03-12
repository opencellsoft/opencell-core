/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
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