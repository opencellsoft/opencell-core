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

package org.meveo.admin.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 * @lastModifiedVersion 10.0
 */

@Stateless
public class UnitPaymentJobBean {

    @Inject
    private Logger log;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private PaymentService paymentService;


    public void execute(JobExecutionResultImpl result, Long customerAccountId, List<Long> listAOids, Long amountToPay, boolean createAO, boolean matchingAO,
            OperationCategoryEnum operationCategory, PaymentGateway paymentGateway, PaymentMethodEnum paymentMethodType, AccountOperationFilterScript aoFilterScript) {
        
        log.debug("Running with CustomerAccount ID={}", customerAccountId); 
        CustomerAccount customerAccount = null;
        try {
            customerAccount = customerAccountService.findById(customerAccountId);
            if (customerAccount == null) {
                return;
            }
            PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.payByCardToken(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                } else {
                    doPaymentResponseDto = paymentService.payByMandat(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                }
            } else {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.refundByCardToken(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                } else {
                    doPaymentResponseDto = paymentService.refundByMandat(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                }
            }
            if (PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()) {
                result.registerError(customerAccountId, doPaymentResponseDto.getErrorMessage());
                result.addReport("AccountOperation id  : " + listAOids + " RejectReason : " + doPaymentResponseDto.getErrorMessage());
                this.checkPaymentRetry(doPaymentResponseDto.getErrorCode(), listAOids, aoFilterScript);
            } else if (PaymentStatusEnum.ACCEPTED == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.PENDING == doPaymentResponseDto.getPaymentStatus()){
                result.registerSucces();               
            }            

        } catch (Exception e) {
            log.error("Failed to pay recorded AccountOperation id:{} customerAccountId:{}, " + listAOids,customerAccountId, e);
            result.registerError(listAOids.toString(), e.getMessage());
            result.addReport("AccountOperation id  : " + listAOids + " RejectReason : " + e.getMessage());
            this.checkPaymentRetry(null, listAOids, aoFilterScript);
        }

    }

	private void checkPaymentRetry(String errorCode, List<Long> listAOids, AccountOperationFilterScript aoFilterScript) {
		if (aoFilterScript != null) {
			try {
				Map<String, Object> methodContext = new HashMap<>();
				methodContext.put("ERROR_CODE", errorCode);
				methodContext.put("LIST_AO_IDs", listAOids);
				aoFilterScript.checkPaymentRetry(methodContext);
			} catch (Exception e) {
				log.error(" Error on checkPaymentRetry [{}]", e.getMessage());
			}
		}
    }
}