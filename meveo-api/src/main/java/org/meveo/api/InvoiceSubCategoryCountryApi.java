package org.meveo.api;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.InvoiceSubCategoryCountryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceSubCategoryCountryApi extends BaseApi {

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private TaxService taxService;

	public void create(InvoiceSubCategoryCountryDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getInvoiceSubCategory())
				&& !StringUtils.isBlank(postData.getCountry())
				&& !StringUtils.isBlank(postData.getTax())) {
			Provider provider = currentUser.getProvider();

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(postData.getCountry(), provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class,
						postData.getCountry());
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getInvoiceSubCategory(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class,
						postData.getInvoiceSubCategory());
			}

			Tax tax = taxService.findByCode(postData.getTax(), provider);
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class,
						postData.getTax());
			}

			if (invoiceSubCategoryCountryService
					.findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
							tradingCountry, provider) != null) {
				throw new EntityAlreadyExistsException(
						"InvoiceSubCategoryCountry with invoiceSubCategory="
								+ postData.getInvoiceSubCategory()
								+ ", tradingCountry=" + postData.getCountry()
								+ " already exists.");
			}

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
			invoiceSubcategoryCountry.setInvoiceSubCategory(invoiceSubCategory);
			invoiceSubcategoryCountry.setTax(tax);
			invoiceSubcategoryCountry.setTradingCountry(tradingCountry);
			invoiceSubcategoryCountry.setFilterEL(postData.getFilterEL());
			invoiceSubCategoryCountryService.create(invoiceSubcategoryCountry,
					currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
				missingParameters.add("invoiceSubCategory");
			}
			if (StringUtils.isBlank(postData.getCountry())) {
				missingParameters.add("country");
			}
			if (StringUtils.isBlank(postData.getTax())) {
				missingParameters.add("tax");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(InvoiceSubCategoryCountryDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getInvoiceSubCategory())
				&& !StringUtils.isBlank(postData.getCountry())
				&& !StringUtils.isBlank(postData.getTax())) {
			Provider provider = currentUser.getProvider();

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(postData.getCountry(), provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class,
						postData.getCountry());
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(postData.getInvoiceSubCategory(), provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class,
						postData.getInvoiceSubCategory());
			}

			Tax tax = taxService.findByCode(postData.getTax(), provider);
			if (tax == null) {
				throw new EntityDoesNotExistsException(Tax.class,
						postData.getTax());
			}

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
							tradingCountry, provider);
			if (invoiceSubcategoryCountry == null) {
				throw new EntityDoesNotExistsException(
						"InvoiceSubCategoryCountry with invoiceSubCategory="
								+ postData.getInvoiceSubCategory()
								+ ", tradingCountry=" + postData.getCountry()
								+ " does not exists.");
			}

			invoiceSubcategoryCountry.setTax(tax);

			invoiceSubCategoryCountryService.update(invoiceSubcategoryCountry,
					currentUser);
		} else {
			if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
				missingParameters.add("invoiceSubCategory");
			}
			if (StringUtils.isBlank(postData.getCountry())) {
				missingParameters.add("country");
			}
			if (StringUtils.isBlank(postData.getTax())) {
				missingParameters.add("tax");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public InvoiceSubCategoryCountryDto find(String invoiceSubCategoryCode,
			String countryCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(invoiceSubCategoryCode)
				&& !StringUtils.isBlank(countryCode)) {

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryCode, provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class,
						countryCode);
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(invoiceSubCategoryCode, provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class, invoiceSubCategoryCode);
			}

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
							tradingCountry, Arrays.asList("invoiceSubCategory",
									"tradingCountry", "tax"), provider);
			if (invoiceSubcategoryCountry == null) {
				throw new EntityDoesNotExistsException(
						"InvoiceSubCategoryCountry with invoiceSubCategory="
								+ invoiceSubCategoryCode + ", tradingCountry="
								+ countryCode + " already exists.");
			}

			return new InvoiceSubCategoryCountryDto(invoiceSubcategoryCountry);
		} else {
			if (StringUtils.isBlank(invoiceSubCategoryCode)) {
				missingParameters.add("invoiceSubCategoryCode");
			}
			if (StringUtils.isBlank(countryCode)) {
				missingParameters.add("country");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void remove(String invoiceSubCategoryCode, String countryCode,
			Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(invoiceSubCategoryCode)
				&& !StringUtils.isBlank(countryCode)) {

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryCode, provider);
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class,
						countryCode);
			}

			InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
					.findByCode(invoiceSubCategoryCode, provider);
			if (invoiceSubCategory == null) {
				throw new EntityDoesNotExistsException(
						InvoiceSubCategory.class, invoiceSubCategoryCode);
			}

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
							tradingCountry, Arrays.asList("invoiceSubCategory",
									"tradingCountry", "tax"), provider);
			if (invoiceSubcategoryCountry == null) {
				throw new EntityDoesNotExistsException(
						"InvoiceSubCategoryCountry with invoiceSubCategory="
								+ invoiceSubCategoryCode + ", tradingCountry="
								+ countryCode + " already exists.");
			}

			invoiceSubCategoryCountryService.remove(invoiceSubcategoryCountry);
		} else {
			if (StringUtils.isBlank(invoiceSubCategoryCode)) {
				missingParameters.add("invoiceSubCategoryCode");
			}
			if (StringUtils.isBlank(countryCode)) {
				missingParameters.add("country");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}
	
	/**
	 * Create or update InvoiceSubCategoryCountry based on the invoice sub-category 
	 * and country attached.
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(InvoiceSubCategoryCountryDto postData, User currentUser) throws MeveoApiException {
		
		Provider provider = currentUser.getProvider();
		
		TradingCountry tradingCountry = tradingCountryService
				.findByTradingCountryCode(postData.getCountry(), provider);
		
		InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
				.findByCode(postData.getInvoiceSubCategory(), provider);
		
		InvoiceSubcategoryCountry invoiceSubcategoryCountry 
			= invoiceSubCategoryCountryService
				.findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
							tradingCountry, provider);
		
		if (invoiceSubcategoryCountry == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
		
	}
}
