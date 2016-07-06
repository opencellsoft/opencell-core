package org.meveo.api.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.UserParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserAccountApi extends AccountApi {

	@Inject
	private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@EJB
	private AccountHierarchyApi accountHierarchyApi;

	public void create(UserAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {
		create(postData, currentUser, true);
	}

	public UserAccount create(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getBillingAccount())) {
			missingParameters.add("billingAccount");
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount(), provider);
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
		}

		UserAccount userAccount = new UserAccount();
		populate(postData, userAccount, currentUser);

		userAccount.setBillingAccount(billingAccount);
		userAccount.setProvider(currentUser.getProvider());
		userAccount.setExternalRef1(postData.getExternalRef1());
		userAccount.setExternalRef2(postData.getExternalRef2());

		try {
			userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
		} catch (AccountAlreadyExistsException e) {
			throw new EntityAlreadyExistsException(UserAccount.class, postData.getCode());
		}

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), userAccount, true, currentUser, checkCustomFields);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw new MeveoApiException("Failed to associate custom field instance to an entity");
		}

		return userAccount;
	}

	public void update(UserAccountDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
		update(postData, currentUser, true);
	}

	public UserAccount update(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getBillingAccount())) {
			missingParameters.add("billingAccount");
		}

		handleMissingParameters();

		Provider provider = currentUser.getProvider();

		UserAccount userAccount = userAccountService.findByCode(postData.getCode(), provider);
		if (userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, postData.getCode());
		}

		if (!StringUtils.isBlank(postData.getBillingAccount())) {
			BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount(), provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
			}
			userAccount.setBillingAccount(billingAccount);
		}

		if (!StringUtils.isBlank(postData.getExternalRef1())) {
			userAccount.setExternalRef1(postData.getExternalRef1());
		}
		if (!StringUtils.isBlank(postData.getExternalRef1())) {
			userAccount.setExternalRef2(postData.getExternalRef2());
		}

		updateAccount(userAccount, postData, currentUser, checkCustomFields);

		try {
			userAccount = userAccountService.update(userAccount, currentUser);
		} catch (BusinessException e1) {
			throw new MeveoApiException(e1.getMessage());
		}

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), userAccount, false, currentUser, checkCustomFields);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Failed to associate custom field instance to an entity", e);
			throw new MeveoApiException("Failed to associate custom field instance to an entity");
		}

		return userAccount;
	}

	@SecuredBusinessEntityMethod(
			validate = @SecureMethodParameter(entity = UserAccount.class), 
			user = @SecureMethodParameter(index = 1, parser = UserParser.class))
	public UserAccountDto find(String userAccountCode, User user) throws MeveoApiException {

		if (StringUtils.isBlank(userAccountCode)) {
			missingParameters.add("userAccountCode");
			handleMissingParameters();
		}

		UserAccount userAccount = userAccountService.findByCode(userAccountCode, user.getProvider());
		if (userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
		}

		return accountHierarchyApi.userAccountToDto(userAccount);
	}

	public void remove(String userAccountCode, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(userAccountCode)) {
			missingParameters.add("userAccountCode");
			handleMissingParameters();
		}

		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
		}
		try {
			userAccountService.remove(userAccount);
			userAccountService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(UserAccount.class, userAccountCode);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	public UserAccountsDto listByBillingAccount(String billingAccountCode, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(billingAccountCode)) {
			missingParameters.add("billingAccountCode");
			handleMissingParameters();
		}

		BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, provider);
		if (billingAccount == null) {
			throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
		}

		UserAccountsDto result = new UserAccountsDto();
		List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);
		if (userAccounts != null) {
			for (UserAccount ua : userAccounts) {
				result.getUserAccount().add(accountHierarchyApi.userAccountToDto(ua));
			}
		}

		return result;
	}

	/**
	 * Create or update User Account entity based on code.
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void createOrUpdate(UserAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {

		UserAccount userAccount = userAccountService.findByCode(postData.getCode(), currentUser.getProvider());

		if (userAccount == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}

	public UserAccount terminate(UserAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {
		SubscriptionTerminationReason terminationReason = null;
		try {
			terminationReason = subscriptionTerminationReasonService.findByCodeReason(postData.getTerminationReason(),
					currentUser.getProvider());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (terminationReason == null) {
			throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
		}
		
		UserAccount userAccount = userAccountService.findByCode(postData.getCode(), currentUser.getProvider());
		if(userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, postData.getCode());
		}
		
		userAccountService.userAccountTermination(userAccount, postData.getTerminationDate(), terminationReason, currentUser);
		
		return userAccount;
	}
	
	public List<CounterInstance> filterCountersByPeriod(String userAccountCode, Date date, Provider provider) 
			throws MeveoApiException, BusinessException {
		
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		
		if (userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
		}
		
		if(StringUtils.isBlank(date)) {
			throw new MeveoApiException("date is null");
		}
		
		return new ArrayList<>(userAccountService.filterCountersByPeriod(userAccount.getCounters(), date).values());
	}

}