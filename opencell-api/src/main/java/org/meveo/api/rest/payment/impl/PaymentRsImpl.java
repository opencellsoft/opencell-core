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
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.DDRequestBuilderResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DDRequestBuilderApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.payment.PaymentGatewayApi;
import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.PaymentRs;

/**
 * The implementation for PaymentRs.
 * 
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.2
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
    
    @Inject
    private DDRequestBuilderApi ddRequestBuilderApi;

    /**
     * Deprecated and replaced by createPayment
     * @return payment action status which contains payment id.
     * @see org.meveo.api.rest.payment.PaymentRs#create(org.meveo.api.dto.payment.PaymentDto)
     */
    @Override
    @Deprecated
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
    
    /**
     * @return payment action status which contains payment id.
     * @see org.meveo.api.rest.payment.PaymentRs#create(org.meveo.api.dto.payment.PaymentDto)
     */
    @Override
    public PaymentActionStatus createPayment(PaymentDto postData) {
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
    /**** Card Payment Method ****/
    /************************************************************************************************/

    @Override
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethodDto) {
        PaymentMethodTokenDto response = new PaymentMethodTokenDto();
        try {
            PaymentMethodDto paymentMethodDto = new PaymentMethodDto(cardPaymentMethodDto);
            Long tokenId = paymentMethodApi.create(paymentMethodDto);
            PaymentMethodDto find = paymentMethodApi.find(tokenId);
            response.setPaymentMethod(find);
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
            response = paymentMethodApi.list(customerAccountId, customerAccountCode);
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
    /**** Payment Methods ****/
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
    public PaymentMethodTokensDto listPaymentMethodGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        PaymentMethodTokensDto result = new PaymentMethodTokensDto();
        try {
            result = paymentMethodApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentMethodTokensDto listPaymentMethodPost(PagingAndFiltering pagingAndFiltering) {
        PaymentMethodTokensDto result = new PaymentMethodTokensDto();
        try {
            result = paymentMethodApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
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
    /* Payment Gateways */
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

    @Override
    public PaymentGatewayResponseDto listPaymentGatewaysGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        PaymentGatewayResponseDto result = new PaymentGatewayResponseDto();
        try {
            result = paymentGatewayApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentGatewayResponseDto listPaymentGatewaysPost(PagingAndFiltering pagingAndFiltering) {
        PaymentGatewayResponseDto result = new PaymentGatewayResponseDto();
        try {
            result = paymentGatewayApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public PaymentHistoriesDto listPaymentHistoryGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        PaymentHistoriesDto result = new PaymentHistoriesDto();

        try {
            result = paymentApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentHistoriesDto listPaymentHistoryPost(PagingAndFiltering pagingAndFiltering) {
        PaymentHistoriesDto result = new PaymentHistoriesDto();

        try {
            result = paymentApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enablePaymentMethod(Long id) {
        ActionStatus result = new ActionStatus();

        try {
            paymentMethodApi.enableOrDisable(id, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disablePaymentMethod(Long id) {

        ActionStatus result = new ActionStatus();

        try {
            paymentMethodApi.enableOrDisable(id, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enablePaymentGateway(String code) {

        ActionStatus result = new ActionStatus();

        try {
            paymentGatewayApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disablePaymentGateway(String code) {

        ActionStatus result = new ActionStatus();

        try {
            paymentGatewayApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    /********************************************/
    /**** DDRequest Builder                 ****/
    /******************************************/
    
    @Override
    public DDRequestBuilderResponseDto addDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder) {
        DDRequestBuilderResponseDto response = new DDRequestBuilderResponseDto();
        try {
            ddRequestBuilderApi.create(ddRequestBuilder);
            response.getDdRequestBuilders().add(ddRequestBuilderApi.find(ddRequestBuilder.getCode()));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updateDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ddRequestBuilderApi.update(ddRequestBuilder);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeDDRequestBuilder(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ddRequestBuilderApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DDRequestBuilderResponseDto listDDRequestBuildersPost(PagingAndFiltering pagingAndFiltering) {
        DDRequestBuilderResponseDto result = new DDRequestBuilderResponseDto();
        try {
            result = ddRequestBuilderApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return null;
    }

    @Override
    public DDRequestBuilderResponseDto findDDRequestBuilder(String code) {
        DDRequestBuilderResponseDto result = new DDRequestBuilderResponseDto();

        try {
            result.getDdRequestBuilders().add(ddRequestBuilderApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public DDRequestBuilderResponseDto createOrUpdateDDRequestBuilder(DDRequestBuilderDto ddRequestBuilder) {
        DDRequestBuilderResponseDto response = new DDRequestBuilderResponseDto();
        try {
            ddRequestBuilderApi.createOrUpdate(ddRequestBuilder);
            response.getDdRequestBuilders().add(ddRequestBuilderApi.find(ddRequestBuilder.getCode()));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }
        return response;
    }

    @Override
    public ActionStatus enableDDRequestBuilder(String code) {
        ActionStatus result = new ActionStatus();
        try {
            ddRequestBuilderApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus disableDDRequestBuilder(String code) {
        ActionStatus result = new ActionStatus();
        try {
            ddRequestBuilderApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }


    @Override
    public DDRequestBuilderResponseDto listDDRequestBuildersGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        DDRequestBuilderResponseDto result = new DDRequestBuilderResponseDto();
        try {
            result = ddRequestBuilderApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return null;
    }
}