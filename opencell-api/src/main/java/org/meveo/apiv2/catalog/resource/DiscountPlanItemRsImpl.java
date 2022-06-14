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

package org.meveo.apiv2.catalog.resource;

import org.meveo.api.catalog.DiscountPlanItemApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.model.catalog.DiscountPlanItem;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DiscountPlanItemRsImpl extends BaseRs implements DiscountPlanItemRs {

    @Inject
    private DiscountPlanItemApi discountPlanItemApi;

    @Override
    public ActionStatus create(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	DiscountPlanItem discountPlanItem = discountPlanItemApi.create(postData);
        	result.setEntityId(discountPlanItem.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(String code, DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	DiscountPlanItem discountPlanItem = discountPlanItemApi.update(code, postData);
        	result.setEntityId(discountPlanItem.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DiscountPlanItemResponseDto find(String discountPlanItemCode) {
        DiscountPlanItemResponseDto result = new DiscountPlanItemResponseDto();

        try {
            result.setDiscountPlanItem(discountPlanItemApi.find(discountPlanItemCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String discountPlanItemCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.remove(discountPlanItemCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DiscountPlanItemsResponseDto list() {
        DiscountPlanItemsResponseDto result = new DiscountPlanItemsResponseDto();

        try {
            result.setDiscountPlanItems(discountPlanItemApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public DiscountPlanItemsResponseDto list(PagingAndFilteringRest pagingAndFiltering) {
        DiscountPlanItemsResponseDto result = new DiscountPlanItemsResponseDto();

        try {
            result = discountPlanItemApi.list(
                    GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering(pagingAndFiltering));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            discountPlanItemApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            discountPlanItemApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}