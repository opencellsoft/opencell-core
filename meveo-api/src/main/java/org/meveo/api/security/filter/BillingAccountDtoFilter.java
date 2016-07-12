package org.meveo.api.security.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * This will parse a BusinesAccountDto result from a
 * {@link SecuredBusinessEntityMethod} annotated method. It will check if the
 * child {@link UserAccountsDto} element has items that the user does not have
 * access to. Then it will filter them out and just return the items that are
 * accessible to the user.
 * 
 * @author Tony Alejandro
 *
 */
public class BillingAccountDtoFilter extends SecureMethodResultFilter {

	@Inject
	SecuredBusinessEntityService securedBusinessEntityService;

	@Override
	public Object filterResult(Object result, User user) {
		if (result != null && !BillingAccountDto.class.isAssignableFrom(result.getClass())) {
			// result is of a different type, log warning and return immediately
			log.warn("Result is not a BillingAccountDto. Skipping filter...");
			return result;
		}

		// retrieve the associated UserAccountsDto
		BillingAccountDto dto = (BillingAccountDto) result;
		UserAccountsDto userAccountsDto = dto.getUserAccounts();

		UserAccount userAccount = null;
		boolean entityAllowed = false;

		List<UserAccountDto> filteredList = new ArrayList<>();
		Set<SecuredEntity> allowedUserAccounts = user.getSecuredEntitiesMap().get(UserAccount.class);

		for (UserAccountDto userAccountDto : userAccountsDto.getUserAccount()) {
			userAccount = new UserAccount();
			userAccount.setCode(userAccountDto.getCode());
			entityAllowed = false;
			if (allowedUserAccounts != null && !allowedUserAccounts.isEmpty()) {
				log.debug("UserAccount: {} is checked against allowed user accounts list: {}.", userAccount, allowedUserAccounts);
				// this means that the user is only allowed to access specific
				// user accounts.
				for (SecuredEntity allowedBillingAccount : allowedUserAccounts) {
					if (allowedBillingAccount.equals(userAccount)) {
						log.debug("User account was found in allowed user accounts list.");
						entityAllowed = true;
						break;
					}
				}
			} else {
				// this means that the user does not have only specific customer
				// accounts allowed, so we check the entity and its parents for
				// access
				log.debug("Checking user account access authorization.");
				entityAllowed = securedBusinessEntityService.isEntityAllowed(userAccount, user, false);
			}
			if (entityAllowed) {
				log.debug("Adding billing account {} to filtered list.", userAccount);
				filteredList.add(userAccountDto);
			}
		}

		userAccountsDto.getUserAccount().clear();
		userAccountsDto.getUserAccount().addAll(filteredList);
		log.debug("New user accounts dto: {}", userAccountsDto);

		return dto;
	}

}
