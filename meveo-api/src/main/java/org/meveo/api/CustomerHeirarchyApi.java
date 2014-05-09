package org.meveo.api;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomerHeirarchyDto;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CustomerHeirarchyApi extends BaseApi {

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private TradingCountryService countryService;

	@Inject
	private TradingLanguageService languageService;

	@Inject
	private CustomerService customerService;

	public void createCustomerHeirarchy(
			CustomerHeirarchyDto customerHeirarchyDto) throws BusinessException {

		Provider provider = em.find(Provider.class,
				customerHeirarchyDto.getProviderId());
		User currentUser = em.find(User.class,
				customerHeirarchyDto.getCurrentUserId());

		CustomerBrand customerBrand = customerBrandService.findByCode(em,
				customerHeirarchyDto.getCustomerBrandCode());

		CustomerCategory customerCategory = customerCategoryService.findByCode(
				em, customerHeirarchyDto.getCustomerCategoryCode());

		if (customerBrand == null) {
			customerBrand = new CustomerBrand();
			customerBrand.setCode(customerHeirarchyDto.getCustomerBrandCode());
			customerBrandService.create(em, customerBrand, currentUser,
					provider);
		}

		if (customerCategory == null) {
			customerCategory = new CustomerCategory();
			customerCategory.setCode(customerHeirarchyDto
					.getCustomerCategoryCode());

			customerCategoryService.create(em, customerCategory, currentUser,
					provider);
		}

		if (customerHeirarchyDto != null) {
			Customer customer = new Customer();
			customer.setCode(customerHeirarchyDto.getCustomerId());
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);

			Currency currency = currencyService.findByCode(customerHeirarchyDto
					.getCurrencyCode());

			if (currency == null) {
				throw new BusinessException("Invalid currency code "
						+ customerHeirarchyDto.getCurrentUserId());
			}

			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setCode(customerHeirarchyDto.getCustomerId());
			Name name = new Name();
			name.setLastName(customerHeirarchyDto.getLastName());
			customerAccount.setName(name);

			customer.getCustomerAccounts().add(customerAccount);

			BillingAccount billingAccount = new BillingAccount();
			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setCode(customerHeirarchyDto.getCustomerId());

			TradingCountry country = countryService.findByTradingCountryCode(
					em, customerHeirarchyDto.getCountryCode(), provider);

			if (country == null) {
				throw new BusinessException("Invalid country code "
						+ customerHeirarchyDto.getCountryCode());
			}

			TradingLanguage language = languageService
					.findByTradingLanguageCode(em,
							customerHeirarchyDto.getLanguageCode(), provider);

			if (language == null) {
				throw new BusinessException("Invalid language code "
						+ customerHeirarchyDto.getLanguageCode());
			}

			billingAccount.setTradingCountry(country);
			billingAccount.setTradingLanguage(language);

			UserAccount userAccount = new UserAccount();
			userAccount.setCode(customerHeirarchyDto.getCustomerId());

			userAccountService.create(em, userAccount, currentUser, provider);

			// TODO: Billing Cycles needed to be added to billing account

			billingAccount.getUsersAccounts().add(userAccount);

			billingAccountService.create(em, billingAccount, currentUser,
					provider);

			customerAccount.getBillingAccounts().add(billingAccount);

			customerAccountService.create(em, customerAccount, currentUser,
					provider);

			customerService.create(em, customer, currentUser, provider);
		}
	}
}
