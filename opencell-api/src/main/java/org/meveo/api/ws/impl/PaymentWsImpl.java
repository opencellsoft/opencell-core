package org.meveo.api.ws.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.DDRequestBuilderResponseDto;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CreditCategoryApi;
import org.meveo.api.payment.DDRequestBuilderApi;
import org.meveo.api.payment.DDRequestLotOpApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.payment.PaymentGatewayApi;
import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.api.ws.PaymentWs;
import org.meveo.model.payments.DDRequestOpStatusEnum;

/**
 * The implementation for PaymentWs.
 * 
 * @author anasseh
 * @lastModifiedVersion 5.2
 */
@WebService(serviceName = "PaymentWs", endpointInterface = "org.meveo.api.ws.PaymentWs")
@Interceptors({ WsRestApiInterceptor.class })
public class PaymentWsImpl extends BaseWs implements PaymentWs {

    @Inject
    private PaymentApi paymentApi;

    @Inject
    private PaymentMethodApi paymentMethodApi;

    @Inject
    private DDRequestLotOpApi ddrequestLotOpApi;

    @Inject
    private CreditCategoryApi creditCategoryApi;

    @Inject
    private PaymentGatewayApi paymentGatewayApi;
    
    @Inject
    private DDRequestBuilderApi ddRequestBuilderApi;

    @Override
    public ActionStatus create(PaymentDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage("" + paymentApi.createPayment(postData));
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
    public PaymentResponseDto payByCard(PayByCardDto doPaymentRequestDto) {
        PaymentResponseDto response = new PaymentResponseDto();
        response.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, ""));
        try {
            response = paymentApi.payByCard(doPaymentRequestDto);
            response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }
        return response;
    }

    /************************************************************************************************/
    /**** Card Payment Method ****/
    /************************************************************************************************/
    @Override
    @Deprecated // Use addPaymentMthod operation
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethodDto) {
        PaymentMethodTokenDto response = new PaymentMethodTokenDto();
        try {
            Long pmId = paymentMethodApi.create(new PaymentMethodDto(cardPaymentMethodDto));
            response.setPaymentMethod(paymentMethodApi.find(pmId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return new CardPaymentMethodTokenDto(response);
    }

    @Override
    @Deprecated // Use updatePaymentMthod operation
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
    @Deprecated // Use removePaymentMthod operation
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
    @Deprecated // Use listPaymentMthod operation
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
    @Deprecated // Use findPaymentMthod operation
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
    public ActionStatus updatePaymentMethod(PaymentMethodDto paymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentMethodApi.update(paymentMethod);
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
    public PaymentMethodTokensDto listPaymentMethods(PagingAndFiltering pagingAndFiltering) {

        PaymentMethodTokensDto response = new PaymentMethodTokensDto();

        try {
            response = paymentMethodApi.list(pagingAndFiltering);
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
    public ActionStatus createCreditCategory(CreditCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            creditCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCreditCategory(CreditCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            creditCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCreditCategory(CreditCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            creditCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CreditCategoryResponseDto findCreditCategory(String creditCategoryCode) {
        CreditCategoryResponseDto result = new CreditCategoryResponseDto();

        try {
            result.setCreditCategory(creditCategoryApi.find(creditCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public CreditCategoriesResponseDto listCreditCategory() {
        CreditCategoriesResponseDto result = new CreditCategoriesResponseDto();

        try {
            result.setCreditCategories(creditCategoryApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCreditCategory(String creditCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            creditCategoryApi.remove(creditCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /********************************************/
    /**** Payment Gateway                   ****/
    /******************************************/

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
    public PaymentGatewayResponseDto listPaymentGateways(PagingAndFiltering pagingAndFiltering) {
        PaymentGatewayResponseDto result = new PaymentGatewayResponseDto();
        try {
            result = paymentGatewayApi.list(pagingAndFiltering);
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

    @Override
    public PaymentHistoriesDto listHistory(PagingAndFiltering pagingAndFiltering) {
        PaymentHistoriesDto result = new PaymentHistoriesDto();

        try {
            result = paymentApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
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
    public DDRequestBuilderResponseDto listDDRequestBuilders(PagingAndFiltering pagingAndFiltering) {
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
}