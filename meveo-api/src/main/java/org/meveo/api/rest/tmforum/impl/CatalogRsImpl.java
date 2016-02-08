package org.meveo.api.rest.tmforum.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.BusinessOfferApi;
import org.meveo.api.catalog.CatalogApi;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.CatalogRs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class CatalogRsImpl extends BaseRs implements CatalogRs {

	@Inject
	private Logger log = LoggerFactory.getLogger(CatalogRsImpl.class);

	@Context
	private UriInfo uriInfo;

	@Inject
	private CatalogApi catalogApi;

	@Inject
	private BusinessOfferApi businessOfferApi;
	
	@Inject
	private OfferTemplateCategoryApi offerTemplateCategoryApi;

	@Override
	public List<Category> findCategories() {
		List<Category> categories = new ArrayList<Category>();
		try {
			List<OfferTemplateCategoryDto> offerTemplateCategoryDtos =  offerTemplateCategoryApi.list();
			if (offerTemplateCategoryDtos != null && offerTemplateCategoryDtos.size() > 0) {
				for (OfferTemplateCategoryDto otcd: offerTemplateCategoryDtos) {
					Category category = new Category();
					category.setId(String.valueOf(otcd.getId()));
					category.setVersion(String.valueOf(otcd.getVersion()));
					category.setHref(otcd.getHref());
					category.setName(otcd.getName());
					category.setDescription(otcd.getDescription());
					category.setLastUpdate(otcd.getLastModified());
					//TODO where to get life cycle status??
					if (otcd.isActive()) {
						category.setLifecycleStatus(LifecycleStatus.ACTIVE);
					} else {
						category.setLifecycleStatus(LifecycleStatus.RETIRED);
					}
					//TODO where to get set valid for??
					if (otcd.getParentId() != null) {
						category.setParentId(String.valueOf(otcd.getParentId()));
						category.setIsRoot(false);
					} else {
						category.setIsRoot(true);
					}
					categories.add(category);
				}
			}
		} catch (MeveoApiException e) {
			log.error("MeveoApiException caught while retrieving categories: " + e.getMessage());			
		} catch (Exception e) {
			log.error("Exception caught while retrieving categories: " + e.getMessage());
		}
		
		
		return categories;
	}

	@Override
	public Response findCategoryById(String id) {
		log.debug("find catetegory by id {}", id);
		if (!"1".equals(id)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		Category category = null;
		try {
			OfferTemplateCategoryDto otcd = offerTemplateCategoryApi.findById(id, getCurrentUser());
			if (otcd == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			
			category = new Category();
			category.setId(String.valueOf(otcd.getId()));
			category.setVersion(String.valueOf(otcd.getVersion()));
			category.setHref(otcd.getHref());
			category.setName(otcd.getName());
			category.setDescription(otcd.getDescription());
			category.setLastUpdate(otcd.getLastModified());
			//TODO where to get life cycle status??
			if (otcd.isActive()) {
				category.setLifecycleStatus(LifecycleStatus.ACTIVE);
			} else {
				category.setLifecycleStatus(LifecycleStatus.RETIRED);
			}
			//TODO where to get set valid for??
			if (otcd.getParentId() != null) {
				category.setParentId(String.valueOf(otcd.getParentId()));
				category.setIsRoot(false);
			} else {
				category.setIsRoot(true);
			}
			
			
		} catch (MeveoApiException e) {
			log.error("MeveoApiException caught while retrieving categories: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (Exception e) {
			log.error("Exception caught while retrieving categories: " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		
		return Response.ok().entity(category).build();
	}

	public List<ProductOffering> findProductOfferings() {
		log.debug("find productOfferings ... ");
		List<ProductOffering> productOfferings;
		try {
			productOfferings = catalogApi
					.findProductOfferings(uriInfo, Category.createProto(uriInfo), getCurrentUser());
			return productOfferings;
		} catch (MeveoApiException e) {
			return null;
		}
	}

	@Override
	public Response findProductOfferingById(String id) {
		log.debug("find productOffering by id {}", id);
		ProductOffering productOffering = null;
		try {
			productOffering = catalogApi.findProductOffering(id, getCurrentUser(), uriInfo,
					Category.createProto(uriInfo));
		} catch (MeveoApiException e) {
			return Response.status(Status.NOT_FOUND).entity(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION).build();
		} catch (Exception e) {
		}
		if (productOffering == null) {
			return Response.status(Status.NOT_FOUND).entity("not found").build();
		}
		return Response.ok().entity(productOffering).build();
	}

    @Override
    public List<ProductSpecification> findProductSpecifications() {
        log.debug("find productSpecifications ... ");
        try {
            return catalogApi.findProductSpecifications(getCurrentUser(), uriInfo);
        } catch (MeveoApiException e) {
            return null;
        }
    }

	@Override
	public Response findProductSpecificationById(String id) {
		log.debug("find productSpecification by id {}", id);
		ProductSpecification productSpecification = null;
		try {
			productSpecification = catalogApi.findProductSpecification(id, getCurrentUser(), uriInfo);
		} catch (Exception e) {
		}
		if (productSpecification == null) {
			return Response.status(Status.NOT_FOUND).entity("no found!").build();
		}
		return Response.ok().entity(productSpecification).build();
	}

	@Override
	public Response createOfferFromBOM(BomOfferDto postData) {
		Response.ResponseBuilder responseBuilder = null;

		try {
			businessOfferApi.createOfferFromBOM(postData, getCurrentUser());
			responseBuilder = Response.ok();
		} catch (ConstraintViolationException e) {
			log.error(e.getMessage());
			responseBuilder = Response.status(Response.Status.BAD_REQUEST);
		} catch (MeveoApiException e) {
			log.error(e.getMessage());
			responseBuilder = Response.status(Response.Status.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}

		return responseBuilder.build();
	}
}
