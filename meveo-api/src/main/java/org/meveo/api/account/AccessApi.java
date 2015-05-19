package org.meveo.api.account;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccessesDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.medina.impl.AccessService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccessApi extends BaseApi {

	@Inject
	private AccessService accessService;

	@Inject
	private SubscriptionService subscriptionService;
	
	@Inject
	private CustomFieldTemplateService customFieldTemplateService;
	
	@Inject
	private CustomFieldInstanceService customFieldInstanceService;
	
	@Inject
	private Logger log;

	public void create(AccessDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getSubscription())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			Access access = new Access();
			access.setStartDate(postData.getStartDate());
			access.setEndDate(postData.getEndDate());
			access.setAccessUserId(postData.getCode());
			access.setSubscription(subscription);

			if (accessService.isDuplicate(access)) {
				throw new MeveoApiException(MeveoApiErrorCode.DUPLICATE_ACCESS, "Duplicate subscription / access point pair.");
			}
			
			// populate customFields
			if (postData.getCustomFields() != null) {
				for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) {
					// check if custom field exists has a template
					List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(AccountLevelEnum.ACC, provider);
				if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
					for (CustomFieldTemplate cft : customFieldTemplates) {
						if (cf.getCode().equals(cft.getCode())) {
							// create
							CustomFieldInstance cfiNew = new CustomFieldInstance();
							cfiNew.setAccess(access);
							cfiNew.setActive(true);
							cfiNew.setCode(cf.getCode());
							cfiNew.setDateValue(cf.getDateValue());
							cfiNew.setDescription(cf.getDescription());
							cfiNew.setDoubleValue(cf.getDoubleValue());
							cfiNew.setLongValue(cf.getLongValue());
							cfiNew.setProvider(currentUser.getProvider());
							cfiNew.setStringValue(cf.getStringValue());
							cfiNew.updateAudit(currentUser);
							access.getCustomFields().put(cfiNew.getCode(), cfiNew);
						}
					}
					} else {
						log.warn("No custom field template defined.");
					}
				}
			}

					accessService.create(access, currentUser, provider);
				} else {
					if (StringUtils.isBlank(postData.getCode())) {
						missingParameters.add("code");
					}
					if (postData.getSubscription() == null) {
						missingParameters.add("subscription");
					}
		
					throw new MissingParameterException(getMissingParametersExceptionMessage());
				}
	}

	public void update(AccessDto postData, User currentUser) throws MeveoApiException {
		if (postData.getCode() != null && !StringUtils.isBlank(postData.getSubscription())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			Access access = accessService.findByUserIdAndSubscription(postData.getCode(), subscription);
			if (access == null) {
				throw new EntityDoesNotExistsException(Access.class, postData.getCode());
			}

			access.setStartDate(postData.getStartDate());
			access.setEndDate(postData.getEndDate());
			// populate customFields
			if (postData.getCustomFields() != null) {
				for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) {
					// check if custom field exists has a template
					List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(AccountLevelEnum.ACC, provider);
					boolean found = false;
					if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
					  for (CustomFieldTemplate cft : customFieldTemplates) {
					    if (cf.getCode().equals(cft.getCode())) {
						found = true;
						CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(), access, currentUser.getProvider());
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
							cfiNew.setAccess(access);
							cfiNew.setActive(true);
							cfiNew.setCode(cf.getCode());
							cfiNew.setDateValue(cf.getDateValue());
							cfiNew.setDescription(cf.getDescription());
							cfiNew.setDoubleValue(cf.getDoubleValue());
							cfiNew.setLongValue(cf.getLongValue());
							cfiNew.setProvider(currentUser.getProvider());
							cfiNew.setStringValue(cf.getStringValue());
							cfiNew.updateAudit(currentUser);
							access.getCustomFields().put(cfiNew.getCode(), cfiNew);
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
			accessService.update(access, currentUser);
		} else {
			if (postData.getCode() == null) {
				missingParameters.add("code");
			}
			if (postData.getSubscription() == null) {
				missingParameters.add("subscription");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public AccessDto find(String accessCode, String subscriptionCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(accessCode) && !StringUtils.isBlank(subscriptionCode)) {
			Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
			}

			Access access = accessService.findByUserIdAndSubscription(accessCode, subscription);
			if (access == null) {
				throw new EntityDoesNotExistsException(Access.class, accessCode);
			}

			return new AccessDto(access);
		} else {
			if (StringUtils.isBlank(accessCode)) {
				missingParameters.add("accessCode");
			}
			if (StringUtils.isBlank(subscriptionCode)) {
				missingParameters.add("subscriptionCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String accessCode, String subscriptionCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(accessCode) && !StringUtils.isBlank(subscriptionCode)) {
			Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
			}

			Access access = accessService.findByUserIdAndSubscription(accessCode, subscription);
			if (access == null) {
				throw new EntityDoesNotExistsException(Access.class, accessCode);
			}

			accessService.remove(access);
		} else {
			if (StringUtils.isBlank(accessCode)) {
				missingParameters.add("accessCode");
			}
			if (StringUtils.isBlank(subscriptionCode)) {
				missingParameters.add("subscriptionCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public AccessesDto listBySubscription(String subscriptionCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(subscriptionCode)) {
			Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
			}

			AccessesDto result = new AccessesDto();
			List<Access> accesses = accessService.listBySubscription(subscription);
			if (accesses != null) {
				for (Access ac : accesses) {
					result.getAccess().add(new AccessDto(ac));
				}
			}

			return result;
		} else {
			missingParameters.add("subscriptionCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
