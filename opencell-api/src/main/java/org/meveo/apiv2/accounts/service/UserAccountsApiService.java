package org.meveo.apiv2.accounts.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.meveo.api.dto.account.UserAccountCodeIdsDto;
import org.meveo.api.dto.account.UserAccountIdCodeDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class UserAccountsApiService {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject 
    private UserAccountService userAccountService;

    @Inject 
    private BillingAccountService billingAccountService;
    
    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    protected List<String> missingParameters = new ArrayList<>();

    public UserAccountCodeIdsDto allowedUserAccountParents(String userAccountCode) throws MeveoApiException {
    	
    	UserAccountCodeIdsDto accountCodeIdsDto = new UserAccountCodeIdsDto();

        if (StringUtils.isBlank(userAccountCode)) {
        	throw new ValidationException("The user account code must be non-null");
        }

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
        }


        BillingAccount billingAccount = billingAccountService.findByCode(userAccount.getBillingAccount().getCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, userAccount.getBillingAccount().getCode());
        }
        
        
        List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);
        
        if (userAccounts != null) {
        	List<UserAccount> userAccountsToRemove = new ArrayList<>();
			for (UserAccount ua : userAccounts) {
				if(ua.getParentUserAccount() != null && ua.getParentUserAccount().equals(userAccount)) {
					removeChildrenUserAccount(userAccounts, userAccountsToRemove , ua);
				}
			}
			
			userAccounts.removeAll(userAccountsToRemove);
		}
        
		for (UserAccount ua : userAccounts) {
			UserAccountIdCodeDto accountIdCodeDto = new UserAccountIdCodeDto();
			accountIdCodeDto.setId(ua.getId());
			accountIdCodeDto.setCode(ua.getCode());
			
			accountCodeIdsDto.getUserAccounts().add(accountIdCodeDto);
		}

        return accountCodeIdsDto;
    }
    
    private void removeChildrenUserAccount(List<UserAccount> userAccounts,List<UserAccount> userAccountListToRemove, UserAccount userAccountToRemove) {
    	userAccountListToRemove.add(userAccountToRemove);
    	for(UserAccount ua:userAccounts) {
    		if(ua.getParentUserAccount() != null && ua.getParentUserAccount().getId().equals(userAccountToRemove.getId())) {
    			removeChildrenUserAccount(userAccounts, userAccountListToRemove , ua);
    		}
    	}
    }
    
}
