package org.meveo.api.security.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * This will parse a {@link CustomerDto} result from a
 * {@link SecuredBusinessEntityMethod} annotated method. It will check if the
 * child {@link CustomerAccountsDto} element has items that the user does not have
 * access to. Then it will filter them out and just return the items that are
 * accessible to the user.
 * 
 * @author Tony Alejandro
 *
 */
public class CustomerDtoFilter extends SecureMethodResultFilter {

	@Inject
	private SecuredBusinessEntityService securedBusinessEntityService;
	
	@Inject
	private SecureMethodResultFilterFactory filterFactory;

	@Override
	public Object filterResult(Object result, User user) {
		if (result != null && !CustomerDto.class.isAssignableFrom(result.getClass())) {
			// result is of a different type, log warning and return immediately
			log.warn("Result is not a CustomerDto. Skipping filter...");
			return result;
		}

		// retrieve the associated CustomerAccountsDto
		CustomerDto dto = (CustomerDto) result;
		CustomerAccountsDto customerAccountsDto = dto.getCustomerAccounts();

		CustomerAccount customerAccount = null;
		boolean entityAllowed = false;

		List<CustomerAccountDto> filteredList = new ArrayList<>();
		Set<SecuredEntity> allowedCustomerAccounts = user.getSecuredEntitiesMap().get(CustomerAccount.class);
		SecureMethodResultFilter customerAccountDtoFilter = filterFactory.getFilter(CustomerAccountDtoFilter.class);

		for (CustomerAccountDto customerAccountDto : customerAccountsDto.getCustomerAccount()) {
			customerAccount = new CustomerAccount();
			customerAccount.setCode(customerAccountDto.getCode());
			entityAllowed = false;
			if (allowedCustomerAccounts != null && !allowedCustomerAccounts.isEmpty()) {
				log.debug("CustomerAccount: {} is checked against allowed customer accounts list: {}.", customerAccount, allowedCustomerAccounts);
				// this means that the user is only allowed to access specific
				// customer accounts.
				for (SecuredEntity allowedCustomerAccount : allowedCustomerAccounts) {
					if (allowedCustomerAccount.equals(customerAccount)) {
						log.debug("Customer account was found in allowed customer accounts list.");
						entityAllowed = true;
						break;
					}
				}
			} else {
				// this means that the user does not have only specific customer
				// accounts allowed, so we check the entity and its parents for
				// access
				log.debug("Checking customer account access authorization.");
				entityAllowed = customerAccount == null || securedBusinessEntityService.isEntityAllowed(customerAccount, user, false);
			}
			if (entityAllowed) {
				log.debug("Adding customer account {} to filtered list.", customerAccount);
				customerAccountDto = (CustomerAccountDto) customerAccountDtoFilter.filterResult(customerAccountDto, user);
				filteredList.add(customerAccountDto);
			}
		}

		customerAccountsDto.getCustomerAccount().clear();
		customerAccountsDto.getCustomerAccount().addAll(filteredList);
		log.debug("New customer accounts dto: {}", customerAccountsDto);
		
		return dto;
	}
}
