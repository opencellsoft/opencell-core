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
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.Refund;
import org.meveo.service.base.PersistenceService;

/**
 * Refund service implementation.
 */
@Stateless
public class RefundService extends PersistenceService<Refund> {

    @Inject
    private PaymentService paymentService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @MeveoAudit
    @Override
    public void create(Refund entity) throws BusinessException {
        super.create(entity);
    }

    /**
     * Refund by card token. An existing and preferred card payment method will be used. If currently preferred card payment method is not valid, a new currently valid card payment
     * will be used (and marked as preferred)
     * 
     * @param customerAccount Customer account
     * @param ctsAmount Amount to mpau
     * @param aoIdsToRefund list of account operations ids to be refund
     * @param createAO true if wanting to create account operation
     * @param matchingAO true if matching account operation.
     * @param paymentGateway if set, this paymentGateway will be used
     * @return payment by card response dto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException un balance amount exception.
     */
    public PaymentResponseDto refundByCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToRefund, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        return paymentService.doPayment(customerAccount, ctsAmount, aoIdsToRefund, createAO, matchingAO, paymentGateway, null, null, null, null, null, false,PaymentMethodEnum.CARD);
    }

    /**
     * Refund by card. A new card payment type is registered if payment was successfull.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent
     * @param cardNumber card number
     * @param ownerName owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoIdsToRefund list of account operation ids to be refunded
     * @param createAO true if creating account operation
     * @param matchingAO true if matching account operation
     * @return payment by card dto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException un balance amount exception.
     */
    public PaymentResponseDto refundByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoIdsToRefund, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        return paymentService.doPayment(customerAccount, ctsAmount, aoIdsToRefund, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, false,PaymentMethodEnum.CARD);
    }

    /**
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent
     * @param doPaymentResponseDto payment by card dto
     * @return the AO id created
     * @throws BusinessException business exception.
     */
    public Long createRefundAO(CustomerAccount customerAccount, Long ctsAmount, PaymentResponseDto doPaymentResponseDto) throws BusinessException {
        OCCTemplate occTemplate = oCCTemplateService.findByCode(ParamBean.getInstance().getProperty("occ.refund.card", "RF_CARD"));
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + (ParamBean.getInstance().getProperty("occ.refund.card", "RF_CARD")));
        }
        Refund refund = new Refund();
        refund.setPaymentMethod(customerAccount.getPreferredPaymentMethod().getPaymentType());
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
        create(refund);
        return refund.getId();

    }
}
