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

package org.meveo.api.ws.impl;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.jws.WebService;
import jakarta.servlet..http.HttpServletResponse;
import jakarta.xml.ws.handler.MessageContext;

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
@Deprecated
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
    public GetProviderResponse findProvider() {
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