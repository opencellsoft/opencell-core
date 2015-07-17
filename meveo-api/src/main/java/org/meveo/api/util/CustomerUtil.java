package org.meveo.api.util;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.TitleService;

/**
 * 
 * @author Luis Alfonso L. Mance
 * 
 */
@Stateless
public class CustomerUtil {

	@Inject
	private TitleService titleService;

	@Inject
	private CountryService countryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private SellerService sellerService;

	public Customer getCustomer(AccountHierarchyDto postData, Provider provider)
			throws MeveoApiException {
		Customer customer = new Customer();
		Country country = null;

		if (!StringUtils.isEmpty(postData.getCountryCode())) {
			country = countryService.findByCode(postData.getCountryCode());

			if (country == null) {
				throw new EntityDoesNotExistsException(Country.class,
						postData.getCountryCode());
			}
		}

		Currency currency = null;
		if (!StringUtils.isEmpty(postData.getCurrencyCode())) {
			currency = currencyService.findByCode(postData.getCurrencyCode());

			if (currency == null) {
				throw new EntityDoesNotExistsException(Currency.class,
						postData.getCurrencyCode());
			}
		}

		Title title = titleService
				.findByCode(provider, postData.getTitleCode());

		if (!org.meveo.commons.utils.StringUtils.isBlank(postData
				.getSellerCode())) {
			Seller seller = sellerService.findByCode(postData.getSellerCode(),
					provider);
			customer.setSeller(seller);
		}

		customer.setCode(postData.getCustomerId());
		customer.getContactInformation().setEmail(postData.getEmail());
		customer.getContactInformation().setPhone(postData.getPhoneNumber());
		customer.setAddress(new Address());
		customer.getAddress().setAddress1(postData.getAddress1());
		customer.getAddress().setAddress2(postData.getAddress2());
		customer.getAddress().setZipCode(postData.getZipCode());
		customer.getAddress().setCity(postData.getCity());
		customer.getAddress().setCountry(postData.getCountryCode());
		customer.setProvider(provider);

		if (country != null) {
			customer.getAddress().setCountry(country.getCountryCode());
		}

		customer.setName(new Name());
		customer.getName().setTitle(title);
		customer.getName().setLastName(postData.getLastName());
		customer.getName().setFirstName(postData.getFirstName());

		return customer;
	}

}
