package org.meveo.service.security.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.model.SecuredBusinessEntityFilter;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.meveo.service.security.SecuredBusinessEntityServiceFactory;

@Stateless
public class CustomerAccountDtoFilter extends SecuredBusinessEntityFilter {

	@Inject
	private SecuredBusinessEntityServiceFactory serviceFactory;
	
	@Inject
	private SecuredBusinessEntityFilterFactory filterFactory;
	
	@Override
	public Object filterResult(Object result, User user, Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap) {
		if (result != null && !CustomerAccountDto.class.isAssignableFrom(result.getClass())) {
			// result is of a different type, log warning and return immediately
			log.warn("Result is not a CustomerAccountDto. Skipping filter...");
			return result;
		}

		CustomerAccountDto dto = (CustomerAccountDto) result;
		BillingAccountsDto billingAccountsDto = dto.getBillingAccounts();

		BillingAccount billingAccount = null;
		boolean entityAllowed = false;

		List<BillingAccountDto> filteredList = new ArrayList<>();
		Set<SecuredEntity> allowedBillingAccounts = securedEntitiesMap.get(BillingAccount.class);
		SecuredBusinessEntityFilter billingAccountDtoFilter = filterFactory.getFilter(BillingAccountDtoFilter.class);

		for (BillingAccountDto billingAccountDto : billingAccountsDto.getBillingAccount()) {
			billingAccount = new BillingAccount();
			billingAccount.setCode(billingAccountDto.getCode());
			entityAllowed = false;
			if (allowedBillingAccounts != null && !allowedBillingAccounts.isEmpty()) {
				log.debug("BillingAccount: {} is checked against allowed billing accounts list: {}.", billingAccount, allowedBillingAccounts);
				// this means that the user is only allowed to access specific
				// billing accounts.
				for (SecuredEntity allowedBillingAccount : allowedBillingAccounts) {
					if (allowedBillingAccount.equals(billingAccount)) {
						log.debug("Billing account was found in allowed billing accounts list.");
						entityAllowed = true;
						break;
					}
				}
			} else {
				// this means that the user does not have only specific customer
				// accounts allowed, so we check the entity and its parents for
				// access
				log.debug("Checking billing account access authorization.");
				entityAllowed = SecuredBusinessEntityService.isEntityAllowed(billingAccount, user, serviceFactory, false);
			}
			if (entityAllowed) {
				log.debug("Adding billing account {} to filtered list.", billingAccount);
				billingAccountDto = (BillingAccountDto) billingAccountDtoFilter.filterResult(billingAccountDto, user, securedEntitiesMap);
				filteredList.add(billingAccountDto);
			}
		}

		billingAccountsDto.getBillingAccount().clear();
		billingAccountsDto.getBillingAccount().addAll(filteredList);
		log.debug("New billing accounts dto: {}", billingAccountsDto);
		
		return dto;
	}

}
