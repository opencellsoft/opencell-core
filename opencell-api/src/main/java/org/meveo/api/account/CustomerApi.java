package org.meveo.api.account;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CustomerApi extends AccountEntityApi {

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

    public void create(CustomerDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public Customer create(CustomerDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public Customer create(CustomerDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
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
        } catch (MissingParameterException e) {
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
            missingParameters.add("code");
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
        } catch (MissingParameterException e) {
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
        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
        }
        handleMissingParameters();

        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        return accountHierarchyApi.customerToDto(customer);
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
    public CustomersResponseDto list(CustomerDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        if (postData != null) {
            pagingAndFiltering.addFilter("customerCategory.code", postData.getCustomerCategory());
            pagingAndFiltering.addFilter("seller.code", postData.getSeller());
            pagingAndFiltering.addFilter("customerBrand.code", postData.getCustomerBrand());
            pagingAndFiltering.addFilter("code", postData.getCode());
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, Customer.class);

        Long totalCount = customerService.count(paginationConfig);

        CustomersDto customerDtos = new CustomersDto();
        CustomersResponseDto result = new CustomersResponseDto();

        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        customerDtos.setTotalNumberOfRecords(totalCount);

        if (totalCount > 0) {
            List<Customer> customers = customerService.list(paginationConfig);
            for (Customer c : customers) {
                customerDtos.getCustomer().add(accountHierarchyApi.customerToDto(c));
            }
        }

        result.setCustomers(customerDtos);
        return result;
    }

    public void createBrand(CustomerBrandDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
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
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCode());

        if (customerBrand == null) {
            throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCode());
        }

        customerBrand.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        customerBrand.setDescription(postData.getDescription());

        customerBrandService.update(customerBrand);
    }

    public void createCategory(CustomerCategoryDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        if (customerCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomerCategory.class, postData.getCode());
        }

        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setCode(postData.getCode());
        customerCategory.setDescription(postData.getDescription());
        customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());
        customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
        customerCategory.setExonerationReason(postData.getExonerationReason());

        customerCategoryService.create(customerCategory);
    }

    public void updateCategory(CustomerCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCode());
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCode());
        }
        customerCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        customerCategory.setDescription(postData.getDescription());
        customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());
        customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
        customerCategory.setExonerationReason(postData.getExonerationReason());

        customerCategoryService.update(customerCategory);
    }

    public void createOrUpdateCategory(CustomerCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
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
     * @param postData
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdateBrand(CustomerBrandDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        if (customerBrandService.findByCode(postData.getCode()) == null) {
            createBrand(postData);
        } else {
            updateBrand(postData);
        }
    }

    public void createOrUpdatePartial(CustomerDto customerDto) throws MeveoApiException, BusinessException {
        CustomerDto existedCustomerDto = null;
        try {
            existedCustomerDto = find(customerDto.getCode());
        } catch (Exception e) {
            existedCustomerDto = null;
        }
        log.debug("createOrUpdate customer {}", customerDto);
        if (existedCustomerDto == null) {
            create(customerDto);
        } else {
            existedCustomerDto.setSeller(customerDto.getSeller());
            String customerBrandCode = StringUtils.normalizeHierarchyCode(customerDto.getCustomerBrand());
            if (!StringUtils.isBlank(customerDto.getCustomerBrand())) {
                CustomerBrandDto customerBrand = new CustomerBrandDto();
                customerBrand.setCode(customerBrandCode);
                customerBrand.setDescription(customerBrandCode);
                createOrUpdateBrand(customerBrand);
                existedCustomerDto.setCustomerBrand(customerBrandCode);
            }
            String customerCategoryCode = StringUtils.normalizeHierarchyCode(customerDto.getCustomerCategory());
            if (!StringUtils.isBlank(customerDto.getCustomerCategory())) {
                CustomerCategoryDto customerCategory = new CustomerCategoryDto();
                customerCategory.setCode(customerCategoryCode);
                customerCategory.setDescription(customerCategoryCode);
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
}