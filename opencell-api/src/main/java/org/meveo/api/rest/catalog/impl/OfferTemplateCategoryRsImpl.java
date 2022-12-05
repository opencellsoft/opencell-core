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

import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.OfferTemplateCategoriesResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OfferTemplateCategoryRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OfferTemplateCategoryRsImpl extends BaseRs implements OfferTemplateCategoryRs {

    @Inject
    private OfferTemplateCategoryApi offerTemplateCategoryApi;

    @Override
    public ActionStatus create(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateCategoryResponseDto find(String offerTemplateCategoryCode) {
        GetOfferTemplateCategoryResponseDto result = new GetOfferTemplateCategoryResponseDto();

        try {
            result.setOfferTemplateCategory(offerTemplateCategoryApi.find(offerTemplateCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus delete(String offerTemplateCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.remove(offerTemplateCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            offerTemplateCategoryApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            offerTemplateCategoryApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public OfferTemplateCategoriesResponseDto listGetAll() {

        OfferTemplateCategoriesResponseDto result = new OfferTemplateCategoriesResponseDto();

        try {
            result = offerTemplateCategoryApi.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}