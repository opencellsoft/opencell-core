package org.meveo.api.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.payment.RefundDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.payments.impl.RefundService;

@Stateless
public class RefundApi extends BaseApi {

    @Inject
    private RefundService refundService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    /**
     * @param refundDto refund object which encapsulates the input data sent by client
     * @return the id of payment if created successful otherwise null
     * @throws NoAllOperationUnmatchedException no all operation unmatched exception
     * @throws UnbalanceAmountException unbalance amount exception
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     */
    public Long createRefund(RefundDto refundDto) throws NoAllOperationUnmatchedException, UnbalanceAmountException, BusinessException, MeveoApiException {
        log.info("create payment for amount:" + refundDto.getAmount() + " paymentMethodEnum:" + refundDto.getPaymentMethod() + " isToMatching:" + refundDto.isToMatching()
                + "  customerAccount:" + refundDto.getCustomerAccountCode() + "...");

        if (StringUtils.isBlank(refundDto.getAmount())) {
            missingParameters.add("amount");
        }
        if (StringUtils.isBlank(refundDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(refundDto.getOccTemplateCode())) {
            missingParameters.add("occTemplateCode");
        }
        if (StringUtils.isBlank(refundDto.getReference())) {
            missingParameters.add("reference");
        }
        if (StringUtils.isBlank(refundDto.getPaymentMethod())) {
            missingParameters.add("paymentMethod");
        }
        handleMissingParameters();
        CustomerAccount customerAccount = customerAccountService.findByCode(refundDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new BusinessException("Cannot find customer account with code=" + refundDto.getCustomerAccountCode());
        }

        OCCTemplate occTemplate = oCCTemplateService.findByCode(refundDto.getOccTemplateCode());
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + refundDto.getOccTemplateCode());
        }

        Refund refund = new Refund();
        refund.setPaymentMethod(refundDto.getPaymentMethod());
        refund.setAmount(refundDto.getAmount());
        refund.setUnMatchingAmount(refundDto.getAmount());
        refund.setMatchingAmount(BigDecimal.ZERO);
        refund.setAccountCode(occTemplate.getAccountCode());
        refund.setOccCode(occTemplate.getCode());
        refund.setOccDescription(StringUtils.isBlank(refundDto.getDescription()) ? occTemplate.getDescription() : refundDto.getDescription());
        refund.setTransactionCategory(occTemplate.getOccCategory());
        refund.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        refund.setCustomerAccount(customerAccount);
        refund.setReference(refundDto.getReference());
        refund.setDueDate(refundDto.getDueDate());
        refund.setTransactionDate(refundDto.getTransactionDate());
        refund.setMatchingStatus(MatchingStatusEnum.O);

        // populate customFields
        try {
            populateCustomFields(refundDto.getCustomFields(), refund, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        refundService.create(refund);

        int nbOccMatched = 0;
        if (refundDto.isToMatching()) {
            List<Long> listReferenceToMatch = new ArrayList<Long>();
            if (refundDto.getListOCCReferenceforMatching() != null) {
                nbOccMatched = refundDto.getListOCCReferenceforMatching().size();
                for (int i = 0; i < nbOccMatched; i++) {
                    RecordedInvoice accountOperationToMatch = recordedInvoiceService.getRecordedInvoice(refundDto.getListOCCReferenceforMatching().get(i));
                    if (accountOperationToMatch == null) {
                        throw new BusinessApiException("Cannot find account operation with reference:" + refundDto.getListOCCReferenceforMatching().get(i));
                    }
                    listReferenceToMatch.add(accountOperationToMatch.getId());
                }
                listReferenceToMatch.add(refund.getId());
                matchingCodeService.matchOperations(null, customerAccount.getCode(), listReferenceToMatch, null, MatchingTypeEnum.A);
            }

        } else {
            log.info("no matching created ");
        }
        log.debug("refund created for amount:" + refund.getAmount());

        return refund.getId();

    }

    public List<RefundDto> getRefundList(String customerAccountCode) throws Exception {
        List<RefundDto> result = new ArrayList<RefundDto>();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        customerAccountService.getEntityManager().refresh(customerAccount);

        List<AccountOperation> ops = customerAccount.getAccountOperations();
        for (AccountOperation op : ops) {
            if (op instanceof Refund) {
                Refund refund = (Refund) op;
                RefundDto refundDto = new RefundDto();
                refundDto.setType(refund.getType());
                refundDto.setAmount(refund.getAmount());
                refundDto.setDueDate(refund.getDueDate());
                refundDto.setOccTemplateCode(refund.getOccCode());
                refundDto.setPaymentMethod(refund.getPaymentMethod());
                refundDto.setReference(refund.getReference());
                refundDto.setTransactionDate(refund.getTransactionDate());
                refundDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(op, true));
                result.add(refundDto);
            }
        }
        return result;
    }

    public PaymentResponseDto refundByCard(PayByCardDto cardPaymentRequestDto)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, MeveoApiException {

        if (StringUtils.isBlank(cardPaymentRequestDto.getCtsAmount())) {
            missingParameters.add("ctsAmount");
        }

        if (StringUtils.isBlank(cardPaymentRequestDto.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        boolean useCard = false;

        // case card payment
        if (!StringUtils.isBlank(cardPaymentRequestDto.getCardNumber())) {
            useCard = true;
            if (StringUtils.isBlank(cardPaymentRequestDto.getCvv())) {
                missingParameters.add("cvv");
            }
            if (StringUtils.isBlank(cardPaymentRequestDto.getExpiryDate()) || cardPaymentRequestDto.getExpiryDate().length() != 4
                    || !org.apache.commons.lang3.StringUtils.isNumeric(cardPaymentRequestDto.getExpiryDate())) {

                missingParameters.add("expiryDate");
            }
            if (StringUtils.isBlank(cardPaymentRequestDto.getOwnerName())) {
                missingParameters.add("ownerName");
            }
            if (StringUtils.isBlank(cardPaymentRequestDto.getCardType())) {
                missingParameters.add("cardType");
            }
        }
        if (cardPaymentRequestDto.isToMatch()) {
            if (cardPaymentRequestDto.getAoToPay() == null || cardPaymentRequestDto.getAoToPay().isEmpty()) {
                missingParameters.add("aoToPay");
            }
        }

        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(cardPaymentRequestDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, cardPaymentRequestDto.getCustomerAccountCode());
        }

        PaymentMethodEnum preferedMethod = customerAccount.getPreferredPaymentMethodType();
        if (preferedMethod != null && PaymentMethodEnum.CARD != preferedMethod) {
            throw new BusinessApiException("Can not process payment as prefered payment method is " + preferedMethod);
        }

        PaymentResponseDto doPaymentResponseDto = null;
        if (useCard) {

            doPaymentResponseDto = paymentService.refundByCard(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getCardNumber(),
                cardPaymentRequestDto.getOwnerName(), cardPaymentRequestDto.getCvv(), cardPaymentRequestDto.getExpiryDate(), cardPaymentRequestDto.getCardType(),
                cardPaymentRequestDto.getAoToPay(), cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
        } else {
            doPaymentResponseDto = paymentService.refundByCardToken(customerAccount, cardPaymentRequestDto.getCtsAmount(), cardPaymentRequestDto.getAoToPay(),
                cardPaymentRequestDto.isCreateAO(), cardPaymentRequestDto.isToMatch(), null);
        }

        return doPaymentResponseDto;
    }

}