package org.meveo.service.crm.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.CustomField;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class CustomerImportService {

	@Inject
	private TradingCurrencyService tradingCurrencyService;

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

			if (cust.getCustomFields() != null && cust.getCustomFields().getCustomField() != null
					&& cust.getCustomFields().getCustomField().size() > 0) {
				for (CustomField customField : cust.getCustomFields().getCustomField()) {
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
		address.setAddress1(custAcc.getAddress().getAddress1());
		address.setAddress2(custAcc.getAddress().getAddress2());
		address.setAddress3(custAcc.getAddress().getAddress3());
		address.setCity(custAcc.getAddress().getCity());
		address.setCountry(custAcc.getAddress().getCountry());
		address.setZipCode("" + custAcc.getAddress().getZipCode());
		address.setState(custAcc.getAddress().getState());
		customerAccount.setAddress(address);

		ContactInformation contactInformation = new ContactInformation();
		contactInformation.setEmail(custAcc.getEmail());
		contactInformation.setPhone(custAcc.getTel1());
		contactInformation.setMobile(custAcc.getTel2());
		customerAccount.setContactInformation(contactInformation);
		customerAccount.setCreditCategory(CreditCategoryEnum.valueOf(custAcc.getCreditCategory()));
		customerAccount.setExternalRef1(custAcc.getExternalRef1());
		customerAccount.setExternalRef2(custAcc.getExternalRef2());
		customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(custAcc.getPaymentMethod()));
		org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

		if (custAcc.getName() != null) {
			name.setFirstName(custAcc.getName().getFirstname());
			name.setLastName(custAcc.getName().getName());
			Title title = titleService.findByCode(provider, custAcc.getName().getTitle().trim());
			name.setTitle(title);
			customerAccount.setName(name);
		}

		if (custAcc.getCustomFields() != null && custAcc.getCustomFields().getCustomField() != null
				&& custAcc.getCustomFields().getCustomField().size() > 0) {
			for (CustomField customField : custAcc.getCustomFields().getCustomField()) {
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
		customerAccount.setProvider(provider);
		customerAccount.setCustomer(customer);
		customerAccountService.create(customerAccount, currentUser, provider);
	}

}
