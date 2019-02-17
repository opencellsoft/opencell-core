package org.meveo.api.rest.catalog.impl;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OfferTemplateRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.commons.utils.StringUtils;
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
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(offerTemplate.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateResponseDto find(String offerTemplateCode, Date validFrom, Date validTo, CustomFieldInheritanceEnum inheritCF, boolean loadOfferServiceTemplate,
            boolean loadOfferProductTemplate, boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate, boolean loadAllowedDiscountPlan) {
        GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();

        try {
            result.setOfferTemplate(offerTemplateApi.find(offerTemplateCode, validFrom, validTo, inheritCF, loadOfferServiceTemplate, loadOfferProductTemplate,
                loadServiceChargeTemplate, loadProductChargeTemplate, loadAllowedDiscountPlan));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetListOfferTemplateResponseDto listGet(@Deprecated String code, @Deprecated @RestDateParam Date validFrom, @Deprecated @RestDateParam Date validTo, String query,
            String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder, CustomFieldInheritanceEnum inheritCF) {

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();

        try {
            result = (offerTemplateApi.list(code, validFrom, validTo, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder), inheritCF));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
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
    public ActionStatus remove(String offerTemplateCode, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.remove(offerTemplateCode, validFrom, validTo);
        } catch (Exception e) {
            processException(e, result);
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
}