package org.meveo.api.dto.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomerHierarchyDto;

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
public class CustomerDtoService {
	@Inject
	private TitleService titleService;

	@Inject
	private CountryService countryService;

	@Inject
	private CurrencyService currencyService;

	@PersistenceContext
	private EntityManager em;

	public Customer getCustomer(CustomerHierarchyDto customerDto)
			throws BusinessException {
		Provider provider = customerDto.getCurrentUser().getProvider();
		Customer customer = new Customer();
		Country country = null;
		if (!StringUtils.isEmpty(customerDto.getCountryCode())) {
			country = countryService.findByCode(em,
					customerDto.getCountryCode());

			if (country == null) {
				throw new BusinessException("Invalid country code "
						+ customerDto.getCountryCode());
			}
		}

		Currency currency = null;
		if (!StringUtils.isEmpty(customerDto.getCurrencyCode())) {
			currency = currencyService.findByCode(em,
					customerDto.getCurrencyCode());

			if (currency == null) {
				throw new BusinessException("Invalid currency code "
						+ customerDto.getCountryCode());
			}
		}

		Title title = null;
		if (!StringUtils.isEmpty(customerDto.getCurrencyCode())) {
			title = titleService.findByCode(em, provider,
					customerDto.getTitleCode());
			if (title == null) {
				throw new BusinessException("Invalid title code "
						+ customerDto.getTitleCode()
						+ ". Please check if it is in uppercase.");
			}
		}

		customer.setCode(customerDto.getCustomerId());
		customer.getContactInformation().setEmail(customerDto.getEmail());
		customer.getContactInformation().setPhone(customerDto.getPhoneNumber());
		customer.setAddress(new Address());
		customer.getAddress().setAddress1(customerDto.getAddress1());
		customer.getAddress().setAddress2(customerDto.getAddress2());
		customer.getAddress().setZipCode(customerDto.getZipCode());
		customer.getAddress().setCity(customerDto.getCity());
		if (country != null) {
			customer.getAddress().setCountry(country.getCountryCode());
		}
		customer.setName(new Name());
		customer.getName().setTitle(title);
		customer.getName().setLastName(customerDto.getLastName());
		customer.getName().setFirstName(customerDto.getFirstName());

		return customer;
	}

	public CustomerHierarchyDto getCustomerDTO(Customer customer) {
		CustomerHierarchyDto result = new CustomerHierarchyDto();

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
