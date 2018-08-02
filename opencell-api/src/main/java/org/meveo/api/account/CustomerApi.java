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
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.export.CustomBigDecimalConverter;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.primefaces.model.SortOrder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CustomerApi extends AccountEntityApi {

    /**
     * Default sort for list call.
     */
    private static final String DEFAULT_SORT_ORDER_CODE = "code";

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

    public void create(CustomerDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public Customer create(CustomerDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public Customer create(CustomerDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
        }
        if (StringUtils.isBlank(postData.getCustomerCategory())) {
            missingParameters.add("customerCategory");
        }
        if (StringUtils.isBlank(postData.getSeller())) {
            missingParameters.add("seller");
        }
        if (postData.getName() != null && !StringUtils.isBlank(postData.getName().getTitle()) && StringUtils.isBlank(postData.getName().getLastName())) {
            missingParameters.add("name.lastName");
        }

        handleMissingParametersAndValidate(postData);

        // check if customer already exists
        if (customerService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Customer.class, postData.getCode());
        }

        CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory());
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
        }

        CustomerBrand customerBrand = null;
        if (!StringUtils.isBlank(postData.getCustomerBrand())) {
            customerBrand = customerBrandService.findByCode(postData.getCustomerBrand());
            if (customerBrand == null) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
            }
        }

        Seller seller = sellerService.findByCode(postData.getSeller());
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
        }

        Customer customer = new Customer();
        populate(postData, customer);

        customer.setCustomerCategory(customerCategory);
        customer.setCustomerBrand(customerBrand);
        customer.setSeller(seller);
        customer.setExternalRef1(postData.getExternalRef1());
        customer.setExternalRef2(postData.getExternalRef2());
        customer.setVatNo(postData.getVatNo());
        customer.setRegistrationNo(postData.getRegistrationNo());

        if (postData.getContactInformation() != null) {
            if (customer.getContactInformation() == null) {
                customer.setContactInformation(new ContactInformation());
            }
            customer.getContactInformation().setEmail(postData.getContactInformation().getEmail());
            customer.getContactInformation().setPhone(postData.getContactInformation().getPhone());
            customer.getContactInformation().setMobile(postData.getContactInformation().getMobile());
            customer.getContactInformation().setFax(postData.getContactInformation().getFax());
        }

        if (businessAccountModel != null) {
            customer.setBusinessAccountModel(businessAccountModel);
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customer, true, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        customerService.create(customer);

        return customer;
    }

    public void update(CustomerDto postData) throws MeveoApiException, BusinessException {
        update(postData, true);
    }

    public Customer update(CustomerDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return update(postData, true, null);
    }

    public Customer update(CustomerDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add(DEFAULT_SORT_ORDER_CODE);
        }
        if (StringUtils.isBlank(postData.getCustomerCategory())) {
            missingParameters.add("customerCategory");
        }
        if (StringUtils.isBlank(postData.getSeller())) {
            missingParameters.add("seller");
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
        customer.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        if (!StringUtils.isBlank(postData.getCustomerCategory())) {
            CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory());
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
            }
            customer.setCustomerCategory(customerCategory);
        }

        if (!StringUtils.isBlank(postData.getCustomerBrand())) {
            CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand());
            if (customerBrand == null) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
            }
            customer.setCustomerBrand(customerBrand);
        }

        if (!StringUtils.isBlank(postData.getSeller())) {
            Seller seller = sellerService.findByCode(postData.getSeller());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
            customer.setSeller(seller);
        }

        updateAccount(customer, postData, checkCustomFields);

        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            customer.setExternalRef1(postData.getExternalRef1());
        }
        if (!StringUtils.isBlank(postData.getExternalRef2())) {
            customer.setExternalRef2(postData.getExternalRef2());
        }
        if (!StringUtils.isBlank(postData.getVatNo())) {
            customer.setVatNo(postData.getVatNo());
        }
        if (!StringUtils.isBlank(postData.getRegistrationNo())) {
            customer.setRegistrationNo(postData.getRegistrationNo());
        }

        if (postData.getContactInformation() != null) {
            if (customer.getContactInformation() == null) {
                customer.setContactInformation(new ContactInformation());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getEmail())) {
                customer.getContactInformation().setEmail(postData.getContactInformation().getEmail());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getPhone())) {
                customer.getContactInformation().setPhone(postData.getContactInformation().getPhone());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getMobile())) {
                customer.getContactInformation().setMobile(postData.getContactInformation().getMobile());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getFax())) {
                customer.getContactInformation().setFax(postData.getContactInformation().getFax());
            }
        }

        if (businessAccountModel != null) {
            customer.setBusinessAccountModel(businessAccountModel);
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customer, false, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        customer = customerService.update(customer);

        return customer;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Customer.class))
    public CustomerDto find(String customerCode) throws MeveoApiException {
        return find(customerCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Customer.class))
    public CustomerDto find(String customerCode, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {
        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
        }
        handleMissingParameters();

        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        return accountHierarchyApi.customerToDto(customer, inheritCF);
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
    @FilterResults(propertyToFilter = "customers.customer", itemPropertiesToFilter = { @FilterProperty(property = DEFAULT_SORT_ORDER_CODE, entityClass = Customer.class) })
    public CustomersResponseDto list(CustomerDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return list(postData, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "customers.customer", itemPropertiesToFilter = { @FilterProperty(property = DEFAULT_SORT_ORDER_CODE, entityClass = Customer.class) })
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
            handleMissingParametersAndValidate(postData);
        }

        if (customerCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomerCategory.class, postData.getCode());
        }

        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setCode(postData.getCode());
        customerCategory.setDescription(postData.getDescription());
        if (postData.isExoneratedFromTaxes() != null) {
            customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());
        }
        customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
        customerCategory.setExonerationTaxElSpark(postData.getExonerationTaxElSpark());
        customerCategory.setExonerationReason(postData.getExonerationReason());

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
        if (postData.getExonerationTaxElSpark() != null && StringUtils.compare(postData.getExonerationTaxElSpark(), customerCategory.getExonerationTaxElSpark()) != 0) {
            customerCategory.setExonerationTaxElSpark(postData.getExonerationTaxElSpark());
            toUpdate = true;
        }
        if (postData.getExonerationReason() != null && StringUtils.compare(postData.getExonerationReason(), customerCategory.getExonerationReason()) != 0) {
            customerCategory.setExonerationReason(postData.getExonerationReason());
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

    public void createOrUpdate(CustomerDto postData) throws MeveoApiException, BusinessException {
        if (customerService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
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
                if (!StringUtils.isBlank(customerDto.getContactInformation().getFax())) {
                    existedCustomerDto.getContactInformation().setFax(customerDto.getContactInformation().getFax());
                }
            }
            accountHierarchyApi.populateNameAddress(existedCustomerDto, customerDto);
            if (!StringUtils.isBlank(customerDto.getCustomFields())) {
                existedCustomerDto.setCustomFields(customerDto.getCustomFields());
            }
            update(existedCustomerDto);
        }
    }

    /**
     * Exports an account hierarchy given a specific customer selected in the GUI. It includes Subscription, AccountOperation and Invoice details. It packaged the json output as a
     * zipped file along with the pdf invoices.
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

        result = new CustomerDto(customer);
        if (customer.getCustomerAccounts() != null && !customer.getCustomerAccounts().isEmpty()) {
            for (CustomerAccount ca : customer.getCustomerAccounts()) {
                result.getCustomerAccounts().getCustomerAccount().add(customerAccountApi.exportCustomerAccountHierarchy(ca));
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
}