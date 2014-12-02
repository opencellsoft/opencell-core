package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.InvoiceSubCategoryCountryDto;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.dto.response.GetCurrencyResponse;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.dto.response.GetInvoiceSubCategoryCountryResponse;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import org.meveo.api.dto.response.GetLanguageResponse;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetUserResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface SettingsWs extends IBaseWs {

	@WebMethod
	ActionStatus createCountry(CountryDto countryDto);

	@WebMethod
	GetCountryResponse findCountry(String countryCode);

	@WebMethod
	ActionStatus removeCountry(String countryCode, String currencyCode);

	@WebMethod
	ActionStatus updateCountry(CountryDto countryDto);

	@WebMethod
	ActionStatus createCurrency(CurrencyDto postData);

	@WebMethod
	GetCurrencyResponse findCurrency(String currencyCode);

	@WebMethod
	ActionStatus removeCurrency(String currencyCode);

	@WebMethod
	ActionStatus updateCurrency(CurrencyDto postData);

	@WebMethod
	public ActionStatus createInvoiceCategory(InvoiceCategoryDto postData);

	@WebMethod
	public ActionStatus updateInvoiceCategory(InvoiceCategoryDto postData);

	@WebMethod
	public GetInvoiceCategoryResponse findInvoiceCategory(
			String invoiceCategoryCode);

	@WebMethod
	public ActionStatus removeInvoiceCategory(String invoiceCategoryCode);

	@WebMethod
	public ActionStatus createInvoiceSubCategoryCountry(
			InvoiceSubCategoryCountryDto postData);

	@WebMethod
	public ActionStatus updateInvoiceSubCategoryCountry(
			InvoiceSubCategoryCountryDto postData);

	@WebMethod
	public GetInvoiceSubCategoryCountryResponse findInvoiceSubCategoryCountry(
			String invoiceSubCategoryCode, String country);

	@WebMethod
	public ActionStatus removeInvoiceSubCategoryCountry(
			String invoiceSubCategoryCode, String country);

	@WebMethod
	public ActionStatus createInvoiceSubCategory(InvoiceSubCategoryDto postData);

	@WebMethod
	public ActionStatus updateInvoiceSubCategory(InvoiceSubCategoryDto postData);

	@WebMethod
	public GetInvoiceSubCategoryResponse findInvoiceSubCategory(
			String invoiceSubCategoryCode);

	@WebMethod
	public ActionStatus removeInvoiceSubCategory(String invoiceSubCategoryCode);

	@WebMethod
	public ActionStatus createLanguage(LanguageDto postData);

	@WebMethod
	public GetLanguageResponse findLanguage(String languageCode);

	@WebMethod
	public ActionStatus removeLanguage(String languageCode);

	@WebMethod
	public ActionStatus updateLanguage(LanguageDto postData);

	@WebMethod
	public ActionStatus createProvider(ProviderDto postData);

	@WebMethod
	public GetProviderResponse findProvider(String providerCode);

	@WebMethod
	public ActionStatus updateProvider(ProviderDto postData);

	@WebMethod
	public ActionStatus createSeller(SellerDto postData);

	@WebMethod
	public ActionStatus updateSeller(SellerDto postData);

	@WebMethod
	public GetSellerResponse findSeller(String sellerCode);

	@WebMethod
	public ActionStatus removeSeller(String sellerCode);

	@WebMethod
	public ActionStatus createTax(TaxDto postData);

	@WebMethod
	public ActionStatus updateTax(TaxDto postData);

	@WebMethod
	public GetTaxResponse findTax(String taxCode);

	@WebMethod
	public ActionStatus removeTax(String taxCode);

	@WebMethod
	public ActionStatus createUser(UserDto postData);

	@WebMethod
	public ActionStatus updateUser(UserDto postData);

	@WebMethod
	public ActionStatus removeUser(String username);

	@WebMethod
	public GetUserResponse findUser(String username);

}
