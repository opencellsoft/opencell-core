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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.PaymentException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.*;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Payment service implementation.
 *
 * @author Edward P. Legaspi
 * @author anasseh
 * @author Said Ramli
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class PaymentService extends PersistenceService<Payment> {

    /** The payment method service. */
    @Inject
    private PaymentMethodService paymentMethodService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The matching code service. */
    @Inject
    private MatchingCodeService matchingCodeService;

    /** The gateway payment factory. */
    @Inject
    private GatewayPaymentFactory gatewayPaymentFactory;

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The payment gateway service. */
    @Inject
    private PaymentGatewayService paymentGatewayService;

    /** The payment history service. */
    @Inject
    private PaymentHistoryService paymentHistoryService;

    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;

    /** The refund service. */
    @Inject
    private RefundService refundService;


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
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true, PaymentMethodEnum.CARD);
    }

    /**
     * Pay by card. A new card payment type is registered as token if payment was successfull.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoIdsToPay list of account operation's id to pay
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto payByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, true,
            PaymentMethodEnum.CARD);
    }

    /**
     * Pay by sepa.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to pay
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto payByMandat(CustomerAccount customerAccount, long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true, PaymentMethodEnum.DIRECTDEBIT);
    }

    /**
     * Refund by sepa.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param createAO if true refund account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto refundByMandat(CustomerAccount customerAccount, long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPayment(customerAccount, ctsAmount, aoIdsToPay, createAO, matchingAO, paymentGateway, null, null, null, null, null, true, PaymentMethodEnum.DIRECTDEBIT);
    }

    /**
     * Refund by card token. An existing and preferred card payment method will be used. If currently preferred card payment method is not valid, a new currently valid card payment
     * will be used (and marked as preferred)
     *
     * @param customerAccount Customer account
     * @param ctsAmount Amount to refund
     * @param aoIdsToRefund list of account operations ids to be refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway if set, this paymentGateway will be used
     * @return instance of PaymentResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException un balance amount exception.
     */
    public PaymentResponseDto refundByCardToken(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToRefund, boolean createAO, boolean matchingAO,
            PaymentGateway paymentGateway) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPayment(customerAccount, ctsAmount, aoIdsToRefund, createAO, matchingAO, paymentGateway, null, null, null, null, null, false, PaymentMethodEnum.CARD);
    }

    /**
     * Refund by card. A new card payment type is registered as token if refund was successfull.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoToRefund list of account operation's id to refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @return instance of PaymentResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto refundByCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expiryDate,
            CreditCardTypeEnum cardType, List<Long> aoToRefund, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        return doPayment(customerAccount, ctsAmount, aoToRefund, createAO, matchingAO, paymentGateway, cardNumber, ownerName, cvv, expiryDate, cardType, false,
            PaymentMethodEnum.CARD);
    }

    /**
     * Do payment or refund by token or card.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param aoIdsToPay list of account operation's id to refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param isPayment if true is a payment else is a refund.
     * @param paymentMethodType payment method to use, CARD or DIRECTDEIBT.
     * @return instance of PaymentResponseDto
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException exception thrown when not all operations are matched.
     * @throws UnbalanceAmountException balance amount exception.
     */
    public PaymentResponseDto doPayment(CustomerAccount customerAccount, Long ctsAmount, List<Long> aoIdsToPay, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway,
            String cardNumber, String ownerName, String cvv, String expiryDate, CreditCardTypeEnum cardType, boolean isPayment, PaymentMethodEnum paymentMethodType)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.NOT_PROCESSED);
        PaymentMethod preferredMethod = null;
        OperationCategoryEnum operationCat = isPayment ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT;
        try {
            boolean isNewCard = !StringUtils.isBlank(cardNumber);
            AccountOperation aoToPayRefund = accountOperationService.findById(aoIdsToPay.get(0));
            preferredMethod = customerAccountService.getPreferredPaymentMethod(aoToPayRefund, paymentMethodType);
            
            
            if (!isNewCard) {
                if (preferredMethod == null) {
                    throw new PaymentException(PaymentErrorEnum.NO_PAY_METHOD_FOR_CA, "There no payment method for customerAccount:" + customerAccount.getCode());
                }
            }
            GatewayPaymentInterface gatewayPaymentInterface = null;
            PaymentGateway matchedPaymentGatewayForTheCA = paymentGatewayService.getPaymentGateway(customerAccount, preferredMethod, cardType);
            if (matchedPaymentGatewayForTheCA == null) {
                throw new PaymentException(PaymentErrorEnum.NO_PAY_GATEWAY_FOR_CA, "No payment gateway for customerAccount:" + customerAccount.getCode());
            }

            if (paymentGateway != null) {
            	paymentGateway = paymentGatewayService.refreshOrRetrieve(paymentGateway);
                if (!paymentGateway.getCode().equals(matchedPaymentGatewayForTheCA.getCode())) {
                	log.warn("Cant process payment for the customerAccount:" + customerAccount.getCode() + " with the selected paymentGateway:" + paymentGateway.getCode());
                	return doPaymentResponseDto;
                }
            } else {
                paymentGateway = matchedPaymentGatewayForTheCA;
            }
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
            if (PaymentMethodEnum.CARD == paymentMethodType) {
                if (!(preferredMethod instanceof CardPaymentMethod)) {
                    throw new PaymentException(PaymentErrorEnum.PAY_CARD_CANNOT_BE_PREFERED, "Can not process payment card as prefered payment method is " + preferredMethod.getPaymentType());
                }
                // If card payment method is currently not valid, find a valid
                // one and mark it as preferred or throw an exception
                if (!((CardPaymentMethod) preferredMethod).isValidForDate(new Date())) {
                    preferredMethod = customerAccount.markCurrentlyValidCardPaymentAsPreferred();
                    if (preferredMethod != null) {
                        customerAccount = customerAccountService.update(customerAccount);
                    } else {
                        throw new PaymentException(PaymentErrorEnum.PAY_CB_INVALID, "There is no currently valid payment method for customerAccount:" + customerAccount.getCode());
                    }
                }
                if (isPayment) {
                    if (isNewCard) {
                        doPaymentResponseDto = gatewayPaymentInterface.doPaymentCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
                    } else {
                        doPaymentResponseDto = gatewayPaymentInterface.doPaymentToken(((CardPaymentMethod) preferredMethod), ctsAmount, null);
                    }
                } else {
                    if (isNewCard) {
                        doPaymentResponseDto = gatewayPaymentInterface.doRefundCard(customerAccount, ctsAmount, cardNumber, ownerName, cvv, expiryDate, cardType, null, null);
                    } else {
                        doPaymentResponseDto = gatewayPaymentInterface.doRefundToken(((CardPaymentMethod) preferredMethod), ctsAmount, null);
                    }
                }

            }
            if (PaymentMethodEnum.DIRECTDEBIT == paymentMethodType) {
                if (!(preferredMethod instanceof DDPaymentMethod)) {
                    throw new PaymentException(PaymentErrorEnum.PAY_METHOD_IS_NOT_DD, "Can not process payment sepa as prefered payment method is " + preferredMethod.getPaymentType());
                }
                if (StringUtils.isBlank(((DDPaymentMethod) preferredMethod).getMandateIdentification())) {
                    throw new PaymentException(PaymentErrorEnum.PAY_SEPA_MANDATE_BLANK, "Can not process payment sepa as Mandate is blank");
                }
                if (isPayment) {
                    doPaymentResponseDto = gatewayPaymentInterface.doPaymentSepa(((DDPaymentMethod) preferredMethod), ctsAmount, null);
                } else {
                    doPaymentResponseDto = gatewayPaymentInterface.doRefundSepa(((DDPaymentMethod) preferredMethod), ctsAmount, null);
                }
            }

            Long aoPaymentId = null;
            PaymentErrorTypeEnum errorType = null;
            PaymentStatusEnum status = doPaymentResponseDto.getPaymentStatus();
            if (PaymentStatusEnum.ACCEPTED == status || PaymentStatusEnum.PENDING == status) {
                if (isNewCard) {
                    preferredMethod = addCardFromPayment(doPaymentResponseDto.getTokenId(), customerAccount, cardNumber, cardType, ownerName, cvv, expiryDate);
                }
                preferredMethod.setUserId(doPaymentResponseDto.getCodeClientSide());
                preferredMethod = paymentMethodService.update(preferredMethod);

                if (createAO) {
                    try {
                        if (isPayment) {
                            aoPaymentId = createPaymentAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay);
                        } else {
                            aoPaymentId = refundService.createRefundAO(customerAccount, ctsAmount, doPaymentResponseDto, paymentMethodType, aoIdsToPay);
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
                log.warn("Payment with method id {} was rejected. Status: {}", preferredMethod.getId(), doPaymentResponseDto.getPaymentStatus());
            }

			Refund refund = (!isPayment && aoPaymentId != null) ? refundService.findById(aoPaymentId) : null;
			Payment payment = (isPayment && aoPaymentId != null) ? findById(aoPaymentId) : null;

			paymentHistoryService.addHistory(customerAccount, payment, refund, ctsAmount, status, doPaymentResponseDto.getErrorCode(), doPaymentResponseDto.getErrorMessage(),
					errorType, operationCat, paymentGateway.getCode(), preferredMethod,aoIdsToPay);

        } catch (PaymentException e) {
            log.error("PaymentException during payment AO:", e);
            doPaymentResponseDto = processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat, e.getCode(), e.getMessage(),aoIdsToPay);
        } catch (Exception e) {
            log.error("Error during payment AO:", e);
            doPaymentResponseDto = processPaymentException(customerAccount, ctsAmount, paymentGateway, doPaymentResponseDto, preferredMethod, operationCat, null, e.getMessage(),aoIdsToPay);
        }
        return doPaymentResponseDto;
    }

    private PaymentResponseDto processPaymentException(CustomerAccount customerAccount, Long ctsAmount, PaymentGateway paymentGateway, PaymentResponseDto doPaymentResponseDto,
            PaymentMethod preferredMethod, OperationCategoryEnum operationCat, String code, String msg,List<Long> aoIdsToPay) throws BusinessException {
        if (doPaymentResponseDto == null) {
            doPaymentResponseDto = new PaymentResponseDto();
        }
        doPaymentResponseDto.setErrorMessage(msg);
        doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
        doPaymentResponseDto.setErrorCode(code);
        paymentHistoryService.addHistory(customerAccount, null, null, ctsAmount, PaymentStatusEnum.ERROR, code, msg, PaymentErrorTypeEnum.ERROR, operationCat,
            paymentGateway.getCode(), preferredMethod,aoIdsToPay);
        return doPaymentResponseDto;
    }

    /**
     * Create the payment account operation for the payment that was processed.
     *
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param doPaymentResponseDto payment responsse dto
     * @param paymentMethodType payment method used
     * @param aoIdsToPay list AO to paid
     * @return the AO id created
     * @throws BusinessException business exception.
     */
    public Long createPaymentAO(CustomerAccount customerAccount, Long ctsAmount, PaymentResponseDto doPaymentResponseDto, PaymentMethodEnum paymentMethodType,
            List<Long> aoIdsToPay) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String occTemplateCode = paramBean.getProperty("occ.payment.card", "PAY_CRD");
        if (paymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
            occTemplateCode = paramBean.getProperty("occ.payment.dd", "PAY_DDT");
        }
        if (paymentMethodType == PaymentMethodEnum.STRIPE) {
            occTemplateCode = paramBean.getProperty("occ.payment.stp", "PAY_STP");
        }
        if (paymentMethodType == PaymentMethodEnum.PAYPAL) {
            occTemplateCode = paramBean.getProperty("occ.payment.pal", "PAY_PAL");
        }


        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethodType);
        payment.setAmount((new BigDecimal(ctsAmount).divide(new BigDecimal(100))));
        payment.setUnMatchingAmount(payment.getAmount());
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setCode(occTemplate.getCode());
        payment.setDescription(occTemplate.getDescription());
        payment.setType(doPaymentResponseDto.getPaymentBrand());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setAccountCodeClientSide(doPaymentResponseDto.getCodeClientSide());
        payment.setCustomerAccount(customerAccount);
        payment.setReference(doPaymentResponseDto.getPaymentID());
        payment.setTransactionDate(new Date());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        payment.setBankReference(doPaymentResponseDto.getBankRefenrence());
        setSumAndOrdersNumber(payment, aoIdsToPay);
        create(payment);
        return payment.getId();

    }

    public void setSumAndOrdersNumber(Payment payment, List<Long> aoIdsToPay) {
        BigDecimal sumTax = BigDecimal.ZERO;
        BigDecimal sumWithoutTax = BigDecimal.ZERO;
//        String orderNums = "";
        StringBuffer orderNumsSB = new StringBuffer();
        for (Long aoId : aoIdsToPay) {
            AccountOperation ao = accountOperationService.findById(aoId);
            sumTax = sumTax.add(ao.getTaxAmount());
            sumWithoutTax = sumWithoutTax.add(ao.getAmountWithoutTax());
            if (!StringUtils.isBlank(ao.getOrderNumber())) {
//                orderNums = orderNums + ao.getOrderNumber() + "|";
                orderNumsSB.append(ao.getOrderNumber() + "|");
            }
        }
        payment.setTaxAmount(sumTax);
        payment.setAmountWithoutTax(sumWithoutTax);
        String orderNums = orderNumsSB.toString();
        payment.setOrderNumber(orderNums);
    }

    /**
     * Create a card as CardPaymentMethod from initial payment.
     *
     * @param tokenId tokenId returned from the initial payment
     * @param customerAccount customer Account
     * @param cardNumber card Number
     * @param cardType card Type
     * @param ownerName owner Name
     * @param cvv cvv
     * @param expiryDate expiryDate
     * @return The CardPaymentMethod created
     * @throws BusinessException Business Exception
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

    /**
     * Handle payment callBack, if the payment/refund is accepted then nothing to do, if it's rejected a new AO rejected payment/refund will be created.
     * 
     * @param paymentReference payment reference
     * @param paymentStatus payment Status
     * @param errorCode error Code
     * @param errorMessage error Message
     * @throws BusinessException Business Exception
     */
    public void paymentCallback(String paymentReference, PaymentStatusEnum paymentStatus, String errorCode, String errorMessage) throws BusinessException {       
        try {
            if (paymentStatus == null) {
                throw new BusinessException("paymentStatus is required");
            }
            if (StringUtils.isBlank(paymentReference)) {
                throw new BusinessException("paymentReference is required");
            }
            AccountOperation accountOperation = accountOperationService.findByReference(paymentReference);

            if (accountOperation == null) {
                throw new BusinessException("Payment " + paymentReference + " not found");
            }
            if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L && accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
                throw new BusinessException("CallBack unexpected  for payment " + paymentReference);
            }
            if (PaymentStatusEnum.ACCEPTED == paymentStatus) {
                log.debug("Payment ok, nothing to do.");
            } else {
               
                List<AccountOperation> listAoThatSupposedPaid = getAccountOperationThatWasPaid(accountOperation);
                OCCTemplate occTemplate = getOCCTemplateRejectPayment(accountOperation);
                CustomerAccount ca = accountOperation.getCustomerAccount();
                Long aoPaymentIdWasRejected = accountOperation.getId();

                matchingCodeService.unmatchingByAOid(aoPaymentIdWasRejected);

                RejectedPayment rejectedPayment = new RejectedPayment();
                rejectedPayment.setType("R");
                rejectedPayment.setMatchingAmount(BigDecimal.ZERO);
                rejectedPayment.setMatchingStatus(MatchingStatusEnum.O);
                rejectedPayment.setUnMatchingAmount(accountOperation.getUnMatchingAmount());
                rejectedPayment.setAmount(accountOperation.getUnMatchingAmount());
                rejectedPayment.setReference("r_" + paymentReference);
                rejectedPayment.setCustomerAccount(ca);
                rejectedPayment.setAccountingCode(occTemplate.getAccountingCode());
                rejectedPayment.setCode(occTemplate.getCode());
                rejectedPayment.setDescription(occTemplate.getDescription());
                rejectedPayment.setTransactionCategory(occTemplate.getOccCategory());
                rejectedPayment.setAccountCodeClientSide(accountOperation.getAccountCodeClientSide());
                rejectedPayment.setPaymentMethod(accountOperation.getPaymentMethod());
                rejectedPayment.setTaxAmount(accountOperation.getTaxAmount());
                rejectedPayment.setAmountWithoutTax(accountOperation.getAmountWithoutTax());
                rejectedPayment.setOrderNumber(accountOperation.getOrderNumber());
                rejectedPayment.setRejectedType(RejectedType.A);
                rejectedPayment.setRejectedDate(new Date());
                rejectedPayment.setTransactionDate(new Date());
                rejectedPayment.setRejectedDescription(errorMessage);
                rejectedPayment.setRejectedCode(errorCode);
                rejectedPayment.setListAaccountOperationSupposedPaid(listAoThatSupposedPaid);

                accountOperationService.create(rejectedPayment);
                for(AccountOperation ao : listAoThatSupposedPaid) {
                    ao.setRejectedPayment(rejectedPayment);
                }
                Long oARejectPaymentID = rejectedPayment.getId();

                List<Long> aos = new ArrayList<>();
                aos.add(aoPaymentIdWasRejected);
                aos.add(oARejectPaymentID);

                matchingCodeService.matchOperations(ca.getId(), null, aos, null);
            }
            PaymentHistory paymentHistory = paymentHistoryService.findHistoryByPaymentId(paymentReference);
            if (paymentHistory != null) {
                paymentHistory.setAsyncStatus(paymentStatus);
                paymentHistory.setLastUpdateDate(new Date());
                paymentHistory.setErrorCode(errorCode);
                paymentHistory.setErrorMessage(errorMessage);
                paymentHistoryService.update(paymentHistory);
            }
        } catch (Exception e) {
            log.error("Error on payment callback processing:", e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Retrieve the list account Operation that was paid.
     * 
     * @param paymentOrRefund the payment or refund.
     * @return list of the account Operation that was paid
     * @throws BusinessException Business Exception
     */
    public  List<AccountOperation> getAccountOperationThatWasPaid(AccountOperation paymentOrRefund) throws BusinessException {
        if (paymentOrRefund.getMatchingStatus() != MatchingStatusEnum.L && paymentOrRefund.getMatchingStatus() != MatchingStatusEnum.P) {
            return null;
        }
        List<AccountOperation> listAoThatSupposedPaid = new ArrayList<AccountOperation>();
        List<MatchingAmount> matchingAmounts = paymentOrRefund.getMatchingAmounts();
        log.trace("matchingAmounts:" + matchingAmounts);
        for (MatchingAmount ma : paymentOrRefund.getMatchingAmounts().get(0).getMatchingCode().getMatchingAmounts()) {
            log.trace("ma.getAccountOperation() id:{} , occ code:{}", ma.getAccountOperation().toString(), ma.getAccountOperation().getCode());
            if (!(ma.getAccountOperation() instanceof Payment) && !(ma.getAccountOperation() instanceof Refund) && !(ma.getAccountOperation() instanceof RejectedPayment) ) {
                listAoThatSupposedPaid.add(ma.getAccountOperation());
            }
        }
        if (listAoThatSupposedPaid.isEmpty()) {
            throw new BusinessException("Cant find invoice account operation to unmatching");
        }
        return listAoThatSupposedPaid;

    }

    private OCCTemplate getOCCTemplateRejectPayment(AccountOperation accountOperation) {
    	ParamBean paramBean = paramBeanFactory.getInstance();
    	String occTemplateCode = null;
        if (accountOperation instanceof AutomatedRefund || accountOperation instanceof Refund) {
            if (PaymentMethodEnum.CARD == accountOperation.getPaymentMethod()) {
                occTemplateCode = paramBean.getProperty("occ.rejectedRefund.card", "REJ_RCR");
            } else {
                occTemplateCode = paramBean.getProperty("occ.rejectedRefund.dd", "REJ_RDD");
            }
        } else if (accountOperation instanceof Payment) {
            if (PaymentMethodEnum.CARD == accountOperation.getPaymentMethod()) {
                occTemplateCode = paramBean.getProperty("occ.rejectedPayment.card", "REJ_CRD");
            } 
           if(PaymentMethodEnum.DIRECTDEBIT == accountOperation.getPaymentMethod()) {
                occTemplateCode = paramBean.getProperty("occ.rejectedPayment.dd", "REJ_DDT");
            }
           if(PaymentMethodEnum.CHECK == accountOperation.getPaymentMethod()) {
               occTemplateCode = paramBean.getProperty("occ.rejectedPayment.chk", "REJ_CHK");
           }
           if(PaymentMethodEnum.WIRETRANSFER == accountOperation.getPaymentMethod()) {
               occTemplateCode = paramBean.getProperty("occ.rejectedPayment.chk", "REJ_WTF");
           }
        }

        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find AO Template with code:" + occTemplateCode);
        }
        return occTemplate;
        
    }
}