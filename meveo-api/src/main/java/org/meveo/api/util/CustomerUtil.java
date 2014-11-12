package org.meveo.api.util;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.Country;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
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

	@PersistenceContext
	private EntityManager em;

	public Customer getCustomer(AccountHierarchyDto postData, Provider provider)
			throws MeveoApiException {
		Customer customer = new Customer();
		Country country = null;

		if (!StringUtils.isEmpty(postData.getCountryCode())) {
			country = countryService.findByCode(em, postData.getCountryCode());

			if (country == null) {
				throw new EntityDoesNotExistsException(Country.class,
						postData.getCountryCode());
			}
		}

		Currency currency = null;
		if (!StringUtils.isEmpty(postData.getCurrencyCode())) {
			currency = currencyService.findByCode(em,
					postData.getCurrencyCode());

			if (currency == null) {
				throw new EntityDoesNotExistsException(Currency.class,
						postData.getCurrencyCode());
			}
		}

		Title title = null;
		if (!StringUtils.isEmpty(postData.getCurrencyCode())) {
			title = titleService.findByCode(em, provider,
					postData.getTitleCode());
			if (title == null) {
				throw new EntityDoesNotExistsException(Title.class,
						postData.getTitleCode());
			}
		}

		customer.setCode(postData.getCustomerId());
		customer.getContactInformation().setEmail(postData.getEmail());
		customer.getContactInformation().setPhone(postData.getPhoneNumber());
		customer.setAddress(new Address());
		customer.getAddress().setAddress1(postData.getAddress1());
		customer.getAddress().setAddress2(postData.getAddress2());
		customer.getAddress().setZipCode(postData.getZipCode());
		customer.getAddress().setCity(postData.getCity());

		if (country != null) {
			customer.getAddress().setCountry(country.getCountryCode());
		}

		customer.setName(new Name());
		customer.getName().setTitle(title);
		customer.getName().setLastName(postData.getLastName());
		customer.getName().setFirstName(postData.getFirstName());

		return customer;
	}

	public AccountHierarchyDto getCustomerDTO(Customer customer) {
		AccountHierarchyDto result = new AccountHierarchyDto();

		result.setCustomerId(customer.getCode());
		result.setEmail(customer.getContactInformation().getEmail());
		result.setPhoneNumber(customer.getContactInformation().getPhone());
		if (customer.getAddress() != null) {
			result.setAddress1(customer.getAddress().getAddress1());
			result.setAddress2(customer.getAddress().getAddress2());
			result.setZipCode(customer.getAddress().getZipCode());
			result.setCountryCode(customer.getAddress().getCountry());
			result.setCity(customer.getAddress().getCity());
		}

		if (customer.getName() != null) {
			if (customer.getName().getTitle() != null) {
				result.setTitleCode(customer.getName().getTitle().getCode());
			}
			result.setLastName(customer.getName().getLastName());
			result.setFirstName(customer.getName().getFirstName());
		}

		return result;
	}

}
