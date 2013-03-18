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
package org.meveo.service.payments.impl;

import javax.ejb.Stateless;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.payments.local.RecordedInvoiceServiceLocal;

/**
 * RecordedInvoice service implementation.
 * 
 * @author Ignas
 * @created 2009.09.04
 */
@Stateless
@Named
public class RecordedInvoiceService extends PersistenceService<RecordedInvoice> implements
		RecordedInvoiceServiceLocal {

	public void addLitigation(Long recordedInvoiceId, User user) throws BusinessException {
		if (recordedInvoiceId == null) {
			throw new BusinessException("recordedInvoiceId is null");
		}
		addLitigation(findById(recordedInvoiceId), user);
	}

	public void addLitigation(RecordedInvoice recordedInvoice, User user) throws BusinessException {
		if (user == null) {
			throw new BusinessException("user is null");
		}
		if (recordedInvoice == null) {
			throw new BusinessException("recordedInvoice is null");
		}
		log.info("addLitigation recordedInvoice.Reference:" + recordedInvoice.getReference()
				+ "status:" + recordedInvoice.getMatchingStatus() + " , user:" + user.getName());
		if (recordedInvoice.getMatchingStatus() != MatchingStatusEnum.O) {
			throw new BusinessException("recordedInvoice is not open");
		}
		recordedInvoice.setMatchingStatus(MatchingStatusEnum.I);
		update(recordedInvoice, user);
		log.info("addLitigation recordedInvoice.Reference:" + recordedInvoice.getReference()
				+ " , user:" + user.getName() + " ok");
	}

	public void cancelLitigation(Long recordedInvoiceId, User user) throws BusinessException {
		if (recordedInvoiceId == null) {
			throw new BusinessException("recordedInvoiceId is null");
		}
		cancelLitigation(findById(recordedInvoiceId), user);
	}

	public void cancelLitigation(RecordedInvoice recordedInvoice, User user)
			throws BusinessException {
		if (user == null) {
			throw new BusinessException("user is null");
		}
		if (recordedInvoice == null) {
			throw new BusinessException("recordedInvoice is null");
		}
		log.info("cancelLitigation recordedInvoice.Reference:" + recordedInvoice.getReference()
				+ " , user:" + user.getName());
		if (recordedInvoice.getMatchingStatus() != MatchingStatusEnum.I) {
			throw new BusinessException("recordedInvoice is not on Litigation");
		}
		recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
		update(recordedInvoice, user);
		log.info("cancelLitigation recordedInvoice.Reference:" + recordedInvoice.getReference()
				+ " , user:" + user.getName() + " ok");
	}
}
