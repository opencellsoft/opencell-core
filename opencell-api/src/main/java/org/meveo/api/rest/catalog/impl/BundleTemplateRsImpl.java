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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.catalog.BundleTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.BundleTemplateRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BundleTemplate;

/**
 * @author abdelmounaim akadid
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BundleTemplateRsImpl extends BaseRs implements BundleTemplateRs {

    @Inject
    private BundleTemplateApi bundleTemplateApi;
    
    @Override
    public Response createBundleTemplate(BundleTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            BundleTemplate bundleTemplate = bundleTemplateApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(bundleTemplate.getCode());
            }
            responseBuilder = Response.ok().entity(result);
        } catch (Exception e) {
            processException(e, result);
        }

        return getResponse(responseBuilder);
    }

    @Override
    public Response updateBundleTemplate(BundleTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            BundleTemplate bundleTemplate = bundleTemplateApi.update(postData);
            responseBuilder = Response.ok().entity(result);
        } catch (Exception e) {
            processException(e, result);
        }

        return getResponse(responseBuilder);
    }

    @Override
    public Response createOrUpdateBundleTemplate(BundleTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            BundleTemplate bundleTemplate = bundleTemplateApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(bundleTemplate.getCode());
            }
            responseBuilder = Response.ok().entity(result);
        } catch (Exception e) {
            processException(e, result);
        }

        return getResponse(responseBuilder);
    }
    
    /**
     * @param responseBuilder response builder
     * @return response
     */
    private Response getResponse(Response.ResponseBuilder responseBuilder) {
        Response response = null;
        if (responseBuilder != null) {
            response = responseBuilder.build();
            log.debug("RESPONSE={}", response.getEntity());
        }

        return response;
    }
}