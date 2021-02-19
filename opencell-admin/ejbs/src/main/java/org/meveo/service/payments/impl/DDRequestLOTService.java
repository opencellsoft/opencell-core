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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentLevelEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.job.JobExecutionService;

/**
 * The Class DDRequestLOTService.
 * 
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 10.0
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

	@Inject
	private DDRequestBuilderFactory ddRequestBuilderFactory;

	@Inject
	private AccountOperationService accountOperationService;
	
	
	@Inject
	private PaymentGatewayService paymentGatewayService;

	@Inject
    protected JobExecutionService jobExecutionService;

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

			DDRequestBuilderInterface ddRequestBuilderInterface = ddRequestBuilderFactory.getInstance(ddRequestBuilder);

			DDRequestLOT ddRequestLOT = new DDRequestLOT();
			ddRequestLOT.setDdRequestBuilder(ddRequestBuilder);
			ddRequestLOT.setSendDate(new Date());
			ddRequestLOT.setPaymentOrRefundEnum(ddrequestLotOp.getPaymentOrRefundEnum());
			ddRequestLOT.setSeller(ddrequestLotOp.getSeller());
			ddRequestLOT.setSendDate(new Date());
			create(ddRequestLOT);
			ddRequestLOT.setFileName(ddRequestBuilderInterface.getDDFileName(ddRequestLOT, appProvider));

			return ddRequestLOT;
		} catch (Exception e) {
			log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
			ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
			ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
			jobExecutionService.registerError(result, ddrequestLotOp.getId(), e.getMessage());
			result.addReport("ddrequestLotOp id : " + ddrequestLotOp.getId() + " RejectReason : " + e.getMessage());
			return null;
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addItems(DDRequestLotOp ddrequestLotOp, DDRequestLOT ddRequestLOT, List<AccountOperation> listAoToPay, DDRequestBuilder ddRequestBuilder,
			JobExecutionResultImpl result) throws BusinessEntityException, Exception {
		try {
			BigDecimal totalAmount = BigDecimal.ZERO;
			int nbItemsKo = 0;
			int nbItemsOk = 0;
			String allErrors = "";

			if (ddRequestBuilder.getPaymentLevel() == PaymentLevelEnum.AO) {

				List<Future<Map<String, Object>>> futures = new ArrayList<>();
				SubListCreator<AccountOperation> subListCreator = new SubListCreator(listAoToPay, Runtime.getRuntime().availableProcessors());
				while (subListCreator.isHasNext()) {
					futures.add(sepaDirectDebitAsync.launchAndForgetDDRequesltLotCreation(ddRequestLOT, subListCreator.getNextWorkSet(), appProvider));
				}
				// Wait for all async methods to finish
				for (Future<Map<String, Object>> future : futures) {
					try {
						Map<String, Object> futureResult = future.get();
						nbItemsKo += (Long) futureResult.get("nbItemsKo");
						nbItemsOk += (Long) futureResult.get("nbItemsOk");
						totalAmount = totalAmount.add((BigDecimal) futureResult.get("totalAmount"));
						allErrors += (String) futureResult.get("allErrors");

					} catch (InterruptedException e) {
						// It was cancelled from outside - no interest

					} catch (ExecutionException e) {
						Throwable cause = e.getCause();
						jobExecutionService.registerError(result, cause.getMessage());
						result.addReport(cause.getMessage());
						log.error("Failed to execute async method", cause);
					}
				}

			}

			if (ddRequestBuilder.getPaymentLevel() == PaymentLevelEnum.CA) {
				Map<CustomerAccount, List<AccountOperation>> aosByCA = new HashMap<CustomerAccount, List<AccountOperation>>();
				for (AccountOperation ao : listAoToPay) {
					ao = accountOperationService.refreshOrRetrieve(ao);
					List<AccountOperation> aos = new ArrayList<AccountOperation>();
					if (aosByCA.containsKey(ao.getCustomerAccount())) {
						aos = aosByCA.get(ao.getCustomerAccount());
					}
					aos.add(ao);
					aosByCA.put(ao.getCustomerAccount(), aos);
				}
				for (Map.Entry<CustomerAccount, List<AccountOperation>> entry : aosByCA.entrySet()) {
					BigDecimal amountToPayByItem = BigDecimal.ZERO;
					String allErrorsByItem = "";
					CustomerAccount ca = entry.getKey();
					String caFullName = ca.getName() != null ? ca.getName().getFullName() : "";
					for (AccountOperation ao : entry.getValue()) {
						String errorMsg = getMissingField(ao, ddRequestLOT, appProvider, ca);
						if (errorMsg != null) {
							allErrorsByItem += errorMsg + " ; ";
						} else {
							amountToPayByItem = amountToPayByItem.add(ao.getUnMatchingAmount());
						}
					}

					ddRequestLOT.getDdrequestItems().add(ddRequestItemService.createDDRequestItem(amountToPayByItem, ddRequestLOT, caFullName, allErrorsByItem, entry.getValue()));

					if (StringUtils.isBlank(allErrorsByItem)) {
						nbItemsOk++;
						totalAmount = totalAmount.add(amountToPayByItem);
					} else {
						nbItemsKo++;
						allErrors += allErrorsByItem + " ; ";
					}
				}
			}
			ddRequestLOT.setNbItemsKo(nbItemsKo);
			ddRequestLOT.setNbItemsOk(nbItemsOk);
			ddRequestLOT.setRejectedCause(StringUtils.truncate(allErrors, 255, true));
			ddRequestLOT.setTotalAmount(totalAmount);
			update(ddRequestLOT);
			log.info("Successful createDDRquestLot totalAmount: {}", ddRequestLOT.getTotalAmount());

		} catch (Exception e) {
			log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
			ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
			ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
			jobExecutionService.registerError(result, ddrequestLotOp.getId(), e.getMessage());
			result.addReport("ddrequestLotOp id : " + ddrequestLotOp.getId() + " RejectReason : " + e.getMessage());

		}

	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void generateDDRquestLotFile(DDRequestLOT ddRequestLOT, final DDRequestBuilderInterface ddRequestBuilderInterface, Provider appProvider)
			throws BusinessEntityException, Exception {		
		ddRequestBuilderInterface.generateDDRequestLotFile(ddRequestLOT, appProvider);
	}

	public void createPaymentsOrRefundsForDDRequestLot(DDRequestLOT ddRequestLOT) throws Exception {
		createPaymentsOrRefundsForDDRequestLot(ddRequestLOT, 1L, 0L, null);
	}

	/**
	 * Creates the payments or refunds for DD request lot.
	 *
	 * @param ddRequestLOT the dd request LOT
	 * @throws Exception
	 */
	public void createPaymentsOrRefundsForDDRequestLot(DDRequestLOT ddRequestLOT, Long nbRuns, Long waitingMillis, JobExecutionResultImpl result) throws Exception {
		ddRequestLOT = refreshOrRetrieve(ddRequestLOT);
		log.info("createPaymentsForDDRequestLot ddRequestLotId: {}, size:{}", ddRequestLOT.getId(), ddRequestLOT.getDdrequestItems().size());
		if (ddRequestLOT.isPaymentCreated()) {
			throw new BusinessException("Payment Already created.");
		}

		SubListCreator subListCreator = new SubListCreator(ddRequestLOT.getDdrequestItems(), nbRuns.intValue());
		List<Future<String>> futures = new ArrayList<Future<String>>();
		while (subListCreator.isHasNext()) {
			futures.add(sepaDirectDebitAsync.launchAndForgetPaymentCreation((List<DDRequestItem>) subListCreator.getNextWorkSet(), result));
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
				if (result != null) {
					jobExecutionService.registerError(result, cause.getMessage());
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
	
	

	/**
	 * Gets the missing field.
	 *
	 * @param accountOperation the account operation
	 * @param ddRequestLOT     the dd request LOT
	 * @param appProvider      the app provider
	 * @param ca 
	 * @return the missing field
	 * @throws BusinessException the business exception
	 */
	public String getMissingField(AccountOperation accountOperation, DDRequestLOT ddRequestLOT, Provider appProvider, CustomerAccount ca) throws BusinessException {
		String prefix = "AO.id:" + accountOperation.getId() + " : ";
		if (ca == null) {
			return prefix + "recordedInvoice.ca";
		}
		prefix = "CA.code:"+ca.getCode()+" AO.id:" + accountOperation.getId() + " : ";
		if (ca.getName() == null) {
			return prefix + "ca.name";
		}
		PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
		if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
			if (((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification() == null) {
				return prefix + "paymentMethod.mandateIdentification";
			}
			if (((DDPaymentMethod) preferedPaymentMethod).getMandateDate() == null) {
				return prefix + "paymentMethod.mandateDate";
			}
		} else {
			return prefix + "DDPaymentMethod";
		}

		if (accountOperation.getUnMatchingAmount() == null) {
			return prefix + "invoice.amount";
		}
		if (StringUtils.isBlank(appProvider.getDescription())) {
			return prefix + "provider.description";
		}
		 BankCoordinates bankCoordinates = null;
	        if (ddRequestLOT.getSeller() != null) {
	             
	            PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(ddRequestLOT.getSeller(), PaymentMethodEnum.DIRECTDEBIT);
	            if (paymentGateway == null) {
	                throw new BusinessException("Cant find payment gateway for seller : " + ddRequestLOT.getSeller());
	            }
	            bankCoordinates =  paymentGateway.getBankCoordinates();
	        } else {
	            bankCoordinates =  appProvider.getBankCoordinates();
	        }       
	               
	        if (bankCoordinates == null) {
	            return prefix + "provider or seller bankCoordinates";
	        }
		if (bankCoordinates.getIban() == null) {
			return prefix + "bankCoordinates.iban";
		}
		if (bankCoordinates.getBic() == null) {
			return prefix + "bankCoordinates.bic";
		}
		if (bankCoordinates.getIcs() == null) {
			return prefix + "bankCoordinates.ics";
		}
		if (accountOperation.getReference() == null) {
			return prefix + "accountOperation.reference";
		}
		if (ca.getDescription() == null) {
			return prefix + "ca.description";
		}
		return null;
	}

}
