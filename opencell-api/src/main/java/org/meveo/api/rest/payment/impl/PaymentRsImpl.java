package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.CheckPaymentMethodDto;
import org.meveo.api.dto.payment.CheckPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CheckPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDPaymentMethodDto;
import org.meveo.api.dto.payment.DDPaymentMethodTokenDto;
import org.meveo.api.dto.payment.DDPaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.TipPaymentMethodDto;
import org.meveo.api.dto.payment.TipPaymentMethodTokenDto;
import org.meveo.api.dto.payment.TipPaymentMethodTokensDto;
import org.meveo.api.dto.payment.WirePaymentMethodDto;
import org.meveo.api.dto.payment.WirePaymentMethodTokenDto;
import org.meveo.api.dto.payment.WirePaymentMethodTokensDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CardPaymentMethodApi;
import org.meveo.api.payment.CheckPaymentMethodApi;
import org.meveo.api.payment.DDPaymentMethodApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.payment.TipPaymentMethodApi;
import org.meveo.api.payment.WirePaymentMethodApi;
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
    private DDPaymentMethodApi ddPaymentMethodApi;
    
    @Inject
    private CardPaymentMethodApi cardPaymentMethodApi;
    
    @Inject
    private CheckPaymentMethodApi checkPaymentMethodApi;
    
    @Inject
    private TipPaymentMethodApi tipPaymentMethodApi;
    
    @Inject
    private WirePaymentMethodApi wirePaymentMethodApi;

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
    
    /************************************************************************************************/
    /****                                 Card Payment Method                                    ****/
    /************************************************************************************************/

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
    
    /************************************************************************************************/
    /****                                 DirectDebit Payment Method                             ****/
    /************************************************************************************************/
    @Override
    public DDPaymentMethodTokenDto addDDPaymentMethod(DDPaymentMethodDto ddPaymentMethodDto) {
    	DDPaymentMethodTokenDto response = new DDPaymentMethodTokenDto();
        try {
            Long ddPaymentMethodId = ddPaymentMethodApi.create(ddPaymentMethodDto);
            response.setDDPaymentMethod(ddPaymentMethodApi.find(ddPaymentMethodId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updateDDPaymentMethod(DDPaymentMethodDto ddPaymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ddPaymentMethodApi.update(ddPaymentMethod);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeDDPaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ddPaymentMethodApi.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DDPaymentMethodTokensDto listDDPaymentMethods(Long customerAccountId, String customerAccountCode) {

        DDPaymentMethodTokensDto response = new DDPaymentMethodTokensDto();

        try {
            response.setDdPaymentMethods(ddPaymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public DDPaymentMethodTokenDto findDDPaymentMethod(Long id) {

        DDPaymentMethodTokenDto response = new DDPaymentMethodTokenDto();

        try {
            response.setDDPaymentMethod(ddPaymentMethodApi.find(id));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }
    /************************************************************************************************/
    /****                                 Tip Payment Method                                    ****/
    /************************************************************************************************/
    @Override
    public TipPaymentMethodTokenDto addTipPaymentMethod(TipPaymentMethodDto tipPaymentMethodDto) {
        TipPaymentMethodTokenDto response = new TipPaymentMethodTokenDto();
        try {
            Long paymentMethodId = tipPaymentMethodApi.create(tipPaymentMethodDto);
            response.setTipPaymentMethod(tipPaymentMethodApi.find(paymentMethodId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updateTipPaymentMethod(TipPaymentMethodDto tipPaymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            tipPaymentMethodApi.update(tipPaymentMethod);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeTipPaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            tipPaymentMethodApi.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TipPaymentMethodTokensDto listTipPaymentMethods(Long customerAccountId, String customerAccountCode) {

        TipPaymentMethodTokensDto response = new TipPaymentMethodTokensDto();

        try {
            response.setTipPaymentMethods(tipPaymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public TipPaymentMethodTokenDto findTipPaymentMethod(Long id) {

        TipPaymentMethodTokenDto response = new TipPaymentMethodTokenDto();

        try {
            response.setTipPaymentMethod(tipPaymentMethodApi.find(id));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }
    /************************************************************************************************/
    /****                                 Check Payment Method                                   ****/
    /************************************************************************************************/
    @Override
    public CheckPaymentMethodTokenDto addCheckPaymentMethod(CheckPaymentMethodDto checkPaymentMethodDto) {
        CheckPaymentMethodTokenDto response = new CheckPaymentMethodTokenDto();
        try {
            Long paymentMethodId = checkPaymentMethodApi.create(checkPaymentMethodDto);
            response.setCheckPaymentMethod(checkPaymentMethodApi.find(paymentMethodId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updateCheckPaymentMethod(CheckPaymentMethodDto checkPaymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            checkPaymentMethodApi.update(checkPaymentMethod);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeCheckPaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            checkPaymentMethodApi.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CheckPaymentMethodTokensDto listCheckPaymentMethods(Long customerAccountId, String customerAccountCode) {

        CheckPaymentMethodTokensDto response = new CheckPaymentMethodTokensDto();

        try {
            response.setCheckPaymentMethods(checkPaymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public CheckPaymentMethodTokenDto findCheckPaymentMethod(Long id) {

        CheckPaymentMethodTokenDto response = new CheckPaymentMethodTokenDto();

        try {
            response.setCheckPaymentMethod(checkPaymentMethodApi.find(id));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }
    /************************************************************************************************/
    /****                                 Wire Payment Method                                    ****/
    /************************************************************************************************/
    @Override
    public WirePaymentMethodTokenDto addWirePaymentMethod(WirePaymentMethodDto wirePaymentMethodDto) {
        WirePaymentMethodTokenDto response = new WirePaymentMethodTokenDto();
        try {
            Long paymentMethodId = wirePaymentMethodApi.create(wirePaymentMethodDto);
            response.setWirePaymentMethod(wirePaymentMethodApi.find(paymentMethodId));

        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public ActionStatus updateWirePaymentMethod(WirePaymentMethodDto wirePaymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            wirePaymentMethodApi.update(wirePaymentMethod);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeWirePaymentMethod(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            wirePaymentMethodApi.remove(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public WirePaymentMethodTokensDto listWirePaymentMethods(Long customerAccountId, String customerAccountCode) {

        WirePaymentMethodTokensDto response = new WirePaymentMethodTokensDto();

        try {
            response.setWirePaymentMethods(wirePaymentMethodApi.list(customerAccountId, customerAccountCode));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }

    @Override
    public WirePaymentMethodTokenDto findWirePaymentMethod(Long id) {

        WirePaymentMethodTokenDto response = new WirePaymentMethodTokenDto();

        try {
            response.setWirePaymentMethod(wirePaymentMethodApi.find(id));
        } catch (Exception e) {
            processException(e, response.getActionStatus());
        }

        return response;
    }
}
