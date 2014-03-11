package org.meveo.service.crm.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class CustomerImportService {
	
	@Inject
	TradingCountryService tradingCountryService;

	@Inject
	TradingCurrencyService tradingCurrencyService;

	@Inject
	TradingLanguageService tradingLanguageService;
	
	@Inject
	private SellerService sellerService;
	
	@Inject
	private CustomerService customerService;

	@Inject
	CustomerBrandService customerBrandService;

	@Inject
	CustomerCategoryService customerCategoryService;

	@Inject
	private TitleService titleService;

	@Inject
	private CustomerAccountService customerAccountService;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Customer createCustomer(Provider provider,
			User userJob, org.meveo.model.admin.Seller seller,
			org.meveo.model.jaxb.customer.Seller sell,
			org.meveo.model.jaxb.customer.Customer cust) {
		Customer customer=null;

			if (seller == null) {
				seller = new org.meveo.model.admin.Seller();
				seller.setCode(sell.getCode());
				seller.setDescription(sell.getDescription());
				seller.setTradingCountry(tradingCountryService
						.findByTradingCountryCode(sell.getTradingCountryCode(),
								provider));
				seller.setTradingCurrency(tradingCurrencyService
						.findByTradingCurrencyCode(
								sell.getTradingCurrencyCode(), provider));
				seller.setTradingLanguage(tradingLanguageService
						.findByTradingLanguageCode(
								sell.getTradingLanguageCode(), provider));
				seller.setProvider(provider);
				sellerService.create(seller, userJob);
			}
			if (customer == null) {
				customer = new Customer();
				customer.setCode(cust.getCode());
				customer.setDescription(cust.getDesCustomer());
				customer.setCustomerBrand(customerBrandService.findByCode(cust
						.getCustomerBrand()));
				customer.setCustomerCategory(customerCategoryService
						.findByCode(cust.getCustomerCategory()));
				customer.setSeller(seller);
				customer.setProvider(provider);
				customerService.create(customer, userJob);
			}
			return customer;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createCustomerAccount( Provider provider,
			User userJob, Customer customer,
			org.meveo.model.admin.Seller seller,
			org.meveo.model.jaxb.customer.CustomerAccount custAcc,
			org.meveo.model.jaxb.customer.Customer cust,
			org.meveo.model.jaxb.customer.Seller sell) {
		
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
		customerAccount.setCreditCategory(CreditCategoryEnum.valueOf(custAcc
				.getCreditCategory()));
		customerAccount.setExternalRef1(custAcc.getExternalRef1());
		customerAccount.setExternalRef2(custAcc.getExternalRef2());
		customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(custAcc
				.getPaymentMethod()));
		org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
		if (custAcc.getName() != null) {
			name.setFirstName(custAcc.getName().getFirstname());
			name.setLastName(custAcc.getName().getName());
			Title title = titleService.findByCode(provider, custAcc.getName()
					.getTitle().trim());
			name.setTitle(title);
			customerAccount.setName(name);
		}
		customerAccount.setTradingCurrency(tradingCurrencyService
				.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode(),
						provider));
		customerAccount.setProvider(provider);
		customerAccount.setCustomer(customer);
		customerAccountService.create(customerAccount, userJob);
	}


}
