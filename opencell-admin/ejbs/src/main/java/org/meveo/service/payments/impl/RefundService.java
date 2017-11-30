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
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.Refund;
import org.meveo.service.base.PersistenceService;

/**
 * Refund service implementation.
 */
@Stateless
public class RefundService extends PersistenceService<Refund> {

    @Inject
    private PaymentMethodService paymentMethodService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private GatewayPaymentFactory gatewayPaymentFactory;

    @Inject
    private CustomerAccountService customerAccountService;

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
     * @param aoIdsToRefund
     * @param createAO
     * @param matchingAO
     * @return
     * @throws BusinessException
     * @throws NoAllOperationUnmatchedException
     * @throws UnbalanceAmountException
     */
    public PayByCardResponseDto refundByCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToRefund, boolean createAO, boolean matchingAO)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        if (customerAccount.getPaymentMethods() == null || customerAccount.getPaymentMethods().isEmpty()) {
            throw new BusinessException("There no payment token for customerAccount:" + customerAccount.getCode());
        }

        PaymentMethod preferredMethod = customerAccount.getPreferredPaymentMethod();
        if (preferredMethod == null) {
            throw new BusinessException("There is no payment method for customerAccount:" + customerAccount.getCode());

        } else if (!(preferredMethod instanceof CardPaymentMethod)) {
            throw new BusinessException("Can not process payment as prefered payment method is " + preferredMethod.getPaymentType());
        }

        // If card payment method is currently not valid, find a valid one and mark it as preferred or throw an exception
        if (!((CardPaymentMethod) preferredMethod).isValidForDate(new Date())) {
            preferredMethod = customerAccount.markCurrentlyValidCardPaymentAsPreferred();
            if (preferredMethod != null) {
                customerAccount = customerAccountService.update(customerAccount);
            } else {
                throw new BusinessException("There is no currently valid payment method for customerAccount:" + customerAccount.getCode());
            }
        }

        CardPaymentMethod cardPaymentMethod = (CardPaymentMethod) preferredMethod;
        GatewayPaymentInterface gatewayPaymentInterface = null;
        try{
             gatewayPaymentInterface = gatewayPaymentFactory
                .getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));
        }catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        PayByCardResponseDto doPaymentResponseDto = gatewayPaymentInterface.doRefundToken(cardPaymentMethod, ctsAmount, null);

        if (PaymentStatusEnum.ACCEPTED == doPaymentResponseDto.getPaymentStatus()) {
            // log.error("AKK updating card payment with user id {} {}", cardPaymentMethod.getAlias(), doPaymentResponseDto.getCodeClientSide());
            cardPaymentMethod.setUserId(doPaymentResponseDto.getCodeClientSide());
            cardPaymentMethod = (CardPaymentMethod) paymentMethodService.update(cardPaymentMethod);
            Long aoRefundId = null;
            if (createAO) {
                try {
                    aoRefundId = createRefundAO(customerAccount, ctsAmount, doPaymentResponseDto);
                    doPaymentResponseDto.setAoCreated(true);
                } catch (Exception e) {
                    log.warn("Cant create Account operation payment :", e);
                }
                if (matchingAO) {
                    try {
                        List<Long> aoIdsToMatch = aoIdsToRefund;
                        aoIdsToMatch.add(aoRefundId);
                        matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);
                        doPaymentResponseDto.setMatchingCreated(true);
                    } catch (Exception e) {
                        log.warn("Cant create matching :", e);
                    }
                }
            }
        } else {
            log.warn("Payment by card {} was not successfull. Status: {}", cardPaymentMethod.getTokenId(), doPaymentResponseDto.getPaymentStatus());
        }

        return doPaymentResponseDto;
    }

    /**
     * Refund by card. A new card payment type is registered if payment was successfull.
     * 
     * @param customerAccount
     * @param ctsAmount
     * @param cardNumber
     * @param ownerName
     * @param cvv
     * @param expiryDate
     * @param cardType
     * @param aoIdsToRefund
     * @param createAO
     * @param matchingAO
     * @return
     * @throws BusinessException
     * @throws NoAllOperationUnmatchedException
     * @throws UnbalanceAmountException
     */
    public PayByCardResponseDto refundByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoIdsToRefund, boolean createAO, boolean matchingAO)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        String coutryCode = null;//TODO : waiting #2830
                
        GatewayPaymentInterface gatewayPaymentInterface = null;
        try{        
             gatewayPaymentInterface = gatewayPaymentFactory
                .getInstance(GatewayPaymentNamesEnum.valueOf(ParamBean.getInstance().getProperty("meveo.gatewayPayment", "CUSTOM_API")));
        }catch (Exception e) {
            log.warn("Cant find payment gateway");
        }
        
        PayByCardResponseDto doPaymentResponseDto = gatewayPaymentInterface.doRefundCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, coutryCode,
            null);

        if (PaymentStatusEnum.ACCEPTED == doPaymentResponseDto.getPaymentStatus()) {
            CardPaymentMethod paymentMethod = new CardPaymentMethod();
            paymentMethod.setAlias("Card_" + cardNumber.substring(12, 16));
            paymentMethod.setCardNumber(cardNumber);
            paymentMethod.setCardType(cardType);
            paymentMethod.setCustomerAccount(customerAccount);
            paymentMethod.setPreferred(true);
            paymentMethod.setMonthExpiration(new Integer(expiryDate.substring(0, 2)));
            paymentMethod.setYearExpiration(new Integer(expiryDate.substring(2, 4)));
            paymentMethod.setOwner(ownerName);
            paymentMethod.setTokenId(doPaymentResponseDto.getTokenId());
            paymentMethodService.create(paymentMethod);

            Long aoRefundId = null;
            if (createAO) {
                try {
                    aoRefundId = createRefundAO(customerAccount, ctsAmount, doPaymentResponseDto);
                    doPaymentResponseDto.setAoCreated(true);
                } catch (Exception e) {
                    log.warn("Cant create Account operation payment :" + e.getMessage());
                }
                if (matchingAO) {
                    try {
                        List<Long> aoIdsToMatch = aoIdsToRefund;
                        aoIdsToMatch.add(aoRefundId);
                        matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);
                        doPaymentResponseDto.setMatchingCreated(true);
                    } catch (Exception e) {
                        log.warn("Cant create matching :" + e.getMessage());
                    }
                }
            }
        }
        return doPaymentResponseDto;
    }
    
    /**
     * 
     * @param customerAccount
     * @param ctsAmount
     * @param paymentID
     * @return the AO id created
     * @throws BusinessException
     */
    public Long createRefundAO(CustomerAccount customerAccount, Long ctsAmount, PayByCardResponseDto doPaymentResponseDto) throws BusinessException {
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
