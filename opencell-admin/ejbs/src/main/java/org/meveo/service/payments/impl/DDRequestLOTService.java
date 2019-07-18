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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SepaDirectDebitAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.commons.utils.StringUtils;

import org.meveo.model.crm.Provider;

import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;

import org.meveo.model.payments.PaymentStatusEnum;

import org.meveo.service.base.PersistenceService;



/**
 * The Class DDRequestLOTService.
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
@Stateless
public class DDRequestLOTService extends PersistenceService<DDRequestLOT> {

	/** The dd request item service. */
	@Inject
	private DDRequestItemService ddRequestItemService;

	@Inject
	private PaymentService paymentService;

	@Inject
	private SepaDirectDebitAsync sepaDirectDebitAsync;


	/**
	 * Creates the DDRequest lot.
	 *
	 * @param ddrequestLotOp   the ddrequest lot op
	 * @param listAoToPay      list of account operations
	 * @param ddRequestBuilder direct debit request builder
	 * @param result           the result
	 * @return the DD request LOT
	 * @throws BusinessEntityException the business entity exception
	 * @throws Exception               the exception
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DDRequestLOT createDDRquestLot(DDRequestLotOp ddrequestLotOp, List<AccountOperation> listAoToPay, DDRequestBuilder ddRequestBuilder, JobExecutionResultImpl result)
			throws BusinessEntityException, Exception {

		try {
			if (listAoToPay == null || listAoToPay.isEmpty()) {
				throw new BusinessEntityException("no invoices!");
			}
			Future<DDRequestLOT> futureisNow = sepaDirectDebitAsync.launchAndForgetDDRequesltLotCreation(ddrequestLotOp, ddRequestBuilder, listAoToPay, appProvider);
			DDRequestLOT ddRequestLOT = retrieveIfNotManaged(futureisNow.get());
			create(ddRequestLOT);
			log.info("Successful createDDRquestLot totalAmount: {}", ddRequestLOT.getTotalAmount());
			return ddRequestLOT;
		} catch (Exception e) {
			log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
			ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
			ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
			result.registerError(ddrequestLotOp.getId(), e.getMessage());
			result.addReport("ddrequestLotOp id : " + ddrequestLotOp.getId() + " RejectReason : " + e.getMessage());
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void generateDDRquestLotFile(DDRequestLOT ddRequestLOT, final DDRequestBuilderInterface ddRequestBuilderInterface, Provider appProvider)
			throws BusinessEntityException, Exception {
		ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
		ddRequestLOT.setFileName(ddRequestBuilderInterface.getDDFileName(ddRequestLOT, appProvider));	
		ddRequestBuilderInterface.generateDDRequestLotFile(ddRequestLOT, appProvider);
		ddRequestLOT.setSendDate(new Date());
		update(ddRequestLOT);

	}
	
	public void createPaymentsOrRefundsForDDRequestLot(DDRequestLOT ddRequestLOT) throws Exception {
		createPaymentsOrRefundsForDDRequestLot( ddRequestLOT ,1L, 0L,null);
	}

	/**
	 * Creates the payments or refunds for DD request lot.
	 *
	 * @param ddRequestLOT the dd request LOT
	 * @throws Exception
	 */
	public void createPaymentsOrRefundsForDDRequestLot(DDRequestLOT ddRequestLOT ,Long nbRuns, Long waitingMillis,JobExecutionResultImpl result) throws Exception {
		ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
		log.info("createPaymentsForDDRequestLot ddRequestLotId: {}, size:{}", ddRequestLOT.getId(), ddRequestLOT.getDdrequestItems().size());
		if (ddRequestLOT.isPaymentCreated()) {
			throw new BusinessException("Payment Already created.");
		}

		SubListCreator subListCreator = new SubListCreator(ddRequestLOT.getDdrequestItems(), nbRuns.intValue());
		List<Future<String>> futures = new ArrayList<Future<String>>();
		while (subListCreator.isHasNext()) {
			futures.add(sepaDirectDebitAsync.launchAndForgetPaymentCreation((List<DDRequestItem>) subListCreator.getNextWorkSet(),result));
			try {
				Thread.sleep(waitingMillis);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		}

		for (Future<String> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				// It was cancelled from outside - no interest
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				if(result != null) {
					result.registerError(cause.getMessage());
					result.addReport(cause.getMessage());
				}
				log.error("Failed to execute async method", cause);
			}
		}
		ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
		ddRequestLOT.setPaymentCreated(true);
		update(ddRequestLOT);
		log.info("Successful createPaymentsForDDRequestLot ddRequestLotId: {}", ddRequestLOT.getId());

	}

	/**
	 * Reject payment.
	 *
	 * @param ddRequestItem the dd request item
	 * @param rejectCause   the reject cause
	 * @throws BusinessException the business exception
	 */
	public void rejectPayment(DDRequestItem ddRequestItem, String rejectCause, String fileName) throws BusinessException {
		if (ddRequestItem.getRejectedFileName() != null) {
			log.warn("DDRequestItem already rejected.");
			return;
		}
		ddRequestItem.setRejectedFileName(fileName);
		AccountOperation automatedPaymentorRefund = null;
		if (ddRequestItem.getAutomatedPayment() != null) {
			automatedPaymentorRefund = ddRequestItem.getAutomatedPayment();
		} else {
			automatedPaymentorRefund = ddRequestItem.getAutomatedRefund();
		}
		if (automatedPaymentorRefund == null || automatedPaymentorRefund.getMatchingAmounts() == null || automatedPaymentorRefund.getMatchingAmounts().isEmpty()) {
			throw new BusinessException("ddRequestItem id :" + ddRequestItem.getId() + " Callback not expected");
		}
		paymentService.paymentCallback(automatedPaymentorRefund.getReference(), PaymentStatusEnum.REJECTED, rejectCause, rejectCause);
	}

	/**
	 * Process reject file.
	 *
	 * @param ddRejectFileInfos the dd reject file infos
	 * @throws BusinessException the business exception
	 */
	public void processRejectFile(DDRejectFileInfos ddRejectFileInfos) throws BusinessException {
		DDRequestLOT dDRequestLOT = null;
		if (ddRejectFileInfos.getDdRequestLotId() != null) {
			dDRequestLOT = findById(ddRejectFileInfos.getDdRequestLotId(), Arrays.asList("ddrequestItems"));
		}
		if (dDRequestLOT != null) {
			if (ddRejectFileInfos.isTheDDRequestFileWasRejected()) {
				// original message rejected at protocol level control
				CopyOnWriteArrayList<DDRequestItem> items = new CopyOnWriteArrayList<>(dDRequestLOT.getDdrequestItems());
				for (DDRequestItem ddRequestItem : items) {
					if (!ddRequestItem.hasError()) {
						rejectPayment(ddRequestItem, "RJCT", ddRejectFileInfos.getFileName());
					}
				}
				dDRequestLOT.setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
			}
			dDRequestLOT.setReturnFileName(ddRejectFileInfos.getFileName());
		}
		for (Entry<Long, String> entry : ddRejectFileInfos.getListInvoiceRefsRejected().entrySet()) {
			DDRequestItem ddRequestItem = ddRequestItemService.findById(entry.getKey(), Arrays.asList("ddRequestLOT"));
			if (ddRequestItem == null) {
				throw new BusinessException("Cant find item by id:" + entry.getKey());
			}

			rejectPayment(ddRequestItem, entry.getValue(), ddRejectFileInfos.getFileName());
			ddRequestItem.getDdRequestLOT().setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
			ddRequestItem.getDdRequestLOT().setReturnFileName(ddRejectFileInfos.getFileName());
		}
	}
}
