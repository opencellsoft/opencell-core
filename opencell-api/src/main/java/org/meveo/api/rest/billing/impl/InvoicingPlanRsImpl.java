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

package org.meveo.api.rest.billing.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.billing.InvoicingPlanApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.InvoicingPlanDto;
import org.meveo.api.dto.response.billing.InvoicingPlanResponseDto;
import org.meveo.api.dto.response.billing.InvoicingPlansResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.InvoicingPlanRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoicingPlanRsImpl extends BaseRs implements InvoicingPlanRs {

	@Inject
	private InvoicingPlanApi invoicingPlanApi;

	@Override
	public ActionStatus create(InvoicingPlanDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanApi.create(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public InvoicingPlanResponseDto find(String invoicingPlanCode) {
		InvoicingPlanResponseDto result = new InvoicingPlanResponseDto();

		try {
			result.setInvoicingPlanDto(invoicingPlanApi.find(invoicingPlanCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ActionStatus update(InvoicingPlanDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus remove(String invoicingPlanCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanApi.remove(invoicingPlanCode);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public Response createOrUpdate(InvoicingPlanDto postData) {
		InvoicingPlanResponseDto result = new InvoicingPlanResponseDto();

		try {
			result.setInvoicingPlanDto(new InvoicingPlanDto(invoicingPlanApi.createOrUpdate(postData), null));
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		    return errorResponse(e, result.getActionStatus());
		}

	}

	@Override
	public InvoicingPlansResponseDto list(PagingAndFiltering pagingAndFiltering) {
		InvoicingPlansResponseDto result = new InvoicingPlansResponseDto();

		try {
			result = new InvoicingPlansResponseDto(invoicingPlanApi.search(pagingAndFiltering));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

}
