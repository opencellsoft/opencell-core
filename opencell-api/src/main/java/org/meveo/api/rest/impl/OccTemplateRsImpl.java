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

package org.meveo.api.rest.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

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

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OccTemplateRsImpl extends BaseRs implements OccTemplateRs {

    @Inject
    private OccTemplateApi occTemplateApi;

    @Override
    public GetOccTemplateResponseDto create(OccTemplateDto postData) {
        GetOccTemplateResponseDto result = new GetOccTemplateResponseDto();

        try {
            occTemplateApi.create(postData);
            result.setOccTemplate(occTemplateApi.find(postData.getCode()));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetOccTemplateResponseDto update(OccTemplateDto postData) {
        GetOccTemplateResponseDto result = new GetOccTemplateResponseDto();

        try {
            occTemplateApi.update(postData);
            result.setOccTemplate(occTemplateApi.find(postData.getCode()));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
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
