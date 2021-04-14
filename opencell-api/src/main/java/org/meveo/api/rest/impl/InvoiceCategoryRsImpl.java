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

import org.meveo.api.InvoiceCategoryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.dto.response.InvoiceCategoryResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.InvoiceCategoryRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceCategoryRsImpl extends BaseRs implements InvoiceCategoryRs {

    @Inject
    private InvoiceCategoryApi invoiceCategoryApi;

    @Override
    public InvoiceCategoryResponseDto list() {
        InvoiceCategoryResponseDto result = new InvoiceCategoryResponseDto();

        try {
            result = new InvoiceCategoryResponseDto(
                    invoiceCategoryApi.search(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering()) );
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus create(InvoiceCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(InvoiceCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetInvoiceCategoryResponse find(String invoiceCategoryCode) {
        GetInvoiceCategoryResponse result = new GetInvoiceCategoryResponse();

        try {
            result.setInvoiceCategory(invoiceCategoryApi.find(invoiceCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String invoiceCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.remove(invoiceCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(InvoiceCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public InvoiceCategoryResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        InvoiceCategoryResponseDto result = new InvoiceCategoryResponseDto();

        try {
            result = new InvoiceCategoryResponseDto(invoiceCategoryApi.search(pagingAndFiltering));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
