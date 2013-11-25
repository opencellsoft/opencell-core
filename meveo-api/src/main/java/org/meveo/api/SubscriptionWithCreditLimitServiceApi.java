package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.ServiceToAddDto;
import org.meveo.api.dto.SubscriptionWithCreditLimitDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.OfferTemplateDoesNotExistsException;
import org.meveo.api.exception.ParentSellerDoesNotExistsException;
import org.meveo.api.exception.SellerDoesNotExistsException;
import org.meveo.api.exception.ServiceTemplateDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
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
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private SellerService sellerService;

	public void create(SubscriptionWithCreditLimitDto subscriptionDto)
			throws MeveoApiException {

		if (!StringUtils.isBlank(subscriptionDto.getUserId())
				&& !StringUtils.isBlank(subscriptionDto.getOrganizationId())
				&& !StringUtils.isBlank(subscriptionDto.getOfferId())
				&& subscriptionDto.getServicesToAdd() != null
				&& subscriptionDto.getServicesToAdd().size() > 0) {

			Provider provider = providerService.findById(subscriptionDto
					.getProviderId());

			String offerTemplateCode = paramBean.getProperty(
					"asg.api.offer.offer.prefix", "_OF_")
					+ subscriptionDto.getOfferId();
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

			if (seller.getSeller() == null) {
				throw new ParentSellerDoesNotExistsException(
						subscriptionDto.getOrganizationId());
			} else {
				Seller parentSeller = seller.getSeller();
				String sellerId = parentSeller.getCode();

				ForSubscription forSubscription = new ForSubscription();
				forSubscription.setSeller(seller);

				// We look if this SELLER has a "charged offer service"
				// associated
				// at the offer with the code begining with
				// "_CH_OF_["OfferId"]_[SellerID] (we use
				// asg.api.offer.charged.prefix).
				// In the case the offer to select for this seller is too
				// "_OF_["OfferId"]"
				// chargedOffer + offerCode + sellerId
				ServiceTemplate chargedServiceTemplate = serviceTemplateService
						.findByCode(
								em,
								paramBean.getProperty(
										"asg.api.offer.charged.prefix",
										"_CH_OF_")
										+ subscriptionDto.getOfferId()
										+ "_"
										+ sellerId, provider);
				if (chargedServiceTemplate != null) {
					forSubscription.getOfferTemplateForSubscription()
							.setOfferTemplate(offerTemplate);
					forSubscription.getOfferTemplateForSubscription()
							.getServiceTemplates().add(chargedServiceTemplate);
					forSubscription.setChargedOffer(true);
				} else {
					// It's not the case we have to take the offers linked at
					// each
					// service "_SE_["ServiceId"]"
					// (asg.api.service.offer.prefix).
					forSubscription.setChargedOffer(false);
					for (ServiceToAddDto serviceToAddDto : subscriptionDto
							.getServicesToAdd()) {
						// find offer
						String tempOfferTemplateCode = paramBean.getProperty(
								"asg.api.service.offer.prefix", "_SE_")
								+ serviceToAddDto.getServiceId();
						OfferTemplate tempOfferTemplate = offerTemplateService
								.findByCode(em, tempOfferTemplateCode, provider);
						if (tempOfferTemplate == null) {
							throw new OfferTemplateDoesNotExistsException(
									tempOfferTemplateCode);
						}

						// find service template
						String tempChargedServiceTemplateCode = paramBean
								.getProperty("asg.api.service.charged.prefix",
										"_CH_SE_")
								+ serviceToAddDto.getServiceId()
								+ "_"
								+ sellerId;
						ServiceTemplate tempChargedServiceTemplate = serviceTemplateService
								.findByCode(em, tempChargedServiceTemplateCode,
										provider);
						if (tempChargedServiceTemplate == null) {
							throw new ServiceTemplateDoesNotExistsException(
									tempChargedServiceTemplateCode);
						}
						forSubscription.getOfferTemplateForSubscription()
								.setOfferTemplate(tempOfferTemplate);
						forSubscription.getOfferTemplateForSubscription()
								.getServiceTemplates()
								.add(chargedServiceTemplate);
					}
				}

				// processParent
				ForSubscription finalForSubscription = processParent(
						forSubscription, parentSeller,
						subscriptionDto.getOfferId(), provider,
						subscriptionDto.getServicesToAdd());
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(subscriptionDto.getUserId())) {
				missingFields.add("userId");
			}
			if (StringUtils.isBlank(subscriptionDto.getOrganizationId())) {
				missingFields.add("organizationId");
			}
			if (StringUtils.isBlank(subscriptionDto.getOfferId())) {
				missingFields.add("offerId");
			}
			if (subscriptionDto.getServicesToAdd() == null
					|| subscriptionDto.getServicesToAdd().size() == 0) {
				missingFields.add("servicesToAdd");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}

	}

	private ForSubscription processParent(ForSubscription forSubscription,
			Seller seller, String offerId, Provider provider,
			List<ServiceToAddDto> servicesToAdd) throws MeveoApiException {
		if (seller.getSeller() == null) {
			return forSubscription;
		}

		String sellerId = seller.getSeller().getCode();
		ForSubscription newForSubscription = new ForSubscription();
		newForSubscription.setChild(forSubscription);

		if (forSubscription.isChargedOffer()) {
			String chargedServiceTemplateCode = paramBean.getProperty(
					"asg.api.offer.charged.prefix", "_CH_OF_")
					+ offerId
					+ "_"
					+ sellerId;
			ServiceTemplate tempChargedOfferServiceTemplate = serviceTemplateService
					.findByCode(em, chargedServiceTemplateCode, provider);
			if (tempChargedOfferServiceTemplate != null) {
				newForSubscription.setSeller(seller);
				newForSubscription.setChargedOffer(true);
				newForSubscription.getOfferTemplateForSubscription()
						.setOfferTemplate(
								forSubscription
										.getOfferTemplateForSubscription()
										.getOfferTemplate());
				newForSubscription.getOfferTemplateForSubscription()
						.getServiceTemplates()
						.add(tempChargedOfferServiceTemplate);
				newForSubscription = processParent(newForSubscription,
						seller.getSeller(), offerId, provider, servicesToAdd);
			} else {
				newForSubscription.setChargedOffer(false);
				for (ServiceToAddDto serviceToAddDto : servicesToAdd) {
					// find offer
					String tempOfferTemplateCode = paramBean.getProperty(
							"asg.api.service.offer.prefix", "_SE_")
							+ serviceToAddDto.getServiceId();
					OfferTemplate tempOfferTemplate = offerTemplateService
							.findByCode(em, tempOfferTemplateCode, provider);
					if (tempOfferTemplate == null) {
						throw new OfferTemplateDoesNotExistsException(
								tempOfferTemplateCode);
					}

					// find service template
					String tempChargedServiceTemplateCode = paramBean
							.getProperty("asg.api.service.charged.prefix",
									"_CH_SE_")
							+ serviceToAddDto.getServiceId() + "_" + sellerId;
					ServiceTemplate tempChargedServiceTemplate = serviceTemplateService
							.findByCode(em, tempChargedServiceTemplateCode,
									provider);
					if (tempChargedServiceTemplate == null) {
						throw new ServiceTemplateDoesNotExistsException(
								tempChargedServiceTemplateCode);
					}
					forSubscription.getOfferTemplateForSubscription()
							.setOfferTemplate(tempOfferTemplate);
					forSubscription.getOfferTemplateForSubscription()
							.getServiceTemplates()
							.add(tempChargedServiceTemplate);

					newForSubscription = processParent(newForSubscription,
							seller.getSeller(), offerId, provider,
							servicesToAdd);
				}
			}
		} else {
			newForSubscription.setChargedOffer(false);
			for (ServiceToAddDto serviceToAddDto : servicesToAdd) {
				// find offer
				String tempOfferTemplateCode = paramBean.getProperty(
						"asg.api.service.offer.prefix", "_SE_")
						+ serviceToAddDto.getServiceId();
				OfferTemplate tempOfferTemplate = offerTemplateService
						.findByCode(em, tempOfferTemplateCode, provider);
				if (tempOfferTemplate == null) {
					throw new OfferTemplateDoesNotExistsException(
							tempOfferTemplateCode);
				}

				// find service template
				String tempChargedServiceTemplateCode = paramBean.getProperty(
						"asg.api.service.charged.prefix", "_CH_SE_")
						+ serviceToAddDto.getServiceId() + "_" + sellerId;
				ServiceTemplate tempChargedServiceTemplate = serviceTemplateService
						.findByCode(em, tempChargedServiceTemplateCode,
								provider);
				if (tempChargedServiceTemplate == null) {
					throw new ServiceTemplateDoesNotExistsException(
							tempChargedServiceTemplateCode);
				}
				forSubscription.getOfferTemplateForSubscription()
						.setOfferTemplate(tempOfferTemplate);
				forSubscription.getOfferTemplateForSubscription()
						.getServiceTemplates().add(tempChargedServiceTemplate);

				newForSubscription = processParent(newForSubscription,
						seller.getSeller(), offerId, provider, servicesToAdd);
			}
		}

		return newForSubscription;
	}

	class ForSubscription {
		private Seller seller;
		private OfferTemplateForSubscription offerTemplateForSubscription = new OfferTemplateForSubscription();
		private ForSubscription child;
		private boolean chargedOffer;

		public Seller getSeller() {
			return seller;
		}

		public void setSeller(Seller seller) {
			this.seller = seller;
		}

		public OfferTemplateForSubscription getOfferTemplateForSubscription() {
			return offerTemplateForSubscription;
		}

		public void setOfferTemplateForSubscription(
				OfferTemplateForSubscription offerTemplateForSubscription) {
			this.offerTemplateForSubscription = offerTemplateForSubscription;
		}

		public boolean isChargedOffer() {
			return chargedOffer;
		}

		public void setChargedOffer(boolean chargedOffer) {
			this.chargedOffer = chargedOffer;
		}

		public ForSubscription getChild() {
			return child;
		}

		public void setChild(ForSubscription child) {
			this.child = child;
		}

	}

	class OfferTemplateForSubscription {
		private OfferTemplate offerTemplate;
		private List<ServiceTemplate> serviceTemplates = new ArrayList<ServiceTemplate>();

		public OfferTemplate getOfferTemplate() {
			return offerTemplate;
		}

		public void setOfferTemplate(OfferTemplate offerTemplate) {
			this.offerTemplate = offerTemplate;
		}

		public List<ServiceTemplate> getServiceTemplates() {
			return serviceTemplates;
		}

		public void setServiceTemplates(List<ServiceTemplate> serviceTemplates) {
			this.serviceTemplates = serviceTemplates;
		}
	}

}
