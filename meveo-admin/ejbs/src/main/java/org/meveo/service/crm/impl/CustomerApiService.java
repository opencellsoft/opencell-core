package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
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
public class CustomerApiService extends AccountApiService {

	@Inject
	private CustomerService customerService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private SellerService sellerService;

	public void create(CustomerDto postData, User currentUser) throws MeveoApiException {
		create(postData, currentUser, true);
	}

	public void create(CustomerDto postData, User currentUser, boolean checkCustomField) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomerCategory())
				&& !StringUtils.isBlank(postData.getCustomerBrand()) && !StringUtils.isBlank(postData.getSeller())
				&& postData.getName() != null && !StringUtils.isBlank(postData.getName().getLastName())) {
			// check if customer already exists
			if (customerService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(Customer.class, postData.getCode());
			}

			CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(),
					currentUser.getProvider());
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
			}

			CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(),
					currentUser.getProvider());
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
			}

			Seller seller = sellerService.findByCode(postData.getSeller(), currentUser.getProvider());
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
			}

			Customer customer = new Customer();
			populate(postData, customer, currentUser, AccountLevelEnum.CUST, checkCustomField);

			customer.setCustomerCategory(customerCategory);
			customer.setCustomerBrand(customerBrand);
			customer.setSeller(seller);
			customer.setMandateDate(postData.getMandateDate());
			customer.setMandateIdentification(postData.getMandateIdentification());

			if (postData.getContactInformation() != null) {
				customer.getContactInformation().setEmail(postData.getContactInformation().getEmail());
				customer.getContactInformation().setPhone(postData.getContactInformation().getPhone());
				customer.getContactInformation().setMobile(postData.getContactInformation().getMobile());
				customer.getContactInformation().setFax(postData.getContactInformation().getFax());
			}

			customerService.create(customer, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCustomerCategory())) {
				missingParameters.add("customerCategory");
			}
			if (StringUtils.isBlank(postData.getCustomerBrand())) {
				missingParameters.add("customerBrand");
			}
			if (StringUtils.isBlank(postData.getSeller())) {
				missingParameters.add("seller");
			}
			if (postData.getName() == null) {
				missingParameters.add("name.lastName");
			}
			if (StringUtils.isBlank(postData.getName().getLastName())) {
				missingParameters.add("name.lastName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(CustomerDto postData, User currentUser) throws MeveoApiException {
		update(postData, currentUser, true);
	}

	public void update(CustomerDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& postData.getName() != null && !StringUtils.isBlank(postData.getName().getLastName())) {
			// check if customer exists
			Customer customer = customerService.findByCode(postData.getCode(), currentUser.getProvider());
			
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, postData.getCode());
			}

			if (!StringUtils.isBlank(postData.getCustomerCategory())) {
				CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(),
						currentUser.getProvider());
				if (customerCategory == null) {
					throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
				}
				customer.setCustomerCategory(customerCategory);
			}

			if (!StringUtils.isBlank(postData.getCustomerBrand())) {
				CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(),
						currentUser.getProvider());
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

			updateAccount(customer, postData, currentUser, AccountLevelEnum.CUST, checkCustomFields);
			if (!StringUtils.isBlank(postData.getMandateDate())) {
				customer.setMandateDate(postData.getMandateDate());
			}
			if (!StringUtils.isBlank(postData.getMandateIdentification())) {
				customer.setMandateIdentification(postData.getMandateIdentification());
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
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCustomerCategory())) {
				missingParameters.add("customerCategory");
			}
			if (StringUtils.isBlank(postData.getCustomerBrand())) {
				missingParameters.add("customerBrand");
			}
			if (StringUtils.isBlank(postData.getSeller())) {
				missingParameters.add("seller");
			}
			if (postData.getName() == null) {
				missingParameters.add("name.lastName");
			}
			if (StringUtils.isBlank(postData.getName().getLastName())) {
				missingParameters.add("name.lastName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public CustomerDto find(String customerCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerCode)) {
			Customer customer = customerService.findByCode(customerCode, provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, customerCode);
			}

			return new CustomerDto(customer);
		} else {
			missingParameters.add("customerCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String customerCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerCode)) {
			Customer customer = customerService.findByCode(customerCode, provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, customerCode);
			}

			customerService.remove(customer);
		} else {
			missingParameters.add("customerCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
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
		List<Customer> customers = customerService.filter(postData.getCode(), customerCategory, seller, customerBrand,
				provider);
		if (customers != null) {
			for (Customer c : customers) {
				result.getCustomer().add(new CustomerDto(c));
			}
		}

		return result;
	}

	public void createBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			if (customerBrandService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(CustomerBrand.class, postData.getCode());
			}

			CustomerBrand customerBrand = new CustomerBrand();
			customerBrand.setCode(postData.getCode());
			customerBrand.setDescription(postData.getDescription());

			customerBrandService.create(customerBrand, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	public void updateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			
			CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCode(), currentUser.getProvider()); 
			
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCode());
			}
			
			//TODO Please check if this is to be commented out
			//customerBrand.setCode(postData.getCode());
			customerBrand.setDescription(postData.getDescription());

			customerBrandService.update(customerBrand, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void createCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			if (customerCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(CustomerCategory.class, postData.getCode());
			}

			CustomerCategory customerCategory = new CustomerCategory();
			customerCategory.setCode(postData.getCode());
			customerCategory.setDescription(postData.getDescription());
			customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());

			customerCategoryService.create(customerCategory, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	
	public void updateCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			
			CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCode(), currentUser.getProvider());
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCode());
			}
			
			customerCategory.setDescription(postData.getDescription());
			customerCategory.setExoneratedFromTaxes(postData.isExoneratedFromTaxes());

			customerCategoryService.update(customerCategory, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	public void createOrUpdateCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			if (customerCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
				createCategory(postData, currentUser);
			} else {
				updateCategory(postData, currentUser);
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	
	public void removeBrand(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			CustomerBrand customerBrand = customerBrandService.findByCode(code, provider);
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class, code);
			}

			customerBrandService.remove(customerBrand);
		} else {
			missingParameters.add("brandCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void removeCategory(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			CustomerCategory customerCategory = customerCategoryService.findByCode(code, provider);
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class, code);
			}

			customerCategoryService.remove(customerCategory);
		} else {
			missingParameters.add("categoryCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void createOrUpdate(CustomerDto postData, User currentUser) throws MeveoApiException {
		if (customerService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
	
	/**
	 * Create or update customer brand based on code.
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			if (customerBrandService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
				createBrand(postData, currentUser);
			} else {
				updateBrand(postData, currentUser);
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
