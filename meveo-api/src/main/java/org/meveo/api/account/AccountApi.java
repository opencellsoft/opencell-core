package org.meveo.api.account;

import java.util.List;

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
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private CountryService countryService;

	@Inject
	private TitleService titleService;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	public void populate(AccountDto postData, AccountEntity accountEntity, User currentUser,
			AccountLevelEnum accountLevel) throws EntityDoesNotExistsException {
		Address address = new Address();
		if (postData.getAddress() != null) {
			// check country
			if (!StringUtils.isBlank(postData.getAddress().getCountry())
					&& countryService.findByCode(postData.getAddress().getCountry()) == null) {
				throw new EntityDoesNotExistsException(Country.class, postData.getAddress().getCountry());
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
			for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) {
				// check if custom field exists has a template
				List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService
						.findByAccountLevel(accountLevel);
				if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
					for (CustomFieldTemplate cft : customFieldTemplates) {
						if (cf.getCode().equals(cft.getCode())) {
							// create
							CustomFieldInstance cfiNew = new CustomFieldInstance();
							cfiNew.setAccount(accountEntity);
							cfiNew.setActive(true);
							cfiNew.setCode(cf.getCode());
							cfiNew.setDateValue(cf.getDateValue());
							cfiNew.setDescription(cf.getDescription());
							cfiNew.setDoubleValue(cf.getDoubleValue());
							cfiNew.setLongValue(cf.getLongValue());
							cfiNew.setProvider(currentUser.getProvider());
							cfiNew.setStringValue(cf.getStringValue());
							cfiNew.updateAudit(currentUser);
							accountEntity.getCustomFields().put(cfiNew.getCode(), cfiNew);
						}
					}
				} else {
					log.warn("No custom field template defined.");
				}
			}
		}
	}

	public void updateAccount(AccountEntity accountEntity, AccountDto postData, User currentUser,
			AccountLevelEnum accountLevel) throws EntityDoesNotExistsException {
		Address address = accountEntity.getAddress() == null ? new Address() : accountEntity.getAddress();
		if (postData.getAddress() != null) {
			// check country
			if (!StringUtils.isBlank(postData.getAddress().getCountry())
					&& countryService.findByCode(postData.getAddress().getCountry()) == null) {
				throw new EntityDoesNotExistsException(Country.class, postData.getAddress().getCountry());
			}

			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress3(postData.getAddress().getAddress3());
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

		// populate customFields
		if (postData.getCustomFields() != null) {
			for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) {
				// check if custom field exists has a template
				List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService
						.findByAccountLevel(accountLevel);
				boolean found = false;
				if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
					for (CustomFieldTemplate cft : customFieldTemplates) {
						if (cf.getCode().equals(cft.getCode())) {
							found = true;
							CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(),
									accountEntity,currentUser.getProvider());
							if (cfi != null) {
								// update
								cfi.setActive(true);
								cfi.setDateValue(cf.getDateValue());
								cfi.setDescription(cf.getDescription());
								cfi.setDoubleValue(cf.getDoubleValue());
								cfi.setLongValue(cf.getLongValue());
								cfi.setStringValue(cf.getStringValue());
								cfi.updateAudit(currentUser);
							} else {
								// create
								CustomFieldInstance cfiNew = new CustomFieldInstance();
								cfiNew.setAccount(accountEntity);
								cfiNew.setActive(true);
								cfiNew.setCode(cf.getCode());
								cfiNew.setDateValue(cf.getDateValue());
								cfiNew.setDescription(cf.getDescription());
								cfiNew.setDoubleValue(cf.getDoubleValue());
								cfiNew.setLongValue(cf.getLongValue());
								cfiNew.setProvider(currentUser.getProvider());
								cfiNew.setStringValue(cf.getStringValue());
								cfiNew.updateAudit(currentUser);
								accountEntity.getCustomFields().put(cfiNew.getCode(), cfiNew);
							}
						}
					}
				} else {
					log.warn("No custom field template defined.");
				}

				if (!found) {
					log.warn("No custom field template with code={}", cf.getCode());
				}
			}
		}
	}

}
