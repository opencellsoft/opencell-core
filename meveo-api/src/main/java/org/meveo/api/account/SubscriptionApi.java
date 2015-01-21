package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.SubscriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class SubscriptionApi extends BaseApi {

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private UserAccountService userAccountService;

	public void create(SubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getOfferTemplate())
				&& !StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			if (subscriptionService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(Subscription.class, postData.getCode());
			}

			UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount(), provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
			}

			Subscription subscription = new Subscription();
			subscription.setCode(postData.getCode());
			subscription.setUserAccount(userAccount);
			subscription.setOffer(offerTemplate);
			subscription.setSubscriptionDate(postData.getSubscriptionDate());
			subscription.setTerminationDate(postData.getTerminationDate());

			subscriptionService.create(subscription, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getUserAccount())) {
				missingParameters.add("userAccount");
			}
			if (StringUtils.isBlank(postData.getOfferTemplate())) {
				missingParameters.add("offerTemplate");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(SubscriptionDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getUserAccount()) && !StringUtils.isBlank(postData.getOfferTemplate())
				&& !StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			Subscription subscription = subscriptionService.findByCode(postData.getCode(), provider);
			if (subscription == null) {
				throw new EntityDoesNotExistsException(Subscription.class, postData.getCode());
			}

			UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount(), provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
			}

			subscription.setUserAccount(userAccount);
			subscription.setOffer(offerTemplate);
			subscription.setSubscriptionDate(postData.getSubscriptionDate());
			subscription.setTerminationDate(postData.getTerminationDate());

			subscriptionService.update(subscription, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getUserAccount())) {
				missingParameters.add("userAccount");
			}
			if (StringUtils.isBlank(postData.getOfferTemplate())) {
				missingParameters.add("offerTemplate");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
