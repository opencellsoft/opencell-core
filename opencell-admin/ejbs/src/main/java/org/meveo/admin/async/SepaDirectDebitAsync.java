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
package org.meveo.admin.async;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jfree.util.Log;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.UnitSepaDirectDebitJobBean;
import org.meveo.admin.sepa.SepaFile;
import org.meveo.commons.utils.ParamBeanFactory;
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
import org.meveo.model.payments.PaymentLevelEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.Name;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestItemService;
import org.meveo.service.payments.impl.DDRequestLOTService;

/**
 * The Class SepaDirectDebitAsync.
 *
 * @author anasseh
 */

@Stateless
public class SepaDirectDebitAsync {

	/** The param bean factory. */
	@Inject
	protected ParamBeanFactory paramBeanFactory;

	/** The unit SSD job bean. */
	@Inject
	private UnitSepaDirectDebitJobBean unitSSDJobBean;

	/** The account operation service. */
	@Inject
	private AccountOperationService accountOperationService;

	/** The dd request LOT service. */
	@Inject
	private DDRequestLOTService ddRequestLOTService;

	/** The dd request item service. */
	@Inject
	private DDRequestItemService ddRequestItemService;

	/** The job execution service. */
	@Inject
	private JobExecutionService jobExecutionService;
	
	@Inject
	private SepaFile sepaFile;

	/**
	 * Create payments for all items from the ddRequestLot. One Item at a time in a
	 * separate transaction.
	 *
	 * @param ddRequestItems the dd request items
	 * @param result         Job execution result
	 * @return Future String
	 * @throws BusinessException BusinessException
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<String> launchAndForgetPaymentCreation(List<DDRequestItem> ddRequestItems, JobExecutionResultImpl result) throws BusinessException {
		for (DDRequestItem ddRequestItem : ddRequestItems) {

			if (result != null && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
				break;
			}
			try {
				unitSSDJobBean.execute(result, ddRequestItem);
			} catch (Exception e) {
				Log.warn("Error on launchAndForgetPaymentCreation", e);
				if(result != null) {
					result.registerError(e.getMessage());
				}
			}
		}
		return new AsyncResult<String>("OK");
	}

	/**
	 * Launch and forget DD requeslt lot creation.
	 *
	 * @param ddrequestLotOp   the ddrequest lot op
	 * @param ddRequestBuilder the dd request builder
	 * @param listAoToPay      the list ao to pay
	 * @param appProvider      the app provider
	 * @return the future
	 * @throws BusinessException the business exception
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Future<DDRequestLOT> launchAndForgetDDRequesltLotCreation(DDRequestLotOp ddrequestLotOp, DDRequestBuilder ddRequestBuilder, List<AccountOperation> listAoToPay,
			Provider appProvider) throws BusinessException {

		// currentUserProvider.reestablishAuthentication(lastCurrentUser);
		BigDecimal totalAmount = BigDecimal.ZERO;
		DDRequestLOT ddRequestLOT = new DDRequestLOT();
		ddRequestLOT.setDdRequestBuilder(ddRequestBuilder);
		ddRequestLOT.setSendDate(new Date());
		ddRequestLOT.setPaymentOrRefundEnum(ddrequestLotOp.getPaymentOrRefundEnum());
		ddRequestLOTService.create(ddRequestLOT);
		ddRequestLOT.setFileName(sepaFile.getDDFileName(ddRequestLOT, appProvider));
		int nbItemsKo = 0;
		int nbItemsOk = 0;
		String allErrors = "";

		if (ddRequestBuilder.getPaymentLevel() == PaymentLevelEnum.AO) {
			for (AccountOperation ao : listAoToPay) {
				ao = accountOperationService.refreshOrRetrieve(ao);
				CustomerAccount ca = ao.getCustomerAccount();
				String errorMsg = getMissingField(ao, ddRequestLOT, appProvider, ca);
				Name caName = ca.getName();
				String caFullName = this.getCaFullName(caName);
				ddRequestLOT.getDdrequestItems().add(ddRequestItemService.createDDRequestItem(ao.getUnMatchingAmount(), ddRequestLOT, caFullName, errorMsg, Arrays.asList(ao)));
				if (errorMsg != null) {
					nbItemsKo++;
					allErrors += errorMsg + " ; ";
				} else {
					nbItemsOk++;
					totalAmount = totalAmount.add(ao.getUnMatchingAmount());
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
				String caFullName = this.getCaFullName(ca.getName());
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

		return new AsyncResult<DDRequestLOT>(ddRequestLOT);
	}

	/**
	 * Launch and forget generation file.
	 *
	 * @param ddRequestBuilderInterface the dd request builder interface
	 * @param ddRequestLOT              the dd request LOT
	 * @param provider                  the provider
	 * @return the future
	 * @throws BusinessException the business exception
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Future<String> launchAndForgetGenerationFile(DDRequestBuilderInterface ddRequestBuilderInterface, DDRequestLOT ddRequestLOT, Provider provider)
			throws BusinessException {

		ddRequestBuilderInterface.generateDDRequestLotFile(ddRequestLOTService.retrieveIfNotManaged(ddRequestLOT), provider);
		return new AsyncResult<String>("OK");
	}

	/**
	 * Gets the ca full name.
	 *
	 * @param caName the ca name
	 * @return the ca full name
	 */
	private String getCaFullName(Name caName) {
		return caName != null ? caName.getFullName() : "";
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
		BankCoordinates bankCoordinates = appProvider.getBankCoordinates();

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
