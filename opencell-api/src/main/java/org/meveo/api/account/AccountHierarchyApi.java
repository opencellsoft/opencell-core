package org.meveo.api.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.CountryApi;
import org.meveo.api.CurrencyApi;
import org.meveo.api.LanguageApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CRMAccountTypeSearchDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.api.dto.account.ParentEntitiesDto;
import org.meveo.api.dto.account.ParentEntityDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.DDPaymentMethodDto;
import org.meveo.api.dto.payment.OtherPaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.TipPaymentMethodDto;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.CRMAccountHierarchyDtoParser;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.TipPaymentMethod;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.AccountModelScriptService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;

/**
 * 
 * Creates the customer hierarchy including : - Trading Country - Trading Currency - Trading Language - Customer Brand - Customer Category - Seller - Customer - Customer Account -
 * Billing Account - User Account
 * 
 * Required Parameters :customerId, customerCategoryCode, sellerCode ,currencyCode,countryCode,lastname if title provided, languageCode,billingCycleCode
 * 
 */

@SuppressWarnings("deprecation")
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class AccountHierarchyApi extends BaseApi {

    @Inject
    private AccountModelScriptService accountModelScriptService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomerApi customerApi;

    @Inject
    private CustomerAccountApi customerAccountApi;

    @Inject
    private BillingAccountApi billingAccountApi;

    @Inject
    private UserAccountApi userAccountApi;

    @Inject
    private SellerApi sellerApi;

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
    private TradingCountryService tradingCountryService;

    @Inject
    private CountryApi countryApi;

    @Inject
    private LanguageApi languageApi;

    @Inject
    private CurrencyApi currencyApi;

    @Inject
    private SellerService sellerService;

    @Inject
    private CustomerService customerService;

    @Inject
    private SubscriptionApi subscriptionApi;

    @Inject
    private TitleService titleService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private BusinessAccountModelService businessAccountModelService;

    @Inject
    @MeveoParamBean
    private ParamBean paramBean;

    public static final String CUSTOMER_PREFIX = "CUST_";
    public static final String CUSTOMER_ACCOUNT_PREFIX = "CA_";
    public static final String BILLING_ACCOUNT_PREFIX = "BA_";
    public static final String USER_ACCOUNT_PREFIX = "UA_";

    public static final int CUST = 1;
    public static final int CA = 2;
    public static final int BA = 4;
    public static final int UA = 8;

    /**
     * 
     * Creates the customer heirarchy including : - Trading Country - Trading Currency - Trading Language - Customer Brand - Customer Category - Seller - Customer - Customer
     * Account - Billing Account - User Account
     * 
     * Required Parameters :customerId, customerCategoryCode, sellerCode ,currencyCode,countryCode,lastName if title provided,languageCode,billingCycleCode
     * 
     * @throws BusinessException
     */
    public void create(AccountHierarchyDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCustomerId()) && StringUtils.isBlank(postData.getCustomerCode())) {
            missingParameters.add("customerCode");
        }
        if (StringUtils.isBlank(postData.getCustomerCategoryCode())) {
            missingParameters.add("customerCategoryCode");
        }
        if (StringUtils.isBlank(postData.getSellerCode())) {
            missingParameters.add("sellerCode");
        }
        if (StringUtils.isBlank(postData.getCurrencyCode())) {
            missingParameters.add("currencyCode");
        }
        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("countryCode");
        }
        if (!StringUtils.isBlank(postData.getTitleCode()) && StringUtils.isBlank(postData.getLastName())) {
            missingParameters.add("lastName");
        }
        if (StringUtils.isBlank(postData.getBillingCycleCode())) {
            missingParameters.add("billingCycleCode");
        }
        if (StringUtils.isBlank(postData.getLanguageCode())) {
            missingParameters.add("languageCode");
        }
        if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }

        handleMissingParameters();

        String customerCodeOrId = postData.getCustomerCode();
        if (StringUtils.isBlank(customerCodeOrId)) {
            customerCodeOrId = postData.getCustomerId();
        }

        SellerDto sellerDto = null;
        try {
            sellerDto = sellerApi.find(postData.getSellerCode());
        } catch (Exception e) {
            sellerDto = new SellerDto();
            sellerDto.setCode(postData.getSellerCode());
        }
        countryApi.findOrCreate(postData.getCountryCode());
        currencyApi.findOrCreate(postData.getCurrencyCode());
        languageApi.findOrCreate(postData.getLanguageCode());

        sellerDto.setCountryCode(postData.getCountryCode());
        sellerDto.setCurrencyCode(postData.getCurrencyCode());
        sellerDto.setLanguageCode(postData.getLanguageCode());
        sellerApi.createOrUpdate(sellerDto);

        String customerCode = CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            customerCode = customerCodeOrId;
        }

        CustomerDto customerDto = new CustomerDto();
        customerDto.setCode(customerCode);

        customerDto.setSeller(postData.getSellerCode());
        String customerBrandCode = StringUtils.normalizeHierarchyCode(postData.getCustomerBrandCode());
        // CustomerBrand customerBrand = null;
        if (!StringUtils.isBlank(customerBrandCode)) {
            findOrCreateCustomerBrand(customerBrandCode);
            customerDto.setCustomerBrand(customerBrandCode);
        }

        String customerCategoryCode = StringUtils.normalizeHierarchyCode(postData.getCustomerCategoryCode());
        if (!StringUtils.isBlank(customerCategoryCode)) {
            findOrCreateCustomerCategory(customerCategoryCode);
            customerDto.setCustomerCategory(customerCategoryCode);
        }

        String creditCategory = paramBean.getProperty("api.default.customerAccount.creditCategory", "NEWCUSTOMER");

        AddressDto address = customerDto.getAddress();
        address.setAddress1(postData.getAddress1());
        address.setAddress2(postData.getAddress2());
        address.setZipCode(postData.getZipCode());
        address.setCity(postData.getCity());
        address.setCountry(postData.getCountryCode());

        ContactInformationDto contactInformation = customerDto.getContactInformation();
        contactInformation.setEmail(postData.getEmail());
        contactInformation.setPhone(postData.getPhoneNumber());

        NameDto name = customerDto.getName();
        if (!StringUtils.isBlank(postData.getTitleCode()) && !StringUtils.isBlank(titleService.findByCode(postData.getTitleCode()))) {
            name.setTitle(postData.getTitleCode());
        }
        name.setFirstName(postData.getFirstName());
        name.setLastName(postData.getLastName());

        customerApi.create(customerDto);

        CustomerAccountDto customerAccountDto = new CustomerAccountDto();
        String customerAccountCode = CUSTOMER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            customerAccountCode = customerCodeOrId;
        }
        customerAccountDto.setCode(customerAccountCode);
        customerAccountDto.setCustomer(customerCode);
        customerAccountDto.setAddress(address);
        customerAccountDto.setContactInformation(contactInformation);
        customerAccountDto.setName(name);
        customerAccountDto.setCode(customerAccountCode);
        customerAccountDto.setStatus(CustomerAccountStatusEnum.ACTIVE);
        if (!StringUtils.isBlank(creditCategory)) {
            customerAccountDto.setCreditCategory(creditCategory);
        }
        customerAccountDto.setCurrency(postData.getCurrencyCode());
        customerAccountDto.setLanguage(postData.getLanguageCode());
        customerAccountDto.setDateDunningLevel(new Date());
        customerAccountDto.setPaymentMethods(postData.getPaymentMethods());

        customerAccountApi.create(customerAccountDto);

        String billingCycleCode = StringUtils.normalizeHierarchyCode(postData.getBillingCycleCode());

        BillingAccountDto billingAccountDto = new BillingAccountDto();
        billingAccountDto.setName(name);
        billingAccountDto.setEmail(postData.getEmail());
        String billingAccountCode = BILLING_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            billingAccountCode = customerCodeOrId;
        }
        billingAccountDto.setCode(billingAccountCode);
        billingAccountDto.setStatus(AccountStatusEnum.ACTIVE);
        billingAccountDto.setCustomerAccount(customerAccountCode);
        billingAccountDto.setElectronicBilling(Boolean.valueOf(paramBean.getProperty("api.customerHeirarchy.billingAccount.electronicBilling", "true")));
        billingAccountDto.setCountry(postData.getCountryCode());
        billingAccountDto.setLanguage(postData.getLanguageCode());
        billingAccountDto.setBillingCycle(billingCycleCode);
        billingAccountDto.setAddress(address);
        billingAccountDto.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingAccountDto.setDiscountPlan(postData.getDiscountPlan());

        billingAccountApi.create(billingAccountDto);

        String userAccountCode = USER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            userAccountCode = customerCodeOrId;
        }
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setName(name);
        userAccountDto.setStatus(AccountStatusEnum.ACTIVE);
        userAccountDto.setBillingAccount(billingAccountCode);
        userAccountDto.setCode(userAccountCode);
        userAccountDto.setAddress(address);
        userAccountApi.create(userAccountDto);
    }

    /**
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void update(AccountHierarchyDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCustomerId()) && StringUtils.isBlank(postData.getCustomerCode())) {
            missingParameters.add("customerCode");
        }
        if (StringUtils.isBlank(postData.getCustomerCategoryCode())) {
            missingParameters.add("customerCategoryCode");
        }
        if (StringUtils.isBlank(postData.getSellerCode())) {
            missingParameters.add("sellerCode");
        }
        if (StringUtils.isBlank(postData.getCurrencyCode())) {
            missingParameters.add("currencyCode");
        }
        if (StringUtils.isBlank(postData.getCountryCode())) {
            missingParameters.add("countryCode");
        }
        if (!StringUtils.isBlank(postData.getTitleCode()) && StringUtils.isBlank(postData.getLastName())) {
            missingParameters.add("lastName");
        }
        if (StringUtils.isBlank(postData.getBillingCycleCode())) {
            missingParameters.add("billingCycleCode");
        }
        if (StringUtils.isBlank(postData.getLanguageCode())) {
            missingParameters.add("languageCode");
        }
        if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }

        handleMissingParameters();

        String customerCodeOrId = postData.getCustomerCode();
        if (StringUtils.isBlank(customerCodeOrId)) {
            customerCodeOrId = postData.getCustomerId();
        }
        SellerDto sellerDto = null;
        try {
            sellerDto = sellerApi.find(postData.getSellerCode());
        } catch (Exception e) {
            sellerDto = new SellerDto();
            sellerDto.setCode(postData.getSellerCode());
        }

        countryApi.findOrCreate(postData.getCountryCode());
        currencyApi.findOrCreate(postData.getCurrencyCode());
        languageApi.findOrCreate(postData.getLanguageCode());

        sellerDto.setCountryCode(postData.getCountryCode());
        sellerDto.setCurrencyCode(postData.getCurrencyCode());
        sellerDto.setLanguageCode(postData.getLanguageCode());
        sellerApi.createOrUpdate(sellerDto);

        CustomerDto customerDto = null;
        String customerCode = CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            customerCode = customerCodeOrId;
        }
        try {
            customerDto = customerApi.find(customerCode);
        } catch (Exception e) {
            throw new MeveoApiException("Customer " + customerCode + " isn't found");
        }
        customerDto.setSeller(postData.getSellerCode());

        String customerBrandCode = StringUtils.normalizeHierarchyCode(postData.getCustomerBrandCode());
        if (!StringUtils.isBlank(customerBrandCode)) {
            findOrCreateCustomerBrand(customerBrandCode);
            customerDto.setCustomerBrand(customerBrandCode);
        }

        String customerCategoryCode = StringUtils.normalizeHierarchyCode(postData.getCustomerCategoryCode());
        if (!StringUtils.isBlank(customerCategoryCode)) {
            findOrCreateCustomerCategory(customerCategoryCode);
            customerDto.setCustomerCategory(customerCategoryCode);
        }

        
        String creditCategory = paramBean.getProperty("api.default.customerAccount.creditCategory", "NEWCUSTOMER");

        AddressDto address = customerDto.getAddress();
        address.setAddress1(postData.getAddress1());
        address.setAddress2(postData.getAddress2());
        address.setAddress3(postData.getAddress3());
        address.setZipCode(postData.getZipCode());
        address.setCity(postData.getCity());
        address.setCountry(postData.getCountryCode());

        ContactInformationDto contactInformation = customerDto.getContactInformation();
        contactInformation.setEmail(postData.getEmail());
        contactInformation.setPhone(postData.getPhoneNumber());

        NameDto name = customerDto.getName();
        if (!StringUtils.isBlank(postData.getTitleCode()) && !StringUtils.isBlank(titleService.findByCode(postData.getTitleCode()))) {
            name.setTitle(StringUtils.normalizeHierarchyCode(postData.getTitleCode()));
        }
        name.setFirstName(postData.getFirstName());
        name.setLastName(postData.getLastName());

        customerApi.update(customerDto);

        String customerAccountCode = CUSTOMER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            customerAccountCode = customerCodeOrId;
        }

        CustomerAccountDto customerAccountDto = null;
        try {
            customerAccountDto = customerAccountApi.find(customerAccountCode, false);
        } catch (Exception e) {
            customerAccountDto = new CustomerAccountDto();
            customerAccountDto.setCode(customerAccountCode);
            customerAccountDto.setCustomer(customerCode);
        }
        if (!customerCode.equalsIgnoreCase(customerAccountDto.getCustomer())) {
            throw new MeveoApiException("CustomerAccount's customer " + customerAccountDto.getCustomer() + " doesn't match with parent customer " + customerCode);
        }
        customerAccountDto.setAddress(address);
        customerAccountDto.setContactInformation(contactInformation);

        customerAccountDto.setName(name);
        customerAccountDto.setStatus(CustomerAccountStatusEnum.ACTIVE);
        if (!StringUtils.isBlank(creditCategory)) {
            customerAccountDto.setCreditCategory(creditCategory);
        }
        customerAccountDto.setCurrency(postData.getCurrencyCode());
        customerAccountDto.setLanguage(postData.getLanguageCode());
        customerAccountDto.setPaymentMethods(postData.getPaymentMethods());
        
        customerAccountApi.createOrUpdate(customerAccountDto);

        String billingCycleCode = StringUtils.normalizeHierarchyCode(postData.getBillingCycleCode());

        String billingAccountCode = BILLING_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            billingAccountCode = customerCodeOrId;
        }

        BillingAccountDto billingAccountDto = null;
        try {
            billingAccountDto = billingAccountApi.find(billingAccountCode);
        } catch (Exception e) {
            billingAccountDto = new BillingAccountDto();
            billingAccountDto.setCode(billingAccountCode);
            billingAccountDto.setCustomerAccount(customerAccountCode);
        }
        if (!customerAccountCode.equalsIgnoreCase(billingAccountDto.getCustomerAccount())) {
            throw new MeveoApiException(
                "BillingAccount's customerAccount " + billingAccountDto.getCustomerAccount() + " doesn't match with parent customerAccount " + customerAccountCode);
        }

        billingAccountDto.setEmail(postData.getEmail());
        billingAccountDto.setName(name);
        billingAccountDto.setStatus(AccountStatusEnum.ACTIVE);
        billingAccountDto.setElectronicBilling(Boolean.valueOf(paramBean.getProperty("api.customerHeirarchy.billingAccount.electronicBilling", "true")));
        billingAccountDto.setCountry(postData.getCountryCode());
        billingAccountDto.setLanguage(postData.getLanguageCode());
        billingAccountDto.setBillingCycle(billingCycleCode);
        billingAccountDto.setAddress(address);
        billingAccountDto.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingAccountDto.setDiscountPlan(postData.getDiscountPlan());

        billingAccountApi.createOrUpdate(billingAccountDto);

        String userAccountCode = USER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (postData.getUsePrefix() != null && !postData.getUsePrefix()) {
            userAccountCode = customerCodeOrId;
        }

        UserAccountDto userAccountDto = null;
        try {
            userAccountDto = userAccountApi.find(userAccountCode);
        } catch (Exception e) {
            userAccountDto = new UserAccountDto();
            userAccountDto.setCode(userAccountCode);
            userAccountDto.setBillingAccount(billingAccountCode);
        }
        if (!billingAccountCode.equalsIgnoreCase(userAccountDto.getBillingAccount())) {
            throw new MeveoApiException("UserAccount's billingAccount " + userAccountDto.getBillingAccount() + " doesn't match with parent billingAccount " + billingAccountCode);
        }

        userAccountDto.setName(name);
        userAccountDto.setStatus(AccountStatusEnum.ACTIVE);
        userAccountDto.setAddress(address);
        userAccountApi.createOrUpdate(userAccountDto);
    }

    /**
     * 
     * @param postData
     * 
     * @return
     * @throws MeveoApiException
     */
    public CustomersDto find(AccountHierarchyDto postData) throws MeveoApiException {

        CustomersDto result = new CustomersDto();

        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(postData.getIndex(), postData.getLimit(), null, null, null, postData.getSortField(), null);
        QueryBuilder qb = new QueryBuilder(Customer.class, "c", null);

        String customerCodeOrId = postData.getCustomerCode();
        if (StringUtils.isBlank(customerCodeOrId)) {
            customerCodeOrId = postData.getCustomerId();
        }

        if (postData.getUsePrefix() != null && postData.getUsePrefix()) {
            customerCodeOrId = CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        }

        if (!StringUtils.isBlank(customerCodeOrId)) {
            qb.addCriterion("c.code", "=", customerCodeOrId, true);
        }
        if (!StringUtils.isBlank(postData.getSellerCode())) {
            Seller seller = sellerService.findByCode(postData.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSellerCode());
            }
            qb.addCriterionEntity("c.seller", seller);
        }
        if (!StringUtils.isBlank(postData.getCustomerBrandCode())) {
            CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrandCode());
            if (customerBrand == null) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrandCode());
            }
            qb.addCriterionEntity("c.customerBrand", customerBrand);
        }
        if (!StringUtils.isBlank(postData.getCustomerCategoryCode())) {
            CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategoryCode());
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategoryCode());
            }
            qb.addCriterionEntity("c.customerCategory", customerCategory);
        }
        if (!StringUtils.isBlank(postData.getCountryCode())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
            }
            qb.addCriterion("c.address.country", "=", tradingCountry.getPrDescription(), true);
        }
        if (!StringUtils.isBlank(postData.getFirstName())) {
            qb.addCriterion("c.name.firstName", "=", postData.getFirstName(), true);
        }
        if (!StringUtils.isBlank(postData.getLastName())) {
            qb.addCriterion("c.name.lastName", "=", postData.getLastName(), true);
        }
        if (!StringUtils.isBlank(postData.getAddress1())) {
            qb.addCriterion("c.address.address1", "=", postData.getAddress1(), true);
        }
        if (!StringUtils.isBlank(postData.getAddress2())) {
            qb.addCriterion("c.address.address2", "=", postData.getAddress2(), true);
        }
        if (!StringUtils.isBlank(postData.getAddress3())) {
            qb.addCriterion("c.address.address3", "=", postData.getAddress3(), true);
        }
        if (!StringUtils.isBlank(postData.getCity())) {
            qb.addCriterion("c.address.city", "=", postData.getCity(), true);
        }
        if (!StringUtils.isBlank(postData.getState())) {
            qb.addCriterion("c.address.state", "=", postData.getState(), true);
        }
        if (!StringUtils.isBlank(postData.getZipCode())) {
            qb.addCriterion("c.address.zipCode", "=", postData.getZipCode(), true);
        }

        // custom fields
        if (postData.getCustomFields() != null) {
            for (@SuppressWarnings("unused")
            CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                // qb.addCriterion("KEY(c.customFields)", "=", cfDto.getCode(),
                // true); // TODO FIX me - custom fields are no longer tied to
                // entity
            }
        }

        qb.addPaginationConfiguration(paginationConfiguration);
        @SuppressWarnings("unchecked")
        List<Customer> customers = qb.getQuery(customerService.getEntityManager()).getResultList();

        if (customers != null) {
            for (Customer cust : customers) {
                if (postData.getCustomFields() == null || postData.getCustomFields().getCustomField() == null) {
                    result.getCustomer().add(customerToDto(cust));
                } else {
                    for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {

                        if (!cfDto.isEmpty()) {
                            Object cfValue = customFieldInstanceService.getCFValue(cust, cfDto.getCode());
                            if (getValueConverted(cfDto).equals(cfValue)) {
                                result.getCustomer().add(customerToDto(cust));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void customerHierarchyUpdate(CustomerHierarchyDto postData) throws MeveoApiException, BusinessException {
        if (postData.getSellers() == null || postData.getSellers().getSeller().isEmpty()) {
            missingParameters.add("sellers");
            handleMissingParameters();
        }

        for (SellerDto sellerDto : postData.getSellers().getSeller()) {
            if (StringUtils.isBlank(sellerDto.getCode())) {
                missingParameters.add("seller.code");
                handleMissingParameters();
            }

            countryApi.findOrCreate(sellerDto.getCountryCode());
            currencyApi.findOrCreate(sellerDto.getCurrencyCode());
            languageApi.findOrCreate(sellerDto.getLanguageCode());

            sellerApi.createOrUpdate(sellerDto);

            // customers
            if (sellerDto.getCustomers() != null) {
                for (CustomerDto customerDto : sellerDto.getCustomers().getCustomer()) {
                    if (StringUtils.isBlank(customerDto.getCode())) {
                        log.warn("CustomerDto's code is null={}", customerDto);
                        continue;
                    }
                    if (!StringUtils.isBlank(customerDto.getSeller()) && !customerDto.getSeller().equalsIgnoreCase(sellerDto.getCode())) {
                        throw new MeveoApiException("Customer's seller " + customerDto.getSeller() + " doesn't match with parent seller " + sellerDto.getCode());
                    } else {
                        customerDto.setSeller(sellerDto.getCode());
                    }
                    customerApi.createOrUpdatePartial(customerDto);

                    // customerAccounts
                    if (customerDto.getCustomerAccounts() != null) {
                        for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts().getCustomerAccount()) {
                            if (StringUtils.isBlank(customerAccountDto.getCode())) {
                                log.warn("code is null={}", customerAccountDto);
                                continue;
                            }
                            if (!StringUtils.isBlank(customerAccountDto.getCustomer()) && !customerAccountDto.getCustomer().equalsIgnoreCase(customerDto.getCode())) {
                                throw new MeveoApiException(
                                    "CustomerAccount's customer " + customerAccountDto.getCustomer() + " doesn't match with parent Customer " + customerDto.getCode());
                            } else {
                                customerAccountDto.setCustomer(customerDto.getCode());
                            }

                            customerAccountApi.createOrUpdatePartial(customerAccountDto);

                            // billing accounts
                            if (customerAccountDto.getBillingAccounts() != null) {
                                for (BillingAccountDto billingAccountDto : customerAccountDto.getBillingAccounts().getBillingAccount()) {
                                    if (StringUtils.isBlank(billingAccountDto.getCode())) {
                                        log.warn("code is null={}", billingAccountDto);
                                        continue;
                                    }
                                    if (!StringUtils.isBlank(billingAccountDto.getCustomerAccount())
                                            && !billingAccountDto.getCustomerAccount().equalsIgnoreCase(customerAccountDto.getCode())) {
                                        throw new MeveoApiException("BillingAccount's customerAccount " + billingAccountDto.getCustomerAccount()
                                                + " doesn't match with parent customerAccount " + customerAccountDto.getCode());
                                    } else {
                                        billingAccountDto.setCustomerAccount(customerAccountDto.getCode());
                                    }
                                    billingAccountApi.createOrUpdatePartial(billingAccountDto);

                                    // user accounts
                                    if (billingAccountDto.getUserAccounts() != null) {
                                        for (UserAccountDto userAccountDto : billingAccountDto.getUserAccounts().getUserAccount()) {
                                            if (StringUtils.isBlank(userAccountDto.getCode())) {
                                                log.warn("code is null={}", userAccountDto);
                                                continue;
                                            }
                                            if (!StringUtils.isBlank(userAccountDto.getBillingAccount())
                                                    && !userAccountDto.getBillingAccount().equalsIgnoreCase(billingAccountDto.getCode())) {
                                                throw new MeveoApiException("UserAccount's billingAccount " + userAccountDto.getBillingAccount()
                                                        + " doesn't match with parent billingAccount " + billingAccountDto.getCode());
                                            } else {
                                                userAccountDto.setBillingAccount(billingAccountDto.getCode());
                                            }
                                            userAccountApi.createOrUpdatePartial(userAccountDto);

                                            // subscriptions
                                            if (userAccountDto.getSubscriptions() != null) {
                                                for (SubscriptionDto subscriptionDto : userAccountDto.getSubscriptions().getSubscription()) {
                                                    if (StringUtils.isBlank(subscriptionDto.getCode())) {
                                                        log.warn("code is null={}", subscriptionDto);
                                                        throw new MeveoApiException("Subscription's code is null");
                                                    }
                                                    if (!StringUtils.isBlank(subscriptionDto.getUserAccount())
                                                            && !subscriptionDto.getUserAccount().equalsIgnoreCase(userAccountDto.getCode())) {
                                                        throw new MeveoApiException("Subscription's userAccount " + subscriptionDto.getUserAccount()
                                                                + " doesn't match with parent userAccount " + userAccountDto.getCode());
                                                    } else {
                                                        subscriptionDto.setUserAccount(userAccountDto.getCode());
                                                    }
                                                    subscriptionApi.createOrUpdatePartial(subscriptionDto);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * @param postData
     * 
     * @return
     * @throws MeveoApiException
     */
    public GetAccountHierarchyResponseDto findAccountHierarchy2(FindAccountHierachyRequestDto postData) throws MeveoApiException {

        GetAccountHierarchyResponseDto result = new GetAccountHierarchyResponseDto();
        Name name = null;

        if (postData.getName() == null && postData.getAddress() == null) {
            throw new MeveoApiException("At least name or address must not be null.");
        }

        if (postData.getName() != null) {
            name = new Name();
            name.setFirstName(postData.getName().getFirstName());
            name.setLastName(postData.getName().getLastName());
        }

        Address address = null;
        if (postData.getAddress() != null) {
            address = new Address();
            address.setAddress1(postData.getAddress().getAddress1());
            address.setAddress2(postData.getAddress().getAddress2());
            address.setAddress3(postData.getAddress().getAddress3());
            address.setCity(postData.getAddress().getCity());
            address.setCountry(postData.getAddress().getCountry());
            address.setState(postData.getAddress().getState());
            address.setZipCode(postData.getAddress().getZipCode());
        }

        boolean validLevel = false;

        // check each level
        if ((postData.getLevel() & CUST) != 0) {
            validLevel = true;
            List<Customer> customers = customerService.findByNameAndAddress(name, address);
            if (customers != null) {
                for (Customer customer : customers) {
                    result.getCustomers().getCustomer().add(customerToDto(customer));
                }
            }
        }

        if ((postData.getLevel() & CA) != 0) {
            validLevel = true;
            List<CustomerAccount> customerAccounts = customerAccountService.findByNameAndAddress(name, address);
            if (customerAccounts != null) {
                for (CustomerAccount customerAccount : customerAccounts) {
                    addCustomerAccount(result, customerAccount);
                }
            }
        }
        if ((postData.getLevel() & BA) != 0) {
            validLevel = true;
            List<BillingAccount> billingAccounts = billingAccountService.findByNameAndAddress(name, address);
            if (billingAccounts != null) {
                for (BillingAccount billingAccount : billingAccounts) {
                    addBillingAccount(result, billingAccount);
                }
            }
        }
        if ((postData.getLevel() & UA) != 0) {
            validLevel = true;
            List<UserAccount> userAccounts = userAccountService.findByNameAndAddress(name, address);
            if (userAccounts != null) {
                for (UserAccount userAccount : userAccounts) {
                    addUserAccount(result, userAccount);
                }
            }
        }

        if (!validLevel) {
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "INVALID_LEVEL_TYPE");
        }

        return result;
    }

    /**
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = CRMAccountHierarchyDtoParser.class))
    public void createCRMAccountHierarchy(CRMAccountHierarchyDto postData) throws MeveoApiException, BusinessException {

        if (postData.getCrmAccountType() == null) {
            missingParameters.add("crmAccountType");
        }

        handleMissingParameters();

        NameDto name = new NameDto();
        name.setFirstName(postData.getName().getFirstName());
        name.setLastName(postData.getName().getLastName());
        name.setTitle(postData.getName().getTitle());

        AddressDto address = new AddressDto();
        if (postData.getAddress() != null) {
            address.setAddress1(postData.getAddress().getAddress1());
            address.setAddress2(postData.getAddress().getAddress2());
            address.setAddress3(postData.getAddress().getAddress3());
            address.setCity(postData.getAddress().getCity());
            address.setCountry(postData.getAddress().getCountry());
            address.setState(postData.getAddress().getState());
            address.setZipCode(postData.getAddress().getZipCode());
        }

        ContactInformationDto contactInformation = new ContactInformationDto();
        if (postData.getContactInformation() != null) {
            contactInformation.setEmail(postData.getContactInformation().getEmail());
            contactInformation.setFax(postData.getContactInformation().getFax());
            contactInformation.setMobile(postData.getContactInformation().getMobile());
            contactInformation.setPhone(postData.getContactInformation().getPhone());
        }

        String accountType = postData.getCrmAccountType();
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType);
        if (businessAccountModel != null) {
            accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
        } else {
            try {
                accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
            } catch (Exception e) {
                throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
            }
        }

        Seller seller = null;
        AccountEntity accountEntity = null;

        if (accountHierarchyTypeEnum.getHighLevel() == 4) {
            // create seller
            log.debug("create seller");

            if (StringUtils.isBlank(postData.getSeller())) {
                postData.setSeller(postData.getCode());
            }

            SellerDto sellerDto = new SellerDto();
            sellerDto.setCode(postData.getSeller());
            sellerDto.setDescription(postData.getDescription());
            sellerDto.setCountryCode(postData.getCountry());
            sellerDto.setCurrencyCode(postData.getCurrency());
            sellerDto.setLanguageCode(postData.getLanguage());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(Seller.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                sellerDto.setCustomFields(cfsDto);
            }

            seller = sellerApi.create(sellerDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
            // create customer
            log.debug("create cust");

            CustomerDto customerDto = new CustomerDto();
            customerDto.setCode(postData.getCode());
            customerDto.setDescription(postData.getDescription());
            customerDto.setCustomerCategory(postData.getCustomerCategory());
            customerDto.setCustomerBrand(postData.getCustomerBrand());
            if (accountHierarchyTypeEnum.getHighLevel() == 3) {
                customerDto.setSeller(postData.getCrmParentCode());
            } else {
                customerDto.setSeller(postData.getCode());
            }
            customerDto.setMandateDate(postData.getMandateDate());
            customerDto.setMandateIdentification(postData.getMandateIdentification());
            customerDto.setName(name);
            customerDto.setAddress(address);
            customerDto.setContactInformation(contactInformation);
            customerDto.setExternalRef1(postData.getExternalRef1());
            customerDto.setExternalRef2(postData.getExternalRef2());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(Customer.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                customerDto.setCustomFields(cfsDto);
            }

            accountEntity = customerApi.create(customerDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
            // create customer account
            log.debug("create ca");

            CustomerAccountDto customerAccountDto = new CustomerAccountDto();
            customerAccountDto.setCode(postData.getCode());
            customerAccountDto.setDescription(postData.getDescription());
            if (accountHierarchyTypeEnum.getHighLevel() == 2) {
                customerAccountDto.setCustomer(postData.getCrmParentCode());
            } else {
                customerAccountDto.setCustomer(postData.getCode());
            }
            customerAccountDto.setCurrency(postData.getCurrency());
            customerAccountDto.setLanguage(postData.getLanguage());
            customerAccountDto.setStatus(postData.getCaStatus());
            customerAccountDto.setCreditCategory(postData.getCreditCategory());
            customerAccountDto.setDateStatus(postData.getDateStatus());
            customerAccountDto.setDateDunningLevel(postData.getDateDunningLevel());
            customerAccountDto.setContactInformation(contactInformation);
            customerAccountDto.setDunningLevel(postData.getDunningLevel());
            customerAccountDto.setMandateDate(postData.getMandateDate());
            customerAccountDto.setMandateIdentification(postData.getMandateIdentification());
            customerAccountDto.setName(name);
            customerAccountDto.setAddress(address);
            customerAccountDto.setContactInformation(contactInformation);
            customerAccountDto.setExternalRef1(postData.getExternalRef1());
            customerAccountDto.setExternalRef2(postData.getExternalRef2());
            customerAccountDto.setPaymentMethods(postData.getPaymentMethods());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(CustomerAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                customerAccountDto.setCustomFields(cfsDto);
            }

            accountEntity = customerAccountApi.create(customerAccountDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
            // create billing account
            log.debug("create ba");

            BillingAccountDto billingAccountDto = new BillingAccountDto();
            billingAccountDto.setCode(postData.getCode());
            billingAccountDto.setDescription(postData.getDescription());
            if (accountHierarchyTypeEnum.getHighLevel() == 1) {
                billingAccountDto.setCustomerAccount(postData.getCrmParentCode());
            } else {
                billingAccountDto.setCustomerAccount(postData.getCode());
            }
            billingAccountDto.setBillingCycle(postData.getBillingCycle());
            billingAccountDto.setCountry(postData.getCountry());
            billingAccountDto.setLanguage(postData.getLanguage());
            billingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
            billingAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
            billingAccountDto.setElectronicBilling(postData.getElectronicBilling());
            billingAccountDto.setStatus(postData.getBaStatus());
            billingAccountDto.setTerminationReason(postData.getTerminationReason());
            billingAccountDto.setEmail(postData.getEmail());
            billingAccountDto.setInvoicingThreshold(postData.getInvoicingThreshold());
            billingAccountDto.setDiscountPlan(postData.getDiscountPlan());
            billingAccountDto.setName(name);
            billingAccountDto.setAddress(address);
            billingAccountDto.setExternalRef1(postData.getExternalRef1());
            billingAccountDto.setExternalRef2(postData.getExternalRef2());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(BillingAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                billingAccountDto.setCustomFields(cfsDto);
            }

            accountEntity = billingAccountApi.create(billingAccountDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 0 && accountHierarchyTypeEnum.getLowLevel() <= 0) {
            // create user account
            log.debug("create ua");

            UserAccountDto userAccountDto = new UserAccountDto();
            userAccountDto.setCode(postData.getCode());
            userAccountDto.setDescription(postData.getDescription());
            if (accountHierarchyTypeEnum.getHighLevel() == 0) {
                userAccountDto.setBillingAccount(postData.getCrmParentCode());
            } else {
                userAccountDto.setBillingAccount(postData.getCode());
            }
            userAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
            userAccountDto.setTerminationReason(postData.getTerminationReason());
            userAccountDto.setStatus(postData.getUaStatus());
            userAccountDto.setName(name);
            userAccountDto.setAddress(address);
            userAccountDto.setExternalRef1(postData.getExternalRef1());
            userAccountDto.setExternalRef2(postData.getExternalRef2());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(UserAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                userAccountDto.setCustomFields(cfsDto);
            }

            accountEntity = userAccountApi.create(userAccountDto, true, businessAccountModel);
        }

        if (businessAccountModel != null && businessAccountModel.getScript() != null) {
            try {
                accountModelScriptService.createAccount(businessAccountModel.getScript().getCode(), seller, accountEntity, postData);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
            }
        }
    }

    /**
     * 
     * @param postData
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = CRMAccountHierarchyDtoParser.class))
    public void updateCRMAccountHierarchy(CRMAccountHierarchyDto postData) throws MeveoApiException, BusinessException {

        if (postData.getCrmAccountType() == null) {
            missingParameters.add("crmAccountType");
        }

        handleMissingParameters();

        NameDto name = new NameDto();
        name.setFirstName(postData.getName().getFirstName());
        name.setLastName(postData.getName().getLastName());
        name.setTitle(postData.getName().getTitle());

        AddressDto address = new AddressDto();
        if (postData.getAddress() != null) {
            address.setAddress1(postData.getAddress().getAddress1());
            address.setAddress2(postData.getAddress().getAddress2());
            address.setAddress3(postData.getAddress().getAddress3());
            address.setCity(postData.getAddress().getCity());
            address.setCountry(postData.getAddress().getCountry());
            address.setState(postData.getAddress().getState());
            address.setZipCode(postData.getAddress().getZipCode());
        }

        ContactInformationDto contactInformation = new ContactInformationDto();
        if (postData.getContactInformation() != null) {
            contactInformation.setEmail(postData.getContactInformation().getEmail());
            contactInformation.setFax(postData.getContactInformation().getFax());
            contactInformation.setMobile(postData.getContactInformation().getMobile());
            contactInformation.setPhone(postData.getContactInformation().getPhone());
        }

        String accountType = postData.getCrmAccountType();
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType);
        if (businessAccountModel != null) {
            accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
        } else {
            try {
                accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
            } catch (Exception e) {
                throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
            }
        }

        Seller seller = null;
        AccountEntity accountEntity = null;

        if (accountHierarchyTypeEnum.getHighLevel() == 4) {
            // update seller
            log.debug("update seller");

            if (StringUtils.isBlank(postData.getSeller())) {
                postData.setSeller(postData.getCode());
            }

            SellerDto sellerDto = new SellerDto();
            sellerDto.setCode(postData.getSeller());
            sellerDto.setDescription(postData.getDescription());
            sellerDto.setCountryCode(postData.getCountry());
            sellerDto.setCurrencyCode(postData.getCurrency());
            sellerDto.setLanguageCode(postData.getLanguage());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(Seller.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                sellerDto.setCustomFields(cfsDto);
            }

            seller = sellerApi.update(sellerDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
            // update customer
            log.debug("update c");

            CustomerDto customerDto = new CustomerDto();
            customerDto.setCode(postData.getCode());
            customerDto.setDescription(postData.getDescription());
            customerDto.setCustomerCategory(postData.getCustomerCategory());
            customerDto.setCustomerBrand(postData.getCustomerBrand());
            if (accountHierarchyTypeEnum.getHighLevel() == 3) {
                customerDto.setSeller(postData.getCrmParentCode());
            } else {
                customerDto.setSeller(postData.getCode());
            }
            customerDto.setMandateDate(postData.getMandateDate());
            customerDto.setMandateIdentification(postData.getMandateIdentification());
            customerDto.setName(name);
            customerDto.setAddress(address);
            customerDto.setContactInformation(contactInformation);
            customerDto.setExternalRef1(postData.getExternalRef1());
            customerDto.setExternalRef2(postData.getExternalRef2());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(Customer.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                customerDto.setCustomFields(cfsDto);
            }

            accountEntity = customerApi.update(customerDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
            // update customer account
            log.debug("update ca");

            CustomerAccountDto customerAccountDto = new CustomerAccountDto();
            customerAccountDto.setCode(postData.getCode());
            customerAccountDto.setDescription(postData.getDescription());
            if (accountHierarchyTypeEnum.getHighLevel() == 2) {
                customerAccountDto.setCustomer(postData.getCrmParentCode());
            } else {
                customerAccountDto.setCustomer(postData.getCode());
            }
            customerAccountDto.setCurrency(postData.getCurrency());
            customerAccountDto.setLanguage(postData.getLanguage());
            customerAccountDto.setStatus(postData.getCaStatus());
            customerAccountDto.setCreditCategory(postData.getCreditCategory());
            customerAccountDto.setDateStatus(postData.getDateStatus());
            customerAccountDto.setDateDunningLevel(postData.getDateDunningLevel());
            customerAccountDto.setContactInformation(contactInformation);
            customerAccountDto.setDunningLevel(postData.getDunningLevel());
            customerAccountDto.setMandateDate(postData.getMandateDate());
            customerAccountDto.setMandateIdentification(postData.getMandateIdentification());
            customerAccountDto.setName(name);
            customerAccountDto.setAddress(address);
            customerAccountDto.setContactInformation(contactInformation);
            customerAccountDto.setExternalRef1(postData.getExternalRef1());
            customerAccountDto.setExternalRef2(postData.getExternalRef2());
            customerAccountDto.setPaymentMethods(postData.getPaymentMethods());
            
            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(CustomerAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                customerAccountDto.setCustomFields(cfsDto);
            }

            accountEntity = customerAccountApi.update(customerAccountDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
            // update billing account
            log.debug("update ba");

            BillingAccountDto billingAccountDto = new BillingAccountDto();
            billingAccountDto.setCode(postData.getCode());
            billingAccountDto.setDescription(postData.getDescription());
            if (accountHierarchyTypeEnum.getHighLevel() == 1) {
                billingAccountDto.setCustomerAccount(postData.getCrmParentCode());
            } else {
                billingAccountDto.setCustomerAccount(postData.getCode());
            }
            billingAccountDto.setBillingCycle(postData.getBillingCycle());
            billingAccountDto.setCountry(postData.getCountry());
            billingAccountDto.setLanguage(postData.getLanguage());
            billingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
            billingAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
            billingAccountDto.setElectronicBilling(postData.getElectronicBilling());
            billingAccountDto.setStatus(postData.getBaStatus());
            billingAccountDto.setTerminationReason(postData.getTerminationReason());
            billingAccountDto.setEmail(postData.getEmail());
            billingAccountDto.setInvoicingThreshold(postData.getInvoicingThreshold());
            billingAccountDto.setDiscountPlan(postData.getDiscountPlan());
            billingAccountDto.setName(name);
            billingAccountDto.setAddress(address);
            billingAccountDto.setExternalRef1(postData.getExternalRef1());
            billingAccountDto.setExternalRef2(postData.getExternalRef2());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(BillingAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                billingAccountDto.setCustomFields(cfsDto);
            }

            accountEntity = billingAccountApi.update(billingAccountDto, true, businessAccountModel);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 0 && accountHierarchyTypeEnum.getLowLevel() <= 0) {
            // update user account
            log.debug("update ua");

            UserAccountDto userAccountDto = new UserAccountDto();
            userAccountDto.setCode(postData.getCode());
            userAccountDto.setDescription(postData.getDescription());
            if (accountHierarchyTypeEnum.getHighLevel() == 0) {
                userAccountDto.setBillingAccount(postData.getCrmParentCode());
            } else {
                userAccountDto.setBillingAccount(postData.getCode());
            }
            userAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
            userAccountDto.setTerminationReason(postData.getTerminationReason());
            userAccountDto.setStatus(postData.getUaStatus());
            userAccountDto.setName(name);
            userAccountDto.setAddress(address);
            userAccountDto.setExternalRef1(postData.getExternalRef1());
            userAccountDto.setExternalRef2(postData.getExternalRef2());

            CustomFieldsDto cfsDto = new CustomFieldsDto();
            if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
                Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(UserAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix());
                for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
                    if (cfts.containsKey(cfDto.getCode())) {
                        cfsDto.getCustomField().add(cfDto);
                    }
                }

                userAccountDto.setCustomFields(cfsDto);
            }

            accountEntity = userAccountApi.update(userAccountDto, true, businessAccountModel);
        }

        if (businessAccountModel != null && businessAccountModel.getScript() != null) {
            try {
                accountModelScriptService.updateAccount(businessAccountModel.getScript().getCode(), seller, accountEntity, postData);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
            }
        }
    }

    /**
     * Create or update Account Hierarchy based on code.
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdate(AccountHierarchyDto postData) throws MeveoApiException, BusinessException {
        String customerCodeOrId = postData.getCustomerCode();
        if (StringUtils.isBlank(customerCodeOrId)) {
            customerCodeOrId = postData.getCustomerId();
        }
        if (StringUtils.isBlank(customerCodeOrId)) {
            missingParameters.add("customerCode");
            handleMissingParameters();
        }

        String customerCode = CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
        if (customerService.findByCode(customerCode) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = CRMAccountHierarchyDtoParser.class))
    public void createOrUpdateCRMAccountHierarchy(CRMAccountHierarchyDto postData) throws MeveoApiException, BusinessException {

        if (postData.getCrmAccountType() == null) {
            missingParameters.add("crmAccountType");
        }

        handleMissingParameters();

        String accountType = postData.getCrmAccountType();
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType);
        if (businessAccountModel != null) {
            accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
        } else {
            try {
                accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
            } catch (Exception e) {
                throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
            }
        }

        boolean accountExist = false;

        if (accountHierarchyTypeEnum.getHighLevel() == 4) {
            Seller seller = sellerService.findByCode(postData.getCode());
            if (seller != null) {
                accountExist = true;
            }
        } else if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
            Customer customer = customerService.findByCode(postData.getCode());
            if (customer != null) {
                accountExist = true;
            }
        } else if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
            CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode());
            if (customerAccount != null) {
                accountExist = true;
            }
        } else if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
            BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode());
            if (billingAccount != null) {
                accountExist = true;
            }
        } else {
            UserAccount userAccount = userAccountService.findByCode(postData.getCode());
            if (userAccount != null) {
                accountExist = true;
            }
        }

        if (accountExist) {
            updateCRMAccountHierarchy(postData);
        } else {
            createCRMAccountHierarchy(postData);
        }
    }

    public void populateNameAddress(AccountDto accountEntity, AccountDto accountDto) throws MeveoApiException {

        if (!StringUtils.isBlank(accountDto.getDescription())) {
            accountEntity.setDescription(accountDto.getDescription());
        }
        if (!StringUtils.isBlank(accountDto.getExternalRef1())) {
            accountEntity.setExternalRef1(accountDto.getExternalRef1());
        }
        if (!StringUtils.isBlank(accountDto.getExternalRef2())) {
            accountEntity.setExternalRef2(accountDto.getExternalRef2());
        }

        if (accountDto.getName() != null) {
            if (!StringUtils.isBlank(accountDto.getName().getFirstName())) {
                accountEntity.getName().setFirstName(accountDto.getName().getFirstName());
            }
            if (!StringUtils.isBlank(accountDto.getName().getLastName())) {
                accountEntity.getName().setLastName(accountDto.getName().getLastName());
            }
            if (!StringUtils.isBlank(accountDto.getName().getTitle())) {
                Title title = titleService.findByCode(accountDto.getName().getTitle());
                if (title != null) {
                    // accountEntity.getName().setTitle(title);
                    accountEntity.getName().setTitle(accountDto.getName().getTitle());
                }
            }
        }

        if (accountDto.getAddress() != null) {
            if (!StringUtils.isBlank(accountDto.getAddress().getAddress1())) {
                accountEntity.getAddress().setAddress1(accountDto.getAddress().getAddress1());
            }
            if (!StringUtils.isBlank(accountDto.getAddress().getAddress2())) {
                accountEntity.getAddress().setAddress2(accountDto.getAddress().getAddress2());
            }
            if (!StringUtils.isBlank(accountDto.getAddress().getAddress3())) {
                accountEntity.getAddress().setAddress3(accountDto.getAddress().getAddress3());
            }
            if (!StringUtils.isBlank(accountDto.getAddress().getZipCode())) {
                accountEntity.getAddress().setZipCode(accountDto.getAddress().getZipCode());
            }
            if (!StringUtils.isBlank(accountDto.getAddress().getCity())) {
                accountEntity.getAddress().setCity(accountDto.getAddress().getCity());
            }
            if (!StringUtils.isBlank(accountDto.getAddress().getState())) {
                accountEntity.getAddress().setState(accountDto.getAddress().getState());
            }
            if (!StringUtils.isBlank(accountDto.getAddress().getCountry())) {
                accountEntity.getAddress().setCountry(accountDto.getAddress().getCountry());
            }
        }

    }

    private void addUserAccount(GetAccountHierarchyResponseDto result, UserAccount userAccount) {
        BillingAccount billingAccount = userAccount.getBillingAccount();

        addBillingAccount(result, billingAccount);

        for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
            for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts().getCustomerAccount()) {
                if (customerAccountDto.getBillingAccounts() != null) {
                    for (BillingAccountDto billingAccountDto : customerAccountDto.getBillingAccounts().getBillingAccount()) {
                        if (billingAccountDto.getCode().equals(billingAccount.getCode())) {
                            if (billingAccountDto.getUserAccounts() != null && billingAccountDto.getUserAccounts().getUserAccount().size() > 0) {
                                UserAccountDto userAccountDto = userAccountToDto(userAccount);
                                if (!billingAccountDto.getUserAccounts().getUserAccount().contains(userAccountDto)) {
                                    billingAccountDto.getUserAccounts().getUserAccount().add(userAccountDto);
                                }
                            } else {
                                billingAccountDto.getUserAccounts().getUserAccount().add(userAccountToDto(userAccount));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addBillingAccount(GetAccountHierarchyResponseDto result, BillingAccount billingAccount) {
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        Customer customer = customerAccount.getCustomer();

        addCustomer(result, customer);
        addCustomerAccount(result, customerAccount);

        for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
            for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts().getCustomerAccount()) {
                if (customerAccountDto.getCode().equals(customerAccount.getCode())) {
                    if (customerAccountDto.getBillingAccounts() != null && customerAccountDto.getBillingAccounts().getBillingAccount().size() > 0) {
                        BillingAccountDto billingAccountDto = billingAccountToDto(billingAccount);
                        if (!customerAccountDto.getBillingAccounts().getBillingAccount().contains(billingAccountDto)) {
                            customerAccountDto.getBillingAccounts().getBillingAccount().add(billingAccountDto);
                        }
                    } else {
                        customerAccountDto.getBillingAccounts().getBillingAccount().add(billingAccountToDto(billingAccount));
                    }
                }
            }
        }
    }

    private void addCustomerAccount(GetAccountHierarchyResponseDto result, CustomerAccount customerAccount) {
        Customer customer = customerAccount.getCustomer();
        CustomerAccountDto customerAccountDto = customerAccountToDto(customerAccount);

        if (result.getCustomers() == null || result.getCustomers().getCustomer().size() == 0) {
            CustomerDto customerDto = customerToDto(customer);
            customerDto.getCustomerAccounts().getCustomerAccount().add(customerAccountDto);
            result.getCustomers().getCustomer().add(customerDto);
        } else {
            for (CustomerDto customerDtoLoop : result.getCustomers().getCustomer()) {
                if (customerDtoLoop.getCode().equals(customer.getCode())) {
                    if (!customerDtoLoop.getCustomerAccounts().getCustomerAccount().contains(customerAccountDto)) {
                        customerDtoLoop.getCustomerAccounts().getCustomerAccount().add(customerAccountDto);
                    }
                }
            }
        }
    }

    private void addCustomer(GetAccountHierarchyResponseDto result, Customer customer) {
        if (result.getCustomers() == null || result.getCustomers().getCustomer().size() == 0) {
            result.getCustomers().getCustomer().add(customerToDto(customer));
        } else {
            boolean found = false;
            for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
                if (customerDto.getCode().equals(customer.getCode())) {
                    if (!customerDto.isLoaded()) {
                        customerDto.initFromEntity(customer, entityToDtoConverter.getCustomFieldsWithInheritedDTO(customer, true));
                    }

                    found = true;
                    break;
                }
            }

            if (!found) {
                result.getCustomers().getCustomer().add(customerToDto(customer));
            }
        }
    }

    public void accountEntityToDto(AccountDto dto, AccountEntity account) {
        dto.setCode(account.getCode());
        dto.setDescription(account.getDescription());
        dto.setExternalRef1(account.getExternalRef1());
        dto.setExternalRef2(account.getExternalRef2());
        dto.setName(new NameDto(account.getName()));
        dto.setAddress(new AddressDto(account.getAddress()));

        BusinessAccountModel businessAccountModel = account.getBusinessAccountModel();

        if (businessAccountModel != null) {
            dto.setBusinessAccountModel(new BusinessEntityDto(businessAccountModel));
        }

        dto.setCustomFields(entityToDtoConverter.getCustomFieldsWithInheritedDTO(account, true));
    }

    public CustomerDto customerToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        accountEntityToDto(dto, customer);

        if (customer.getCustomerCategory() != null) {
            dto.setCustomerCategory(customer.getCustomerCategory().getCode());
        }

        if (customer.getCustomerBrand() != null) {
            dto.setCustomerBrand(customer.getCustomerBrand().getCode());
        }

        if (customer.getSeller() != null) {
            dto.setSeller(customer.getSeller().getCode());
        }

        if (customer.getContactInformation() != null) {
            dto.setContactInformation(new ContactInformationDto(customer.getContactInformation()));
        }

        if (!dto.isLoaded() && customer.getCustomerAccounts() != null) {
            dto.setCustomerAccounts(new CustomerAccountsDto());

            for (CustomerAccount ca : customer.getCustomerAccounts()) {
                dto.getCustomerAccounts().getCustomerAccount().add(customerAccountToDto(ca));
            }
        }

        dto.setLoaded(true);
        return dto;
    }

    public CustomerAccountDto customerAccountToDto(CustomerAccount ca) {
        CustomerAccountDto dto = new CustomerAccountDto();
        accountEntityToDto(dto, ca);

        if (ca.getCustomer() != null) {
            dto.setCustomer(ca.getCustomer().getCode());
        }

        if (ca.getTradingCurrency() != null) {
            dto.setCurrency(ca.getTradingCurrency().getCurrencyCode());
        }

        if (ca.getTradingLanguage() != null) {
            dto.setLanguage(ca.getTradingLanguage().getLanguageCode());
        }

        dto.setStatus(ca.getStatus());
        dto.setDateStatus(ca.getDateStatus());
        try {
            dto.setCreditCategory(ca.getCreditCategory().getCode());
        } catch (NullPointerException ex) {
        }
        dto.setDunningLevel(ca.getDunningLevel());
        dto.setDateStatus(ca.getDateStatus());
        dto.setDateDunningLevel(ca.getDateDunningLevel());
        if (ca.getContactInformation() != null) {
            dto.setContactInformation(new ContactInformationDto(ca.getContactInformation()));
        }

        dto.setMandateIdentification(ca.getMandateIdentification());
        dto.setMandateDate(ca.getMandateDate());
        dto.setDueDateDelayEL(ca.getDueDateDelayEL());

        if (!dto.isLoaded() && ca.getBillingAccounts() != null) {
            dto.setBillingAccounts(new BillingAccountsDto());

            for (BillingAccount ba : ca.getBillingAccounts()) {
                dto.getBillingAccounts().getBillingAccount().add(billingAccountToDto(ba));
            }
        }

        if (ca.getPaymentMethods() != null && !ca.getPaymentMethods().isEmpty()) {
            dto.setPaymentMethods(new ArrayList<>());
            PaymentMethodDto pmDto = null;
            for (PaymentMethod paymentMethod : ca.getPaymentMethods()) {
                switch (paymentMethod.getPaymentType()) {
                case CARD:
                    pmDto = new CardPaymentMethodDto((CardPaymentMethod) paymentMethod);
                    break;
                case DIRECTDEBIT:
                    pmDto = new DDPaymentMethodDto((DDPaymentMethod) paymentMethod);
                    break;
                case TIP:
                    pmDto = new TipPaymentMethodDto((TipPaymentMethod) paymentMethod);
                    break;
                case CHECK:
                case WIRETRANSFER:
                    pmDto = new OtherPaymentMethodDto(paymentMethod);
                    break;

                }
                dto.getPaymentMethods().add(pmDto);
            }

        }

        dto.setLoaded(true);
        return dto;
    }

    public BillingAccountDto billingAccountToDto(BillingAccount ba) {

        BillingAccountDto dto = new BillingAccountDto();
        accountEntityToDto(dto, ba);

        if (ba.getCustomerAccount() != null) {
            dto.setCustomerAccount(ba.getCustomerAccount().getCode());
        }
        BillingCycle billingCycle = ba.getBillingCycle();
        if (billingCycle != null) {
            dto.setBillingCycle(billingCycle.getCode());
            dto.setInvoicingThreshold(billingCycle.getInvoicingThreshold());
        }
        if (ba.getTradingCountry() != null) {
            dto.setCountry(ba.getTradingCountry().getCountryCode());
        }
        if (ba.getTradingLanguage() != null) {
            dto.setLanguage(ba.getTradingLanguage().getLanguageCode());
        }
        dto.setNextInvoiceDate(ba.getNextInvoiceDate());
        dto.setSubscriptionDate(ba.getSubscriptionDate());
        dto.setTerminationDate(ba.getTerminationDate());
        dto.setElectronicBilling(ba.getElectronicBilling());
        dto.setStatus(ba.getStatus());
        dto.setStatusDate(ba.getStatusDate());
        if (ba.getTerminationReason() != null) {
            dto.setTerminationReason(ba.getTerminationReason().getCode());
        }
        dto.setEmail(ba.getEmail());

        if (!dto.isLoaded() && ba.getUsersAccounts() != null) {
            for (UserAccount userAccount : ba.getUsersAccounts()) {
                dto.getUserAccounts().getUserAccount().add(userAccountToDto(userAccount));
            }
        }

        dto.setLoaded(true);

        if (ba.getDiscountPlan() != null) {
            dto.setDiscountPlan(ba.getDiscountPlan().getCode());
        }

        return dto;

    }

    public UserAccountDto userAccountToDto(UserAccount ua) {

        UserAccountDto dto = new UserAccountDto();
        accountEntityToDto(dto, ua);

        if (ua.getBillingAccount() != null) {
            dto.setBillingAccount(ua.getBillingAccount().getCode());
        }

        dto.setSubscriptionDate(ua.getSubscriptionDate());
        dto.setTerminationDate(ua.getTerminationDate());
        dto.setStatus(ua.getStatus());
        dto.setStatusDate(ua.getStatusDate());
        dto.setLoaded(true);

        return dto;
    }

    public void terminateCRMAccountHierarchy(CRMAccountHierarchyDto postData) throws MeveoApiException, BusinessException {
        String accountType = postData.getCrmAccountType();
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType);
        if (businessAccountModel != null) {
            accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
        } else {
            try {
                accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
            } catch (Exception e) {
                throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
            }
        }

        AccountEntity accountEntity1 = null;
        AccountEntity accountEntity2 = null;
        if (accountHierarchyTypeEnum.getHighLevel() >= 0 && accountHierarchyTypeEnum.getLowLevel() <= 0) {
            UserAccountDto userAccountDto = new UserAccountDto();
            userAccountDto.setCode(postData.getCode());
            userAccountDto.setTerminationDate(postData.getTerminationDate());
            userAccountDto.setTerminationReason(postData.getTerminationReason());
            accountEntity1 = userAccountApi.terminate(userAccountDto);
        }

        if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
            // terminate ba
            BillingAccountDto billingAccountDto = new BillingAccountDto();
            billingAccountDto.setCode(postData.getCode());
            billingAccountDto.setTerminationDate(postData.getTerminationDate());
            billingAccountDto.setTerminationReason(postData.getTerminationReason());
            accountEntity2 = billingAccountApi.terminate(billingAccountDto);
        }

        if (businessAccountModel != null && businessAccountModel.getScript() != null) {
            try {
                accountModelScriptService.terminateAccount(businessAccountModel.getScript().getCode(), null, (accountEntity1 != null ? accountEntity1 : accountEntity2), postData);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
            }
        }
    }

    public void closeCRMAccountHierarchy(CRMAccountHierarchyDto postData) throws MeveoApiException, BusinessException {
        String accountType = postData.getCrmAccountType();
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType);
        if (businessAccountModel != null) {
            accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
        } else {
            try {
                accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
            } catch (Exception e) {
                throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
            }
        }

        CustomerAccount customerAccount = null;
        if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
            // close customer account
            CustomerAccountDto customerAccountDto = new CustomerAccountDto();
            customerAccountDto.setCode(postData.getCode());
            customerAccount = customerAccountApi.closeAccount(customerAccountDto);
        }

        if (businessAccountModel != null && businessAccountModel.getScript() != null && customerAccount != null) {
            try {
                accountModelScriptService.closeAccount(businessAccountModel.getScript().getCode(), null, customerAccount, postData);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
            }
        }
    }

    public ParentEntitiesDto getParentList(CRMAccountTypeSearchDto postData) throws MeveoApiException, BusinessException {
        String accountType = postData.getAccountTypeCode();
        AccountHierarchyTypeEnum hierarchyType = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType);
        if (businessAccountModel != null) {
            hierarchyType = businessAccountModel.getHierarchyType();
        } else {
            try {
                hierarchyType = AccountHierarchyTypeEnum.valueOf(accountType);
            } catch (Exception e) {
                throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
            }
        }

        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(postData.getOffset(), postData.getLimit(), null, null, null, postData.getSortField(), null);
        List<BusinessEntity> parentList = businessAccountModelService.listParents(postData.getSearchTerm(), hierarchyType.parentClass(), paginationConfiguration);

        ParentEntityDto parentDto = null;
        ParentEntitiesDto parentsDto = new ParentEntitiesDto();

        if (parentList != null) {
            for (BusinessEntity parent : parentList) {
                parentDto = new ParentEntityDto(parent.getCode(), parent.getDescription());
                parentsDto.getParent().add(parentDto);
            }
        }
        return parentsDto;
    }

    private void findOrCreateCustomerCategory(String customerCategoryCode) throws BusinessException {
        CustomerCategory customerCategory = customerCategoryService.findByCode(customerCategoryCode);
        if (customerCategory == null) {
            customerCategory = new CustomerCategory();
            customerCategory.setCode(customerCategoryCode);
            customerCategory.setDescription(customerCategoryCode);
            customerCategoryService.create(customerCategory);
        }
    }

    private void findOrCreateCustomerBrand(String customerBrandCode) throws BusinessException {
        CustomerBrand customerBrand = customerBrandService.findByCode(customerBrandCode);
        if (customerBrand == null) {
            customerBrand = new CustomerBrand();
            customerBrand.setCode(customerBrandCode);
            customerBrand.setDescription(customerBrandCode);
            customerBrandService.create(customerBrand);
        }
    }

}
