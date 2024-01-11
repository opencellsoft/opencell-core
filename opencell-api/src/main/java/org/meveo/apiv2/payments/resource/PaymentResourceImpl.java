package org.meveo.apiv2.payments.resource;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.Response.ok;

import java.util.Objects;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.PayByCardOrSepaDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.PaymentApi;
import org.meveo.apiv2.payments.ImportRejectionCodeInput;
import org.meveo.apiv2.payments.PaymentGatewayInput;
import org.meveo.apiv2.payments.RejectionAction;
import org.meveo.apiv2.payments.RejectionCode;
import org.meveo.apiv2.refund.CardRefund;
import org.meveo.model.payments.CreditCardTypeEnum;

@Interceptors({ WsRestApiInterceptor.class })
public class PaymentResourceImpl implements PaymentResource {

    @Inject
    private PaymentApi paymentApi;

    @Override
    public Response paymentByCard(CardRefund cardPayment) {
    	PayByCardOrSepaDto payByCardDto = toPayByCardDto(cardPayment);
        try {
            paymentApi.payByCard(payByCardDto);
            return Response.ok(new ActionStatus()).build();
        } catch (Exception e) {
            throw new ClientErrorException(e.getMessage(), PRECONDITION_FAILED);
        }
    }
    
	@Override
	public Response paymentBySepa(CardRefund cardPayment) {
		PayByCardOrSepaDto payByCardDto = toPayByCardDto(cardPayment);
        try {
            paymentApi.payBySepa(payByCardDto);
            return Response.ok(new ActionStatus()).build();
        } catch (Exception e) {
            throw new ClientErrorException(e.getMessage(), PRECONDITION_FAILED);
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

    /**
     * Create payment rejection code
     *
     * @param rejectionCode payment rejection code
     * @return RejectionCode id
     */
    @Override
    public Response createRejectionCode(RejectionCode rejectionCode) {
        try {
            validateRejectionCodeInput(rejectionCode);
            Long id = paymentApi.createPaymentRejectionCode(rejectionCode);
            return ok()
                    .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"" +
                            ",\"message\":\"Rejection code successfully created\"},\"id\":" + id + "}")
                    .build();
        } catch (MissingParameterException missingParameterException) {
            return Response.status(PRECONDITION_FAILED)
                    .entity("{\"actionStatus\":{\"status\":\"FAIL\"" +
                                    ",\"message\":\"" + missingParameterException.getMessage() + "\"}")
                    .type(APPLICATION_JSON)
                    .build();
        } catch (Exception exception) {
            throw new BadRequestException(exception);
        }
    }

    private void validateRejectionCodeInput(RejectionCode rejectionCode) {
        ofNullable(rejectionCode.getCode())
                .orElseThrow(() -> new MissingParameterException("Code is mandatory"));
        ofNullable(rejectionCode.getPaymentGateway())
                .orElseThrow(() -> new MissingParameterException("Payment gateway is mandatory"));
    }

    /**
     * Update payment rejection code
     *
     * @param id payment rejection code id
     * @param rejectionCode rejection code input
     * @return RejectionCode updated result
     */
    @Override
    public Response updateRejectionCode(Long id, RejectionCode rejectionCode) {
        RejectionCode result = paymentApi.updatePaymentRejectionCode(id, rejectionCode);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"" +
                        ",\"message\":\"Rejection code successfully updated\"},\"id\":" + result.getId() + "}")
                .build();
    }

    /**
     * Delete rejection code
     *
     * @param id payment rejection code id
     */
    @Override
    public Response removeRejectionCode(Long id) {
        paymentApi.removeRejectionCode(id);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"" +
                        ",\"message\":\"Rejection code successfully deleted\"}")
                .build();
    }

    /**
     * Clear rejection codes by gateway
     *
     * @param paymentGatewayInput payment gateway
     */
    @Override
    public Response clearAll(PaymentGatewayInput paymentGatewayInput) {
        return ok()
                .entity(paymentApi.clearAll(paymentGatewayInput))
                .build();
    }

    /**
     * Import rejectionsCodes
     *
     * @param importRejectionCodeInput rejection code import input
     */
    @Override
    public Response importRejectionCodes(ImportRejectionCodeInput importRejectionCodeInput) {
        return ok()
                .entity(paymentApi.importRejectionCodes(importRejectionCodeInput))
                .build();
    }

    /**
     * Export rejectionsCodes by gateway
     *
     * @param paymentGateway payment gateway
     */
    @Override
    public Response export(PaymentGatewayInput paymentGateway) {
        paymentApi.export(paymentGateway);
        return ok()
                .entity(paymentApi.export(paymentGateway))
                .build();
    }

    /**
     * Create rejection action
     *
     * @param rejectionAction PaymentRejectionAction
     */
    @Override
    public Response createRejectionAction(RejectionAction rejectionAction) {
        final RejectionAction saved = paymentApi.createRejectionAction(rejectionAction);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"" +
                        ",\"message\":\"Rejection action successfully created\"},\"id\":"
                        + saved.getId() + ", \"code\": \""
                        + saved.getCode() + "\"}")
                .build();
    }

    /**
     * update rejection action
     *
     * @param rejectionAction PaymentRejectionAction
     */
    @Override
    public Response updateRejectionAction(Long id, RejectionAction rejectionAction) {
        RejectionAction updatedAction = paymentApi.updateRejectionAction(id, rejectionAction);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"" +
                        ",\"message\":\"Rejection action successfully updated\"},\"id\":"
                        + updatedAction.getId() + ", \"code\": \""
                        + updatedAction.getCode() + "\"}")
                .build();
    }

    /**
     * delete rejection action
     *
     * @param id PaymentRejectionAction id
     */
    @Override
    public Response removeRejectionAction(Long id) {
        paymentApi.removeRejectionAction(id);
        return ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\"" +
                        ",\"message\":\"Rejection action successfully deleted\"}")
                .build();
    }
}
