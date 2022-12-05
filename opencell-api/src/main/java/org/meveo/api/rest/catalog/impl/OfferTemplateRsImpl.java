/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.catalog.impl;

import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Response;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ProductOfferTemplateDto;
import org.meveo.api.dto.cpq.CustomerContextDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListCpqOfferResponseDto;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OfferTemplateRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OfferTemplateRsImpl extends BaseRs implements OfferTemplateRs {

    @Inject
    private OfferTemplateApi offerTemplateApi;

    @Override
    public ActionStatus create(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            OfferTemplate offerTemplate = offerTemplateApi.create(postData);
            result.setEntityCode(offerTemplate.getCode());
            result.setEntityId(offerTemplate.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            OfferTemplate offerTemplate = offerTemplateApi.update(postData);
            result.setEntityCode(offerTemplate.getCode());
            result.setEntityId(offerTemplate.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateResponseDto find(String offerTemplateCode, Date validFrom, Date validTo, CustomFieldInheritanceEnum inheritCF, boolean loadOfferServiceTemplate, boolean loadOfferProductTemplate,
            boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate, boolean loadAllowedDiscountPlan) {
        GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();

        try {
            result=offerTemplateApi.find(offerTemplateCode, validFrom, validTo, inheritCF, loadOfferServiceTemplate, loadOfferProductTemplate, loadServiceChargeTemplate, loadProductChargeTemplate, loadAllowedDiscountPlan);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }


    @Override
    public GetListOfferTemplateResponseDto listGet(@Deprecated String code, @Deprecated @RestDateParam Date validFrom, @Deprecated @RestDateParam Date validTo, String query, String fields, Integer offset, Integer limit,
            String sortBy, SortOrder sortOrder, CustomFieldInheritanceEnum inheritCF) {

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();

        try {
            result = (offerTemplateApi.list(code, validFrom, validTo, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder), inheritCF));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetListOfferTemplateResponseDto list(@Deprecated String code, @Deprecated @RestDateParam Date validFrom,
                                                @Deprecated @RestDateParam Date validTo, CustomFieldInheritanceEnum inheritCF) {
        try {
            return offerTemplateApi.listGetAll( code, validFrom, validTo,
                    GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering(), inheritCF );
        } catch (Exception e) {
            GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public GetListOfferTemplateResponseDto listPost(PagingAndFiltering pagingAndFiltering) {

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();

        try {
            result = (offerTemplateApi.list(null, null, null, pagingAndFiltering));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
	@Override
	public Response listPost(CustomerContextDTO customerContextDto) {
		GetListCpqOfferResponseDto result = new GetListCpqOfferResponseDto();

	        try {
	        	/*****@TODO RAY : create a new method in offertemplateAPI that get offers matching given 
	        	 * pagination/filetring crieria and also BA trading rules and tags ***////
	            result = (offerTemplateApi.list(customerContextDto));
	        } catch (Exception e) {
	            processException(e, result.getActionStatus());
	        }

	        return Response.ok().entity(result).build();
	}

    @Override
    public ActionStatus remove(String offerTemplateCode, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.remove(offerTemplateCode, validFrom, validTo); 
        } catch (Exception e) {
        	if (e.getCause() != null && e.getCause().getCause() != null) {
			if (e.getCause().getCause().getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(OfferTemplate.class, offerTemplateCode);
			}
        	}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            OfferTemplate offerTemplate = offerTemplateApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(offerTemplate.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus();

        try {
            offerTemplateApi.enableOrDisable(code, validFrom, validTo, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus();

        try {
            offerTemplateApi.enableOrDisable(code, validFrom, validTo, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

	@Override
	public Response duplicateOffer(String offerTemplateCode, boolean duplicateHierarchy, boolean preserveCode, Date validFrom, Date validTo) {
		GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();
		try {
            result.setOfferTemplate(offerTemplateApi.duplicate(offerTemplateCode, duplicateHierarchy, preserveCode, validFrom, validTo));
        	return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result.getActionStatus());
        }

	}

	@Override
	public Response updateStatus(String offerTemplateCode, LifeCycleStatusEnum status, Date validFrom, Date validTo) {
		   ActionStatus result = new ActionStatus();
	        try {
	            offerTemplateApi.updateStatus(offerTemplateCode, status, validFrom, validTo);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response addProduct(String offerCode, ProductOfferTemplateDto productDto) {
		GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();
		try {
            result.setOfferTemplate(offerTemplateApi.addProduct(offerCode, productDto));
        	return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result.getActionStatus());
        }

	}

	@Override
	public Response dissociateProduct(String offerCode, Date validFrom, Date validTo, List<String> productCodes) {
		GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();
		try {
            result.setOfferTemplate(offerTemplateApi.dissociateProduct(offerCode, validFrom, validTo, productCodes));
        	return Response.ok(result).build();
        } catch (MeveoApiException e) {
            return errorResponse(e, result.getActionStatus());
        }

	}
    


}