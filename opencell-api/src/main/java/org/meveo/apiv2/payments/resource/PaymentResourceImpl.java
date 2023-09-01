package org.meveo.apiv2.payments.resource;

import java.util.Objects;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.PayByCardOrSepaDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.PaymentApi;
import org.meveo.apiv2.refund.CardRefund;
import org.meveo.model.payments.CreditCardTypeEnum;

@Interceptors({ WsRestApiInterceptor.class })
public class PaymentResourceImpl implements PaymentResource{

    @Inject
    private PaymentApi paymentApi;

    @Override
    public Response paymentByCard(CardRefund cardPayment) {
    	PayByCardOrSepaDto payByCardDto = toPayByCardDto(cardPayment);
        try {
            paymentApi.payByCard(payByCardDto);
            return Response.ok(new ActionStatus()).build();
        } catch (Exception e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.PRECONDITION_FAILED);
        }
    }
    
	@Override
	public Response paymentBySepa(CardRefund cardPayment) {
		PayByCardOrSepaDto payByCardDto = toPayByCardDto(cardPayment);
        try {
            paymentApi.payBySepa(payByCardDto);
            return Response.ok(new ActionStatus()).build();
        } catch (Exception e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.PRECONDITION_FAILED);
        }
    }
   

	private PayByCardOrSepaDto toPayByCardDto(CardRefund cardPayment) {
		PayByCardOrSepaDto payByCardDto = new PayByCardOrSepaDto();
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
