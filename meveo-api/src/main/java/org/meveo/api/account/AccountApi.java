package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountApi extends BaseApi {

	@Inject
	private CountryService countryService;

	@Inject
	private TitleService titleService;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	public void populate(AccountDto postData, AccountEntity accountEntity, User currentUser)
			throws EntityDoesNotExistsException {
		Address address = new Address();
		if (postData.getAddress() != null) {
			// check country
			if (countryService.findByCode(postData.getAddress().getCountry()) == null) {
				throw new EntityDoesNotExistsException(Country.class, postData.getAddress().getCountry());
			}

			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress2(postData.getAddress().getAddress3());
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
				Title title = titleService.findByCode(currentUser.getProvider(), postData.getName().getTitle());
				if (title == null) {
					throw new EntityDoesNotExistsException(Title.class, postData.getName().getTitle());
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

		// populate customFields
		if (postData.getCustomFields() != null) {
			for (CustomFieldDto cf : postData.getCustomFields()) {
				CustomFieldInstance cfi = new CustomFieldInstance();
				cfi.setAccount(accountEntity);
				cfi.setActive(true);
				cfi.setCode(cf.getCode());
				cfi.setDateValue(cf.getDateValue());
				cfi.setDescription(cf.getDescription());
				cfi.setDoubleValue(cf.getDoubleValue());
				cfi.setLongValue(cf.getLongValue());
				cfi.setProvider(currentUser.getProvider());
				cfi.setStringValue(cf.getStringValue());
				cfi.updateAudit(currentUser);
				accountEntity.getCustomFields().put(cfi.getCode(), cfi);
			}
		}
	}

	public void updateAccount(AccountEntity accountEntity, AccountDto postData, User currentUser)
			throws EntityDoesNotExistsException {
		Address address = accountEntity.getAddress() == null ? new Address() : accountEntity.getAddress();
		if (postData.getAddress() != null) {
			// check country
			if (countryService.findByCode(postData.getAddress().getCountry()) == null) {
				throw new EntityDoesNotExistsException(Country.class, postData.getAddress().getCountry());
			}

			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress2(postData.getAddress().getAddress3());
			address.setZipCode(postData.getAddress().getZipCode());
			address.setCity(postData.getAddress().getCity());
			address.setCountry(postData.getAddress().getCountry());
			address.setState(postData.getAddress().getState());
		}

		Name name = accountEntity.getName() == null ? new Name() : accountEntity.getName();
		if (postData.getName() != null) {
			name.setFirstName(postData.getName().getFirstName());
			name.setLastName(postData.getName().getLastName());
			if (!StringUtils.isBlank(postData.getName().getTitle())) {
				Title title = titleService.findByCode(currentUser.getProvider(), postData.getName().getTitle());
				if (title == null) {
					throw new EntityDoesNotExistsException(Title.class, postData.getName().getTitle());
				} else {
					name.setTitle(title);
				}
			}
		}

		accountEntity.setDescription(postData.getDescription());
		accountEntity.setExternalRef1(postData.getExternalRef1());
		accountEntity.setExternalRef2(postData.getExternalRef2());
		accountEntity.setAddress(address);
		accountEntity.setName(name);

		if (accountEntity.getCustomFields() == null || accountEntity.getCustomFields().size() <= 0) {
			for (CustomFieldDto cf : postData.getCustomFields()) {
				CustomFieldInstance cfi = new CustomFieldInstance();
				cfi.setAccount(accountEntity);
				cfi.setActive(true);
				cfi.setCode(cf.getCode());
				cfi.setDateValue(cf.getDateValue());
				cfi.setDescription(cf.getDescription());
				cfi.setDoubleValue(cf.getDoubleValue());
				cfi.setLongValue(cf.getLongValue());
				cfi.setProvider(currentUser.getProvider());
				cfi.setStringValue(cf.getStringValue());
				cfi.updateAudit(currentUser);
				accountEntity.getCustomFields().put(cfi.getCode(), cfi);
			}
		} else {
			for (CustomFieldDto cf : postData.getCustomFields()) {
				CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(), accountEntity);
				if (cfi != null) {
					cfi.setAccount(accountEntity);
					cfi.setActive(true);
					cfi.setCode(cf.getCode());
					cfi.setDateValue(cf.getDateValue());
					cfi.setDescription(cf.getDescription());
					cfi.setDoubleValue(cf.getDoubleValue());
					cfi.setLongValue(cf.getLongValue());
					cfi.setStringValue(cf.getStringValue());
					cfi.updateAudit(currentUser);
				} else {
					// create
					cfi = new CustomFieldInstance();
					cfi.setCode(cf.getCode());
					cfi.setAccount(accountEntity);

					cfi.setActive(true);
					cfi.setCode(cf.getCode());
					cfi.setDateValue(cf.getDateValue());
					cfi.setDescription(cf.getDescription());
					cfi.setDoubleValue(cf.getDoubleValue());
					cfi.setLongValue(cf.getLongValue());
					cfi.setStringValue(cf.getStringValue());

					customFieldInstanceService.create(cfi, currentUser, currentUser.getProvider());
				}
			}
		}
	}

}
