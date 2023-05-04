package org.meveo.apiv2.payments.resource;

import java.util.Objects;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.payment.PaymentApi;
import org.meveo.apiv2.refund.CardRefund;
import org.meveo.model.payments.CreditCardTypeEnum;

public class PaymentResourceImpl implements PaymentResource{

    @Inject
    private PaymentApi paymentApi;

    @Override
    public Response paymentByCard(CardRefund cardPayment) {
        PayByCardDto payByCardDto = toPayByCardDto(cardPayment);
        try {
            paymentApi.payByCard(payByCardDto);
            return Response.ok(new ActionStatus()).build();
        } catch (NoAllOperationUnmatchedException | UnbalanceAmountException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.PRECONDITION_FAILED);
        }
    }

   

	private PayByCardDto toPayByCardDto(CardRefund cardPayment) {
        PayByCardDto payByCardDto = new PayByCardDto();
        payByCardDto.setCtsAmount(cardPayment.getCtsAmount());
        payByCardDto.setCardNumber(cardPayment.getCardNumber());
        payByCardDto.setCustomerAccountCode(cardPayment.getCustomerAccountCode());
        payByCardDto.setOwnerName(cardPayment.getOwnerName());
        payByCardDto.setCvv(cardPayment.getCvv());
        payByCardDto.setExpiryDate(cardPayment.getExpiryDate());
        if(Objects.nonNull(cardPayment.getCardType())){
            payByCardDto.setCardType(CreditCardTypeEnum.valueOf(cardPayment.getCardType()));
        }
        payByCardDto.setAoToPay(cardPayment.getAoToPay());
        payByCardDto.setCreateAO(cardPayment.createAO());
        payByCardDto.setToMatch(cardPayment.toMatch());
        payByCardDto.setComment(cardPayment.getComment());
        return payByCardDto;
    }
}
