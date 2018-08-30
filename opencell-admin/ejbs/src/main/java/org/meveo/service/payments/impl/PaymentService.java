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
import java.util.ArrayList;
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
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.Refund;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.model.payments.RejectedType;
import org.meveo.service.base.PersistenceService;

/**
 * Payment service implementation.
 * 
 * @author Edward P. Legaspi
 *   @author anasseh
 *   @lastModifiedVersion 5.0
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

    @Inject
    private AccountOperationService accountOperationService;

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
     * Do payment or refund by token or card
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent.
     * @param cardNumber card's number
     * @param ownerName card's owner name
     * @param cvv cvv number
     * @param expiryDate expiry date
     * @param cardType card type
     * @param aoIdsToPay list of account operation's id to refund
     * @param createAO if true payment account operation will be created.
     * @param matchingAO if true matching account operation will be created.
     * @param paymentGateway the set this payment gateway will be used.
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
        PaymentResponseDto doPaymentResponseDto = null;
        PaymentMethod preferredMethod = null;
        OperationCategoryEnum operationCat = isPayment ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT;
        try {
            boolean isNewCard = !StringUtils.isBlank(cardNumber);
            preferredMethod = customerAccount.getPreferredPaymentMethod();
            if (!isNewCard) {
                if (preferredMethod == null) {
                    throw new BusinessException("There no payment method for customerAccount:" + customerAccount.getCode());
                }
            }
            GatewayPaymentInterface gatewayPaymentInterface = null;
            PaymentGateway matchedPaymentGatewayForTheCA = paymentGatewayService.getPaymentGateway(customerAccount, preferredMethod, cardType);
            if (matchedPaymentGatewayForTheCA == null) {
                throw new BusinessException("No payment gateway for customerAccount:" + customerAccount.getCode());
            }

            if (paymentGateway != null) {
                if (!paymentGateway.getCode().equals(matchedPaymentGatewayForTheCA.getCode())) {
                    throw new BusinessException(
                        "Cant process payment for the customerAccount:" + customerAccount.getCode() + " with the selected paymentGateway:" + paymentGateway.getCode());
                }
            } else {
                paymentGateway = matchedPaymentGatewayForTheCA;
            }
            gatewayPaymentInterface = gatewayPaymentFactory.getInstance(paymentGateway);
            if (PaymentMethodEnum.CARD == paymentMethodType) {
                if (!(preferredMethod instanceof CardPaymentMethod)) {
                    throw new BusinessException("Can not process payment card as prefered payment method is " + preferredMethod.getPaymentType());
                }
                // If card payment method is currently not valid, find a valid
                // one and mark it as preferred or throw an exception
                if (!((CardPaymentMethod) preferredMethod).isValidForDate(new Date())) {
                    preferredMethod = customerAccount.markCurrentlyValidCardPaymentAsPreferred();
                    if (preferredMethod != null) {
                        customerAccount = customerAccountService.update(customerAccount);
                    } else {
                        throw new BusinessException("There is no currently valid payment method for customerAccount:" + customerAccount.getCode());
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
                    throw new BusinessException("Can not process payment sepa as prefered payment method is " + preferredMethod.getPaymentType());
                }
                if (StringUtils.isBlank(((DDPaymentMethod) preferredMethod).getMandateIdentification())) {
                    throw new BusinessException("Can not process payment sepa as Mandate is blank");
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
            paymentHistoryService.addHistory(customerAccount, findById(aoPaymentId), refundService.findById(aoPaymentId), ctsAmount, status, doPaymentResponseDto.getErrorCode(),
                doPaymentResponseDto.getErrorMessage(), errorType, operationCat, paymentGateway, preferredMethod);

        } catch (Exception e) {
            log.error("Error during payment AO:", e);
            if (doPaymentResponseDto == null) {
                doPaymentResponseDto = new PaymentResponseDto();
            }
            doPaymentResponseDto.setErrorMessage(e.getMessage());
            doPaymentResponseDto.setPaymentStatus(PaymentStatusEnum.ERROR);
            paymentHistoryService.addHistory(customerAccount, null, null, ctsAmount, PaymentStatusEnum.ERROR, null, e.getMessage(), PaymentErrorTypeEnum.ERROR, operationCat,
                paymentGateway, preferredMethod);
        }
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
        payment.setTaxAmount(sumTax);
        payment.setAmountWithoutTax(sumWithoutTax);
        payment.setOrderNumber(orderNums);
        create(payment);
        return payment.getId();

    }

    /**
     * Create a card as CardPaymentMethod from initial payment
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
     * @param paymentId payment Id
     * @param paymentStatus payment Status
     * @param errorCode error Code
     * @param errorMessage error Message
     * @throws BusinessException Business Exception
     */
    public void paymentCallback(String paymentId, PaymentStatusEnum paymentStatus, String errorCode, String errorMessage) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        try {
            if (paymentStatus == null) {
                throw new BusinessException("paymentStatus is required");
            }
            if (StringUtils.isBlank(paymentId)) {
                throw new BusinessException("paymentId is required");
            }
            AccountOperation accountOperation = accountOperationService.findByReference(paymentId);

            if (accountOperation == null) {
                throw new BusinessException("Payment " + paymentId + " not found");
            }
            if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L) {
                throw new BusinessException("CallBack unexpected  for payment " + paymentId);
            }
            if (PaymentStatusEnum.ACCEPTED == paymentStatus) {
                log.debug("Payment ok, nothing to do.");
            } else {
                String occTemplateCode = null;

                if (accountOperation instanceof Payment) {
                    if (PaymentMethodEnum.CARD == ((Payment) accountOperation).getPaymentMethod()) {
                        occTemplateCode = paramBean.getProperty("occ.rejectedPayment.card", "REJ_CRD");
                    } else {
                        occTemplateCode = paramBean.getProperty("occ.rejectedPayment.dd", "REJ_DDT");
                    }
                }
                if (accountOperation instanceof Refund) {
                    if (PaymentMethodEnum.CARD == ((Refund) accountOperation).getPaymentMethod()) {
                        occTemplateCode = paramBean.getProperty("occ.rejectedRefund.card", "REJ_RCR");
                    } else {
                        occTemplateCode = paramBean.getProperty("occ.rejectedRefund.dd", "REJ_RDD");
                    }
                }
                OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
                if (occTemplate == null) {
                    throw new BusinessException("Cannot find AO Template with code:" + occTemplateCode);
                }
                CustomerAccount ca = accountOperation.getCustomerAccount();
                AccountOperation aoThatShouldePaid = getAccountOperationThatWasPaid(accountOperation);
                Long aoPaymentIdWasRejected = accountOperation.getId();

                matchingCodeService.unmatchingByAOid(aoThatShouldePaid.getId());

                RejectedPayment rejectedPayment = new RejectedPayment();
                rejectedPayment.setType("R");
                rejectedPayment.setMatchingAmount(BigDecimal.ZERO);
                rejectedPayment.setMatchingStatus(MatchingStatusEnum.O);
                rejectedPayment.setUnMatchingAmount(aoThatShouldePaid.getUnMatchingAmount());
                rejectedPayment.setAmount(aoThatShouldePaid.getUnMatchingAmount());
                rejectedPayment.setReference("r_" + paymentId);
                rejectedPayment.setCustomerAccount(ca);
                rejectedPayment.setAccountingCode(occTemplate.getAccountingCode());
                rejectedPayment.setOccCode(occTemplate.getCode());
                rejectedPayment.setOccDescription(occTemplate.getDescription());
                rejectedPayment.setTransactionCategory(occTemplate.getOccCategory());
                rejectedPayment.setAccountCodeClientSide(accountOperation.getAccountCodeClientSide());
                rejectedPayment.setPaymentMethod(accountOperation.getPaymentMethod());
                rejectedPayment.setTaxAmount(accountOperation.getTaxAmount());
                rejectedPayment.setAmountWithoutTax(accountOperation.getAmountWithoutTax());
                rejectedPayment.setOrderNumber(accountOperation.getOrderNumber());
                rejectedPayment.setRejectedType(RejectedType.A);
                rejectedPayment.setRejectedDate(new Date());
                rejectedPayment.setRejectedDescription(errorMessage);
                rejectedPayment.setRejectedCode(errorCode);

                accountOperationService.create(rejectedPayment);
                Long oARejectPaymentID = rejectedPayment.getId();

                List<Long> aos = new ArrayList<>();
                aos.add(aoPaymentIdWasRejected);
                aos.add(oARejectPaymentID);

                matchingCodeService.matchOperations(ca.getId(), null, aos, null);
            }
            PaymentHistory paymentHistory = paymentHistoryService.findHistoryByPaymentId(paymentId);
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
     * Retrieve the account Operation that was paid.
     * 
     * @param paymentOrRefund the payment or refund.
     * @return the account Operation that was paid
     * @throws BusinessException Business Exception
     */
    private AccountOperation getAccountOperationThatWasPaid(AccountOperation paymentOrRefund) throws BusinessException {
        List<MatchingAmount> matchingAmounts = paymentOrRefund.getMatchingAmounts();
        log.trace("matchingAmounts:" + matchingAmounts);
        for (MatchingAmount ma : paymentOrRefund.getMatchingAmounts().get(0).getMatchingCode().getMatchingAmounts()) {
            log.trace("ma.getAccountOperation() id:{} , occ code:{}", ma.getAccountOperation().toString(), ma.getAccountOperation().getOccCode());
            if (!(ma.getAccountOperation() instanceof Payment) && !(ma.getAccountOperation() instanceof Refund)) {
                return ma.getAccountOperation();
            }
        }
        throw new BusinessException("Cant find invoice account operation to unmatching");
    }

}