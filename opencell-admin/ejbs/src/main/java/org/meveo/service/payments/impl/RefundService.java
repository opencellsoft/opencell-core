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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBeanFactory;
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
 */
@Stateless
public class RefundService extends PersistenceService<Refund> {

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private AccountOperationService accountOperationService;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

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
        refund.setAccountCode(occTemplate.getAccountCode());
        refund.setOccCode(occTemplate.getCode());
        refund.setOccDescription(occTemplate.getDescription());
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
            sumTax = sumTax.add(ao.getTaxAmount());
            sumWithoutTax = sumWithoutTax.add(ao.getAmountWithoutTax());
            if (!StringUtils.isBlank(ao.getOrderNumber())) {
                orderNums = orderNums + ao.getOrderNumber() + "|";
            }
        }
        refund.setTaxAmount(sumTax);
        refund.setAmountWithoutTax(sumWithoutTax);
        refund.setOrderNumber(orderNums);
        create(refund);
        return refund.getId();

    }

}
