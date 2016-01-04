package org.meveo.api.catalog;

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
		return offerTemplate == null ? null : ProductOffering.parseFromOfferTemplate(offerTemplate, uriInfo, category, null);
	}

	public List<ProductOffering> findProductOfferings(UriInfo uriInfo, Category category) {
		List<OfferTemplate> offerTemplates = offerTemplateService.list();
		Map<String, List<PricePlanMatrix>> offerPriceList = new HashMap<String, List<PricePlanMatrix>>();
		
		for (OfferTemplate offerTemplate : offerTemplates) {
			List<PricePlanMatrix> offerPrices = pricePlanMatrixService.findByOfferTemplate(offerTemplate);
			if (offerPrices != null) {
				offerPriceList.put(offerTemplate.getCode(), offerPrices);
			}
		}
		
		return ProductOffering.parseFromOfferTemplates(offerTemplates, uriInfo, category, offerPriceList);
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
