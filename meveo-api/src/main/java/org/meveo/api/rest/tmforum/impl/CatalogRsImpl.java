package org.meveo.api.rest.tmforum.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.BusinessOfferApi;
import org.meveo.api.catalog.CatalogApi;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.CatalogRs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
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
    public Response findCategories(UriInfo info) {

        Response.ResponseBuilder responseBuilder = null;

        try {

            List<Category> categories = new ArrayList<Category>();

            List<OfferTemplateCategoryDto> offerTemplateCategoryDtos = offerTemplateCategoryApi.list(uriInfo);
            if (offerTemplateCategoryDtos != null && offerTemplateCategoryDtos.size() > 0) {
                for (OfferTemplateCategoryDto otcd : offerTemplateCategoryDtos) {
                    Category category = new Category();
                    category.setId(String.valueOf(otcd.getId()));
                    //TODO where to get data for version??
                    //category.setVersion(String.valueOf(otcd.getVersion()));
                    category.setHref(otcd.getHref());
                    category.setName(otcd.getName());
                    category.setDescription(otcd.getDescription());
                    category.setLastUpdate(otcd.getLastModified());
                    // TODO where to get life cycle status??
                    if (otcd.isActive()) {
                        category.setLifecycleStatus(LifecycleStatus.ACTIVE);
                    } else {
                        category.setLifecycleStatus(LifecycleStatus.RETIRED);
                    }
                    // TODO where to get set valid for??
                    if (otcd.getParentId() != null) {
                        category.setParentId(String.valueOf(otcd.getParentId()));
                        category.setIsRoot(false);
                    } else {
                        category.setIsRoot(true);
                    }
                    categories.add(category);
                }
            }

            responseBuilder = Response.ok().entity(categories);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getCategory(String code, UriInfo info) {
        log.debug("find category by code {}", code);

        Response.ResponseBuilder responseBuilder = null;

        try {
            OfferTemplateCategoryDto otcd = offerTemplateCategoryApi.findByCode(code, getCurrentUser(), uriInfo);

            Category category = new Category();
            category.setId(String.valueOf(otcd.getId()));
            //category.setVersion(String.valueOf(otcd.getVersion()));
            category.setHref(otcd.getHref());
            category.setName(otcd.getName());
            category.setDescription(otcd.getDescription());
            category.setLastUpdate(otcd.getLastModified());
            // TODO where to get life cycle status??
            if (otcd.isActive()) {
                category.setLifecycleStatus(LifecycleStatus.ACTIVE);
            } else {
                category.setLifecycleStatus(LifecycleStatus.RETIRED);
            }
            // TODO where to get set valid for??
            if (otcd.getParentId() != null) {
                category.setParentId(String.valueOf(otcd.getParentId()));
                category.setIsRoot(false);
            } else {
                category.setIsRoot(true);
            }

            responseBuilder = Response.ok().entity(category);

        } catch (EntityDoesNotExistsException e) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    public Response findProductOfferings(UriInfo info) {
        log.debug("find productOfferings ... ");

        Response.ResponseBuilder responseBuilder = null;

        try {
            List<ProductOffering> productOfferings = catalogApi.findProductOfferings(uriInfo, Category.createProto(uriInfo), getCurrentUser());
            responseBuilder = Response.ok().entity(productOfferings);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductOffering(String id, UriInfo info) {
        log.debug("find productOffering by id {}", id);

        Response.ResponseBuilder responseBuilder = null;

        try {
            ProductOffering productOffering = catalogApi.findProductOffering(id, getCurrentUser(), uriInfo, Category.createProto(uriInfo));
            responseBuilder = Response.ok().entity(productOffering);

        } catch (EntityDoesNotExistsException e) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response findProductSpecifications(UriInfo info) {
        log.debug("find productSpecifications ... ");

        Response.ResponseBuilder responseBuilder = null;

        try {
            List<ProductSpecification> productSpecifications = catalogApi.findProductSpecifications(getCurrentUser(), uriInfo);
            responseBuilder = Response.ok().entity(productSpecifications);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductSpecification(String id, UriInfo info) {
        log.debug("find productSpecification by id {}", id);

        Response.ResponseBuilder responseBuilder = null;
        try {
            ProductSpecification productSpecification = catalogApi.findProductSpecification(id, getCurrentUser(), uriInfo);           
            responseBuilder = Response.ok().entity(productSpecification);

        } catch (EntityDoesNotExistsException e) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response createOfferFromBOM(BomOfferDto postData) {
        Response.ResponseBuilder responseBuilder = null;

        try {
            businessOfferApi.createOfferFromBOM(postData, getCurrentUser());
            responseBuilder = Response.ok();

        } catch (ConstraintViolationException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }
}