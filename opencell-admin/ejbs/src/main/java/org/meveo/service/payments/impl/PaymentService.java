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
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * Payment service implementation.
 */
@Stateless
public class PaymentService extends PersistenceService<Payment> {

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

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private PaymentHistoryService paymentHistoryService;

    @MeveoAudit
    @Override
    public void create(Payment entity) throws BusinessException {
        super.create(entity);
    }

    /**
     * Pay by card token. An existing and preferred card payment method will be used. If currently preferred card payment method is not valid, a new currently valid card payment
     * will be used (and marked as preferred).
     * 
     * @param customerAccount Customer account
     * @param ctsAmount Amount to pay in cent
     * @param aoIdsToPay list of account operations's id
     * @param createAO true if need to create account operation
     * @param matchingAO true if matching operation.
     * @param paymentGateway if set, this paymentGateway will be used
     * @return instance of PayByCardResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto payByCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true,PaymentMethodEnum.CARD);
    }

    /**
     * Pay by card. A new card payment type is registered if payment was successfull.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoIdsToPay list of account operation's id
     * @param createAO true if create account operation.
     * @param matchingAO true if matching account operation.
     * @return instance of PayByCardResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto payByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, true,PaymentMethodEnum.CARD);
    }
    
    /**
     * @param customerAccount
     * @param longValue
     * @param listAOids
     * @param createAO
     * @param matchingAO
     * @param paymentGateway
     * @return
     * @throws UnbalanceAmountException 
     * @throws NoAllOperationUnmatchedException 
     * @throws BusinessException 
     */
    public PaymentResponseDto payByMandat(CustomerAccount customerAccount, long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true,PaymentMethodEnum.DIRECTDEBIT);
    }

    

    /**
     * Do payment or refund by token or card
     * 
     * @param customerAccount
     * @param ctsAmount
     * @param aoIdsToPay
     * @param createAO
     * @param matchingAO
     * @param paymentGateway
     * @param cardNumber
     * @param ownerName
     * @param cvv
     * @param expiryDate
     * @param cardType
     * @param isPayment
     * @return
     * @throws BusinessException
     * @throws NoAllOperationUnmatchedException
     * @throws UnbalanceAmountException
     */
    public PaymentResponseDto doPayment(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway, String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment,PaymentMethodEnum paymentMethodType)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        PaymentResponseDto doPaymentResponseDto = null;
        CardPaymentMethod cardPaymentMethod = null;
        OperationCategoryEnum operationCat = isPayment ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT;
        try {
            boolean isNewCard = !StringUtils.isBlank(cardNumber);

            GatewayPaymentInterface gatewayPaymentInterface = null;
            if (paymentGateway == null) {
                paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, cardPaymentMethod);
            }
            if (paymentGateway == null) {
                throw new BusinessException("No payment gateway");
            }

            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
            
            if(!isNewCard) {
                if (customerAccount.getPaymentMethods() == null || customerAccount.getPaymentMethods().isEmpty()) {
                    throw new BusinessException("There no payment method for customerAccount:" + customerAccount.getCode());
                } 
            }
            
            
            if(PaymentMethodEnum.CARD == paymentMethodType) {
                
                
            }
            if(PaymentMethodEnum.DIRECTDEBIT == paymentMethodType) {
                
            }
            
            
            
            if (/*isWithToken*/true) {

                PaymentMethod preferredMethod = customerAccount.getPreferredPaymentMethod();
                if (preferredMethod == null) {
                    throw new BusinessException("There is no payment method for customerAccount:" + customerAccount.getCode());

                } else if (!(preferredMethod instanceof CardPaymentMethod) && paymentMethodType == PaymentMethodEnum.CARD) {
                    throw new BusinessException("Can not process payment card as prefered payment method is " + preferredMethod.getPaymentType());
                }else if (!(preferredMethod instanceof DDPaymentMethod) && paymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
                    throw new BusinessException("Can not process payment sepa as prefered payment method is " + preferredMethod.getPaymentType());
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
                cardPaymentMethod = (CardPaymentMethod) preferredMethod;
                
                
                if (isPayment) {
                    doPaymentResponseDto = gatewayPaymentInterface.doPaymentToken(cardPaymentMethod, ctsAmount, null);
                } else {
                    doPaymentResponseDto = gatewayPaymentInterface.doRefundToken(cardPaymentMethod, ctsAmount, null);
                }
            } else {
                if (isPayment) {
                    doPaymentResponseDto = gatewayPaymentInterface.doPaymentCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
                } else {
                    doPaymentResponseDto = gatewayPaymentInterface.doRefundCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
                }
            }

            Long aoPaymentId = null;
            PaymentErrorTypeEnum errorType = null;
            PaymentStatusEnum status = doPaymentResponseDto.getPaymentStatus();
            if (PaymentStatusEnum.ACCEPTED == status || PaymentStatusEnum.PENDING == status) {
                if (!/*isWithToken*/true) {
                    cardPaymentMethod = addCardFromPayment(doPaymentResponseDto.getTokenId(), customerAccount, cardNumber, cardType, ownerName, cvv, expiryDate);
                }
                cardPaymentMethod.setUserId(doPaymentResponseDto.getCodeClientSide());
                cardPaymentMethod = (CardPaymentMethod) paymentMethodService.update(cardPaymentMethod);

                if (createAO) {
                    try {
                        if (isPayment) {
                        aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto);
                        }else {
                            aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto);
                        }
                        doPaymentResponseDto.setAoCreated(true);
                    } catch (Exception e) {
                        log.warn("Cant create Account operation payment :", e);
                    }
                    if (matchingAO) {
                        try {
                            List<Long> aoIdsToMatch = aoIdsToPay;
                            aoIdsToMatch.add(aoPaymentId);
                            matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIdsToMatch, null, MatchingTypeEnum.A);
                            doPaymentResponseDto.setMatchingCreated(true);
                        } catch (Exception e) {
                            log.warn("Cant create matching :", e);
                        }
                    }
                }
            } else {
                errorType = PaymentErrorTypeEnum.REJECT;
                log.warn("Payment by card {} was not successfull. Status: {}", cardPaymentMethod.getTokenId(), doPaymentResponseDto.getPaymentStatus());
            }
            paymentHistoryService.addHistory(customerAccount, findById(aoPaymentId), ctsAmount, status, doPaymentResponseDto.getErrorCode(), doPaymentResponseDto.getErrorMessage(),
                errorType, operationCat, paymentGateway, cardPaymentMethod);

        } catch (Exception e) {
            log.error("Error during payment AO:", e);
            if (doPaymentResponseDto == null) {
                doPaymentResponseDto = new PaymentResponseDto();
            }
            doPaymentResponseDto.setErrorMessage(e.getMessage());
            doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
            paymentHistoryService.addHistory(customerAccount, null, ctsAmount, PaymentStatusEnum.ERROR, null, e.getMessage(), PaymentErrorTypeEnum.ERROR,
                operationCat, paymentGateway, cardPaymentMethod);
        }
        return doPaymentResponseDto;
    }

    /**
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param doPaymentResponseDto payment responsse dto
     * @return the AO id created
     * @throws BusinessException business exception.
     */
    public Long createPaymentAO(CustomerAccount customerAccount, Long ctsAmount, PaymentResponseDto doPaymentResponseDto) throws BusinessException {
        OCCTemplate occTemplate = oCCTemplateService.findByCode(ParamBean.getInstance().getProperty("occ.payment.card", "RG_CARD"));
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + (ParamBean.getInstance().getProperty("occ.payment.card", "RG_CARD")));
        }
        Payment payment = new Payment();
        payment.setPaymentMethod(customerAccount.getPreferredPaymentMethod().getPaymentType());
        payment.setAmount((new BigDecimal(ctsAmount).divide(new BigDecimal(100))));
        payment.setUnMatchingAmount(payment.getAmount());
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setAccountCode(occTemplate.getAccountCode());
        payment.setOccCode(occTemplate.getCode());
        payment.setOccDescription(occTemplate.getDescription());
        payment.setType(doPaymentResponseDto.getPaymentBrand());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
        payment.setCustomerAccount(customerAccount);
        payment.setReference(doPaymentResponseDto.getPaymentID());
        payment.setTransactionDate(new Date());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        payment.setBankReference(doPaymentResponseDto.getBankRefenrence());
        create(payment);
        return payment.getId();

    }
    /**
     * Create a card as CardPaymentMethod from initial payment
     * 
     * @param tokenId tokenId returned from the initial payment
     * @param customerAccount
     * @param cardNumber
     * @param cardType
     * @param ownerName
     * @param cvv
     * @param expiryDate
     * @return The CardPaymentMethod created
     * @throws BusinessException
     */
    private CardPaymentMethod addCardFromPayment(String tokenId, CustomerAccount customerAccount, String cardNumber, CreditCardTypeEnum cardType, String ownerName, String cvv,
            String expiryDate) throws BusinessException {
        CardPaymentMethod paymentMethod = new CardPaymentMethod();
        paymentMethod.setAlias("Card_" + cardNumber.substring(12, 16));
        paymentMethod.setCardNumber(cardNumber);
        paymentMethod.setCardType(cardType);
        paymentMethod.setCustomerAccount(customerAccount);
        paymentMethod.setPreferred(true);
        paymentMethod.setMonthExpiration(new Integer(expiryDate.substring(0, 2)));
        paymentMethod.setYearExpiration(new Integer(expiryDate.substring(2, 4)));
        paymentMethod.setOwner(ownerName);
        paymentMethod.setTokenId(tokenId);
        paymentMethodService.create(paymentMethod);
        return paymentMethod;
    }

}