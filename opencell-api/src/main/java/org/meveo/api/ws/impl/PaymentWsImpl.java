package org.meveo.api.ws.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CardPaymentMethodApi;
import org.meveo.api.payment.DDRequestLotOpApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.ws.PaymentWs;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@WebService(serviceName = "PaymentWs", endpointInterface = "org.meveo.api.ws.PaymentWs")
@Interceptors({ WsRestApiInterceptor.class })
public class PaymentWsImpl extends BaseWs implements PaymentWs {

    @Inject
    private PaymentApi paymentApi;

    @Inject
    private CardPaymentMethodApi cardPaymentMethodApi;

    @Inject
    private DDRequestLotOpApi ddrequestLotOpApi;

    @Override
    public ActionStatus create(PaymentDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentApi.createPayment(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomerPaymentsResponse list(String customerAccountCode) {
        CustomerPaymentsResponse result = new CustomerPaymentsResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomerPaymentDtoList(paymentApi.getPaymentList(customerAccountCode));
            result.setBalance(paymentApi.getBalance(customerAccountCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createDDRequestLotOp(DDRequestLotOpDto ddrequestLotOp) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ddrequestLotOpApi.create(ddrequestLotOp);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public DDRequestLotOpsResponseDto listDDRequestLotops(Date fromDueDate, Date toDueDate, DDRequestOpStatusEnum status) {
        DDRequestLotOpsResponseDto result = new DDRequestLotOpsResponseDto();

        try {
            result.setDdrequestLotOps(ddrequestLotOpApi.listDDRequestLotOps(fromDueDate, toDueDate, status));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public PayByCardResponseDto payByCard(PayByCardDto doPaymentRequestDto) {
        PayByCardResponseDto response = new PayByCardResponseDto();
        response.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, ""));
        try {
            response = paymentApi.payByCard(doPaymentRequestDto);
            response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }
        return response;
    }

    @Override
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethodDto) {
        CardPaymentMethodTokenDto response = new CardPaymentMethodTokenDto();
        try {
            String tokenId = cardPaymentMethodApi.create(cardPaymentMethodDto);
            response.setCardPaymentMethod(cardPaymentMethodApi.find(null, tokenId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updateCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            cardPaymentMethodApi.update(cardPaymentMethod);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeCardPaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            cardPaymentMethodApi.remove(id, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CardPaymentMethodTokensDto listCardPaymentMethods(Long customerAccountId, String customerAccountCode) {

        CardPaymentMethodTokensDto response = new CardPaymentMethodTokensDto();

        try {
            response.setCardPaymentMethods(cardPaymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public CardPaymentMethodTokenDto findCardPaymentMethod(Long id) {

        CardPaymentMethodTokenDto response = new CardPaymentMethodTokenDto();

        try {
            response.setCardPaymentMethod(cardPaymentMethodApi.find(id, null));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }
}
