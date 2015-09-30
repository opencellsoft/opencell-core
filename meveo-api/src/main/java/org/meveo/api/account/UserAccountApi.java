package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.UserAccountApiService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UserAccountApi{

	@Inject
	private UserAccountApiService userAccountApiService;
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void create(UserAccountDto postData, User currentUser) throws MeveoApiException {
		userAccountApiService.create(postData, currentUser);
	}
    
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 */
	public void create(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		userAccountApiService.create(postData, currentUser, checkCustomFields);
	}
   
	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void update(UserAccountDto postData, User currentUser) throws MeveoApiException {
		userAccountApiService.update(postData, currentUser);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 */
	public void update(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		userAccountApiService.update(postData, currentUser, checkCustomFields);
	}
    
	/**
	 * 
	 * @param userAccountCode
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public UserAccountDto find(String userAccountCode, Provider provider) throws MeveoApiException {
		return userAccountApiService.find(userAccountCode, provider);
	}
    
	/**
	 * 
	 * @param userAccountCode
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void remove(String userAccountCode, Provider provider) throws MeveoApiException {
		userAccountApiService.remove(userAccountCode, provider);
	}
    
	/**
	 * 
	 * @param billingAccountCode
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public UserAccountsDto listByBillingAccount(String billingAccountCode, Provider provider) throws MeveoApiException {
		return userAccountApiService.listByBillingAccount(billingAccountCode, provider);
	}

	/**
	 * Create or update User Account entity based on code.
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(UserAccountDto postData, User currentUser) throws MeveoApiException {
		userAccountApiService.createOrUpdate(postData, currentUser);
	}
}
