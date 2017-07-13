package org.meveo.service.crm.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.RandomStringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class CustomerImportService extends ImportService {

    @Inject
    private CreditCategoryService creditCategoryService;

    @Inject
    private SellerService sellerService;

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
    public Customer createCustomer(org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust) throws BusinessException {
        
        Customer customer = null;

        if (customer == null) {
            customer = new Customer();
            customer.setCode(cust.getCode());
            customer.setDescription(cust.getDesCustomer());
            customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand()));
            customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory()));
            customer.setSeller(seller);

            org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
            Title title = titleService.findByCode(cust.getName().getTitle());
            name.setTitle(title);
            name.setFirstName(cust.getName().getFirstName());
            name.setLastName(cust.getName().getLastName());
            customer.setName(name);

            customerService.create(customer);
            
            if (cust.getCustomFields() != null) {
                populateCustomFields(cust.getCustomFields().getCustomField(), customer);
            }
        }

        return customer;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createCustomerAccount(Customer customer, org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
            org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell) throws BusinessException {

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
            customerAccount.setCreditCategory(creditCategoryService.findByCode(custAcc.getCreditCategory()));
        }
        customerAccount.setExternalRef1(custAcc.getExternalRef1());
        customerAccount.setExternalRef2(custAcc.getExternalRef2());

        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

        if (custAcc.getName() != null) {
            name.setFirstName(custAcc.getName().getFirstName());
            name.setLastName(custAcc.getName().getLastName());
            if (!StringUtils.isBlank(custAcc.getName().getTitle())) {
                Title title = titleService.findByCode(custAcc.getName().getTitle().trim());
                name.setTitle(title);
            }
            customerAccount.setName(name);
        }

        customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode()));
        customerAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(custAcc.getTradingLanguageCode()));
        customerAccount.setCustomer(customer);
        customerAccountService.create(customerAccount);
        
        if (custAcc.getCustomFields() != null) {
            populateCustomFields(custAcc.getCustomFields().getCustomField(), customerAccount);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Customer updateCustomer(Customer customer, org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.Seller sell,
            org.meveo.model.jaxb.customer.Customer cust) throws BusinessException {
        
        customer.setDescription(cust.getDesCustomer());
        customer.setCustomerBrand(customerBrandService.findByCode(cust.getCustomerBrand()));
        customer.setCustomerCategory(customerCategoryService.findByCode(cust.getCustomerCategory()));
        customer.setSeller(seller);

        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();
        Title title = titleService.findByCode(cust.getName().getTitle());
        name.setTitle(title);
        name.setFirstName(cust.getName().getFirstName());
        name.setLastName(cust.getName().getLastName());
        customer.setName(name);

        customer = customerService.updateNoCheck(customer);

        if (cust.getCustomFields() != null) {
            populateCustomFields(cust.getCustomFields().getCustomField(), customer);
        }
        
        return customer;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCustomerAccount(CustomerAccount customerAccount, Customer customer, Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
            org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell) throws BusinessException {
        
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
            customerAccount.setCreditCategory(creditCategoryService.findByCode(custAcc.getCreditCategory()));
        }
        customerAccount.setExternalRef1(custAcc.getExternalRef1());
        customerAccount.setExternalRef2(custAcc.getExternalRef2());

        org.meveo.model.shared.Name name = new org.meveo.model.shared.Name();

        if (custAcc.getName() != null) {
            name.setFirstName(custAcc.getName().getFirstName());
            name.setLastName(custAcc.getName().getLastName());
            if (!StringUtils.isBlank(custAcc.getName().getTitle())) {
                Title title = titleService.findByCode(custAcc.getName().getTitle().trim());
                name.setTitle(title);
            }
            customerAccount.setName(name);
        }

        customerAccount.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(custAcc.getTradingCurrencyCode()));
        customerAccount.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(custAcc.getTradingLanguageCode()));
        customerAccount.setCustomer(customer);
        customerAccount = customerAccountService.updateNoCheck(customerAccount);
        
        if (custAcc.getCustomFields() != null) {
            populateCustomFields(custAcc.getCustomFields().getCustomField(), customerAccount);
        }
        
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSeller(org.meveo.model.admin.Seller seller) {
        sellerService.updateNoCheck(seller);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createSeller(org.meveo.model.admin.Seller seller) throws BusinessException {
        sellerService.create(seller);
    }
}