package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UserAccountApiService extends AccountApiService {

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	public void create(UserAccountDto postData, User currentUser) throws MeveoApiException {
		create(postData, currentUser, true);
	}

	public void create(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getBillingAccount())) {
			Provider provider = currentUser.getProvider();

			BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount(), provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
			}

			UserAccount userAccount = new UserAccount();
			populate(postData, userAccount, currentUser, AccountLevelEnum.UA, checkCustomFields);

			userAccount.setBillingAccount(billingAccount);
			userAccount.setProvider(currentUser.getProvider());

			try {
				userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
			} catch (AccountAlreadyExistsException e) {
				throw new EntityAlreadyExistsException(UserAccount.class, postData.getCode());
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getBillingAccount())) {
				missingParameters.add("billingAccount");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(UserAccountDto postData, User currentUser) throws MeveoApiException {
		update(postData, currentUser, true);
	}

	public void update(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getBillingAccount())) {
			Provider provider = currentUser.getProvider();

			UserAccount userAccount = userAccountService.findByCode(postData.getCode(), provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, postData.getCode());
			}

			if (!StringUtils.isBlank(postData.getBillingAccount())) {
				BillingAccount billingAccount = billingAccountService
						.findByCode(postData.getBillingAccount(), provider);
				if (billingAccount == null) {
					throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
				}
				userAccount.setBillingAccount(billingAccount);
			}

			updateAccount(userAccount, postData, currentUser, AccountLevelEnum.UA, checkCustomFields);

			userAccountService.updateAudit(userAccount, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getBillingAccount())) {
				missingParameters.add("billingAccount");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public UserAccountDto find(String userAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(userAccountCode)) {
			UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
			}

			return new UserAccountDto(userAccount);
		} else {
			missingParameters.add("userAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String userAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(userAccountCode)) {
			UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
			}

			userAccountService.remove(userAccount);
		} else {
			missingParameters.add("userAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public UserAccountsDto listByBillingAccount(String billingAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(billingAccountCode)) {
			BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
			}

			UserAccountsDto result = new UserAccountsDto();
			List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);
			if (userAccounts != null) {
				for (UserAccount ua : userAccounts) {
					result.getUserAccount().add(new UserAccountDto(ua));
				}
			}

			return result;
		} else {
			missingParameters.add("customerAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	/**
	 * Create or update User Account entity based on code.
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(UserAccountDto postData, User currentUser) throws MeveoApiException {

		UserAccount userAccount = userAccountService.findByCode(postData.getCode(), currentUser.getProvider());

		if (userAccount == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
