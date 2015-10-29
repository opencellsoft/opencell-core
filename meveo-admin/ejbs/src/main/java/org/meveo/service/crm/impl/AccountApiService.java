package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public abstract class AccountApiService extends BaseApi {

	@Inject
	private CountryService countryService;

	@Inject
	private TitleService titleService;
	
	@Inject
	private CustomerAccountService customerAccountService;
	
	@Inject
	private BillingAccountService billingAccountService;
	
	@Inject
	private UserAccountService userAccountService;

    public void populate(AccountDto postData, AccountEntity accountEntity, User currentUser) throws MeveoApiException {
        populate(postData, accountEntity, currentUser, true);
    }

    public void populate(AccountDto postData, AccountEntity accountEntity, User currentUser, boolean checkCustomField) throws MeveoApiException {
		Address address = new Address();
		if (postData.getAddress() != null) {
			// check country
			if (!StringUtils.isBlank(postData.getAddress().getCountry())
					&& countryService.findByCode(postData.getAddress()
							.getCountry()) == null) {
				throw new EntityDoesNotExistsException(Country.class, postData
						.getAddress().getCountry());
			}

			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress3(postData.getAddress().getAddress3());
			address.setZipCode(postData.getAddress().getZipCode());
			address.setCity(postData.getAddress().getCity());
			address.setCountry(postData.getAddress().getCountry());
			address.setState(postData.getAddress().getState());
		}

		Name name = new Name();
		if (postData.getName() != null) {
			name.setFirstName(postData.getName().getFirstName());
			name.setLastName(postData.getName().getLastName());
			if (!StringUtils.isBlank(postData.getName().getTitle())) {
				Title title = titleService.findByCode(
						currentUser.getProvider(), postData.getName()
								.getTitle());
				if (title == null) {
					throw new EntityDoesNotExistsException(Title.class,
							postData.getName().getTitle());
				} else {
					name.setTitle(title);
				}
			}
		}

		accountEntity.setCode(postData.getCode());
		accountEntity.setDescription(postData.getDescription());
		accountEntity.setExternalRef1(postData.getExternalRef1());
		accountEntity.setExternalRef2(postData.getExternalRef2());
		accountEntity.setAddress(address);
		accountEntity.setName(name);

        // Validate and populate customFields
		if (postData.getCustomFields() != null
				&& !postData.getCustomFields().isEmpty()) {
			try {
                populateCustomFields(postData.getCustomFields().getCustomField(), accountEntity, currentUser, checkCustomField);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(
						"Failed to associate custom field instance to an entity",
						e);
				throw new MeveoApiException(
						"Failed to associate custom field instance to an entity");
			}
		}
	}

	public void updateAccount(AccountEntity accountEntity, AccountDto postData, User currentUser) throws MeveoApiException {
		updateAccount(accountEntity, postData, currentUser, true);
	}

	public void updateAccount(AccountEntity accountEntity, AccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException {
		Address address = accountEntity.getAddress() == null ? new Address() : accountEntity.getAddress();
		if (postData.getAddress() != null) {
			// check country
			if (!StringUtils.isBlank(postData.getAddress().getCountry())
					&& countryService.findByCode(postData.getAddress()
							.getCountry()) == null) {
				throw new EntityDoesNotExistsException(Country.class, postData
						.getAddress().getCountry());
			}

			if (!StringUtils.isBlank(postData.getAddress().getAddress1())) {
				address.setAddress1(postData.getAddress().getAddress1());
			}
			if (!StringUtils.isBlank(postData.getAddress().getAddress2())) {
				address.setAddress2(postData.getAddress().getAddress2());
			}
			if (!StringUtils.isBlank(postData.getAddress().getAddress3())) {
				address.setAddress3(postData.getAddress().getAddress3());
			}
			if (!StringUtils.isBlank(postData.getAddress().getZipCode())) {
				address.setZipCode(postData.getAddress().getZipCode());
			}
			if (!StringUtils.isBlank(postData.getAddress().getCity())) {
				address.setCity(postData.getAddress().getCity());
			}
			if (!StringUtils.isBlank(postData.getAddress().getCountry())) {
				address.setCountry(postData.getAddress().getCountry());
			}
			if (!StringUtils.isBlank(postData.getAddress().getState())) {
				address.setState(postData.getAddress().getState());
			}

			accountEntity.setAddress(address);
		}

		Name name = accountEntity.getName() == null ? new Name()
				: accountEntity.getName();
		if (postData.getName() != null) {
			if (!StringUtils.isBlank(postData.getName().getFirstName())) {
				name.setFirstName(postData.getName().getFirstName());
			}
			if (!StringUtils.isBlank(postData.getName().getLastName())) {
				name.setLastName(postData.getName().getLastName());
			}
			if (!StringUtils.isBlank(postData.getName().getTitle())) {
				Title title = titleService.findByCode(
						currentUser.getProvider(), postData.getName()
								.getTitle());
				if (title == null) {
					throw new EntityDoesNotExistsException(Title.class,
							postData.getName().getTitle());
				} else {
					name.setTitle(title);
				}
			}

			accountEntity.setName(name);
		}

		if (!StringUtils.isBlank(postData.getDescription())) {
			accountEntity.setDescription(postData.getDescription());
		}
		if (!StringUtils.isBlank(postData.getExternalRef1())) {
			accountEntity.setExternalRef1(postData.getExternalRef1());
		}
		if (!StringUtils.isBlank(postData.getExternalRef2())) {
			accountEntity.setExternalRef2(postData.getExternalRef2());
		}

        // Validate and populate customFields
        if (postData.getCustomFields() != null) {
            try {
                populateCustomFields(postData.getCustomFields().getCustomField(), accountEntity, currentUser, checkCustomFields);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new MeveoApiException("Failed to associate custom field instance to an entity");
            }
        }
	}

	public void checkEntityDefaultLevel(AccountEntity entity)
			throws DuplicateDefaultAccountException {
		if (entity != null && entity.getDefaultLevel() != null
				&& entity.getDefaultLevel()) {
			if (entity instanceof Customer) {
				checkCustomerDefaultLevel((Customer) entity);
			} else if (entity instanceof CustomerAccount) {
				checkCustomerAccountDefaultLevel((CustomerAccount) entity);
			} else if (entity instanceof BillingAccount) {
				checkBillingAccountDefaultLevel((BillingAccount) entity);
			} else if (entity instanceof UserAccount) {
				checkUserAccountDefaultLevel((UserAccount) entity);
			} else {
				throw new IllegalArgumentException(
						"Passed parameter is not yet supported");
			}
		}
	}

	private void checkCustomerDefaultLevel(Customer customer)
			throws DuplicateDefaultAccountException {
		if (customer != null && customer.getCustomerAccounts() != null
				&& customer.getCustomerAccounts().size() > 0) {
			for (CustomerAccount ca : customer.getCustomerAccounts()) {
				if (customerAccountService.isDuplicationExist(ca)) {
					ca.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				} else {
					checkCustomerAccountDefaultLevel(ca);
				}
			}
		}
	}

	private void checkCustomerAccountDefaultLevel(
			CustomerAccount customerAccount)
			throws DuplicateDefaultAccountException {
		if (customerAccount != null
				&& customerAccount.getBillingAccounts() != null
				&& customerAccount.getBillingAccounts().size() > 0) {
			for (BillingAccount ba : customerAccount.getBillingAccounts()) {
				if (billingAccountService.isDuplicationExist(ba)) {
					ba.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				} else {
					checkBillingAccountDefaultLevel(ba);
				}
			}
		}
	}

	private void checkBillingAccountDefaultLevel(BillingAccount billingAccount)
			throws DuplicateDefaultAccountException {
		if (billingAccount != null && billingAccount.getUsersAccounts() != null
				&& billingAccount.getUsersAccounts().size() > 0) {
			for (UserAccount ua : billingAccount.getUsersAccounts()) {
				checkUserAccountDefaultLevel(ua);
			}
		}
	}

	private void checkUserAccountDefaultLevel(UserAccount userAccount)
			throws DuplicateDefaultAccountException {
		if (userAccount != null) {
			if (userAccountService.isDuplicationExist(userAccount)) {
				userAccount.setDefaultLevel(false);
				throw new DuplicateDefaultAccountException();
			}
		}
	}
}
