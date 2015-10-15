package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.CustomerApiService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CustomerApi{

	@Inject
	private CustomerApiService customerApiService;
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void create(CustomerDto postData, User currentUser) throws MeveoApiException {
		customerApiService.create(postData, currentUser);
	}
	
    /**
     * 
     * @param postData
     * @param currentUser
     * @param checkCustomField
     * @throws MeveoApiException
     */
	public void create(CustomerDto postData, User currentUser, boolean checkCustomField) throws MeveoApiException {
		customerApiService.create(postData, currentUser, checkCustomField);
	}
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void update(CustomerDto postData, User currentUser) throws MeveoApiException {
		customerApiService.update(postData, currentUser);
	}
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 */
	public void update(CustomerDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		customerApiService.update(postData, currentUser, checkCustomFields);
	}
	
    /**
     * 
     * @param customerCode
     * @param provider
     * @return
     * @throws MeveoApiException
     */
	public CustomerDto find(String customerCode, Provider provider) throws MeveoApiException {
		return customerApiService.find(customerCode, provider);
	}
    
	/**
	 * 
	 * @param customerCode
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void remove(String customerCode, Provider provider) throws MeveoApiException {
		customerApiService.remove(customerCode, provider);
	}
	
    /**
     * 
     * @param postData
     * @param provider
     * @return
     * @throws MeveoApiException
     */
	public CustomersDto filterCustomer(CustomerDto postData, Provider provider) throws MeveoApiException {
		return customerApiService.filterCustomer(postData, provider);
	}
	
    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
	public void createBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {
		customerApiService.createBrand(postData, currentUser);
	}
	
	 /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
	public void updateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {
		customerApiService.updateBrand(postData, currentUser);
	}
	
	 /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
	public void createOrUpdateBrand(CustomerBrandDto postData, User currentUser) throws MeveoApiException {
		customerApiService.createOrUpdateBrand(postData, currentUser);
	}
    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
	public void createCategory(CustomerCategoryDto postData, User currentUser) throws MeveoApiException {
		customerApiService.createCategory(postData, currentUser);
	}
	
    /**
     * 
     * @param code
     * @param provider
     * @throws MeveoApiException
     */
	public void removeBrand(String code, Provider provider) throws MeveoApiException {
		customerApiService.removeBrand(code, provider);
	}
	
    /**
     * 
     * @param code
     * @param provider
     * @throws MeveoApiException
     */
	public void removeCategory(String code, Provider provider) throws MeveoApiException {
		customerApiService.removeCategory(code, provider);
	}
	
    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
	public void createOrUpdate(CustomerDto postData, User currentUser) throws MeveoApiException {
		customerApiService.createOrUpdate(postData, currentUser);
	}
}
