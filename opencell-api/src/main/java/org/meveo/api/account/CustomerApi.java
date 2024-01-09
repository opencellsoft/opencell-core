/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.account;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.GDPRInfoDto;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.sequence.GenericSequenceApi;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.export.CustomBigDecimalConverter;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.tax.TaxCategory;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.dwh.GdprService;
import org.meveo.service.intcrm.impl.AdditionalDetailsService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.tax.TaxCategoryService;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Mohamed El Youssoufi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CustomerApi extends AccountEntityApi {

    /**
     * Default sort for list call.
     */
    private static final String DEFAULT_SORT_ORDER_CODE = "code";

    /**
     * Remote MQ connection url
     */
    private static final String REMOTE_MQ_HOST = "REMOTE_MQ_HOST";

    /**
     * Remote MQ JMX connection port
     */
    private static final String REMOTE_MQ_JMX_PORT = "REMOTE_MQ_JMX_PORT";

    /**
     * Remote MQ connection username
     */
    private static final String REMOTE_MQ_ADMIN_USER = "REMOTE_MQ_ADMIN_USER";

    /**
     * Remote MQ connection password
     */
    private static final String REMOTE_MQ_ADMIN_PASSWORD = "REMOTE_MQ_ADMIN_PASSWORD";

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerCategoryService customerCategoryService;

    @Inject
    private CustomerBrandService customerBrandService;

    @EJB
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    private SellerService sellerService;

    @Inject
    private CustomerAccountApi customerAccountApi;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private GdprService gdprService;

    @Inject
    private AddressBookService addressBookService;

    @Inject
    private AdditionalDetailsService additionalDetailsService;

    @Inject
    private ProviderService providerService;

    @Inject
    private AccountingCodeService accountingCodeService;

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    private TaxCategoryService taxCategoryService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Resource(lookup = "java:/jms/remoteCF-consumer")
    private ConnectionFactory jmsConnectionFactory;

    @Inject
    @JMSConnectionFactory("java:/jms/remoteCF")
    private JMSContext jmsContextForPublishing;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    public Customer create(CustomerDto postData) throws MeveoApiException, BusinessException {
        return create(postData, true);
    }

    public Customer create(CustomerDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null, null);
    }

    public Customer create(CustomerDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel, Seller associatedSeller) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(Customer.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getCustomerCategory())) {
            missingParameters.add("customerCategory");
        }
        if (postData.getName() != null && !StringUtils.isBlank(postData.getName().getTitle()) && StringUtils.isBlank(postData.getName().getLastName())) {
            missingParameters.add("name.lastName");
        }

        handleMissingParameters(postData);

        // check if customer already exists
        if (customerService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Customer.class, postData.getCode());
        }

        Customer customer = new Customer();

        dtoToEntity(customer, postData, checkCustomFields, businessAccountModel, associatedSeller);

        if (postData.getIsCompany() != null) {
            customer.setIsCompany(postData.getIsCompany());
        }

        if (StringUtils.isBlank(postData.getCode())) {
            customer.setCode(customGenericEntityCodeService.getGenericEntityCode(customer));
        }

        customerService.create(customer);
        customer.getAddressbook().setCustomer(customer);
        addressBookService.update(customer.getAddressbook());

        return customer;
    }

    public Customer update(CustomerDto postData) throws MeveoApiException, BusinessException {
        return update(postData, true);
    }

    public Customer update(CustomerDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return update(postData, true, null);
    }

    public Customer update(CustomerDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getName() != null && !StringUtils.isBlank(postData.getName().getTitle()) && StringUtils.isBlank(postData.getName().getLastName())) {
            missingParameters.add("name.lastName");
        }

        handleMissingParametersAndValidate(postData);

        // check if customer exists
        Customer customer = customerService.findByCode(postData.getCode());

        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, postData.getCode());
        }

        dtoToEntity(customer, postData, checkCustomFields, businessAccountModel, null);

        customer = customerService.update(customer);

        return customer;
    }

    /**
     * Populate entity with fields from DTO entity
     *
     * @param customer Entity to populate
     * @param postData DTO entity object to populate from
     * @param checkCustomFields Should a check be made if CF field is required
     * @param businessAccountModel Business account model
     * @param associatedSeller associated Seller
     **/
    private void dtoToEntity(Customer customer, CustomerDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel, Seller associatedSeller) {

        boolean isNew = customer.getId() == null;

        if (!org.apache.commons.lang3.StringUtils.isEmpty(postData.getCustomerCategory())) {
            CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory());
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
            }
            customer.setCustomerCategory(customerCategory);
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(postData.getCustomerBrand())) {
            if (StringUtils.isBlank(postData.getCustomerBrand())) {
                customer.setCustomerBrand(null);
            } else {
                CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand());
                if (customerBrand == null) {
                    throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
                }
                customer.setCustomerBrand(customerBrand);
            }
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(postData.getSeller())) {
            Seller seller = associatedSeller != null ? associatedSeller : sellerService.findByCode(postData.getSeller());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
            customer.setSeller(seller);
        }

        updateAccount(customer, postData, checkCustomFields);

        if (businessAccountModel != null) {
            customer.setBusinessAccountModel(businessAccountModel);
        }
        if (postData.getInvoicingThreshold() != null) {
            customer.setInvoicingThreshold(postData.getInvoicingThreshold());
        }
        if (postData.isThresholdPerEntity() != null) {
            customer.setThresholdPerEntity(postData.isThresholdPerEntity());
        }
        if (postData.getCheckThreshold() != null) {
            customer.setCheckThreshold(postData.getCheckThreshold());
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customer, isNew, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        if (customer.getAddressbook() == null) {
            AddressBook addressBook = new AddressBook("C_" + customer.getCode());
            addressBookService.create(addressBook);
            customer.setAddressbook(addressBook);
        }

        if (customer.getAdditionalDetails() == null) {
            AdditionalDetails additionalDetails = new AdditionalDetails();
            if (!StringUtils.isBlank(postData.getAdditionalDetails().getCompanyName())) {
                additionalDetails.setCompanyName(postData.getAdditionalDetails().getCompanyName());
            }
            if (!StringUtils.isBlank(postData.getAdditionalDetails().getPosition())) {
                additionalDetails.setPosition(postData.getAdditionalDetails().getPosition());
            }
            additionalDetailsService.create(additionalDetails);
            if (!StringUtils.isBlank(postData.getAdditionalDetails().getInstantMessengers())) {
                additionalDetails.setInstantMessengers(postData.getAdditionalDetails().getInstantMessengers());
            }
            customer.setAdditionalDetails(additionalDetails);
        }

        if (postData.getIsCompany() != null) {
            customer.setIsCompany(postData.getIsCompany());
        }
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Customer.class))
    public CustomerDto find(String customerCode) throws MeveoApiException {
        return find(customerCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, false);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Customer.class))
    public CustomerDto find(String customerCode, CustomFieldInheritanceEnum inheritCF, boolean includeCustomerAccounts) throws MeveoApiException {
        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
        }
        handleMissingParameters();

        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        return accountHierarchyApi.customerToDto(customer, inheritCF, includeCustomerAccounts, includeCustomerAccounts);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Customer.class))
    public void remove(String customerCode) throws MeveoApiException {
        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
            handleMissingParameters();
        }
        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }
        try {
            customerService.remove(customer);
            customerService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(Customer.class, customerCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "customers.customer", itemPropertiesToFilter = { @FilterProperty(property = DEFAULT_SORT_ORDER_CODE, entityClass = Customer.class) }, totalRecords = "customers.totalNumberOfRecords")
    public CustomersResponseDto list(CustomerDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return list(postData, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "customers.customer", itemPropertiesToFilter = { @FilterProperty(property = DEFAULT_SORT_ORDER_CODE, entityClass = Customer.class) }, totalRecords = "customers.totalNumberOfRecords")
    public CustomersResponseDto list(CustomerDto postData, PagingAndFiltering pagingAndFiltering, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        if (postData != null) {
            pagingAndFiltering.addFilter("customerCategory.code", postData.getCustomerCategory());
            pagingAndFiltering.addFilter("seller.code", postData.getSeller());
            pagingAndFiltering.addFilter("customerBrand.code", postData.getCustomerBrand());
            pagingAndFiltering.addFilter(DEFAULT_SORT_ORDER_CODE, postData.getCode());
        }

        String sortBy = DEFAULT_SORT_ORDER_CODE;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, null, pagingAndFiltering, Customer.class);

        Long totalCount = customerService.count(paginationConfig);

        CustomersDto customerDtos = new CustomersDto();
        CustomersResponseDto result = new CustomersResponseDto();

        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        customerDtos.setTotalNumberOfRecords(totalCount);

        if (totalCount > 0) {
            List<Customer> customers = customerService.list(paginationConfig);
            for (Customer c : customers) {
                customerDtos.getCustomer().add(accountHierarchyApi.customerToDto(c, inheritCF, false, false));
            }
        }

        result.setCustomers(customerDtos);
        return result;
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "customers.customer", itemPropertiesToFilter = { @FilterProperty(property = DEFAULT_SORT_ORDER_CODE, entityClass = Customer.class) }, totalRecords = "customers.totalNumberOfRecords")
    public CustomersResponseDto listGetAll(CustomerDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return listGetAll(postData, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "customers.customer", itemPropertiesToFilter = { @FilterProperty(property = DEFAULT_SORT_ORDER_CODE, entityClass = Customer.class) }, totalRecords = "customers.totalNumberOfRecords")
    public CustomersResponseDto listGetAll(CustomerDto postData, PagingAndFiltering pagingAndFiltering, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        if (postData != null) {
            pagingAndFiltering.addFilter("customerCategory.code", postData.getCustomerCategory());
            pagingAndFiltering.addFilter("seller.code", postData.getSeller());
            pagingAndFiltering.addFilter("customerBrand.code", postData.getCustomerBrand());
            pagingAndFiltering.addFilter(DEFAULT_SORT_ORDER_CODE, postData.getCode());
        }

        String sortBy = DEFAULT_SORT_ORDER_CODE;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration(sortBy, pagingAndFiltering.getMultiSortOrder(), null, pagingAndFiltering, Customer.class);

        Long totalCount = customerService.count(paginationConfig);

        CustomersDto customerDtos = new CustomersDto();
        CustomersResponseDto result = new CustomersResponseDto();

        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        customerDtos.setTotalNumberOfRecords(totalCount);

        if (totalCount > 0) {
            List<Customer> customers = customerService.list(paginationConfig);
            for (Customer c : customers) {
                customerDtos.getCustomer().add(accountHierarchyApi.customerToDto(c, inheritCF));
            }
        }

        result.setCustomers(customerDtos);
        return result;
    }

    public void createBrand(CustomerBrandDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
            handleMissingParametersAndValidate(postData);
        }

        if (customerBrandService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomerBrand.class, postData.getCode());
        }

        CustomerBrand customerBrand = new CustomerBrand();
        customerBrand.setCode(postData.getCode());
        customerBrand.setDescription(postData.getDescription());

        customerBrandService.create(customerBrand);
    }

    public void updateBrand(CustomerBrandDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
            handleMissingParametersAndValidate(postData);
        }

        CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCode());

        if (customerBrand == null) {
            throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCode());
        }

        boolean toUpdate = false;
        if (!StringUtils.isBlank(postData.getUpdatedCode()) && !postData.getUpdatedCode().equals(customerBrand.getCode())) {
            customerBrand.setCode(postData.getUpdatedCode());
            toUpdate = true;
        }
        if (postData.getDescription() != null && StringUtils.compare(postData.getDescription(), customerBrand.getDescription()) != 0) {
            customerBrand.setDescription(postData.getDescription());
            toUpdate = true;
        }

        if (toUpdate) {
            customerBrandService.update(customerBrand);
        }
    }

    public void createCategory(CustomerCategoryDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
        }

        if ((postData.isExoneratedFromTaxes() == null || !postData.isExoneratedFromTaxes()) && postData.getTaxCategoryCode() == null) {
            missingParameters.add("Exonerated from taxes or tax category code");
        }

        handleMissingParametersAndValidate(postData);

        if (customerCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomerCategory.class, postData.getCode());
        }

        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setCode(postData.getCode());
        customerCategory.setDescription(postData.getDescription());
        customerCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        if (postData.isExoneratedFromTaxes() != null) {
            customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());
        }
        customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
        customerCategory.setExonerationReason(postData.getExonerationReason());

        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            customerCategory.setAccountingCode(accountingCode);
        }
        if (!StringUtils.isBlank(postData.getTaxCategoryCode())) {
            TaxCategory taxCategory = taxCategoryService.findByCode(postData.getTaxCategoryCode());
            if (taxCategory == null) {
                throw new EntityDoesNotExistsException(TaxCategory.class, postData.getTaxCategoryCode());
            }
            customerCategory.setTaxCategory(taxCategory);
        }

        customerCategory.setTaxCategoryEl(postData.getTaxCategoryEl());

        customerCategoryService.create(customerCategory);
    }

    public void updateCategory(CustomerCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
            handleMissingParametersAndValidate(postData);
        }

        CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCode());
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCode());
        }

        boolean toUpdate = false;
        if (!StringUtils.isBlank(postData.getUpdatedCode()) && !postData.getUpdatedCode().equals(customerCategory.getCode())) {
            customerCategory.setCode(postData.getUpdatedCode());
            toUpdate = true;
        }
        if (postData.getDescription() != null && StringUtils.compare(postData.getDescription(), customerCategory.getDescription()) != 0) {
            customerCategory.setDescription(postData.getDescription());
            toUpdate = true;
        }

        if (postData.isExoneratedFromTaxes() != null && customerCategory.getExoneratedFromTaxes() != postData.isExoneratedFromTaxes().booleanValue()) {
            customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());
            toUpdate = true;
        }
        if (postData.getExonerationTaxEl() != null && StringUtils.compare(postData.getExonerationTaxEl(), customerCategory.getExonerationTaxEl()) != 0) {
            customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
            toUpdate = true;
        }
        if (postData.getExonerationReason() != null && StringUtils.compare(postData.getExonerationReason(), customerCategory.getExonerationReason()) != 0) {
            customerCategory.setExonerationReason(postData.getExonerationReason());
            toUpdate = true;
        }

        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            customerCategory.setAccountingCode(accountingCode);
        }

        if (postData.getTaxCategoryCode() != null && StringUtils.isBlank(postData.getTaxCategoryCode()) && customerCategory.getTaxCategory() != null) {
            customerCategory.setTaxCategory(null);
            toUpdate = true;

        } else if (!StringUtils.isBlank(postData.getTaxCategoryCode()) && (customerCategory.getTaxCategory() == null || !customerCategory.getTaxCategory().getCode().equals(postData.getTaxCategoryCode()))) {
            TaxCategory taxCategory = taxCategoryService.findByCode(postData.getTaxCategoryCode());
            if (taxCategory == null) {
                throw new EntityDoesNotExistsException(TaxCategory.class, postData.getTaxCategoryCode());
            }
            customerCategory.setTaxCategory(taxCategory);
            toUpdate = true;
        }

        if (postData.getTaxCategoryEl() != null && StringUtils.compare(postData.getTaxCategoryEl(), customerCategory.getTaxCategoryEl()) != 0) {
            customerCategory.setTaxCategoryEl(postData.getTaxCategoryEl());
            toUpdate = true;
        }
        if (postData.getLanguageDescriptions() != null) {
            customerCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
            toUpdate = true;
        }

        if (toUpdate) {
            customerCategoryService.update(customerCategory);
        }
    }

    /**
     * Find customer category by customer category code
     * 
     * @param customerCategoryCode customer category code
     * @return customer category dto
     * @throws MeveoApiException Meveo Api Exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public CustomerCategoryDto findCategory(String customerCategoryCode) throws MeveoApiException {

        if (StringUtils.isBlank(customerCategoryCode)) {
            missingParameters.add("customerCategoryCode");
            handleMissingParameters();
        }

        CustomerCategory customerCategory = customerCategoryService.findByCode(customerCategoryCode);
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, customerCategoryCode);
        }

        return new CustomerCategoryDto(customerCategory);
    }

    public void createOrUpdateCategory(CustomerCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
            handleMissingParametersAndValidate(postData);
        }

        if (customerCategoryService.findByCode(postData.getCode()) == null) {
            createCategory(postData);
        } else {
            updateCategory(postData);
        }
    }

    public void removeBrand(String code) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("brandCode");
            handleMissingParameters();
        }
        CustomerBrand customerBrand = customerBrandService.findByCode(code);
        if (customerBrand == null) {
            throw new EntityDoesNotExistsException(CustomerBrand.class, code);
        }

        try {
            customerBrandService.remove(customerBrand);
            customerBrandService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(CustomerBrand.class, code);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    public void removeCategory(String code) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("categoryCode");
            handleMissingParameters();
        }
        CustomerCategory customerCategory = customerCategoryService.findByCode(code);
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, code);
        }
        try {
            customerCategoryService.remove(customerCategory);
            customerCategoryService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(CustomerCategory.class, code);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    public Customer createOrUpdate(CustomerDto postData) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(postData.getCode()) && customerService.findByCode(postData.getCode()) != null) {
            return update(postData);
        } else {
            return create(postData);
        }
    }

    /**
     * Create or update customer brand based on code.
     *
     * @param postData posted data to API containing customer's brand.
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void createOrUpdateBrand(CustomerBrandDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
            handleMissingParametersAndValidate(postData);
        }

        if (customerBrandService.findByCode(postData.getCode()) == null) {
            createBrand(postData);
        } else {
            updateBrand(postData);
        }
    }

    /**
     * @param customerDto customer data
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void createOrUpdatePartial(CustomerDto customerDto) throws MeveoApiException, BusinessException {
        CustomerDto existedCustomerDto = null;
        try {
            existedCustomerDto = find(customerDto.getCode());
        } catch (Exception e) {
            existedCustomerDto = null;
        }
        log.debug("createOrUpdatePartial customer {}", customerDto);
        if (existedCustomerDto == null) {
            create(customerDto);
        } else {
            existedCustomerDto.setSeller(customerDto.getSeller());
            String customerBrandCode = StringUtils.normalizeHierarchyCode(customerDto.getCustomerBrand());
            if (!StringUtils.isBlank(customerDto.getCustomerBrand())) {
                CustomerBrandDto customerBrand = new CustomerBrandDto();
                customerBrand.setCode(customerBrandCode);
                createOrUpdateBrand(customerBrand);
                existedCustomerDto.setCustomerBrand(customerBrandCode);
            }
            String customerCategoryCode = StringUtils.normalizeHierarchyCode(customerDto.getCustomerCategory());
            if (!StringUtils.isBlank(customerDto.getCustomerCategory())) {
                CustomerCategoryDto customerCategory = new CustomerCategoryDto();
                customerCategory.setCode(customerCategoryCode);
                createOrUpdateCategory(customerCategory);
                existedCustomerDto.setCustomerCategory(customerCategoryCode);
            }
            if (!StringUtils.isBlank(customerDto.getMandateDate())) {
                existedCustomerDto.setMandateDate(customerDto.getMandateDate());
            }
            if (!StringUtils.isBlank(customerDto.getMandateIdentification())) {
                existedCustomerDto.setMandateIdentification(customerDto.getMandateIdentification());
            }
            if (customerDto.getContactInformation() != null) {
                if (!StringUtils.isBlank(customerDto.getContactInformation().getEmail())) {
                    existedCustomerDto.getContactInformation().setEmail(customerDto.getContactInformation().getEmail());
                }
                if (!StringUtils.isBlank(customerDto.getContactInformation().getPhone())) {
                    existedCustomerDto.getContactInformation().setPhone(customerDto.getContactInformation().getPhone());
                }
                if (!StringUtils.isBlank(customerDto.getContactInformation().getMobile())) {
                    existedCustomerDto.getContactInformation().setMobile(customerDto.getContactInformation().getMobile());
                }
            }
            if (!StringUtils.isBlank(customerDto.getVatNo())) {
                existedCustomerDto.setVatNo(customerDto.getVatNo());
            }
            if (!StringUtils.isBlank(customerDto.getRegistrationNo())) {
                existedCustomerDto.setRegistrationNo(customerDto.getRegistrationNo());
            }
            accountHierarchyApi.populateNameAddress(existedCustomerDto, customerDto);
            if (!StringUtils.isBlank(customerDto.getCustomFields())) {
                existedCustomerDto.setCustomFields(customerDto.getCustomFields());
            }
            update(existedCustomerDto);
        }
    }

    /**
     * Exports an account hierarchy given a specific customer selected in the GUI. It includes Subscription, AccountOperation and Invoice details. It packaged the json output as a zipped file along with the pdf invoices.
     * 
     * @param customerCode customer code.
     * @param response Http servlet response.
     * @throws Exception when zipping fail
     */
    public void exportCustomerHierarchy(String customerCode, HttpServletResponse response) throws Exception {
        CustomerDto result;

        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        List<GDPRInfoDto> customerGdpr = customFieldTemplateService.findCFMarkAsAnonymize(customer);
        result = new CustomerDto(customer, customerGdpr);
        if (customer.getCustomerAccounts() != null && !customer.getCustomerAccounts().isEmpty()) {
            for (CustomerAccount ca : customer.getCustomerAccounts()) {
                List<GDPRInfoDto> customerAccountGdpr = customFieldTemplateService.findCFMarkAsAnonymize(ca);
                result.getCustomerAccounts().getCustomerAccount().add(customerAccountApi.exportCustomerAccountHierarchy(ca, customerAccountGdpr));
            }
        }

        // zipped invoices
        List<String> invoicePdfs = invoiceService.listPdfInvoice(customer);

        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        xstream.registerConverter(new CustomBigDecimalConverter());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("customer", CustomerDto.class);
        xstream.omitField(AuditableEntityDto.class, "auditable");
        xstream.omitField(AccountOperationDto.class, "accountCode");
        xstream.omitField(AccountOperationDto.class, "accountingCode");
        xstream.omitField(AccountOperationDto.class, "accountCodeClientSide");
        xstream.omitField(PaymentMethodDto.class, "tokenId");
        xstream.omitField(ProductChargeTemplateMapping.class, "chargeTemplate");
        xstream.omitField(Attribute.class, "productVersionAttributes");

        String accountHierarchy = xstream.toXML(result);

        File accountHierarchyFile = File.createTempFile(customerCode, ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountHierarchyFile))) {
            writer.write(accountHierarchy);
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zipOut = new ZipOutputStream(bos)) {

                try (FileInputStream fis = new FileInputStream(accountHierarchyFile)) {
                    ZipEntry zipEntry = new ZipEntry(customerCode + File.separator + accountHierarchyFile.getName());
                    FileUtils.addZipEntry(zipOut, fis, zipEntry);
                }

                for (String pdfFile : invoicePdfs) {
                    try (FileInputStream fis = new FileInputStream(pdfFile)) {
                        ZipEntry zipEntry = new ZipEntry(customerCode + File.separator + Paths.get(pdfFile).getFileName().toString());
                        FileUtils.addZipEntry(zipOut, fis, zipEntry);
                    } catch (FileNotFoundException e) {
                        log.warn("Report is not yet generated with path={}", e.getMessage());
                    }
                }
            }

            try (InputStream is = new ByteArrayInputStream(bos.toByteArray())) {
                response.setContentType("application/octet-stream");
                response.setContentLength((int) bos.size());
                response.addHeader("Content-disposition", "attachment;filename=\"" + customerCode + ".zip\"");
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();

            } catch (IOException e) {
                throw new BusinessApiException("Error zipping customer data.");
            }
        }
    }

    public void anonymizeGdpr(String customerCode) throws BusinessException {
        Customer entity = customerService.findByCode(customerCode);
        if (entity == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }
        gdprService.anonymize(entity);
    }

    public void updateCustomerNumberSequence(GenericSequenceDto postData) throws BusinessException {
        if (postData.getSequenceSize() > 20) {
            throw new MeveoApiException("sequenceSize must be <= 20.");
        }
        providerService.updateCustomerNumberSequence(GenericSequenceApi.toGenericSequence(postData, appProvider.getCustomerNoSequence()));

    }

    public GenericSequenceValueResponseDto getNextCustomerNumber() throws BusinessException {
        GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();
        GenericSequence genericSequence = providerService.getNextCustomerNumber();
        String sequenceNumber = StringUtils.getLongAsNChar(genericSequence.getCurrentSequenceNb(), genericSequence.getSequenceSize());
        result.setSequence(GenericSequenceApi.fromGenericSequence(genericSequence));
        String prefix = genericSequence.getPrefix();
        result.setValue((prefix == null ? "" : prefix) + sequenceNumber);
        return result;
    }

    public List<CounterInstance> filterCountersByPeriod(String customerCode, Date date) {
        Customer customer = customerService.findByCode(customerCode);

        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        if (StringUtils.isBlank(date)) {
            throw new BusinessApiException("date is null");
        }

        return new ArrayList<>(customerService.filterCountersByPeriod(customer.getCounters(), date).values());
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ActionStatus mq1(Integer threads, Integer count, Integer batchSize, Boolean isTempQueue) {

        publishDataFork(threads, count, true, batchSize, isTempQueue);

        return new ActionStatus();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ActionStatus mq2(Integer threads, Integer count, Integer batchSize, Boolean isTempQueue) {

        publishDataFork(threads, count, false, batchSize, isTempQueue);

        return new ActionStatus();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ActionStatus mq3(Integer threads, Integer count, Integer batchSize, Boolean isTempQueue) {

        publishDataExecutor(threads, count, true, batchSize, isTempQueue);

        return new ActionStatus();

    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ActionStatus mq4(Integer threads, Integer count, Integer batchSize, Boolean isTempQueue) {

        publishDataExecutor(threads, count, false, batchSize, isTempQueue);
        return new ActionStatus();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ActionStatus mq5SingleTx(Integer threads, Integer count, Integer batchSize, Boolean isTempQueue) {

        publishDataExecutor(threads, count, false, batchSize, isTempQueue);
        return new ActionStatus();
    }

    private void publishDataFork(Integer threads, Integer count, boolean asList, Integer batchSize, Boolean isTempQueue) {

        long start = new Date().getTime();

        if (threads == null) {
            threads = 1;
        }
        if (count == null) {
            count = 10000;
        }
        if (batchSize == null) {
            batchSize = 1;
        }

        final int countFinal = count;
        final int batchSizeFinal = batchSize;

        AtomicInteger aInteger = new AtomicInteger(countFinal);

        Queue jobQueue = isTempQueue == null || isTempQueue ? jmsContextForPublishing.createTemporaryQueue() : jmsContextForPublishing.createQueue("AKK_Andrius");

        String queueName = null;
        try {
            queueName = jobQueue.getQueueName();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String queueNameFinal = queueName;

        ForkJoinPool executor = new ForkJoinPool();

        MeveoUser lastCurrentUser = currentUser.unProxy();
        for (int threadNr = 0; threadNr < threads; threadNr++) {

            JMSContext jmsContext = jmsConnectionFactory.createContext(System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD), JMSContext.CLIENT_ACKNOWLEDGE);
            JMSProducer jmsProducer = jmsContext.createProducer();

            final int threadNrFinal = threadNr;

            RecursiveAction recursiveAction = new RecursiveAction() {
                @Override
                protected void compute() {
                    // TODO Auto-generated method stub

                    Thread.currentThread().setName("AKK-PublishToCluster-" + threadNrFinal);

                    currentUserProvider.reestablishAuthentication(lastCurrentUser);

                    log.debug("Thread {} will publish data for cluster-wide data processing to queue {}", Thread.currentThread().getName(), queueNameFinal);

                    int nrOfItemsProcessedByThread = 0;
                    int nrMessages = 0;

                    jmsContext.start();

                    while (aInteger.intValue() > 0) {
                        int i = aInteger.decrementAndGet();

                        if (batchSizeFinal > 1) {

                            List<Integer> itemsToProcess = new ArrayList<>();
                            itemsToProcess.add(i);

                            while (aInteger.intValue() > 0 && itemsToProcess.size() < batchSizeFinal) {
                                i = aInteger.decrementAndGet();
                                itemsToProcess.add(i);
                            }

                            jmsProducer.send(jobQueue, (Serializable) itemsToProcess);
                            nrOfItemsProcessedByThread = nrOfItemsProcessedByThread + itemsToProcess.size();
                            nrMessages++;

                        } else if (asList) {
                            List<Integer> itemsToProcess = new ArrayList<>();
                            itemsToProcess.add(i);

                            jmsProducer.send(jobQueue, (Serializable) itemsToProcess);
                            nrOfItemsProcessedByThread++;
                            nrMessages++;

                        } else {
                            jmsProducer.send(jobQueue, i);
                            nrOfItemsProcessedByThread++;
                            nrMessages++;
                        }
                    }

                    log.info("Thread {} published {} data items in {} messages for cluster-wide data processing", Thread.currentThread().getName(), nrOfItemsProcessedByThread, nrMessages);

                    jmsContext.close();

                }
            };

            executor.execute(recursiveAction);
        }

        executor.awaitQuiescence(20, TimeUnit.SECONDS);

        long time = new Date().getTime() - start;

        log.error("AKK done launching all tasks {} in {}", count, time);
    }

    private void publishDataExecutor(Integer threads, Integer count, boolean asList, Integer batchSize, Boolean isTempQueue) {

        long start = new Date().getTime();

        if (threads == null) {
            threads = 1;
        }
        if (count == null) {
            count = 10000;
        }
        if (batchSize == null) {
            batchSize = 1;
        }

        final int countFinal = count;
        final int batchSizeFinal = batchSize;

        AtomicInteger aInteger = new AtomicInteger(countFinal);

        Queue jobQueue = isTempQueue == null || isTempQueue ? jmsContextForPublishing.createTemporaryQueue() : jmsContextForPublishing.createQueue("AKK_Andrius");

        String queueName = null;
        try {
            queueName = jobQueue.getQueueName();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String queueNameFinal = queueName;

        MeveoUser lastCurrentUser = currentUser.unProxy();

        List<Future> futures = new ArrayList<>();
        List<Runnable> tasks = new ArrayList<>();

        for (int threadNr = 0; threadNr < threads; threadNr++) {

            JMSContext jmsContext = jmsConnectionFactory.createContext(System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD), JMSContext.CLIENT_ACKNOWLEDGE);
            JMSProducer jmsProducer = jmsContext.createProducer();

            final int threadNrFinal = threadNr;

            Runnable task = () -> {
                // TODO Auto-generated method stub

                Thread.currentThread().setName("AKK-PublishToCluster-" + threadNrFinal);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);

                log.debug("Thread {} will publish data for cluster-wide data processing to queue {}", Thread.currentThread().getName(), queueNameFinal);

                int nrOfItemsProcessedByThread = 0;
                int nrMessages = 0;

                jmsContext.start();

                while (aInteger.intValue() > 0) {
                    int i = aInteger.decrementAndGet();

                    if (batchSizeFinal > 1) {

                        List<Integer> itemsToProcess = new ArrayList<>();
                        itemsToProcess.add(i);

                        while (aInteger.intValue() > 0 && itemsToProcess.size() < batchSizeFinal) {
                            i = aInteger.decrementAndGet();
                            itemsToProcess.add(i);
                        }

                        jmsProducer.send(jobQueue, (Serializable) itemsToProcess);
                        nrOfItemsProcessedByThread = nrOfItemsProcessedByThread + itemsToProcess.size();
                        nrMessages++;

                    } else if (asList) {
                        List<Integer> itemsToProcess = new ArrayList<>();
                        itemsToProcess.add(i);

                        jmsProducer.send(jobQueue, (Serializable) itemsToProcess);
                        nrOfItemsProcessedByThread++;
                        nrMessages++;

                    } else {
                        jmsProducer.send(jobQueue, i);
                        nrOfItemsProcessedByThread++;
                        nrMessages++;
                    }
                }

                log.info("Thread {} published {} data items in {} messages for cluster-wide data processing", Thread.currentThread().getName(), nrOfItemsProcessedByThread, nrMessages);

                jmsContext.close();

            };
            tasks.add(task);
        }

        for (Runnable task : tasks) {
            futures.add(executor.submit(task));
        }

        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {

            }
        }

        long time = new Date().getTime() - start;

        log.error("AKK done launching all tasks {} in {}", count, time);
    }
}
