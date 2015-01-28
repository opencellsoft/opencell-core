package org.meveo.api.account;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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

	public void create(AccessDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getSubscription())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			if (accessService.findByUserIdAndSubscription(postData.getUserAccount(), subscription) != null) {
				throw new EntityAlreadyExistsException(Access.class, "user.code=" + postData.getUserAccount()
						+ ";subscription.code=" + postData.getSubscription());
			}

			Access access = new Access();
			access.setStartDate(postData.getStartDate());
			access.setEndDate(postData.getEndDate());
			access.setAccessUserId(postData.getUserAccount());
			access.setSubscription(subscription);

			accessService.create(access, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getUserAccount())) {
				missingParameters.add("user");
			}
			if (postData.getSubscription() == null) {
				missingParameters.add("subscription");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(AccessDto postData, User currentUser) throws MeveoApiException {
		if (postData.getAccessId() != null && !StringUtils.isBlank(postData.getUserAccount())
				&& !StringUtils.isBlank(postData.getSubscription())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
			}

			Access access = accessService.findById(postData.getAccessId());
			if (access == null) {
				throw new EntityDoesNotExistsException(Access.class, postData.getAccessId());
			}

			access.setStartDate(postData.getStartDate());
			access.setEndDate(postData.getEndDate());
			access.setAccessUserId(postData.getUserAccount());
			access.setSubscription(subscription);

			accessService.update(access, currentUser);
		} else {
			if (postData.getAccessId() == null) {
				missingParameters.add("accessId");
			}
			if (StringUtils.isBlank(postData.getUserAccount())) {
				missingParameters.add("user");
			}
			if (postData.getSubscription() == null) {
				missingParameters.add("subscription");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public AccessDto find(Long accessId, Provider provider) throws MeveoApiException {
		if (accessId != null) {
			Access access = accessService.findById(accessId);
			if (access == null) {
				throw new EntityDoesNotExistsException(Access.class, accessId);
			}

			return new AccessDto(access);
		} else {
			missingParameters.add("accessId");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(Long accessId, Provider provider) throws MeveoApiException {
		if (accessId != null) {
			Access access = accessService.findById(accessId);
			if (access == null) {
				throw new EntityDoesNotExistsException(Access.class, accessId);
			}

			accessService.remove(access);
		} else {
			missingParameters.add("accessId");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public List<AccessDto> list(String subscriptionCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(subscriptionCode)) {
			Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
			}

			List<Access> accesses = accessService.listBySubscription(subscription);
			if (accesses != null && accesses.size() > 0) {
				List<AccessDto> accessDtos = new ArrayList<AccessDto>();
				for (Access access : accesses) {
					accessDtos.add(new AccessDto(access));
				}

				return accessDtos;
			}

			return null;
		} else {
			missingParameters.add("subscriptionCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
