package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.OccTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.dto.response.GetOccTemplatesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.OccTemplateRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OccTemplateRsImpl extends BaseRs implements OccTemplateRs {

    @Inject
    private OccTemplateApi occTemplateApi;

    @Override
    public ActionStatus create(OccTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OccTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOccTemplateResponseDto find(String occTemplateCode) {
        GetOccTemplateResponseDto result = new GetOccTemplateResponseDto();

        try {
            result.setOccTemplate(occTemplateApi.find(occTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String occTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.remove(occTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OccTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            occTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOccTemplatesResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        GetOccTemplatesResponseDto result = new GetOccTemplatesResponseDto();
        
        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering(query, null, offset, limit, sortBy, sortOrder);

        try {
            result = occTemplateApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public GetOccTemplatesResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        GetOccTemplatesResponseDto result = new GetOccTemplatesResponseDto();

        try {
            result = occTemplateApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }
}
