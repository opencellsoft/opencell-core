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
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.EntityNotAllowedException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 *
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserAccountApi extends AccountEntityApi {

    @Inject
    private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

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
    
    @Inject
    private WalletOperationService walletOperationService;
    
    @Inject
    private RatedTransactionService ratedTransactionService;
    
    @Inject
    private SellerService sellerService;

    public UserAccount create(UserAccountDto postData) throws MeveoApiException, BusinessException {
        return create(postData, true);
    }

    public UserAccount create(UserAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public UserAccount create(UserAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getBillingAccount())) {
            missingParameters.add("billingAccount");
        }

        handleMissingParameters(postData);

        BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
        }

        UserAccount userAccount = new UserAccount();
        populate(postData, userAccount);

        userAccount.setBillingAccount(billingAccount);
        if (!StringUtils.isBlank(postData.getSubscriptionDate())) {
            userAccount.setSubscriptionDate(postData.getSubscriptionDate());
        }
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
            userAccountService.createUserAccount(billingAccount, userAccount);
        } catch (AccountAlreadyExistsException e) {
            throw new EntityAlreadyExistsException(UserAccount.class, postData.getCode());
        }

        return userAccount;
    }

    public UserAccount update(UserAccountDto postData) throws MeveoApiException, DuplicateDefaultAccountException {
        return update(postData, true);
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
                // a safeguard to allow this only if all the WO/RT have been invoiced.
                Long countNonTreatedWO = walletOperationService.countNonTreatedWOByUA(userAccount);
                if(countNonTreatedWO > 0) {
                    throw new BusinessApiException("Can not change the parent account. User account have non treated WO");
                }
                Long countNonInvoicedRT = ratedTransactionService.countNotInvoicedRTByUA(userAccount);
                if(countNonInvoicedRT > 0) {
                    throw new BusinessApiException("Can not change the parent account. User account have non invoiced RT");
                }
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
        
        if (!StringUtils.isBlank(postData.getSubscriptionDate())) {
            userAccount.setSubscriptionDate(postData.getSubscriptionDate());
        }
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
     * @return the user account
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public UserAccount createOrUpdate(UserAccountDto postData) throws MeveoApiException, BusinessException {

        UserAccount userAccount = userAccountService.findByCode(postData.getCode());

        if (userAccount == null) {
            userAccount = create(postData);
        } else {
            userAccount = update(postData);
        }
        return userAccount;
    }

    public UserAccount terminate(UserAccountDto postData) throws MeveoApiException, BusinessException {
        SubscriptionTerminationReason terminationReason = null;
        try {
            terminationReason = subscriptionTerminationReasonService.findByCodeReason(postData.getTerminationReason());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (terminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
        }

        UserAccount userAccount = userAccountService.findByCode(postData.getCode());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getCode());
        }

        userAccountService.userAccountTermination(userAccount, postData.getTerminationDate(), terminationReason);

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
                terminate(postData);
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
        
        Seller seller = null;
        if (StringUtils.isBlank(postData.getSeller())) {
        	// v5.2 : code for API backward compatibility call, seller code must be mandatory in future versions
            seller = userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        } else {
	        seller = sellerService.findByCode(postData.getSeller());
	        if (seller == null) {
	            throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
	        }
	        
	        if(productTemplate.getSellers().size() > 0) {
                if(!productTemplate.getSellers().contains(seller)) {
    	            throw new EntityNotAllowedException(Seller.class, ProductInstance.class, postData.getSeller());
                }
            } 
        }

        if (userAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new MeveoApiException("User account is not ACTIVE.");
        }

        List<WalletOperation> walletOperations = null;

        try {
            ProductInstance productInstance = new ProductInstance(userAccount, null, productTemplate, postData.getQuantity(), postData.getOperationDate(), postData.getProduct(),
                postData.getDescription(), null, seller);

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

    /**
     * Exports a json representation of the UserAcount hierarchy. It include subscription, accountOperations and invoices.
     * 
     * @param ua the selected UserAccount
     * @return DTO representation of the UserAccount
     */
	public UserAccountDto exportUserAccountHierarchy(UserAccount ua) {
		UserAccountDto result = new UserAccountDto(ua);
		
		if (ua.getSubscriptions() != null && !ua.getSubscriptions().isEmpty()) {
			for (Subscription sub : ua.getSubscriptions()) {
				result.getSubscriptions().getSubscription().add(new SubscriptionDto(sub));
			}
		}

		return result;
	}
}