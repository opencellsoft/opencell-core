package org.meveo.apiv2.refund;

import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.payment.RefundApi;
import org.meveo.model.payments.*;
import org.meveo.service.payments.impl.CustomerAccountService;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class RefundResourceImpl implements RefundResource{

    @Inject
    private RefundApi refundApi;
    @Inject
    private CustomerAccountService customerAccountService;


    @Override
    public Response refundByCard(CardRefund cardRefund) {
        PayByCardDto payByCardDto = toPayByCardDto(cardRefund);
        try {
            refundApi.refundByCard(payByCardDto);
            return Response.ok().build();
        } catch (NoAllOperationUnmatchedException | UnbalanceAmountException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.PRECONDITION_FAILED);
        }
    }

    @Override
    public Response refundBySCT(SCTRefund sctRefund) {
        if(Objects.isNull(sctRefund.getCustomerAccountCode()) || sctRefund.getCustomerAccountCode().isBlank()){
            throw new ClientErrorException("Customer account code is required!", Response.Status.BAD_REQUEST);
        }
        CustomerAccount CAByCode = customerAccountService.findByCode(sctRefund.getCustomerAccountCode(), Collections.singletonList("paymentMethods"));
        if(Objects.isNull(CAByCode)){
            throw new EntityDoesNotExistsException(CustomerAccount.class, sctRefund.getCustomerAccountCode());
        }
        if(PaymentMethodEnum.DIRECTDEBIT.equals(CAByCode.getPreferredPaymentMethodType()) && (sctRefund.getAoToRefund() != null && !sctRefund.getAoToRefund().isEmpty())){
            HashSet<Long> aoIds = new HashSet<>(sctRefund.getAoToRefund());
            CAByCode.getAccountOperations().stream()
                    .filter(accountOperation -> aoIds.contains(accountOperation.getId()))
                    .forEach(accountOperation -> accountOperation.setOperationAction(OperationActionEnum.TO_REFUND));

        }
        return Response.ok().build();
    }


    private PayByCardDto toPayByCardDto(CardRefund cardRefund) {
        PayByCardDto payByCardDto = new PayByCardDto();
        payByCardDto.setCtsAmount(cardRefund.getCtsAmount());
        payByCardDto.setCardNumber(cardRefund.getCardNumber());
        payByCardDto.setCustomerAccountCode(cardRefund.getCustomerAccountCode());
        payByCardDto.setOwnerName(cardRefund.getOwnerName());
        payByCardDto.setCvv(cardRefund.getCvv());
        payByCardDto.setExpiryDate(cardRefund.getExpiryDate().getTime()+"");
        if(Objects.nonNull(cardRefund.getCardType())){
            payByCardDto.setCardType(CreditCardTypeEnum.valueOf(cardRefund.getCardType()));
        }
        payByCardDto.setAoToPay(cardRefund.getAoToPay());
        payByCardDto.setCreateAO(cardRefund.createAO());
        payByCardDto.setToMatch(cardRefund.toMatch());
        payByCardDto.setComment(cardRefund.getComment());
        return payByCardDto;
    }
}
