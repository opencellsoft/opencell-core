package org.meveo.service.crm.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.CustomField;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
public class CustomerImportService {

	@Inject
	private Logger log;
	
	@Inject
	private CreditCategoryService creditCategoryService;

	@Inject
	private SellerService sellerService;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;
	
	@Inject
	private TradingLanguageService tradingLanguageService;

	@Inject
	private CustomerService customerService;

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private TitleService titleService;

	@Inject
	private CustomerAccountService customerAccountService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Customer createCustomer(User currentUser, org.meveo.model.admin.Seller seller,
			org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust) {
		Provider provider = currentUser.getProvider();
		Customer customer = null;

		if (customer == null) {
			customer = new Customer();
			customer.setCode(cust.getCode());
			customer.setDescription(cust.getDesCustomer());
			customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand(), provider));
			customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory(), provider));
			customer.setSeller(seller);
			customer.setProvider(provider);
			
			org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
			Title title = titleService.findByCode(provider, cust.getName().getTitle());
			name.setTitle(title);
			name.setFirstName(cust.getName().getFirstName());
			name.setLastName(cust.getName().getLastName());
			customer.setName(name);

			if (cust.getCustomFields() != null && cust.getCustomFields().getCustomField() != null
					&& cust.getCustomFields().getCustomField().size() > 0) {
				for (CustomField customField : cust.getCustomFields().getCustomField()) {
					// check if cft exists
					if (customFieldTemplateService.findByCodeAndAccountLevel(customField.getCode(),
							AccountLevelEnum.CUST, provider) == null) {
						log.warn("CustomFieldTemplate with code={} does not exists.", customField.getCode());
						continue;
					}

					CustomFieldInstance cfi = new CustomFieldInstance();
					cfi.setAccount(customer);
					cfi.setActive(true);
					cfi.setCode(customField.getCode());
					cfi.setDateValue(customField.getDateValue());
					cfi.setDescription(customField.getDescription());
					cfi.setDoubleValue(customField.getDoubleValue());
					cfi.setLongValue(customField.getLongValue());
					cfi.setProvider(provider);
					cfi.setStringValue(customField.getStringValue());
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					cfi.setAuditable(auditable);
					customer.getCustomFields().put(cfi.getCode(), cfi);
				}
			}
			customerService.create(customer, currentUser, provider);
		}

		return customer;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createCustomerAccount(User currentUser, Customer customer, org.meveo.model.admin.Seller seller,
			org.meveo.model.jaxb.customer.CustomerAccount custAcc, org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.Seller sell) {

		Provider provider = currentUser.getProvider();

		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setCode(custAcc.getCode());
		customerAccount.setDescription(custAcc.getDescription());
		customerAccount.setDateDunningLevel(new Date());
		customerAccount.setDunningLevel(DunningLevelEnum.R0);
		customerAccount.setPassword(RandomStringUtils.randomAlphabetic(8));
		customerAccount.setDateStatus(new Date());
		customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);

		Address address = new Address();
		if (custAcc.getAddress() != null) {
			address.setAddress1(custAcc.getAddress().getAddress1());
			address.setAddress2(custAcc.getAddress().getAddress2());
			address.setAddress3(custAcc.getAddress().getAddress3());
			address.setCity(custAcc.getAddress().getCity());
			address.setCountry(custAcc.getAddress().getCountry());
			address.setZipCode("" + custAcc.getAddress().getZipCode());
			address.setState(custAcc.getAddress().getState());
			customerAccount.setAddress(address);
		}

		ContactInformation contactInformation = new ContactInformation();
		contactInformation.setEmail(custAcc.getEmail());
		contactInformation.setPhone(custAcc.getTel1());
		contactInformation.setMobile(custAcc.getTel2());
		customerAccount.setContactInformation(contactInformation);
		if (!StringUtils.isBlank(custAcc.getCreditCategory())) {
			customerAccount.setCreditCategory(creditCategoryService.findByCode(custAcc.getCreditCategory(), provider));
		}
		customerAccount.setExternalRef1(custAcc.getExternalRef1());
		customerAccount.setExternalRef2(custAcc.getExternalRef2());
		if (!StringUtils.isBlank(custAcc.getPaymentMethod())) {
			customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(custAcc.getPaymentMethod()));
		}
		org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

		if (custAcc.getName() != null) {
			name.setFirstName(custAcc.getName().getFirstName());
			name.setLastName(custAcc.getName().getLastName());
			if (!StringUtils.isBlank(custAcc.getName().getTitle())) {
				Title title = titleService.findByCode(provider, custAcc.getName().getTitle().trim());
				name.setTitle(title);
			}
			customerAccount.setName(name);
		}

		if (custAcc.getCustomFields() != null && custAcc.getCustomFields().getCustomField() != null
				&& custAcc.getCustomFields().getCustomField().size() > 0) {
			for (CustomField customField : custAcc.getCustomFields().getCustomField()) {
				// check if cft exists
				if (customFieldTemplateService.findByCodeAndAccountLevel(customField.getCode(), AccountLevelEnum.CA,
						provider) == null) {
					log.warn("CustomFieldTemplate with code={} does not exists.", customField.getCode());
					continue;
				}

				CustomFieldInstance cfi = new CustomFieldInstance();
				cfi.setAccount(customerAccount);
				cfi.setActive(true);
				cfi.setCode(customField.getCode());
				cfi.setDateValue(customField.getDateValue());
				cfi.setDescription(customField.getDescription());
				cfi.setDoubleValue(customField.getDoubleValue());
				cfi.setLongValue(customField.getLongValue());
				cfi.setProvider(provider);
				cfi.setStringValue(customField.getStringValue());
				Auditable auditable = new Auditable();
				auditable.setCreated(new Date());
				auditable.setCreator(currentUser);
				cfi.setAuditable(auditable);
				customerAccount.getCustomFields().put(cfi.getCode(), cfi);
			}
		}

		customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(
				custAcc.getTradingCurrencyCode(), provider));
		customerAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(
				custAcc.getTradingLanguageCode(), provider));
		customerAccount.setProvider(provider);
		customerAccount.setCustomer(customer);
		customerAccountService.create(customerAccount, currentUser, provider);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Customer updateCustomer(Customer customer, User currentUser, org.meveo.model.admin.Seller seller,
			org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust) {
		Provider provider = currentUser.getProvider();

		customer.setDescription(cust.getDesCustomer());
		customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand(), provider));
		customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory(), provider));
		customer.setSeller(seller);
		
		org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
		Title title = titleService.findByCode(provider, cust.getName().getTitle());
		name.setTitle(title);
		name.setFirstName(cust.getName().getFirstName());
		name.setLastName(cust.getName().getLastName());			
		customer.setName(name);			

		if (cust.getCustomFields() != null && cust.getCustomFields().getCustomField() != null
				&& cust.getCustomFields().getCustomField().size() > 0) {
			for (CustomField customField : cust.getCustomFields().getCustomField()) {
				CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(customField.getCode(),
						customer,currentUser
						.getProvider());
				if (cfi == null) {
					// check if cft exists
					if (customFieldTemplateService.findByCodeAndAccountLevel(customField.getCode(),
							AccountLevelEnum.CUST, provider) == null) {
						log.warn("CustomFieldTemplate with code={} does not exists.", customField.getCode());
						continue;
					}

					cfi = new CustomFieldInstance();
					cfi.setAccount(customer);
					cfi.setActive(true);
					cfi.setCode(customField.getCode());
					cfi.setDateValue(customField.getDateValue());
					cfi.setDescription(customField.getDescription());
					cfi.setDoubleValue(customField.getDoubleValue());
					cfi.setLongValue(customField.getLongValue());
					cfi.setProvider(provider);
					cfi.setStringValue(customField.getStringValue());
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					cfi.setAuditable(auditable);
					customer.getCustomFields().put(cfi.getCode(), cfi);
				} else {
					cfi.setDateValue(customField.getDateValue());
					cfi.setDescription(customField.getDescription());
					cfi.setDoubleValue(customField.getDoubleValue());
					cfi.setLongValue(customField.getLongValue());
					cfi.setProvider(provider);
					cfi.setStringValue(customField.getStringValue());
					cfi.getAuditable().setUpdated(new Date());
					cfi.getAuditable().setUpdater(currentUser);
				}
			}
		}

		customer.updateAudit(currentUser);
		customerService.updateNoCheck(customer);

		return customer;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateCustomerAccount(CustomerAccount customerAccount, User currentUser, Customer customer,
			Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
			org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell) {
		Provider provider = currentUser.getProvider();

		customerAccount.setDescription(custAcc.getDescription());
		customerAccount.setDateDunningLevel(new Date());
		customerAccount.setDunningLevel(DunningLevelEnum.R0);
		customerAccount.setPassword(RandomStringUtils.randomAlphabetic(8));
		customerAccount.setDateStatus(new Date());
		customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);

		Address address = customerAccount.getAddress();
		if (address == null) {
			address = new Address();
		}
		if (custAcc.getAddress() != null) {
			address.setAddress1(custAcc.getAddress().getAddress1());
			address.setAddress2(custAcc.getAddress().getAddress2());
			address.setAddress3(custAcc.getAddress().getAddress3());
			address.setCity(custAcc.getAddress().getCity());
			address.setCountry(custAcc.getAddress().getCountry());
			address.setZipCode("" + custAcc.getAddress().getZipCode());
			address.setState(custAcc.getAddress().getState());
			customerAccount.setAddress(address);
		}

		ContactInformation contactInformation = customerAccount.getContactInformation();
		if (contactInformation == null) {
			contactInformation = new ContactInformation();
		}
		contactInformation.setEmail(custAcc.getEmail());
		contactInformation.setPhone(custAcc.getTel1());
		contactInformation.setMobile(custAcc.getTel2());
		customerAccount.setContactInformation(contactInformation);
		if (!StringUtils.isBlank(custAcc.getCreditCategory())) {
			customerAccount.setCreditCategory(creditCategoryService.findByCode(custAcc.getCreditCategory(), provider));
		}
		customerAccount.setExternalRef1(custAcc.getExternalRef1());
		customerAccount.setExternalRef2(custAcc.getExternalRef2());
		if (!StringUtils.isBlank(custAcc.getPaymentMethod())) {
			customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(custAcc.getPaymentMethod()));
		}
		org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

		if (custAcc.getName() != null) {
			name.setFirstName(custAcc.getName().getFirstName());
			name.setLastName(custAcc.getName().getLastName());
			if (!StringUtils.isBlank(custAcc.getName().getTitle())) {
				Title title = titleService.findByCode(provider, custAcc.getName().getTitle().trim());
				name.setTitle(title);
			}
			customerAccount.setName(name);
		}

		if (custAcc.getCustomFields() != null && custAcc.getCustomFields().getCustomField() != null
				&& custAcc.getCustomFields().getCustomField().size() > 0) {
			for (CustomField customField : custAcc.getCustomFields().getCustomField()) {
				CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(customField.getCode(),
						customerAccount,currentUser.getProvider());
				if (cfi == null) {
					// check if cft exists
					if (customFieldTemplateService.findByCodeAndAccountLevel(customField.getCode(),
							AccountLevelEnum.CA, provider) == null) {
						log.warn("CustomFieldTemplate with code={} does not exists.", customField.getCode());
						continue;
					}

					cfi = new CustomFieldInstance();
					cfi.setAccount(customerAccount);
					cfi.setActive(true);
					cfi.setCode(customField.getCode());
					cfi.setDateValue(customField.getDateValue());
					cfi.setDescription(customField.getDescription());
					cfi.setDoubleValue(customField.getDoubleValue());
					cfi.setLongValue(customField.getLongValue());
					cfi.setProvider(provider);
					cfi.setStringValue(customField.getStringValue());
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					cfi.setAuditable(auditable);
					customerAccount.getCustomFields().put(cfi.getCode(), cfi);
				} else {
					cfi.setDateValue(customField.getDateValue());
					cfi.setDescription(customField.getDescription());
					cfi.setDoubleValue(customField.getDoubleValue());
					cfi.setLongValue(customField.getLongValue());
					cfi.setProvider(provider);
					cfi.setStringValue(customField.getStringValue());
					cfi.getAuditable().setUpdated(new Date());
					cfi.getAuditable().setUpdater(currentUser);
				}
			}
		}

		customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(
				custAcc.getTradingCurrencyCode(), provider));
		customerAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(
				custAcc.getTradingLanguageCode(), provider));
		customerAccount.setCustomer(customer);
		customerAccount.updateAudit(currentUser);
		customerAccountService.updateNoCheck(customerAccount);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateSeller(org.meveo.model.admin.Seller seller) {
		sellerService.updateNoCheck(seller);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createSeller(org.meveo.model.admin.Seller seller, User currentUser, Provider provider) {
		sellerService.create(seller, currentUser, provider);
	}

}
