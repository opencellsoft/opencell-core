package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.MessageContext;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.CountryIsoApi;
import org.meveo.api.CurrencyIsoApi;
import org.meveo.api.LanguageIsoApi;
import org.meveo.api.ProviderApi;
import org.meveo.api.admin.FilesApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.dto.LanguageIsoDto;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.ProvidersDto;
import org.meveo.api.dto.response.GetCountriesIsoResponse;
import org.meveo.api.dto.response.GetCountryIsoResponse;
import org.meveo.api.dto.response.GetCurrenciesIsoResponse;
import org.meveo.api.dto.response.GetCurrencyIsoResponse;
import org.meveo.api.dto.response.GetLanguageIsoResponse;
import org.meveo.api.dto.response.GetLanguagesIsoResponse;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.admin.GetFilesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.SuperAdminSettingsWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "SuperAdminSettingsWs", endpointInterface = "org.meveo.api.ws.SuperAdminSettingsWs", targetNamespace = "http://superAdmin.ws.api.meveo.org/")
@Interceptors({ WsRestApiInterceptor.class })
public class SuperAdminSettingsWsImpl extends BaseWs implements SuperAdminSettingsWs {

    @Inject
    private CountryIsoApi countryIsoApi;

    @Inject
    private LanguageIsoApi languageIsoApi;

    @Inject
    private CurrencyIsoApi currencyIsoApi;

    @Inject
    private ProviderApi providerApi;

    @Inject
    private FilesApi filesApi;

    @Override
    public ActionStatus createProvider(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            throw new BusinessException("There should already be a provider setup");

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetProviderResponse findProvider(String providerCode) {
        GetProviderResponse result = new GetProviderResponse();

        try {
            result.setProvider(providerApi.find());

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus updateProvider(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateProvider(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createLanguage(LanguageIsoDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetLanguageIsoResponse findLanguage(String languageCode) {
        GetLanguageIsoResponse result = new GetLanguageIsoResponse();

        try {
            result.setLanguage(languageIsoApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus removeLanguage(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateLanguage(LanguageIsoDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdateLanguage(LanguageIsoDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCountry(CountryIsoDto countryIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryIsoApi.create(countryIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCountryIsoResponse findCountry(String countryCode) {
        GetCountryIsoResponse result = new GetCountryIsoResponse();

        try {
            result.setCountry(countryIsoApi.find(countryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCountry(String countryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryIsoApi.remove(countryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCountry(CountryIsoDto countryIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryIsoApi.update(countryIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCountry(CountryIsoDto countryIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryIsoApi.createOrUpdate(countryIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCurrency(CurrencyIsoDto currencyIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.create(currencyIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCurrencyIsoResponse findCurrency(String currencyCode) {
        GetCurrencyIsoResponse result = new GetCurrencyIsoResponse();

        try {
            result.setCurrency(currencyIsoApi.find(currencyCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCurrency(String currencyCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.remove(currencyCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCurrency(CurrencyIsoDto currencyIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.update(currencyIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCurrency(CurrencyIsoDto currencyIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.createOrUpdate(currencyIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetLanguagesIsoResponse listIsoLanguages() {
        GetLanguagesIsoResponse result = new GetLanguagesIsoResponse();

        try {
            result.setLanguages(languageIsoApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCountriesIsoResponse listIsoCountries() {
        GetCountriesIsoResponse result = new GetCountriesIsoResponse();

        try {
            result.setCountries(countryIsoApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCurrenciesIsoResponse listIsoCurrencies() {
        GetCurrenciesIsoResponse result = new GetCurrenciesIsoResponse();

        try {
            result.setCurrencies(currencyIsoApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetFilesResponseDto listAllFiles() {
        GetFilesResponseDto result = new GetFilesResponseDto();

        try {
            result.setFiles(filesApi.listFiles(null));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetFilesResponseDto listFiles(String dir) {
        GetFilesResponseDto result = new GetFilesResponseDto();

        try {
            result.setFiles(filesApi.listFiles(dir));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createDir(String dir) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.createDir(dir);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus zipFile(String filePath) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.zipFile(filePath);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus zipDir(String dir) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.zipDir(dir);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus suppressFile(String filePath) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.suppressFile(filePath);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus suppressDir(String dir) {
        ActionStatus result = new ActionStatus();

        try {
            filesApi.suppressDir(dir);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus downloadFile(String file) {
        ActionStatus result = new ActionStatus();

        try {
            MessageContext mc = webServiceContext.getMessageContext();
            HttpServletResponse response = (HttpServletResponse) mc.get(MessageContext.SERVLET_RESPONSE);
            filesApi.downloadFile(file, response);
        } catch (Exception e) {
            processException(e, result);
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