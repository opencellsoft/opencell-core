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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;

/**
 * RecordedInvoice service implementation.
 */
@Stateless
public class RecordedInvoiceService extends PersistenceService<RecordedInvoice> {
	
	@Inject
	private InvoiceService invoiceService;
	

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
		    //FIXME Mbarek use NamedQuery
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
	
	public void generateRecordedInvoice(Invoice invoice,User currentUser) throws InvoiceExistException, ImportInvoiceException, BusinessException{
	
		CustomerAccount customerAccount = null;
		RecordedInvoice recordedInvoice = new RecordedInvoice();
		BillingAccount billingAccount = invoice.getBillingAccount();

		if (isRecordedInvoiceExist(invoice.getInvoiceNumber(), invoice.getProvider())) {
			throw new InvoiceExistException("Invoice id" + invoice.getId() + " already exist");
		}

		try {
			customerAccount = invoice.getBillingAccount().getCustomerAccount();
			recordedInvoice.setCustomerAccount(customerAccount);
			recordedInvoice.setProvider(customerAccount.getProvider());
		} catch (Exception e) {
			log.error("error while getting customer account ", e);
			throw new ImportInvoiceException("Cant find customerAccount");
		}
		if (invoice.getNetToPay() == null) {
			throw new ImportInvoiceException("Net to pay is null");
		}
		if (invoice.getInvoiceType() == null) {
			throw new ImportInvoiceException("Invoice type is null");
		}
		
		OCCTemplate invoiceTemplate = invoice.getInvoiceType().getOccTemplate();
		if (invoiceTemplate == null) {
			throw new ImportInvoiceException("Cant find OccTemplate");
		}
		BigDecimal amountWithoutTax = invoice.getAmountWithoutTax();
		BigDecimal amountTax = invoice.getAmountTax();
		BigDecimal amountWithTax = invoice.getAmountWithTax();
		BigDecimal netToPay = invoice.getNetToPay();

		if (netToPay.compareTo(BigDecimal.ZERO) < 0) {				
			invoiceTemplate = invoice.getInvoiceType().getOccTemplateNegative();
			if (invoiceTemplate == null) {
				throw new ImportInvoiceException("Cant find negative OccTemplate");
			}
			netToPay = netToPay.abs();
			if (amountWithoutTax != null) {
				amountWithoutTax = amountWithoutTax.abs();
			}
			if (amountTax != null ) {
				amountTax = amountTax.abs();
			}
			if (amountWithTax != null) {
				amountWithTax = amountWithTax.abs();
			}
		}


		recordedInvoice.setReference(invoice.getInvoiceNumber());
		recordedInvoice.setAccountCode(invoiceTemplate.getAccountCode());
		recordedInvoice.setOccCode(invoiceTemplate.getCode());
		recordedInvoice.setOccDescription(invoiceTemplate.getDescription());
		recordedInvoice.setTransactionCategory(invoiceTemplate.getOccCategory());
		recordedInvoice.setAccountCodeClientSide(invoiceTemplate.getAccountCodeClientSide());

		recordedInvoice.setAmount(amountWithTax);
		recordedInvoice.setUnMatchingAmount(amountWithTax);
		recordedInvoice.setMatchingAmount(BigDecimal.ZERO);

		recordedInvoice.setAmountWithoutTax(amountWithoutTax);
		recordedInvoice.setTaxAmount(amountTax);
		recordedInvoice.setNetToPay(invoice.getNetToPay());
		List<String> orderNums = new ArrayList<String>();
		if(invoice.getOrders()!=null){
			for(Order order : invoice.getOrders()){
				orderNums.add(order.getOrderNumber());
			}
			recordedInvoice.setOrderNumber(StringUtils.concatenate("|", orderNums));
		}
		try {
			recordedInvoice.setDueDate(DateUtils.setTimeToZero(invoice.getDueDate()));
		} catch (Exception e) {
			log.error("error with due date ", e);
			throw new ImportInvoiceException("Error on DueDate");
		}

		try {
			recordedInvoice.setInvoiceDate(DateUtils.setTimeToZero(invoice.getInvoiceDate()));
			recordedInvoice.setTransactionDate(DateUtils.setTimeToZero(invoice.getInvoiceDate()));
		} catch (Exception e) {
			log.error("error with invoice date", e);
			throw new ImportInvoiceException("Error on invoiceDate");
		}

		recordedInvoice.setPaymentMethod(billingAccount.getPaymentMethod());

		if (billingAccount.getBankCoordinates() != null) {
			recordedInvoice.setPaymentInfo(billingAccount.getBankCoordinates().getIban());
			recordedInvoice.setPaymentInfo1(billingAccount.getBankCoordinates().getBankCode());
			recordedInvoice.setPaymentInfo2(billingAccount.getBankCoordinates().getBranchCode());
			recordedInvoice.setPaymentInfo3(billingAccount.getBankCoordinates().getAccountNumber());
			recordedInvoice.setPaymentInfo4(billingAccount.getBankCoordinates().getKey());
			recordedInvoice.setPaymentInfo5(billingAccount.getBankCoordinates().getBankName());
			recordedInvoice.setPaymentInfo6(billingAccount.getBankCoordinates().getBic());
			recordedInvoice.setBillingAccountName(billingAccount.getBankCoordinates().getAccountOwner());
		}

		recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
		create(recordedInvoice, currentUser);
		invoice.setRecordedInvoice(recordedInvoice);
		invoice.updateAudit(currentUser);
		invoiceService.updateNoCheck(invoice);
	}
}
