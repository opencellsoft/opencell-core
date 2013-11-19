package org.meveo.api;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.SubscriptionWithCreditLimitDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.OfferTemplateDoesNotExistsException;
import org.meveo.api.exception.SellerDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SubscriptionWithCreditLimitServiceApi extends BaseApi {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private SellerService sellerService;

	public void create(SubscriptionWithCreditLimitDto subscriptionDto)
			throws MeveoApiException {

		Provider provider = providerService.findById(subscriptionDto
				.getProviderId());
		User currentUser = userService.findById(subscriptionDto
				.getCurrentUserId());

		String offerTemplateCode = paramBean.getProperty(
				"asg.api.offer.offer.prefix", "_OF_");
		OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
				offerTemplateCode, provider);

		if (offerTemplate == null) {
			throw new OfferTemplateDoesNotExistsException(offerTemplateCode);
		}

		Seller seller = sellerService.findByCode(em,
				subscriptionDto.getOrganizationId(), provider);
		if (seller == null) {
			throw new SellerDoesNotExistsException(
					subscriptionDto.getOrganizationId());
		}

		if (seller.getSeller() != null) {
			Seller parentSeller = seller.getSeller();
			String sellerId = parentSeller.getCode();
			// We look if this SELLER has a "charged offer service" associated
			// at the offer with the code begining with
			// "_CH_OF_["OfferId"]_[SellerID] (we use asg.api.offer.charged.prefix). In the case the offer to select for this seller is too "_OF_["OfferId"]"

			// It's not the case we have to take the offers linked at each
			// service "_SE_["ServiceId"]" (asg.api.service.offer.prefix).
		}

	}

}
