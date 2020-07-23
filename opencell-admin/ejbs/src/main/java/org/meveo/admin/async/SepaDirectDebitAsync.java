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
import java.util.Arrays;
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
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.AccountOperationService;
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

	/** The dd request item service. */
	@Inject
	private DDRequestItemService ddRequestItemService;

	/** The job execution service. */
	@Inject
	private JobExecutionService jobExecutionService;
	
	@Inject
	private DDRequestLOTService ddRequestLOTService;
	
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
	 * @param listAoToPay      the list ao to pay
	 * @param appProvider      the app provider
	 * @return the future
	 * @throws BusinessException the business exception
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Future<Map<String,Object>> launchAndForgetDDRequesltLotCreation(DDRequestLOT ddRequestLOT, List<AccountOperation> listAoToPay,
			Provider appProvider) throws BusinessException {
				
		Map<String,Object> result = new HashMap<String, Object>();

		String allErrors="";
		Long nbItemsKo = 0L,nbItemsOk=0L;	
		BigDecimal totalAmount = BigDecimal.ZERO;
			for (AccountOperation ao : listAoToPay) {
				ao = accountOperationService.refreshOrRetrieve(ao);
				CustomerAccount ca = ao.getCustomerAccount();
				String errorMsg = ddRequestLOTService.getMissingField(ao, ddRequestLOT, appProvider, ca);
				String caFullName =  ca.getName() != null ? ca.getName().getFullName() : "";
				ddRequestLOT.getDdrequestItems().add(ddRequestItemService.createDDRequestItem(ao.getUnMatchingAmount(), ddRequestLOT, caFullName, errorMsg, Arrays.asList(ao)));
				if (errorMsg != null) {
					nbItemsKo++;
					allErrors += errorMsg + " ; ";
				} else {
					nbItemsOk++;
					totalAmount = totalAmount.add(ao.getUnMatchingAmount());
				}
			}
			
			result.put("nbItemsOk",nbItemsOk);
			result.put("nbItemsKo",nbItemsKo);
			result.put("allErrors",allErrors);
			result.put("totalAmount",totalAmount);
			
			return new AsyncResult<Map<String,Object>>(result);
		
	}


	
}
