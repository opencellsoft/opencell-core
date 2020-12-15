/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.PaymentActionStatus;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.DDRequestBuilderResponseDto;
import org.meveo.api.dto.payment.GatewayPaymentNamesEnum;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import org.meveo.api.dto.payment.PaymentGatewayRumSequenceDto;
import org.meveo.api.dto.payment.PaymentHistoriesDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceItemDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceItemsDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceResponseDto;
import org.meveo.api.dto.payment.PaymentScheduleInstancesDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplateDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplateResponseDto;
import org.meveo.api.dto.payment.PaymentScheduleTemplatesDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.payment.PaymentGatewayRumSequenceResponseDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DDRequestBuilderApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.payment.PaymentGatewayApi;
import org.meveo.api.payment.PaymentGatewayRumSequenceApi;
import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.api.payment.PaymentScheduleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.PaymentRs;

import java.util.List;

/**
 * The implementation for PaymentRs.
 * 
 * @author anasseh
 * @author Said Ramli
 * @author Edward P. Legaspi
 * @lastModifiedVersion 9.3
 */
@SuppressWarnings("deprecation")
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

    @Inject
    private PaymentScheduleApi paymentScheduleApi;

    @Inject
    private PaymentGatewayRumSequenceApi paymentGatewayRumSequenceApi;

    /**
     * Deprecated and replaced by createPayment
     * 
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
    public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode, PagingAndFiltering pagingAndFiltering) {
        CustomerPaymentsResponse result = new CustomerPaymentsResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result = paymentApi.getPaymentList(customerAccountCode, pagingAndFiltering);
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
    public PaymentMethodTokensDto listPaymentMethodGet(String query, String fields, Integer offset, Integer limit,
            String sortBy, SortOrder sortOrder) {
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
    public PaymentGatewayResponseDto listPaymentGatewaysGet(String query, String fields, Integer offset, Integer limit,
            String sortBy, SortOrder sortOrder) {
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
    public PaymentHistoriesDto listPaymentHistoryGet(String query, String fields, Integer offset, Integer limit,
            String sortBy, SortOrder sortOrder) {

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
    /**** DDRequest Builder ****/
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
    public DDRequestBuilderResponseDto listDDRequestBuildersGet(String query, String fields, Integer offset,
            Integer limit, String sortBy, SortOrder sortOrder) {
        DDRequestBuilderResponseDto result = new DDRequestBuilderResponseDto();
        try {
            result = ddRequestBuilderApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return null;
    }

    @Override
    public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(String customerAccountCode, String returnUrl,
            String locale, String amount, String currencyCode, String authorizationMode, String countryCode,
            Boolean skipAuthentication, String gatewayPaymentName, String variant, String sellerCode) {

        String paymentUrl = "";

        HostedCheckoutInput hostedCheckoutInput = new HostedCheckoutInput();
        hostedCheckoutInput.setCustomerAccountCode(customerAccountCode);
        hostedCheckoutInput.setReturnUrl(returnUrl);
        hostedCheckoutInput.setAmount(amount);
        hostedCheckoutInput.setAuthorizationMode(authorizationMode);
        hostedCheckoutInput.setCountryCode(countryCode);
        hostedCheckoutInput.setCurrencyCode(currencyCode);
        hostedCheckoutInput.setLocale(locale);
        hostedCheckoutInput.setSkipAuthentication(skipAuthentication);
        hostedCheckoutInput.setGatewayPaymentName(GatewayPaymentNamesEnum.valueOf(gatewayPaymentName));
        hostedCheckoutInput.setVariant(variant);
        hostedCheckoutInput.setSellerCode(sellerCode);

        try {
            paymentUrl = paymentMethodApi.getHostedCheckoutUrl(hostedCheckoutInput);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        PaymentHostedCheckoutResponseDto paymentHostedCheckoutResponseDto = new PaymentHostedCheckoutResponseDto(
                paymentUrl, customerAccountCode, returnUrl);

        return paymentHostedCheckoutResponseDto;

    }

    @Override
    public ActionStatus createOrUpdatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            result.setMessage(
                    "" + paymentScheduleApi.createOrUpdatePaymentScheduleTemplate(paymentScheduleTemplateDto));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createPaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            result.setMessage("" + paymentScheduleApi.createPaymentScheduleTemplate(paymentScheduleTemplateDto));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updatePaymentScheduleTemplate(PaymentScheduleTemplateDto paymentScheduleTemplateDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            result.setMessage("" + paymentScheduleApi.updatePaymentScheduleTemplate(paymentScheduleTemplateDto));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removePaymentScheduleTemplate(String paymentScheduleTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            paymentScheduleApi.removePaymentScheduleTemplate(paymentScheduleTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public PaymentScheduleTemplateResponseDto findPaymentScheduleTemplate(String paymentScheduleTemplateCode) {
        PaymentScheduleTemplateResponseDto result = new PaymentScheduleTemplateResponseDto();
        try {
            result.setPaymentScheduleTemplateDto(
                    paymentScheduleApi.findPaymentScheduleTemplate(paymentScheduleTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentScheduleTemplatesDto listPaymentScheduleTemplate(PagingAndFiltering pagingAndFiltering) {
        PaymentScheduleTemplatesDto result = new PaymentScheduleTemplatesDto();
        try {
            result = paymentScheduleApi.listPaymentScheduleTemplate(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentScheduleTemplatesDto listPaymentScheduleTemplate(String query, String fields, Integer offset,
            Integer limit, String sortBy, SortOrder sortOrder) {
        PaymentScheduleTemplatesDto result = new PaymentScheduleTemplatesDto();
        try {
            result = paymentScheduleApi.listPaymentScheduleTemplate(
                    new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus updatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            paymentScheduleApi.updatePaymentScheduleInstance(paymentScheduleInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    @Override
    public PaymentScheduleInstanceResponseDto findPaymentScheduleInstance(Long id) {
    	PaymentScheduleInstanceResponseDto response = new PaymentScheduleInstanceResponseDto();    	
    	try {
    		response = paymentScheduleApi.findPaymentScheduleInstance(id);
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }
    	return response;
    }

    @Override
    public PaymentScheduleInstancesDto listPaymentScheduleInstance(PagingAndFiltering pagingAndFiltering) {
        PaymentScheduleInstancesDto result = new PaymentScheduleInstancesDto();
        try {
            result = paymentScheduleApi.listPaymentScheduleInstance(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentScheduleInstancesDto listPaymentScheduleInstance(String query, String fields, Integer offset,
            Integer limit, String sortBy, SortOrder sortOrder) {
        PaymentScheduleInstancesDto result = new PaymentScheduleInstancesDto();
        try {
            result = paymentScheduleApi.listPaymentScheduleInstance(
                    new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
  

    @Override
    public ActionStatus terminatePaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            paymentScheduleApi.terminatePaymentScheduleInstance(paymentScheduleInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelPaymentScheduleInstance(PaymentScheduleInstanceDto paymentScheduleInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            paymentScheduleApi.cancelPaymentScheduleInstance(paymentScheduleInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createRumSequence(PaymentGatewayRumSequenceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            paymentGatewayRumSequenceApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateRumSequence(PaymentGatewayRumSequenceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            paymentGatewayRumSequenceApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public PaymentGatewayRumSequenceResponseDto findRumSequence(String code) {
        PaymentGatewayRumSequenceResponseDto result = new PaymentGatewayRumSequenceResponseDto();

        try {
            result.setPaymentGatewayRumSequence(paymentGatewayRumSequenceApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus deleteRumSequence(String code) {
        ActionStatus result = new ActionStatus();

        try {
            paymentGatewayRumSequenceApi.delete(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GenericSequenceValueResponseDto getNextPaymentGatewayRumSequenceNumber(String code) {
        GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

        try {
            result = paymentGatewayRumSequenceApi.getNextNumber(code);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public PaymentMethodTokensDto findPaymentMethodByCustomerAccount(String customerAccountCode, Integer offset, Integer limit) {
        PaymentMethodTokensDto result = new PaymentMethodTokensDto();
        try {
            result = paymentMethodApi.listByCustomerAccountCode(customerAccountCode, offset, limit);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public MandatInfoDto checkMandate(String mandateReference, String mandateId,String customerAccountCode) {
    	MandatInfoDto result = new MandatInfoDto();

    	try {
    		result = paymentMethodApi.checkMandate(mandateReference, mandateId, customerAccountCode);
    	} catch (Exception e) {
    		processException(e, result.getActionStatus());
    	}

    	return result;
    }
    @Override
    public ActionStatus approveSepaDDMandate(String customerAccountCode,String tokenId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentMethodApi.approveSepaDDMandate(customerAccountCode,tokenId);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * Update Payment schedule instance item, the update is only about amount and requestPaymentDate.
     *
     * @param paymentScheduleInstanceItemDtos
     * @return
     */
    @Override
    public ActionStatus replacePaymentScheduleInstanceItem(Long paymentScheduleInstanceId, PaymentScheduleInstanceItemsDto paymentScheduleInstanceItemDtos) {
        ActionStatus result = new ActionStatus();

        try {
            paymentScheduleApi.replacePaymentScheduleInstanceItems(paymentScheduleInstanceId, paymentScheduleInstanceItemDtos.getPaymentScheduleInstanceItems());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}