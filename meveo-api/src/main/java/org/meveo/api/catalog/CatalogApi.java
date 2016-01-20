package org.meveo.api.catalog;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.BaseApi;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

@Stateless
public class CatalogApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	public ProductOffering findProductOffering(String code, User currentUser, UriInfo uriInfo, Category category) {
		OfferTemplate offerTemplate = offerTemplateService.findByCode(code, currentUser.getProvider());
		return offerTemplate == null ? null : ProductOffering.parseFromOfferTemplate(offerTemplate, uriInfo, category,
				null);
	}

	public List<ProductOffering> findProductOfferings(UriInfo uriInfo, Category category) {
		List<OfferTemplate> offerTemplates = offerTemplateService.list();
		Map<String, Map<String, BigDecimal>> offerPrices = new HashMap<>();

		for (OfferTemplate offerTemplate : offerTemplates) {
			Map<String, BigDecimal> servicePrices = new HashMap<>();

			for (ServiceTemplate st : offerTemplate.getServiceTemplates()) {
				if (st.getServiceSubscriptionCharges() != null) {
					BigDecimal totalPriceOneShotCharges = new BigDecimal(0);
					for (ServiceChargeTemplateSubscription serviceChargeTemplateSubscription : st
							.getServiceSubscriptionCharges()) {
						BigDecimal chargePricePlans = new BigDecimal(0);

						List<PricePlanMatrix> offerPricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(
								offerTemplate, serviceChargeTemplateSubscription.getChargeTemplate().getCode());
						if (offerPricePlans != null && offerPricePlans.size() > 0) {
							chargePricePlans = offerPricePlans.get(0).getAmountWithTax();
						} else {
							List<PricePlanMatrix> pricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(
									null, serviceChargeTemplateSubscription.getChargeTemplate().getCode());
							if (pricePlans != null && pricePlans.size() > 0) {
								chargePricePlans = pricePlans.get(0).getAmountWithTax();
							}
						}

						totalPriceOneShotCharges = totalPriceOneShotCharges.add(chargePricePlans);
					}

					servicePrices.put(st.getCode() + "_SUB", totalPriceOneShotCharges);
				}

				if (st.getServiceRecurringCharges() != null) {
					BigDecimal totalPriceRecurringCharges = new BigDecimal(0);
					for (ServiceChargeTemplateRecurring serviceChargeTemplateRecurring : st
							.getServiceRecurringCharges()) {
						BigDecimal chargePricePlans = new BigDecimal(0);

						List<PricePlanMatrix> offerPricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(
								offerTemplate, serviceChargeTemplateRecurring.getChargeTemplate().getCode());
						if (offerPricePlans != null && offerPricePlans.size() > 0) {
							chargePricePlans = offerPricePlans.get(0).getAmountWithTax();
						} else {
							List<PricePlanMatrix> pricePlans = pricePlanMatrixService.findByOfferTemplateAndEventCode(
									null, serviceChargeTemplateRecurring.getChargeTemplate().getCode());
							if (pricePlans != null && pricePlans.size() > 0) {
								chargePricePlans = pricePlans.get(0).getAmountWithTax();
							}
						}

						totalPriceRecurringCharges = totalPriceRecurringCharges.add(chargePricePlans);
					}

					servicePrices.put(st.getCode() + "_REC_"
							+ (st.getInvoicingCalendar() == null ? "" : st.getInvoicingCalendar().getCode()),
							totalPriceRecurringCharges);
				}
			}

			offerPrices.put(offerTemplate.getCode(), servicePrices);
		}

		return ProductOffering.parseFromOfferTemplates(offerTemplates, uriInfo, category, offerPrices);
	}

	public ProductSpecification findProductSpecification(String code, User currentUser, UriInfo uriInfo) {
		OfferTemplate offerTemplate = offerTemplateService.findByCode(code, currentUser.getProvider());
		return offerTemplate == null ? null : ProductSpecification.parseFromOfferTemplate(offerTemplate, uriInfo);
	}

	public List<ProductSpecification> findProductSpecifications(UriInfo uriInfo) {
		List<OfferTemplate> offerTemplates = offerTemplateService.list();
		return ProductSpecification.parseFromOfferTemplates(offerTemplates, uriInfo);
	}

}
