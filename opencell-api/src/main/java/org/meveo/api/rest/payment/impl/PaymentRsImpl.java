package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.CardTokenDto;
import org.meveo.api.dto.payment.CardTokenResponseDto;
import org.meveo.api.dto.payment.ListCardTokenResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CardTokenApi;
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
	private CardTokenApi cardTokenApi;

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
	public CardTokenResponseDto createCardToken(CardTokenDto cardTokenRequestDto) {
		CardTokenResponseDto response = new CardTokenResponseDto();
		response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
		try{
			CardTokenDto cardTokenDto = new CardTokenDto();
			cardTokenDto.setTokenId(cardTokenApi.create(cardTokenRequestDto));
			response.setCardTokenDto(cardTokenDto);
			response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
		}catch(Exception e){
			processException(e, response.getActionStatus());
		}
		
		return response;
	}

	@Override
	public ActionStatus updateCardToken(CardTokenDto cardTokenRequestDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try{
			cardTokenApi.update(cardTokenRequestDto);			
		}catch(Exception e){
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus removeCardToken(Long id) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try{
			cardTokenApi.remove(id);			
		}catch(Exception e){
			processException(e, result);
		}

		return result;
	}

	@Override
	public ListCardTokenResponseDto listCardToken(Long customerAccountId,String customerAccountCode) {
		ListCardTokenResponseDto response = new ListCardTokenResponseDto();
		response.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, ""));
		try{
			response.setListCardToken(cardTokenApi.list(customerAccountId,customerAccountCode));
			response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
		}catch(Exception e){
			processException(e, response.getActionStatus());
		}

		return response;
	}

	@Override
	public CardTokenResponseDto findCardToken(Long id) {
		CardTokenResponseDto response = new CardTokenResponseDto();
		response.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, ""));
		try{			
			response.setCardTokenDto(cardTokenApi.find(id));
			response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
		}catch(Exception e){
			processException(e, response.getActionStatus());
		}
		return response;
	}

}
