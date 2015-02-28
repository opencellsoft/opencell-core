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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;

/**
 * RecordedInvoice service implementation.
 */
@Stateless
public class RecordedInvoiceService extends PersistenceService<RecordedInvoice> {

	public void addLitigation(Long recordedInvoiceId, User user)
			throws BusinessException {
		if (recordedInvoiceId == null) {
			throw new BusinessException("recordedInvoiceId is null");
		}
		addLitigation(findById(recordedInvoiceId), user);
	}

	public void addLitigation(RecordedInvoice recordedInvoice, User user)
			throws BusinessException {
		if (user == null) {
			throw new BusinessException("user is null");
		}
		if (recordedInvoice == null) {
			throw new BusinessException("recordedInvoice is null");
		}
		log.info("addLitigation recordedInvoice.Reference:"
				+ recordedInvoice.getReference() + "status:"
				+ recordedInvoice.getMatchingStatus() + " , user:"
				+ user.getName());
		if (recordedInvoice.getMatchingStatus() != MatchingStatusEnum.O) {
			throw new BusinessException("recordedInvoice is not open");
		}
		recordedInvoice.setMatchingStatus(MatchingStatusEnum.I);
		update(recordedInvoice, user);
		log.info("addLitigation recordedInvoice.Reference:"
				+ recordedInvoice.getReference() + " , user:" + user.getName()
				+ " ok");
	}

	public void cancelLitigation(Long recordedInvoiceId, User user)
			throws BusinessException {
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
		log.info("cancelLitigation recordedInvoice.Reference:"
				+ recordedInvoice.getReference() + " , user:" + user.getName());
		if (recordedInvoice.getMatchingStatus() != MatchingStatusEnum.I) {
			throw new BusinessException("recordedInvoice is not on Litigation");
		}
		recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
		update(recordedInvoice, user);
		log.info("cancelLitigation recordedInvoice.Reference:"
				+ recordedInvoice.getReference() + " , user:" + user.getName()
				+ " ok");
	}

	public boolean isRecordedInvoiceExist(String reference, Provider provider) {
		RecordedInvoice recordedInvoice = null;
		try {
			recordedInvoice = (RecordedInvoice) getEntityManager()
					.createQuery(
							"from "
									+ RecordedInvoice.class.getSimpleName()
									+ " where reference =:reference and provider=:provider")
					.setParameter("reference", reference)
					.setParameter("provider", provider).getSingleResult();
		} catch (Exception e) {
		}
		return recordedInvoice != null;
	}

	public RecordedInvoice getRecordedInvoice(String reference,
			Provider provider) {
		RecordedInvoice recordedInvoice = null;
		try {
			recordedInvoice = (RecordedInvoice) getEntityManager()
					.createQuery(
							"from "
									+ RecordedInvoice.class.getSimpleName()
									+ " where reference =:reference and provider=:provider")
					.setParameter("reference", reference)
					.setParameter("provider", provider).getSingleResult();
		} catch (Exception e) {
		}
		return recordedInvoice;
	}

	@SuppressWarnings("unchecked")
	public List<RecordedInvoice> getRecordedInvoices(
			CustomerAccount customerAccount, MatchingStatusEnum o,boolean dunningExclusion) {
		List<RecordedInvoice> invoices = new ArrayList<RecordedInvoice>();
		try {
			invoices = (List<RecordedInvoice>) getEntityManager()
					.createQuery(
							"from "
									+ RecordedInvoice.class.getSimpleName()
									+ " where customerAccount.id=:customerAccountId and matchingStatus=:matchingStatus and excludedFromDunning=:dunningExclusion order by dueDate")
					.setParameter("customerAccountId", customerAccount.getId())
					.setParameter("matchingStatus", MatchingStatusEnum.O)
					.setParameter("dunningExclusion",dunningExclusion)
					.getResultList();
		} catch (Exception e) {

		}
		return invoices;
	}

	@SuppressWarnings("unchecked")
	public List<RecordedInvoice> getInvoices(Date fromDueDate, Date toDueDate,
			String providerCode) throws Exception {
		return getEntityManager()
				.createQuery(
						"from "
								+ RecordedInvoice.class.getSimpleName()
								+ " where matchingStatus=:matchingStatus and dueDate >=:fromDueDate and"
								+ " dueDate<=:toDueDate and paymentMethod=:paymentMethod  and provider.code=:providerCode ")
				.setParameter("fromDueDate", fromDueDate)
				.setParameter("toDueDate", toDueDate)
				.setParameter("matchingStatus", MatchingStatusEnum.O)
				.setParameter("paymentMethod", PaymentMethodEnum.DIRECTDEBIT)
				.setParameter("providerCode", providerCode).getResultList();
	}
}
