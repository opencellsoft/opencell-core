package org.meveo.apiv2.accounts.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
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

    public UserAccountsDto allowedUserAccountParents(String userAccountCode) throws MeveoApiException {
    	UserAccountsDto userAccountsDto = new UserAccountsDto();
    	List<UserAccountDto> userAccountDtos = new ArrayList<UserAccountDto>();

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
        userAccounts.remove(userAccount);
        
		for (UserAccount ua : userAccounts) {
			userAccountDtos.add(buildUserAccountDto(ua));
		}

		userAccountsDto.setUserAccount(userAccountDtos.stream()
        	    .sorted(Comparator.comparing(UserAccountDto::getParentUserAccountCode,
        	        	Comparator.nullsFirst(Comparator.reverseOrder())))
        	    .collect(Collectors.toList()));

        return userAccountsDto;
    }
    
    private UserAccountDto buildUserAccountDto(UserAccount ua) {
		UserAccountDto userAccountDto = new UserAccountDto();
		
		userAccountDto.setId(ua.getId());
        userAccountDto.setCode(ua.getCode());
        userAccountDto.setName(new NameDto(ua.getName()));
        userAccountDto.setDescription(ua.getDescription());
        userAccountDto.setExternalRef1(ua.getExternalRef1());
        userAccountDto.setExternalRef2(ua.getExternalRef2());
        userAccountDto.setStatus(ua.getStatus());
        userAccountDto.setBillingAccount(ua.getBillingAccount().getCode());
        userAccountDto.setAddress(new AddressDto(ua.getAddress()));
        userAccountDto.setJobTitle(ua.getJobTitle());
        userAccountDto.setIsCompany(ua.getIsCompany());
        userAccountDto.setIsConsumer(ua.getIsConsumer());
        userAccountDto.setCustomerAccountDescription(ua.getDescription());
        
        if (ua.getParentUserAccount() != null) {
        	userAccountDto.setParentUserAccountCode(ua.getParentUserAccount().getCode());
        	userAccountDto.setParentUserAccount(buildUserAccountDto(ua.getParentUserAccount()));
        }
        
        

    	return userAccountDto;
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
