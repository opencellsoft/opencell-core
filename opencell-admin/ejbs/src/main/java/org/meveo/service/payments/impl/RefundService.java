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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.Refund;
import org.meveo.service.base.PersistenceService;

/**
 * Refund service implementation.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class RefundService extends PersistenceService<Refund> {

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private AccountOperationService accountOperationService;

    @MeveoAudit
    @Override
    public void create(Refund entity) throws BusinessException {
        super.create(entity);
    }

    /**
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent
     * @param doPaymentResponseDto payment by card dto
     * @param paymentMethodType payment Method Type
     * @param aoIdsToPay list AO to refunded
     * @return the AO id created
     * @throws BusinessException business exception.
     */
    public Long createRefundAO(CustomerAccount customerAccount, Long ctsAmount, PaymentResponseDto doPaymentResponseDto, PaymentMethodEnum paymentMethodType, List<Long> aoIdsToPay)
            throws BusinessException {
        String occTemplateCode = paramBeanFactory.getInstance().getProperty("occ.refund.card", "REF_CRD");
        if (paymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
            occTemplateCode = paramBeanFactory.getInstance().getProperty("occ.refund.dd", "REF_DDT");
        }
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        Refund refund = new Refund();
        refund.setPaymentMethod(paymentMethodType);
        refund.setAmount((new BigDecimal(ctsAmount).divide(new BigDecimal(100))));
        refund.setUnMatchingAmount(refund.getAmount());
        refund.setMatchingAmount(BigDecimal.ZERO);
        refund.setAccountingCode(occTemplate.getAccountingCode());
        refund.setCode(occTemplate.getCode());
        refund.setDescription(occTemplate.getDescription());
        refund.setType(doPaymentResponseDto.getPaymentBrand());
        refund.setTransactionCategory(occTemplate.getOccCategory());
        refund.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
        refund.setCustomerAccount(customerAccount);
        refund.setReference(doPaymentResponseDto.getPaymentID());
        refund.setTransactionDate(new Date());
        refund.setMatchingStatus(MatchingStatusEnum.O);
        refund.setBankReference(doPaymentResponseDto.getBankRefenrence());
        BigDecimal sumTax = BigDecimal.ZERO;
        BigDecimal sumWithoutTax = BigDecimal.ZERO;
        String orderNums = "";
        for (Long aoId : aoIdsToPay) {
            AccountOperation ao = accountOperationService.findById(aoId);
            if(ao != null) {
	            sumTax = sumTax.add(ao.getTaxAmount());
	            sumWithoutTax = sumWithoutTax.add(ao.getAmountWithoutTax());
	            if (!StringUtils.isBlank(ao.getOrderNumber())) {
	                orderNums = orderNums + ao.getOrderNumber() + "|";
	            }
            }
        }
        refund.setTaxAmount(sumTax);
        refund.setAmountWithoutTax(sumWithoutTax);
        refund.setOrderNumber(orderNums);
        create(refund);
        return refund.getId();

    }

}
