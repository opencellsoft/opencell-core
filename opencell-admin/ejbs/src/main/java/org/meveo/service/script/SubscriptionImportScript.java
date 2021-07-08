package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;

public class SubscriptionImportScript extends Script {

	private static final String DATE_FORMAT_PATTERN = "dd/MM/yy";
	private static final String RECORD_VARIABLE_NAME = "record";
	private SubscriptionTerminationReasonService reasonService= (SubscriptionTerminationReasonService) getServiceInterface("SubscriptionTerminationReasonService");
	private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
	private UserAccountService userAccountService = (UserAccountService) getServiceInterface("UserAccountService");
	private SellerService sellerService = (SellerService) getServiceInterface("SellerService");
	private OfferTemplateService offerService = (OfferTemplateService) getServiceInterface("OfferTemplateService");

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		try {
			Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
			if (recordMap != null && !recordMap.isEmpty()) {
				String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
				if (!"SUBSCRIPTION".equals(OC_ENTITY)) {
					throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
				}
				String OC_ACTION = (String) recordMap.get("OC_ACTION");
				if (!Stream.of(SubscriptionActionEnum.values()).anyMatch(e -> e.toString().equals(OC_ACTION))) {
					throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
				}
				SubscriptionActionEnum action = SubscriptionActionEnum.valueOf(OC_ACTION);

				String OC_subscription_code = (String) recordMap.get("OC_subscription_code");
				Subscription subscription = subscriptionService
						.findByCode(OC_subscription_code);
				if (subscription == null && !SubscriptionActionEnum.CREATE.equals(action)) {
					throw new ValidationException("no Subscription Found for subscriptionCode: '"
							+ OC_subscription_code + "'");
				}
				if (subscription != null && SubscriptionActionEnum.CREATE.equals(action)) {
					throw new ValidationException("subscription already exists with code: '"
							+ OC_subscription_code + "'");
				}

				switch (action) {
				case CREATE:
					subscription = new Subscription();
					subscription.setCode(OC_subscription_code);
					mapSubscriptionValues(recordMap, subscription);
					subscriptionService.create(subscription);
					break;
				case ACTIVATE:
					subscriptionService.activateInstantiatedService(subscription);
					break;
				case RESUME:
					subscriptionService.subscriptionReactivation(subscription, new Date());
					break;
				case SUSPEND:
					subscriptionService.subscriptionSuspension(subscription, new Date());
					break;
				case TERMINATE:
					DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
					Date OC_terminationDate = dateFormat.parse((String) recordMap.get("OC_terminationDate"));
					String terminationCode = (String)recordMap.get("OC_subscriptionTerminationReason_code");
					if(terminationCode==null) {
						throw new ValidationException("OC_subscriptionTerminationReason_code is mandatory to terminate subscription" );
					}
					SubscriptionTerminationReason terminationReason = reasonService.findByCodeReason(terminationCode);
					if(terminationReason==null) {
						throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, terminationCode);
					}
					subscription.setSubscriptionTerminationReason(terminationReason);
					subscriptionService.terminateSubscription(subscription, OC_terminationDate, terminationReason, null);
					break;
				case UPDATE:
					mapSubscriptionValues(recordMap, subscription);
					subscriptionService.update(subscription);
					break;
				default:
					break;
				}
			}
		} catch (Exception exception) {
			throw new BusinessException(exception);
		}
	}

	private void mapSubscriptionValues(Map<String, Object> recordMap, Subscription subscription) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		String OC_seller_code = (String) recordMap.get("OC_seller_code");
		String OC_offer_code = (String) recordMap.get("OC_offer_code");
		String OC_userAccount_code = (String) recordMap.get("OC_userAccount_code");
		Date subscriptionDate = dateFormat.parse((String) recordMap.get("OC_subscriptionDate"));
		Date endAgreementDate = dateFormat.parse((String) recordMap.get("OC_endAgreementDate"));
		String OC_subscription_description = (String) recordMap.get("OC_subscription_description");

		Date OC_terminationDate = dateFormat.parse((String) recordMap.get("OC_terminationDate"));

		UserAccount userAccount = userAccountService.findByCode(OC_userAccount_code);
		if(userAccount==null) {
			throw new EntityDoesNotExistsException(UserAccount.class, OC_userAccount_code);
		}
		Seller seller = sellerService.findByCode(OC_seller_code);
		if(seller==null) {
			throw new EntityDoesNotExistsException(Seller.class, OC_seller_code);
		}
		OfferTemplate offer = offerService.findByCode(OC_offer_code);
		if(offer==null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, OC_offer_code);
		}
		subscription.setUserAccount(userAccount);
		subscription.setSeller(seller);
		subscription.setOffer(offer);
		subscription.setTerminationDate(OC_terminationDate);
		subscription.setSubscriptionDate(subscriptionDate);
		subscription.setEndAgreementDate(endAgreementDate);
		subscription.setDescription(OC_subscription_description);
		recordMap.keySet().stream().filter(key -> key.startsWith("CF_"))
				.forEach(key -> subscription.setCfValue(key.substring(3), recordMap.get(key)));
	}

	public enum SubscriptionActionEnum {
		CREATE, UPDATE, SUSPEND, RESUME, ACTIVATE, TERMINATE
	}
}