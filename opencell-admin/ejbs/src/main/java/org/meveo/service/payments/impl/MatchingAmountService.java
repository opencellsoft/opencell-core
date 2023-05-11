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
package org.meveo.service.payments.impl;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class MatchingAmountService extends PersistenceService<MatchingAmount> {

	@Inject
	private AccountOperationService accountOperationService;

	public void unmatching(Long idMatchingAmount) throws BusinessException {
		log.info("start cancelMatchingAmount with id:#0,user:#1", idMatchingAmount);
        if (idMatchingAmount == null) {
            throw new BusinessException("Error when idMatchingAmount is null!");
        }
		MatchingAmount matchingAmount = findById(idMatchingAmount);
		if (matchingAmount == null) {
			log.warn("Error when found a null matchingCode!");
			throw new BusinessException("Error when found a null matchingCode!");
		}

		AccountOperation operation = matchingAmount.getAccountOperation();
		if (operation.getMatchingStatus() != MatchingStatusEnum.P
				&& operation.getMatchingStatus() != MatchingStatusEnum.L) {
			throw new BusinessException("Error:matchingCode containt unMatching operation");
		}
		operation.setUnMatchingAmount(operation.getUnMatchingAmount().add(matchingAmount.getMatchingAmount()));
		operation.setMatchingAmount(operation.getMatchingAmount().subtract(matchingAmount.getMatchingAmount()));
		operation.setTransactionalUnMatchingAmount(operation.
				getTransactionalUnMatchingAmount().add(ofNullable(matchingAmount.getTransactionalMatchingAmount()).orElse(BigDecimal.ZERO)));
		operation.setTransactionalMatchingAmount(operation.
				getTransactionalMatchingAmount().subtract(ofNullable(matchingAmount.getTransactionalMatchingAmount()).orElse(BigDecimal.ZERO)));
		if (BigDecimal.ZERO.compareTo(operation.getMatchingAmount()) == 0) {
			operation.setMatchingStatus(MatchingStatusEnum.O);
		} else {
			operation.setMatchingStatus(MatchingStatusEnum.P);
		}
		operation.getMatchingAmounts().remove(matchingAmount);
		accountOperationService.updateNoCheck(operation);
		log.info("cancel one accountOperation!");

		log.info("successfully end cancelMatching!");
	}

	public MatchingAmount findByCode(String matchingCode) {
		QueryBuilder qb = new QueryBuilder(MatchingAmount.class, "m", null);
		qb.addCriterion("matchingCode", "=", matchingCode, true);

		try {
			return (MatchingAmount) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
