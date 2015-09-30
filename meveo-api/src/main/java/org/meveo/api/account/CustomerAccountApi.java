package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.payment.DunningInclusionExclusionDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.CustomerAccountApiService;

@Stateless
public class CustomerAccountApi {

	@Inject
	private CustomerAccountApiService customerAccountApiService;
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void create(CustomerAccountDto postData, User currentUser) throws MeveoApiException {
		customerAccountApiService.create(postData, currentUser);
	}
 
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 */
	public void create(CustomerAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		customerAccountApiService.create(postData, currentUser, checkCustomFields);
	}
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void update(CustomerAccountDto postData, User currentUser) throws MeveoApiException {
		customerAccountApiService.update(postData, currentUser);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 */
	public void update(CustomerAccountDto postData, User currentUser, boolean checkCustomFields)throws MeveoApiException {
		customerAccountApiService.update(postData, currentUser, checkCustomFields);
	}

	/**
	 *  
	 * @param customerAccountCode
	 * @param currentUser
	 * @return
	 * @throws Exception
	 */
	public CustomerAccountDto find(String customerAccountCode, User currentUser) throws Exception {
		return customerAccountApiService.find(customerAccountCode, currentUser);
	}
    
	/**
	 * 
	 * @param customerAccountCode
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void remove(String customerAccountCode, Provider provider) throws MeveoApiException {
		customerAccountApiService.remove(customerAccountCode, provider);
	}
    
	/**
	 * 
	 * @param customerCode
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public CustomerAccountsDto listByCustomer(String customerCode, Provider provider) throws MeveoApiException {
		return customerAccountApiService.listByCustomer(customerCode, provider);
	}

	/**
	 * 
	 * @param dunningDto
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void dunningExclusionInclusion(DunningInclusionExclusionDto dunningDto, Provider provider)throws MeveoApiException {
		customerAccountApiService.dunningExclusionInclusion(dunningDto, provider);
	}
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createCreditCategory(CreditCategoryDto postData, User currentUser) throws MeveoApiException {
		customerAccountApiService.createCreditCategory(postData, currentUser);
	}

	/**
	 * 
	 * @param code
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void removeCreditCategory(String code, Provider provider) throws MeveoApiException {
		customerAccountApiService.removeCreditCategory(code, provider);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(CustomerAccountDto postData, User currentUser) throws MeveoApiException {
		customerAccountApiService.createOrUpdate(postData, currentUser);
	}
}
