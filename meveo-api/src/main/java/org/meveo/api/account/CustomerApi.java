package org.meveo.api.account;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.AccountDtoListFilter;
import org.meveo.api.security.parameter.NullParser;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.UserParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CustomerApi extends AccountApi {

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

	public void create(CustomerDto postData, User currentUser) throws MeveoApiException, BusinessException {
		create(postData, currentUser, true);
	}

	public Customer create(CustomerDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, BusinessException {
		return create(postData, currentUser, true, null);
	}

	public Customer create(CustomerDto postData, User currentUser, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

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

		if(businessAccountModel != null){
			customer.setBusinessAccountModel(businessAccountModel);
		}

		customerService.create(customer, currentUser);

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), customer, true, currentUser, checkCustomFields);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

		return customer;
	}

	public void update(CustomerDto postData, User currentUser) throws MeveoApiException, BusinessException {
		update(postData, currentUser, true);
	}

	public Customer update(CustomerDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, BusinessException {
		return update(postData, currentUser, true, null);
	}

	public Customer update(CustomerDto postData, User currentUser, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

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

		if(businessAccountModel != null){
			customer.setBusinessAccountModel(businessAccountModel);
		}

		customer = customerService.update(customer, currentUser);

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), customer, false, currentUser, checkCustomFields);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

		return customer;
	}

	@SecuredBusinessEntityMethod(
			validate = @SecureMethodParameter(entity = Customer.class), 
			user = @SecureMethodParameter(index = 1, parser = UserParser.class))
	public CustomerDto find(String customerCode, User user) throws MeveoApiException {
		if (StringUtils.isBlank(customerCode)) {
			missingParameters.add("customerCode");
		}
		handleMissingParameters();

		Customer customer = customerService.findByCode(customerCode, user.getProvider());
		if (customer == null) {
			throw new EntityDoesNotExistsException(Customer.class, customerCode);
		}

		return accountHierarchyApi.customerToDto(customer);
	}

	public void remove(String customerCode, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(customerCode)) {
			missingParameters.add("customerCode");
			handleMissingParameters();
		}
		Customer customer = customerService.findByCode(customerCode, currentUser.getProvider());
		if (customer == null) {
			throw new EntityDoesNotExistsException(Customer.class, customerCode);
		}
		try {
			customerService.remove(customer, currentUser);
			customerService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(Customer.class, customerCode);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	@SecuredBusinessEntityMethod(
			resultFilter = AccountDtoListFilter.class, 
			validate = @SecureMethodParameter(parser = NullParser.class),
			user = @SecureMethodParameter(index = 1, parser = UserParser.class))
	public CustomersDto filterCustomer(CustomerDto postData, User currentUser) throws MeveoApiException {
		Provider provider = currentUser.getProvider();
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
				result.getCustomer().add(accountHierarchyApi.customerToDto(c));
			}
		}

		return result;
	}

	public void createBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

		customerBrandService.create(customerBrand, currentUser);
	}

	public void updateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

	public void createCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {
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
		customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
		customerCategory.setExonerationReason(postData.getExonerationReason());

		customerCategoryService.create(customerCategory, currentUser);
	}

	public void updateCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

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
		customerCategory.setExonerationTaxEl(postData.getExonerationTaxEl());
		customerCategory.setExonerationReason(postData.getExonerationReason());

		customerCategoryService.update(customerCategory, currentUser);
	}

	public void createOrUpdateCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

	public void removeBrand(String code, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("brandCode");
			handleMissingParameters();
		}
		CustomerBrand customerBrand = customerBrandService.findByCode(code, currentUser.getProvider());
		if (customerBrand == null) {
			throw new EntityDoesNotExistsException(CustomerBrand.class, code);
		}

		try {
			customerBrandService.remove(customerBrand, currentUser);
			customerBrandService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(CustomerBrand.class, code);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	public void removeCategory(String code, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("categoryCode");
			handleMissingParameters();
		}
		CustomerCategory customerCategory = customerCategoryService.findByCode(code, currentUser.getProvider());
		if (customerCategory == null) {
			throw new EntityDoesNotExistsException(CustomerCategory.class, code);
		}
		try {
			customerCategoryService.remove(customerCategory, currentUser);
			customerCategoryService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(CustomerCategory.class, code);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	public void createOrUpdate(CustomerDto postData, User currentUser) throws MeveoApiException, BusinessException {
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
	 * @throws BusinessException
	 */
	public void createOrUpdateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

	public void createOrUpdatePartial(CustomerDto customerDto, User currentUser) throws MeveoApiException, BusinessException {
		CustomerDto existedCustomerDto = null;
		try {
			existedCustomerDto = find(customerDto.getCode(), currentUser);
		} catch (Exception e) {
			existedCustomerDto = null;
		}
		log.debug("createOrUpdate customer {}", customerDto);
		if (existedCustomerDto == null) {
			create(customerDto, currentUser);
		} else {
			existedCustomerDto.setSeller(customerDto.getSeller());
			String customerBrandCode = StringUtils.normalizeHierarchyCode(customerDto.getCustomerBrand());
			if (!StringUtils.isBlank(customerDto.getCustomerBrand())) {
				CustomerBrandDto customerBrand = new CustomerBrandDto();
				customerBrand.setCode(customerBrandCode);
				customerBrand.setDescription(customerBrandCode);
				createOrUpdateBrand(customerBrand, currentUser);
				existedCustomerDto.setCustomerBrand(customerBrandCode);
			}
			String customerCategoryCode = StringUtils.normalizeHierarchyCode(customerDto.getCustomerCategory());
			if (!StringUtils.isBlank(customerDto.getCustomerCategory())) {
				CustomerCategoryDto customerCategory = new CustomerCategoryDto();
				customerCategory.setCode(customerCategoryCode);
				customerCategory.setDescription(customerCategoryCode);
				createOrUpdateCategory(customerCategory, currentUser);
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
			accountHierarchyApi.populateNameAddress(existedCustomerDto, customerDto, currentUser);
			if (!StringUtils.isBlank(customerDto.getCustomFields())) {
				existedCustomerDto.setCustomFields(customerDto.getCustomFields());
			}
			update(existedCustomerDto, currentUser);
		}
	}
}