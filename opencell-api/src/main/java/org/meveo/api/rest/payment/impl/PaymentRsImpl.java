package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.PaymentActionStatus;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CardPaymentMethodApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.PaymentRs;

/**
 * @author R.AITYAAZZA
 * 
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class PaymentRsImpl extends BaseRs implements PaymentRs {

    @Inject
    private PaymentApi paymentApi;

    @Inject
    private CardPaymentMethodApi cardPaymentMethodApi;

    @Override
    public PaymentActionStatus create(PaymentDto postData) {
    	PaymentActionStatus result = new PaymentActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Long id = paymentApi.createPayment(postData);
            result.setPaymentId(id);
        } catch (Exception e) {
            processException(e, result);
        }
        
        return result;
    }

    @Override
    public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode) {
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
