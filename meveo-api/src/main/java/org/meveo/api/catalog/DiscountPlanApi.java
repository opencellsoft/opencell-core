package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlanMatrix;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.DiscountPlanMatrixService;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanApi extends BaseApi {

	@Inject
	private ChargeTemplateServiceAll chargeTemplateServiceAll;

	@Inject
	private SellerService sellerService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private DiscountPlanMatrixService discountPlanMatrixService;

	public Long create(DiscountPlanDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getEventCode())
				&& postData.getPercent() != null) {
			Provider provider = currentUser.getProvider();

			// search for eventCode
			if (chargeTemplateServiceAll.findByCode(postData.getEventCode(),
					provider) == null) {
				throw new EntityDoesNotExistsException(ChargeTemplate.class,
						postData.getEventCode());
			}

			DiscountPlanMatrix discountPlanMatrix = new DiscountPlanMatrix();
			discountPlanMatrix.setEventCode(postData.getEventCode());
			discountPlanMatrix.setProvider(provider);
			discountPlanMatrix.setStartSubscriptionDate(postData
					.getStartSubscriptionDate());
			discountPlanMatrix.setEndSubscriptionDate(postData
					.getEndSubscriptionDate());
			discountPlanMatrix.setNbPeriod(postData.getNbPeriod());
			discountPlanMatrix.setPercent(postData.getPercent());

			if (!StringUtils.isBlank(postData.getSeller())) {
				Seller seller = sellerService.findByCode(postData.getSeller(),
						provider);
				if (seller == null) {
					throw new EntityDoesNotExistsException(Seller.class,
							postData.getSeller());
				}
				discountPlanMatrix.setSeller(seller);
			}

			if (!StringUtils.isBlank(postData.getOfferTemplate())) {
				OfferTemplate offerTemplate = offerTemplateService.findByCode(
						postData.getOfferTemplate(), provider);
				if (offerTemplate == null) {
					throw new EntityDoesNotExistsException(OfferTemplate.class,
							postData.getOfferTemplate());
				}
				discountPlanMatrix.setOfferTemplate(offerTemplate);
			}

			discountPlanMatrixService.create(discountPlanMatrix, currentUser,
					provider);

			return discountPlanMatrix.getId();
		} else {
			if (StringUtils.isBlank(postData.getEventCode())) {
				missingParameters.add("eventCode");
			}
			if (postData.getPercent() == null) {
				missingParameters.add("percent");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(DiscountPlanDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getEventCode())
				&& postData.getPercent() != null) {
			Provider provider = currentUser.getProvider();

			// search for eventCode
			if (chargeTemplateServiceAll.findByCode(postData.getEventCode(),
					provider) == null) {
				throw new EntityDoesNotExistsException(ChargeTemplate.class,
						postData.getEventCode());
			}

			// search for price plan
			DiscountPlanMatrix discountPlanMatrix = discountPlanMatrixService
					.findById(postData.getId());
			if (discountPlanMatrix == null) {
				throw new EntityDoesNotExistsException(
						DiscountPlanMatrix.class, postData.getId());
			}

			discountPlanMatrix.setEventCode(postData.getEventCode());
			discountPlanMatrix.setStartSubscriptionDate(postData
					.getStartSubscriptionDate());
			discountPlanMatrix.setEndSubscriptionDate(postData
					.getEndSubscriptionDate());
			discountPlanMatrix.setNbPeriod(postData.getNbPeriod());
			discountPlanMatrix.setPercent(postData.getPercent());

			if (!StringUtils.isBlank(postData.getSeller())) {
				Seller seller = sellerService.findByCode(postData.getSeller(),
						provider);
				if (seller == null) {
					throw new EntityDoesNotExistsException(Seller.class,
							postData.getSeller());
				}
				discountPlanMatrix.setSeller(seller);
			}

			if (!StringUtils.isBlank(postData.getOfferTemplate())) {
				OfferTemplate offerTemplate = offerTemplateService.findByCode(
						postData.getOfferTemplate(), provider);
				if (offerTemplate == null) {
					throw new EntityDoesNotExistsException(OfferTemplate.class,
							postData.getOfferTemplate());
				}
				discountPlanMatrix.setOfferTemplate(offerTemplate);
			}

			discountPlanMatrixService.update(discountPlanMatrix, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getEventCode())) {
				missingParameters.add("eventCode");
			}
			if (postData.getPercent() == null) {
				missingParameters.add("percent");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public DiscountPlanDto find(Long id) throws MeveoApiException {
		if (id != null) {
			DiscountPlanMatrix discountPlanMatrix = discountPlanMatrixService
					.findById(id);
			if (discountPlanMatrix == null) {
				throw new EntityDoesNotExistsException(
						DiscountPlanMatrix.class, id);
			}

			return new DiscountPlanDto(discountPlanMatrix);
		} else {
			missingParameters.add("id");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void remove(Long id) throws MeveoApiException {
		if (id != null) {
			DiscountPlanMatrix discountPlanMatrix = discountPlanMatrixService
					.findById(id);
			if (discountPlanMatrix == null) {
				throw new EntityDoesNotExistsException(
						DiscountPlanMatrix.class, id);
			}

			discountPlanMatrixService.remove(discountPlanMatrix);
		} else {
			missingParameters.add("id");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
