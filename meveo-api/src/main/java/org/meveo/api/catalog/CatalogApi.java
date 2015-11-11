package org.meveo.api.catalog;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

@Stateless
public class CatalogApi {

	@Inject
	private OfferTemplateService offerTemplateService;
	
	public ProductOffering findProductOffering(String code,User currentUser,UriInfo uriInfo,Category category) {
		OfferTemplate offerTemplate=offerTemplateService.findByCode(code, currentUser.getProvider());
		return offerTemplate==null?null:ProductOffering.parseFromOfferTemplate(offerTemplate,uriInfo,category);
	}
	public List<ProductOffering> findProductOfferings(UriInfo uriInfo,Category category){
		List<OfferTemplate> offerTemplates=offerTemplateService.list();
		return ProductOffering.parseFromOfferTemplates(offerTemplates,uriInfo,category);
	}
	public ProductSpecification findProductSpecification(String code,User currentUser,UriInfo uriInfo) {
		OfferTemplate offerTemplate=offerTemplateService.findByCode(code, currentUser.getProvider());
		return offerTemplate==null?null:ProductSpecification.parseFromOfferTemplate(offerTemplate,uriInfo);
	}
	public List<ProductSpecification> findProductSpecifications(UriInfo uriInfo){
		List<OfferTemplate> offerTemplates=offerTemplateService.list();
		return ProductSpecification.parseFromOfferTemplates(offerTemplates,uriInfo);
	}
}
