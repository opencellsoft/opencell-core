package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountApiService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingAccountApi {

	@Inject
	private BillingAccountApiService billingAccountApiService;

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws DuplicateDefaultAccountException
	 */
	public void create(BillingAccountDto postData, User currentUser)
			throws MeveoApiException, DuplicateDefaultAccountException {
		billingAccountApiService.create(postData, currentUser);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 * @throws DuplicateDefaultAccountException
	 */
	public void create(BillingAccountDto postData, User currentUser,
			boolean checkCustomFields) throws MeveoApiException,
			DuplicateDefaultAccountException {
		billingAccountApiService.create(postData, currentUser,
				checkCustomFields);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws DuplicateDefaultAccountException
	 */
	public void update(BillingAccountDto postData, User currentUser)
			throws MeveoApiException, DuplicateDefaultAccountException {
		billingAccountApiService.update(postData, currentUser);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @param checkCustomFields
	 * @throws MeveoApiException
	 * @throws DuplicateDefaultAccountException
	 */
	public void update(BillingAccountDto postData, User currentUser,
			boolean checkCustomFields) throws MeveoApiException,
			DuplicateDefaultAccountException {
		billingAccountApiService.update(postData, currentUser,
				checkCustomFields);
	}

	/**
	 * 
	 * @param billingAccountCode
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public BillingAccountDto find(String billingAccountCode, Provider provider)
			throws MeveoApiException {
		return billingAccountApiService.find(billingAccountCode, provider);
	}

	/**
	 * 
	 * @param billingAccountCode
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void remove(String billingAccountCode, Provider provider)
			throws MeveoApiException {
		billingAccountApiService.remove(billingAccountCode, provider);
	}

	/**
	 * 
	 * @param customerAccountCode
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public BillingAccountsDto listByCustomerAccount(String customerAccountCode,
			Provider provider) throws MeveoApiException {
		return billingAccountApiService.listByCustomerAccount(
				customerAccountCode, provider);
	}

	/**
	 * Create or update Billing Account based on Billing Account Code
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(BillingAccountDto postData, User currentUser)
			throws MeveoApiException, DuplicateDefaultAccountException {
		billingAccountApiService.createOrUpdate(postData, currentUser);
	}
}
