package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
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

/**
 * @author Edward P. Legaspi
 **/
@WebService(targetNamespace = "http://superAdmin.ws.api.meveo.org/")
public interface SuperAdminSettingsWs extends IBaseWs {

    // provider

    @WebMethod
    public ActionStatus createProvider(@WebParam(name = "provider") ProviderDto postData);

    @WebMethod
    public GetProviderResponse findProvider(@WebParam(name = "providerCode") String providerCode);

    @WebMethod
    public ActionStatus updateProvider(@WebParam(name = "provider") ProviderDto postData);

    @WebMethod
    public ActionStatus createOrUpdateProvider(@WebParam(name = "provider") ProviderDto postData);

    // language

    @WebMethod
    public ActionStatus createLanguage(@WebParam(name = "languageIso") LanguageIsoDto languageIsoDto);

    @WebMethod
    public GetLanguageIsoResponse findLanguage(@WebParam(name = "languageCode") String languageCode);

    @WebMethod
    public ActionStatus removeLanguage(@WebParam(name = "languageCode") String languageCode);

    @WebMethod
    public ActionStatus updateLanguage(@WebParam(name = "languageIso") LanguageIsoDto languageIsoDto);

    @WebMethod
    public ActionStatus createOrUpdateLanguage(@WebParam(name = "languageIso") LanguageIsoDto languageIsoDto);

    @WebMethod
    GetLanguagesIsoResponse listIsoLanguages();

    // country

    @WebMethod
    ActionStatus createCountry(@WebParam(name = "countryIso") CountryIsoDto countryIsoDto);

    @WebMethod
    GetCountryIsoResponse findCountry(@WebParam(name = "countryIsoCode") String countryCode);

    @WebMethod
    ActionStatus removeCountry(@WebParam(name = "countryCode") String countryCode);

    @WebMethod
    ActionStatus updateCountry(@WebParam(name = "countryIso") CountryIsoDto countryIsoDto);

    @WebMethod
    ActionStatus createOrUpdateCountry(@WebParam(name = "countryIso") CountryIsoDto countryisoDto);

    @WebMethod
    GetCountriesIsoResponse listIsoCountries();

    // currency

    @WebMethod
    ActionStatus createCurrency(@WebParam(name = "currencyIso") CurrencyIsoDto currencyIsoDto);

    @WebMethod
    GetCurrencyIsoResponse findCurrency(@WebParam(name = "currencyCode") String currencyCode);

    @WebMethod
    ActionStatus removeCurrency(@WebParam(name = "currencyCode") String currencyCode);

    @WebMethod
    ActionStatus updateCurrency(@WebParam(name = "currencyIso") CurrencyIsoDto currencyIsoDto);

    @WebMethod
    ActionStatus createOrUpdateCurrency(@WebParam(name = "currencyIso") CurrencyIsoDto currencyIsoDto);

    @WebMethod
    GetCurrenciesIsoResponse listIsoCurrencies();

    // files
    @WebMethod
    GetFilesResponseDto listAllFiles();

    @WebMethod
    GetFilesResponseDto listFiles(@WebParam(name = "dir") String dir);

    @WebMethod
    ActionStatus createDir(@WebParam(name = "dir") String dir);

    @WebMethod
    ActionStatus zipFile(@WebParam(name = "file") String file);

    @WebMethod
    ActionStatus zipDir(@WebParam(name = "dir") String dir);

    @WebMethod
    ActionStatus suppressFile(@WebParam(name = "file") String file);

    @WebMethod
    ActionStatus suppressDir(@WebParam(name = "dir") String dir);

    @WebMethod
    ActionStatus downloadFile(@WebParam(name = "file") String file);

    // Tenants

    /**
     * Register a new tenant
     * 
     * @param postData Tenant/Provider data
     * @return Action status
     */
    @WebMethod
    public ActionStatus createTenant(@WebParam(name = "provider") ProviderDto postData);

    /**
     * List tenants
     * 
     * @return A list of Tenant/provider data
     */
    @WebMethod
    public ProvidersDto listTenants();

    /**
     * Remove a tenant
     * 
     * @param providerCode Tenant/provider code
     * @return Action status
     */
    @WebMethod
    public ActionStatus removeTenant(@WebParam(name = "providerCode") String providerCode);
}