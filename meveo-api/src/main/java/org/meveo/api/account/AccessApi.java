package org.meveo.api.account;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccessesDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.medina.impl.AccessService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccessApi extends BaseApi {

    @Inject
    private AccessService accessService;

    @Inject
    private SubscriptionService subscriptionService;

    public void create(AccessDto postData, User currentUser) throws MeveoApiException, BusinessException {
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
                throw new MeveoApiException(MeveoApiErrorCodeEnum.DUPLICATE_ACCESS, "Duplicate subscription / access point pair.");
            }

            accessService.create(access, currentUser);

            // populate customFields
            try {
                populateCustomFields(postData.getCustomFields(), access, true, currentUser);
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
            }

        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (postData.getSubscription() == null) {
                missingParameters.add("subscription");
            }

            handleMissingParameters();
        }
    }

    public void update(AccessDto postData, User currentUser) throws MeveoApiException, BusinessException {
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

            access = accessService.update(access, currentUser);

            // populate customFields
            try {
                populateCustomFields(postData.getCustomFields(), access, false, currentUser);
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
            }

        } else {
            if (postData.getCode() == null) {
                missingParameters.add("code");
            }
            if (postData.getSubscription() == null) {
                missingParameters.add("subscription");
            }

            handleMissingParameters();
        }
    }

    public AccessDto find(String accessCode, String subscriptionCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(accessCode)) {
            missingParameters.add("accessCode");
        }
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        Access access = accessService.findByUserIdAndSubscription(accessCode, subscription);
        if (access == null) {
            throw new EntityDoesNotExistsException(Access.class, accessCode);
        }

        return new AccessDto(access, entityToDtoConverter.getCustomFieldsDTO(access));
    }

    public void remove(String accessCode, String subscriptionCode, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(accessCode) && !StringUtils.isBlank(subscriptionCode)) {
            Subscription subscription = subscriptionService.findByCode(subscriptionCode, currentUser.getProvider());
            if (subscription == null) {
                throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
            }

            Access access = accessService.findByUserIdAndSubscription(accessCode, subscription);
            if (access == null) {
                throw new EntityDoesNotExistsException(Access.class, accessCode);
            }

            accessService.remove(access, currentUser);
        } else {
            if (StringUtils.isBlank(accessCode)) {
                missingParameters.add("accessCode");
            }
            if (StringUtils.isBlank(subscriptionCode)) {
                missingParameters.add("subscriptionCode");
            }

            handleMissingParameters();
        }
    }

    public AccessesDto listBySubscription(String subscriptionCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }
        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        AccessesDto result = new AccessesDto();
        List<Access> accesses = accessService.listBySubscription(subscription);
        if (accesses != null) {
            for (Access ac : accesses) {
                result.getAccess().add(new AccessDto(ac, entityToDtoConverter.getCustomFieldsDTO(ac)));
            }
        }

        return result;
    }

    /**
     * 
     * Create or update access based on the access user id and its subscription
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(AccessDto postData, User currentUser) throws MeveoApiException, BusinessException {

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), currentUser.getProvider());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        Access access = accessService.findByUserIdAndSubscription(postData.getCode(), subscription);

        if (access == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
    public void createOrUpdatePartial(AccessDto accessDto,User currentUser) throws MeveoApiException, BusinessException{
    	AccessDto existedAccessDto = null;
		try {
			existedAccessDto = find(accessDto.getCode(), accessDto.getSubscription(), currentUser.getProvider());
		} catch (Exception e) {
			existedAccessDto = null;
		}
		if (existedAccessDto == null) {
			create(accessDto, currentUser);
		} else {

			if (!StringUtils.isBlank(accessDto.getStartDate())) {
				existedAccessDto.setStartDate(accessDto.getStartDate());
			}
			if (!StringUtils.isBlank(accessDto.getEndDate())) {
				existedAccessDto.setEndDate(accessDto.getEndDate());
			}
			if(!StringUtils.isBlank(accessDto.getCustomFields())){
				existedAccessDto.setCustomFields(accessDto.getCustomFields());
			}
			update(existedAccessDto, currentUser);
		}
    }
}