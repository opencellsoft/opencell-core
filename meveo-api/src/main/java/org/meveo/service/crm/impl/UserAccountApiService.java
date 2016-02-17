package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
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

    public void create(UserAccountDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
        create(postData, currentUser, true);
    }

    public void create(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getBillingAccount())) {
            missingParameters.add("billingAccount");
        }

        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

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
    }

    public void update(UserAccountDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
        update(postData, currentUser, true);
    }

    public void update(UserAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getBillingAccount())) {
            missingParameters.add("billingAccount");
        }

        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

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

        userAccountService.updateAudit(userAccount, currentUser);

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), userAccount, false, currentUser, checkCustomFields);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }
    }

    public UserAccountDto find(String userAccountCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(userAccountCode)) {
            missingParameters.add("userAccountCode");
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
        }

        return accountHierarchyApiService.userAccountToDto(userAccount);
    }

    public void remove(String userAccountCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(userAccountCode)) {
            missingParameters.add("userAccountCode");
            throw new MissingParameterException(getMissingParametersExceptionMessage());
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
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, provider);
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }

        UserAccountsDto result = new UserAccountsDto();
        List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);
        if (userAccounts != null) {
            for (UserAccount ua : userAccounts) {
                result.getUserAccount().add(accountHierarchyApiService.userAccountToDto(ua));
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
     * @throws DuplicateDefaultAccountException
     */
    public void createOrUpdate(UserAccountDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {

        UserAccount userAccount = userAccountService.findByCode(postData.getCode(), currentUser.getProvider());

        if (userAccount == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}