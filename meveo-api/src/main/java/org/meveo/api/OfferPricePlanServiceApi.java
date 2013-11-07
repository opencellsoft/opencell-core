package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.OfferPricePlanDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OfferPricePlanServiceApi extends BaseApi {

	@Inject
	private ParamBean paramBean;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	public void create(OfferPricePlanDto offerPricePlanDto)
			throws MeveoApiException {
		if (!StringUtils.isBlank(offerPricePlanDto.getOfferId())
				&& !StringUtils.isBlank(offerPricePlanDto.getOrganizationId())
				&& !StringUtils.isBlank(offerPricePlanDto.getTaxId())) {

			Provider provider = providerService.findById(offerPricePlanDto
					.getProviderId());
			User currentUser = userService.findById(offerPricePlanDto
					.getCurrentUserId());

			// Create a recurring charge with associated services and
			// parameters. Charge code is'_RE_OF_[OrganizationId]_[OfferId]'
			// ('_RE_OF_' must be settable). Charge is associate to step 1
			// service.
			String recurringChargePrefix = paramBean.getProperty(
					"asg.api.offer.recurring.prefix", "_RE_OF_");
			RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
			recurringChargeTemplate.setActive(true);
			recurringChargeTemplate.setCode(recurringChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			recurringChargeTemplateService.create(em, recurringChargeTemplate,
					currentUser, provider);

			// Create a subscription one point charge. Charge code
			// is'_SO_OF_[OrganizationId]_[OfferId]' ('_SO_OF_' must be
			// settable). Charge is associate to step 1 service.
			String subscriptionPointChargePrefix = paramBean
					.getProperty(
							"asg.api.offer.subscription.point.charge.prefix",
							"_SO_OF_");
			OneShotChargeTemplate subscriptionPointCharge = new OneShotChargeTemplate();
			subscriptionPointCharge.setActive(true);
			subscriptionPointCharge.setCode(subscriptionPointChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			oneShotChargeTemplateService.create(em, subscriptionPointCharge,
					currentUser, provider);

			// Create e termination point charge. Charge code is
			// '_TE_OF_[OrganizationId]_[OfferId]' ('_TE_OF_' must be settable).
			// Charge is associate to step 1 service.
			String terminationPointChargePrefix = paramBean.getProperty(
					"asg.api.offer.termination.point.charge.prefix", "_TE_OF_");
			OneShotChargeTemplate terminationPointCharge = new OneShotChargeTemplate();
			terminationPointCharge.setActive(true);
			terminationPointCharge.setCode(terminationPointChargePrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			oneShotChargeTemplateService.create(em, terminationPointCharge,
					currentUser, provider);

			// Create a charged service for defined offer and organization.
			// Service code is '_OF_[OrganizationId]_[OferId]'. Prefix '_OF_'
			// must be settable in properties file.
			String offerPrefix = paramBean.getProperty(
					"asg.api.offer.offer.prefix", "_OF_");
			ServiceTemplate chargedServiceTemplate = new ServiceTemplate();
			chargedServiceTemplate.setCode(offerPrefix
					+ offerPricePlanDto.getOrganizationId() + "_"
					+ offerPricePlanDto.getOfferId());
			chargedServiceTemplate.setActive(true);
			chargedServiceTemplate.getRecurringCharges().add(
					recurringChargeTemplate);
			chargedServiceTemplate.getSubscriptionCharges().add(
					subscriptionPointCharge);
			chargedServiceTemplate.getTerminationCharges().add(
					terminationPointCharge);
			serviceTemplateService.create(em, chargedServiceTemplate,
					currentUser, provider);

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(offerPricePlanDto.getOfferId())) {
				missingFields.add("OfferId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getOrganizationId())) {
				missingFields.add("OrganizationId");
			}
			if (StringUtils.isBlank(offerPricePlanDto.getTaxId())) {
				missingFields.add("TaxId");
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

}
