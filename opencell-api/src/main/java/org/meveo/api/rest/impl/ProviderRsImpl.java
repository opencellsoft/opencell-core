package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ProviderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.ProvidersDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.ProviderRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ProviderRsImpl extends BaseRs implements ProviderRs {

    @Inject
    private ProviderApi providerApi;

    @Override
    public ActionStatus create(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            throw new BusinessException("There should already be a provider setup");
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetProviderResponse find(String providerCode) {
        GetProviderResponse result = new GetProviderResponse();

        try {
            result.setProvider(providerApi.find());

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.update(postData);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTradingConfigurationResponseDto findTradingConfiguration(String providerCode) {
        GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

        try {
            result = providerApi.getTradingConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetInvoicingConfigurationResponseDto findInvoicingConfiguration(String providerCode) {
        GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

        try {
            result = providerApi.getInvoicingConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCustomerConfigurationResponseDto findCustomerConfiguration(String providerCode) {
        GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

        try {
            result = providerApi.getCustomerConfiguration();
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration(String providerCode) {
        GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

        try {
            result = providerApi.getCustomerAccountConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.update(postData);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateProviderCF(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.updateProviderCF(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetProviderResponse findProviderCF(String providerCode) {
        GetProviderResponse result = new GetProviderResponse();

        try {
            result.setProvider(providerApi.findProviderCF());

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createTenant(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.createTenant(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProvidersDto listTenants() {

        ProvidersDto result = new ProvidersDto();

        try {
            result = providerApi.listTenants();
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeTenant(String providerCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.removeTenant(providerCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}