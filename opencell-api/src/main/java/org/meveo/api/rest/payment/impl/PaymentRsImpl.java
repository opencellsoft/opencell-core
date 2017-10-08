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
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentMethodDto;

import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.WsRestApiInterceptor;


import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.payment.PaymentGatewayApi;
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
    private PaymentMethodApi paymentMethodApi;
    
    @Inject
    private PaymentGatewayApi paymentGatewayApi;

   
    /** 
     * @return payment action status which contains payment id.
     * @see org.meveo.api.rest.payment.PaymentRs#create(org.meveo.api.dto.payment.PaymentDto)
     */
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
    
    /************************************************************************************************/
    /****                                 Card Payment Method                                    ****/
    /************************************************************************************************/

    @Override
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethodDto) {
        PaymentMethodTokenDto response = new PaymentMethodTokenDto();
        try {
            Long tokenId = paymentMethodApi.create(new PaymentMethodDto(cardPaymentMethodDto));
            response.setPaymentMethod(paymentMethodApi.find(tokenId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return new CardPaymentMethodTokenDto(response);
    }

    @Override
    public ActionStatus updateCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentMethodApi.update(new PaymentMethodDto(cardPaymentMethod));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeCardPaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentMethodApi.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CardPaymentMethodTokensDto listCardPaymentMethods(Long customerAccountId, String customerAccountCode) {

        PaymentMethodTokensDto response = new PaymentMethodTokensDto();

        try {
            response.setPaymentMethods(paymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return new CardPaymentMethodTokensDto(response);
    }

    @Override
    public CardPaymentMethodTokenDto findCardPaymentMethod(Long id) {

        PaymentMethodTokenDto response = new PaymentMethodTokenDto();

        try {
            response.setPaymentMethod(paymentMethodApi.find(id));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return new CardPaymentMethodTokenDto(response);
    }
    
    /************************************************************************************************/
    /****                                  Payment Methods                                       ****/
    /************************************************************************************************/
    @Override
    public PaymentMethodTokenDto addPaymentMethod(PaymentMethodDto paymentMethodDto) {
    	PaymentMethodTokenDto response = new PaymentMethodTokenDto();
        try {
            Long paymentMethodId = paymentMethodApi.create(paymentMethodDto);
            response.setPaymentMethod(paymentMethodApi.find(paymentMethodId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updatePaymentMethod(PaymentMethodDto paymentMethodDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentMethodApi.update(paymentMethodDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removePaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentMethodApi.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public PaymentMethodTokensDto listPaymentMethods(Long customerAccountId, String customerAccountCode) {

        PaymentMethodTokensDto response = new PaymentMethodTokensDto();

        try {
            response.setPaymentMethods(paymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public PaymentMethodTokenDto findPaymentMethod(Long id) {

        PaymentMethodTokenDto response = new PaymentMethodTokenDto();

        try {
            response.setPaymentMethod(paymentMethodApi.find(id));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }
    
    /*************************************************************************************************/
    /*                            Payment Gateways                                                  */
    /************************************************************************************************/


    @Override
    public PaymentGatewayResponseDto addPaymentGateway(PaymentGatewayDto paymentGateway) {
	PaymentGatewayResponseDto response = new PaymentGatewayResponseDto();
	try {
	    paymentGatewayApi.create(paymentGateway);
	    response.getPaymentGateways().add(paymentGatewayApi.find(paymentGateway.getCode()));

	} catch (Exception e) {
	    processException(e, response.getActionStatus());
	}

	return response;
    }

    @Override
    public ActionStatus updatePaymentGateway(PaymentGatewayDto paymentGateway) {
	ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	try {
	    paymentGatewayApi.update(paymentGateway);
	} catch (Exception e) {
	    processException(e, result);
	}

	return result;
    }

    @Override
    public ActionStatus removePaymentGateway(String code) {
	ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	try {
	    paymentGatewayApi.remove(code);
	} catch (Exception e) {
	    processException(e, result);
	}

	return result;
    }

    @Override
    public PaymentGatewayResponseDto listPaymentGateways() {
	PaymentGatewayResponseDto result = new PaymentGatewayResponseDto();
	try {
	    result.setPaymentGateways(paymentGatewayApi.list());
	} catch (Exception e) {
	    processException(e, result.getActionStatus());
	}
	return null;
    }

    @Override
    public PaymentGatewayResponseDto findPaymentGateway(String code) {
	PaymentGatewayResponseDto result = new PaymentGatewayResponseDto();

	try {
	    result.getPaymentGateways().add(paymentGatewayApi.find(code));
	} catch (Exception e) {
	    processException(e, result.getActionStatus());
	}

	return result;
    }

    @Override
    public PaymentGatewayResponseDto createOrUpdatePaymentGateway(PaymentGatewayDto paymentGateway) {
	PaymentGatewayResponseDto response = new PaymentGatewayResponseDto();
	try {
	    paymentGatewayApi.createOrUpdate(paymentGateway);
	    response.getPaymentGateways().add(paymentGatewayApi.find(paymentGateway.getCode()));

	} catch (Exception e) {
	    processException(e, response.getActionStatus());
	}

	return response;
    }
 
}
