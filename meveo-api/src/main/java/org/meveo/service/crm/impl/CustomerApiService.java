package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CustomerApiService extends AccountApiService {

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerCategoryService customerCategoryService;

    @Inject
    private CustomerBrandService customerBrandService;

    @EJB
    private AccountHierarchyApiService accountHierarchyApiService;

    @Inject
    private SellerService sellerService;

    public void create(CustomerDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
        create(postData, currentUser, true);
    }

    public void create(CustomerDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {

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

        handleMissingParameters();
        
        // check if customer already exists
        if (customerService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(Customer.class, postData.getCode());
        }

        CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(), currentUser.getProvider());
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
        }

        CustomerBrand customerBrand = null;
        if (!StringUtils.isBlank(postData.getCustomerBrand())) {
            customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(), currentUser.getProvider());
            if (customerBrand == null) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
            }
        }

        Seller seller = sellerService.findByCode(postData.getSeller(), currentUser.getProvider());
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
        }

        Customer customer = new Customer();
        populate(postData, customer, currentUser);

        customer.setCustomerCategory(customerCategory);
        customer.setCustomerBrand(customerBrand);
        customer.setSeller(seller);
        customer.setMandateDate(postData.getMandateDate());
        customer.setMandateIdentification(postData.getMandateIdentification());
        customer.setExternalRef1(postData.getExternalRef1());
        customer.setExternalRef2(postData.getExternalRef2());

        if (postData.getContactInformation() != null) {
            customer.getContactInformation().setEmail(postData.getContactInformation().getEmail());
            customer.getContactInformation().setPhone(postData.getContactInformation().getPhone());
            customer.getContactInformation().setMobile(postData.getContactInformation().getMobile());
            customer.getContactInformation().setFax(postData.getContactInformation().getFax());
        }

        customerService.create(customer, currentUser, currentUser.getProvider());

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customer, true, currentUser, checkCustomFields);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }
    }

    public void update(CustomerDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
        update(postData, currentUser, true);
    }

    public void update(CustomerDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {

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

        handleMissingParameters();
        

        // check if customer exists
        Customer customer = customerService.findByCode(postData.getCode(), currentUser.getProvider());

        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, postData.getCode());
        }

        if (!StringUtils.isBlank(postData.getCustomerCategory())) {
            CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(), currentUser.getProvider());
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
            }
            customer.setCustomerCategory(customerCategory);
        }

        if (!StringUtils.isBlank(postData.getCustomerBrand())) {
            CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(), currentUser.getProvider());
            if (customerBrand == null) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
            }
            customer.setCustomerBrand(customerBrand);
        }

        if (!StringUtils.isBlank(postData.getSeller())) {
            Seller seller = sellerService.findByCode(postData.getSeller(), currentUser.getProvider());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
            customer.setSeller(seller);
        }

        updateAccount(customer, postData, currentUser, checkCustomFields);

        if (!StringUtils.isBlank(postData.getMandateDate())) {
            customer.setMandateDate(postData.getMandateDate());
        }
        if (!StringUtils.isBlank(postData.getMandateIdentification())) {
            customer.setMandateIdentification(postData.getMandateIdentification());
        }

        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            customer.setExternalRef1(postData.getExternalRef1());
        }
        if (!StringUtils.isBlank(postData.getExternalRef2())) {
            customer.setExternalRef2(postData.getExternalRef2());
        }

        if (postData.getContactInformation() != null) {
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

        customerService.updateAudit(customer, currentUser);

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customer, false, currentUser, checkCustomFields);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }
    }

    public CustomerDto find(String customerCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
        }
        handleMissingParameters();

        Customer customer = customerService.findByCode(customerCode, provider);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        return accountHierarchyApiService.customerToDto(customer);
    }

    public void remove(String customerCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
            handleMissingParameters();
        }
        Customer customer = customerService.findByCode(customerCode, provider);
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

    public CustomersDto filterCustomer(CustomerDto postData, Provider provider) throws MeveoApiException {
        CustomerCategory customerCategory = null;
        if (!StringUtils.isBlank(postData.getCustomerCategory())) {
            customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(), provider);
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
            }
        }

        Seller seller = null;
        if (!StringUtils.isBlank(postData.getSeller())) {
            seller = sellerService.findByCode(postData.getSeller(), provider);
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
            }
        }

        CustomerBrand customerBrand = null;
        if (!StringUtils.isBlank(postData.getCustomerBrand())) {
            customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(), provider);
            if (customerBrand == null) {
                throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
            }
        }

        CustomersDto result = new CustomersDto();
        List<Customer> customers = customerService.filter(postData.getCode(), customerCategory, seller, customerBrand, provider);
        if (customers != null) {
            for (Customer c : customers) {
                result.getCustomer().add(accountHierarchyApiService.customerToDto(c));
            }
        }

        return result;
    }

    public void createBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (customerBrandService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomerBrand.class, postData.getCode());
        }

        CustomerBrand customerBrand = new CustomerBrand();
        customerBrand.setCode(postData.getCode());
        customerBrand.setDescription(postData.getDescription());

        customerBrandService.create(customerBrand, currentUser, currentUser.getProvider());
    }

    public void updateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCode(), currentUser.getProvider());

        if (customerBrand == null) {
            throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCode());
        }

        // TODO Please check if this is to be commented out
        // customerBrand.setCode(postData.getCode());
        customerBrand.setDescription(postData.getDescription());

        customerBrandService.update(customerBrand, currentUser);
    }

    public void createCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (customerCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(CustomerCategory.class, postData.getCode());
        }

        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setCode(postData.getCode());
        customerCategory.setDescription(postData.getDescription());
        customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());

        customerCategoryService.create(customerCategory, currentUser, currentUser.getProvider());
    }

    public void updateCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCode(), currentUser.getProvider());
        if (customerCategory == null) {
            throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCode());
        }

        customerCategory.setDescription(postData.getDescription());
        customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());

        customerCategoryService.update(customerCategory, currentUser);
    }

    public void createOrUpdateCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (customerCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            createCategory(postData, currentUser);
        } else {
            updateCategory(postData, currentUser);
        }
    }

    public void removeBrand(String code, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("brandCode");
            handleMissingParameters();
        }
        CustomerBrand customerBrand = customerBrandService.findByCode(code, provider);
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

    public void removeCategory(String code, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("categoryCode");
            handleMissingParameters();
        }
        CustomerCategory customerCategory = customerCategoryService.findByCode(code, provider);
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

    public void createOrUpdate(CustomerDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
        if (customerService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    /**
     * Create or update customer brand based on code.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
    public void createOrUpdateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (customerBrandService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            createBrand(postData, currentUser);
        } else {
            updateBrand(postData, currentUser);
        }
    }
}