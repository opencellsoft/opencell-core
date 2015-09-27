package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.service.crm.impl.UserAccountApiService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountApi {

	@Inject
	private UserAccountApiService userAccountApiService;

	/**
	 * 
	 * @param postData
	 * @param accountEntity
	 * @param currentUser
	 * @param accountLevel
	 * @throws MeveoApiException
	 */
	public void populate(AccountDto postData, AccountEntity accountEntity, User currentUser, AccountLevelEnum accountLevel) throws MeveoApiException {
		userAccountApiService.populate(postData, accountEntity, currentUser, accountLevel);
	}

	/**
	 * 
	 * @param postData
	 * @param accountEntity
	 * @param currentUser
	 * @param accountLevel
	 * @param checkCustomField
	 * @throws MeveoApiException
	 */
	public void populate(AccountDto postData, AccountEntity accountEntity, User currentUser, AccountLevelEnum accountLevel, boolean checkCustomField) throws MeveoApiException {
		userAccountApiService.populate(postData, accountEntity, currentUser, accountLevel, checkCustomField);
	}

	/**
	 * 
	 * @param accountEntity
	 * @param postData
	 * @param currentUser
	 * @param accountLevel
	 * @throws MeveoApiException
	 */
	public void updateAccount(AccountEntity accountEntity, AccountDto postData, User currentUser, AccountLevelEnum accountLevel) throws MeveoApiException {
		userAccountApiService.updateAccount(accountEntity, postData, currentUser, accountLevel);
	}
    
	/**
	 * 
	 * @param accountEntity
	 * @param postData
	 * @param currentUser
	 * @param accountLevel
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 */
	public void updateAccount(AccountEntity accountEntity, AccountDto postData, User currentUser, AccountLevelEnum accountLevel, boolean checkCustomFields) throws MeveoApiException {
		userAccountApiService.updateAccount(accountEntity, postData, currentUser, accountLevel, checkCustomFields);
	}

}