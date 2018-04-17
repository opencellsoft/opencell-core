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
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.TerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ProductTemplateService;

/**
 * API to manage {@link UserAccount} information
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserAccountApi extends AccountEntityApi {

    @Inject
    private TerminationReasonService subscriptionTerminationReasonService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    @EJB
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private ProductInstanceService productInstanceService;

    public void create(UserAccountDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public UserAccount create(UserAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public UserAccount create(UserAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getBillingAccount())) {
            missingParameters.add("billingAccount");
        }

        handleMissingParametersAndValidate(postData);

        BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
        }

        UserAccount userAccount = new UserAccount();
        populate(postData, userAccount);

        userAccount.setBillingAccount(billingAccount);
        userAccount.setExternalRef1(postData.getExternalRef1());
        userAccount.setExternalRef2(postData.getExternalRef2());

        if (businessAccountModel != null) {
            userAccount.setBusinessAccountModel(businessAccountModel);
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), userAccount, true, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        try {
            userAccountService.create(userAccount);
        } catch (AccountAlreadyExistsException e) {
            throw new EntityAlreadyExistsException(UserAccount.class, postData.getCode());
        }

        return userAccount;
    }

    public void update(UserAccountDto postData) throws MeveoApiException, DuplicateDefaultAccountException {
        update(postData, true);
    }

    public UserAccount update(UserAccountDto postData, boolean checkCustomFields) throws MeveoApiException {
        return update(postData, true, null);
    }

    public UserAccount update(UserAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        UserAccount userAccount = userAccountService.findByCode(postData.getCode());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getCode());
        }

        if (!StringUtils.isBlank(postData.getBillingAccount())) {
            BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount());
            if (billingAccount == null) {
                throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
            } else if (!userAccount.getBillingAccount().equals(billingAccount)) {
                throw new InvalidParameterException(
                    "Can not change the parent account. User account's current parent account (billing account) is " + userAccount.getBillingAccount().getCode());
            }
            userAccount.setBillingAccount(billingAccount);
        }

        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            userAccount.setExternalRef1(postData.getExternalRef1());
        }
        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            userAccount.setExternalRef2(postData.getExternalRef2());
        }

        updateAccount(userAccount, postData, checkCustomFields);

        if (businessAccountModel != null) {
            userAccount.setBusinessAccountModel(businessAccountModel);
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), userAccount, false, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        try {
            userAccount = userAccountService.update(userAccount);
        } catch (BusinessException e1) {
            throw new MeveoApiException(e1.getMessage());
        }

        return userAccount;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = UserAccount.class))
    public UserAccountDto find(String userAccountCode) throws MeveoApiException {
        return find(userAccountCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = UserAccount.class))
    public UserAccountDto find(String userAccountCode, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (StringUtils.isBlank(userAccountCode)) {
            missingParameters.add("userAccountCode");
            handleMissingParameters();
        }

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
        }

        return accountHierarchyApi.userAccountToDto(userAccount, inheritCF);
    }

    public void remove(String userAccountCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(userAccountCode)) {
            missingParameters.add("userAccountCode");
            handleMissingParameters();
        }

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
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

    public UserAccountsDto listByBillingAccount(String billingAccountCode) throws MeveoApiException {

        if (StringUtils.isBlank(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
            handleMissingParameters();
        }

        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }

        UserAccountsDto result = new UserAccountsDto();
        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
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
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(UserAccountDto postData) throws MeveoApiException, BusinessException {

        UserAccount userAccount = userAccountService.findByCode(postData.getCode());

        if (userAccount == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * Terminate User account. Status will be changed to Terminated. Action will also terminate related Subscriptions.
     * 
     * @param code Billing account code
     * @param terminationReasonCode Termination reason code
     * @param terminationDate Termination date
     * @return Updated User account entity
     * @throws BusinessException Business exception
     * @throws EntityDoesNotExistsException Code does not correspond to an existing entity
     * @throws MissingParameterException Missing parameters
     */
    public UserAccount terminateAccount(String code, String terminationReasonCode, Date terminationDate)
            throws BusinessException, EntityDoesNotExistsException, MissingParameterException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(terminationReasonCode)) {
            missingParameters.add("terminationReason");
        }
        handleMissingParameters();

        UserAccount userAccount = userAccountService.findByCode(code);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, code);
        }

        TerminationReason terminationReason = subscriptionTerminationReasonService.findByCode(terminationReasonCode);
        if (terminationReason == null) {
            throw new EntityDoesNotExistsException(TerminationReason.class, terminationReasonCode);
        }

        userAccount = userAccountService.terminateUserAccount(userAccount, terminationDate, terminationReason);

        return userAccount;
    }

    /**
     * Cancel User account. Status will be changed to Canceled. Action will also cancel related Subscriptions.
     * 
     * @param code User account code
     * @param cancellationDate Cancellation date
     * @return Updated User account entity
     * @throws BusinessException Business exception
     * @throws EntityDoesNotExistsException Code does not correspond to an existing entity
     * @throws MissingParameterException Missing parameters
     */
    public UserAccount cancelAccount(String code, Date cancellationDate) throws BusinessException, EntityDoesNotExistsException, MissingParameterException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        UserAccount userAccount = userAccountService.findByCode(code);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, code);
        }

        userAccount = userAccountService.cancelUserAccount(userAccount, cancellationDate);

        return userAccount;
    }

    /**
     * Activate previously canceled or terminated User account. Status will be changed to Active.
     * 
     * @param code User account code
     * @param activationDate Activation date
     * @return Updated User account entity
     * @throws BusinessException Business exception
     * @throws EntityDoesNotExistsException Code does not correspond to an existing entity
     * @throws MissingParameterException Missing parameters
     */
    public UserAccount reactivateAccount(String code, Date activationDate) throws BusinessException, EntityDoesNotExistsException, MissingParameterException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        UserAccount userAccount = userAccountService.findByCode(code);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, code);
        }

        userAccount = userAccountService.reactivateUserAccount(userAccount, activationDate);

        return userAccount;
    }

    public List<CounterInstance> filterCountersByPeriod(String userAccountCode, Date date) throws MeveoApiException, BusinessException {

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);

        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
        }

        if (StringUtils.isBlank(date)) {
            throw new MeveoApiException("date is null");
        }

        return new ArrayList<>(userAccountService.filterCountersByPeriod(userAccount.getCounters(), date).values());
    }

    public void createOrUpdatePartial(UserAccountDto postData) throws MeveoApiException, BusinessException {
        UserAccountDto existedUserAccountDto = null;
        try {
            existedUserAccountDto = find(postData.getCode());
        } catch (Exception e) {
            existedUserAccountDto = null;
        }
        if (existedUserAccountDto == null) {// create
            create(postData);
        } else {
            if (postData.getTerminationDate() != null) {
                if (StringUtils.isBlank(postData.getTerminationReason())) {
                    missingParameters.add("userAccount.terminationReason");
                    handleMissingParametersAndValidate(postData);
                }
                terminateAccount(postData.getCode(), postData.getTerminationReason(), postData.getTerminationDate());

            } else {

                if (!StringUtils.isBlank(postData.getBillingAccount())) {
                    existedUserAccountDto.setBillingAccount(postData.getBillingAccount());
                }

                if (postData.getStatus() != null) {
                    existedUserAccountDto.setStatus(postData.getStatus());
                }
                if (postData.getStatusDate() != null) {
                    existedUserAccountDto.setStatusDate(postData.getStatusDate());
                }
                if (!StringUtils.isBlank(postData.getSubscriptionDate())) {
                    existedUserAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
                }

                accountHierarchyApi.populateNameAddress(existedUserAccountDto, postData);
                if (postData.getCustomFields() != null && !postData.getCustomFields().isEmpty()) {
                    existedUserAccountDto.setCustomFields(postData.getCustomFields());
                }
                update(existedUserAccountDto);
            }
        }
    }

    public List<WalletOperationDto> applyProduct(ApplyProductRequestDto postData) throws MeveoApiException, BusinessException {
        List<WalletOperationDto> result = new ArrayList<>();
        if (StringUtils.isBlank(postData.getProduct())) {
            missingParameters.add("product");
        }
        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
        }
        if (postData.getOperationDate() == null) {
            missingParameters.add("operationDate");
        }

        handleMissingParametersAndValidate(postData);

        ProductTemplate productTemplate = productTemplateService.findByCode(postData.getProduct(), postData.getOperationDate());
        if (productTemplate == null) {
            throw new EntityDoesNotExistsException(ProductTemplate.class,
                postData.getProduct() + "/" + DateUtils.formatDateWithPattern(postData.getOperationDate(), paramBeanFactory.getInstance().getDateTimeFormat()));
        }

        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        if (userAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new MeveoApiException("User account is not ACTIVE.");
        }

        List<WalletOperation> walletOperations = null;

        try {
            ProductInstance productInstance = new ProductInstance(userAccount, null, productTemplate, postData.getQuantity(), postData.getOperationDate(), postData.getProduct(),
                postData.getDescription(), null);

            // Validate and populate customFields
            try {
                populateCustomFields(postData.getCustomFields(), productInstance, true, false);
            } catch (MissingParameterException | InvalidParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
            }

            productInstanceService.instantiateProductInstance(productInstance, postData.getCriteria1(), postData.getCriteria2(), postData.getCriteria3(), false);

            walletOperations = productInstanceService.applyProductInstance(productInstance, postData.getCriteria1(), postData.getCriteria2(), postData.getCriteria3(), true, false);
            for (WalletOperation walletOperation : walletOperations) {
                result.add(new WalletOperationDto(walletOperation));
            }
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        return result;
    }

}