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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.seam.transaction.Transactional;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.service.base.PersistenceService;


/**
 * @author R.AITYAAZZA
 *
 */
@Stateless @LocalBean
public class MatchingAmountService extends PersistenceService<MatchingAmount> {
	
	@EJB
	private AccountOperationService accountOperationService;
	
	@Transactional
	public void unmatching(Long idMatchingAmount, User user) throws BusinessException {
		log.info("start cancelMatchingAmount with id:#0,user:#1", idMatchingAmount, user);
		if (idMatchingAmount == null)
			throw new BusinessException("Error when idMatchingAmount is null!");
		if (user == null || user.getId() == null) {
			throw new BusinessException("Error when user is null!");
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
				operation.setUnMatchingAmount(operation.getUnMatchingAmount().add(
						matchingAmount.getMatchingAmount()));
				operation.setMatchingAmount(operation.getMatchingAmount().subtract(
						matchingAmount.getMatchingAmount()));
				if (BigDecimal.ZERO.compareTo(operation.getMatchingAmount()) == 0) {
					operation.setMatchingStatus(MatchingStatusEnum.O);
				} else {
					operation.setMatchingStatus(MatchingStatusEnum.P);
				}
				operation.getMatchingAmounts().remove(matchingAmount);
				accountOperationService.update(operation, user);
				log.info("cancel one accountOperation!");
			
		
		log.info("successfully end cancelMatching!");
	}

}
