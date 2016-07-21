package org.meveo.api.security.filter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.service.security.SecuredBusinessEntityService;

public class AccountDtoFilter extends SecureMethodResultFilter {

	@Inject
	private SecuredBusinessEntityService securedBusinessEntityService;

	@SuppressWarnings("unchecked")
	@Override
	public Object filterResult(Object result, User user) throws MeveoApiException {
		if (result != null && !AccountDto.class.isAssignableFrom(result.getClass())) {
			// result is of a different type, log warning and return immediately
			log.warn("Result is not an AccountDto. Skipping filter...");
			return result;
		}

		// retrieve the associated CustomerAccountsDto
		AccountDto accountDto = (AccountDto) result;
		FilterResults filterResults = accountDto.getClass().getAnnotation(FilterResults.class);

		if (filterResults == null) {
			// result is not annotated for filtering, log warning and return
			// immediately
			log.warn("Result is not annotated with FilterResults. Skipping filter...");
			return result;
		}

		BusinessEntity account = null;
		boolean entityAllowed = false;
		List<AccountDto> filteredList = new ArrayList<>();

		List<AccountDto> childAccounts;
		try {
			childAccounts = (List<AccountDto>) getItemsForFiltering(accountDto, filterResults.property());
		} catch (IllegalAccessException e) {
			throw new InvalidParameterException(String.format("Failed to retrieve property: %s of account %s.", filterResults.property(), accountDto));
		}

		for (AccountDto childAccount : childAccounts) {
			try {
				account = filterResults.entityClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new InvalidParameterException(String.format("Failed to create new instance of: %s", filterResults.entityClass()));
			}
			account.setCode(childAccount.getCode());
			entityAllowed = securedBusinessEntityService.isEntityAllowed(account, user, false);

			if (entityAllowed) {
				log.debug("Adding account {} to filtered list.", account);
				childAccount = (AccountDto) filterResult(childAccount, user);
				filteredList.add(childAccount);
			}
		}

		childAccounts.clear();
		childAccounts.addAll(filteredList);
		log.debug("New account dto: {}", accountDto);

		return accountDto;
	}

	/**
	 * This is a recursive function that aims to walk through the properties of
	 * an object until it gets the final value.
	 * 
	 * e.g. If we received an Object named obj and given a string property of
	 * code.name, then the value of obj.code.name will be returned.
	 * 
	 * Logic is the same as {@link ObjectPropertyParser#getPropertyValue()}
	 * 
	 * @param obj
	 *            The object that contains the property value.
	 * @param property
	 *            The property of the object that contains the data.
	 * @return The value of the data contained in obj.property
	 * @throws IllegalAccessException
	 */
	private Object getItemsForFiltering(Object obj, String property) throws IllegalAccessException {
		int fieldIndex = property.indexOf(".");
		if (fieldIndex == -1) {
			return FieldUtils.readField(obj, property, true);
		}
		String fieldName = property.substring(0, fieldIndex);
		Object fieldValue = FieldUtils.readField(obj, fieldName, true);
		return getItemsForFiltering(fieldValue, property.substring(fieldIndex + 1));
	}

}
