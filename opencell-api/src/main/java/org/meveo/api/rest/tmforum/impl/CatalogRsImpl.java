package org.meveo.api.rest.tmforum.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.catalog.BusinessOfferApi;
import org.meveo.api.catalog.CatalogApi;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.catalog.ProductChargeTemplateApi;
import org.meveo.api.catalog.ProductTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BsmServiceDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.catalog.GetListProductTemplateResponseDto;
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

    @Inject
    private ProductTemplateApi productTemplateApi;

    @Inject
    private ProductChargeTemplateApi productChargeTemplateApi;

    @Override
    public Response findCategories(UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            List<Category> categories = new ArrayList<Category>();

            List<OfferTemplateCategoryDto> offerTemplateCategoryDtos = offerTemplateCategoryApi.list(uriInfo);
            if (offerTemplateCategoryDtos != null && offerTemplateCategoryDtos.size() > 0) {
                for (OfferTemplateCategoryDto otcd : offerTemplateCategoryDtos) {
                    Category category = new Category();
                    category.setId(String.valueOf(otcd.getId()));
                    // TODO where to get data for version??
                    // category.setVersion(String.valueOf(otcd.getVersion()));
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

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getCategory(String code, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("find category by code {}", code);

        Response.ResponseBuilder responseBuilder = null;

        try {
            OfferTemplateCategoryDto otcd = offerTemplateCategoryApi.findByCode(code, uriInfo);

            Category category = new Category();
            category.setId(String.valueOf(otcd.getId()));
            // category.setVersion(String.valueOf(otcd.getVersion()));
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

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    public Response findProductOfferings(Date validFrom, Date validTo, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("find productOfferings ... ");

        Response.ResponseBuilder responseBuilder = null;

        try {
            List<ProductOffering> productOfferings = catalogApi.findProductOfferings(validFrom, validTo, uriInfo, Category.createProto(uriInfo));
            responseBuilder = Response.ok().entity(productOfferings);

            // } catch (MeveoApiException e) {
            // responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            // responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductOffering(String id, Date validFrom, Date validTo, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("find productOffering by id {}", id);

        Response.ResponseBuilder responseBuilder = null;

        try {
            ProductOffering productOffering = catalogApi.findProductOffering(id, validFrom, validTo, uriInfo, Category.createProto(uriInfo));
            responseBuilder = Response.ok().entity(productOffering);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response findProductSpecifications(UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("find productSpecifications ... ");

        Response.ResponseBuilder responseBuilder = null;

        try {
            List<ProductSpecification> productSpecifications = catalogApi.findProductSpecifications(uriInfo);
            responseBuilder = Response.ok().entity(productSpecifications);

            // } catch (MeveoApiException e) {
            // responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            // responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductSpecification(String id, Date validFrom, Date validTo, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("find productSpecification by id {}", id);

        Response.ResponseBuilder responseBuilder = null;
        try {
            ProductSpecification productSpecification = catalogApi.findProductSpecification(id, validFrom, validTo, uriInfo);
            responseBuilder = Response.ok().entity(productSpecification);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response createOfferFromBOM(BomOfferDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;

        try {
            responseBuilder = Response.ok().entity(businessOfferApi.createOfferFromBOM(postData));

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductTemplate(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("getProductTemplate by code {}", code);
        Response.ResponseBuilder responseBuilder = null;
        try {
            ProductTemplateDto productTemplateDto = productTemplateApi.find(code, validFrom, validTo);
            responseBuilder = Response.ok().entity(productTemplateDto);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response createProductTemplate(ProductTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productTemplateApi.create(postData);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response createOrUpdateProductTemplate(ProductTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productTemplateApi.createOrUpdate(postData);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response updateProductTemplate(ProductTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productTemplateApi.update(postData);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response removeProductTemplate(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productTemplateApi.remove(code, validFrom, validTo);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response listProductTemplate(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            GetListProductTemplateResponseDto listProductTemplateDto = productTemplateApi.list(code, validFrom, validTo, null);
            responseBuilder = Response.ok().entity(listProductTemplateDto.getListProductTemplate());

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response getProductChargeTemplate(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("getProductChargeTemplate by code {}", code);
        Response.ResponseBuilder responseBuilder = null;
        try {
            ProductChargeTemplateDto productChargeTemplateDto = productChargeTemplateApi.find(code);
            responseBuilder = Response.ok().entity(productChargeTemplateDto);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response createProductChargeTemplate(ProductChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productChargeTemplateApi.create(postData);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response createOrUpdateProductChargeTemplate(ProductChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productChargeTemplateApi.createOrUpdate(postData);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response updateProductChargeTemplate(ProductChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productChargeTemplateApi.update(postData);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response removeProductChargeTemplate(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            productChargeTemplateApi.remove(code);
            responseBuilder = Response.ok();

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response listProductChargeTemplate() {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            List<ProductChargeTemplateDto> listProductChargeTemplate = productChargeTemplateApi.list();
            responseBuilder = Response.ok().entity(listProductChargeTemplate);

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }
	
    @Override
    public Response createServiceFromBSM(BsmServiceDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;

        try {
            responseBuilder = Response.ok().entity(businessOfferApi.createServiceFromBSM(postData));

        } catch (Exception e) {
            processException(e, result);
            responseBuilder.entity(result);
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

}