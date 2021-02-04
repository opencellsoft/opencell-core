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

import org.meveo.api.billing.InvoicingPlanItemApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.InvoicingPlanItemDto;
import org.meveo.api.dto.response.billing.InvoicingPlanItemResponseDto;
import org.meveo.api.dto.response.billing.InvoicingPlanItemsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.InvoicingPlanItemRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoicingPlanItemRsImpl extends BaseRs implements InvoicingPlanItemRs {

	@Inject
	private InvoicingPlanItemApi invoicingPlanItemApi;

	@Override
	public ActionStatus create(InvoicingPlanItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanItemApi.create(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public InvoicingPlanItemResponseDto find(String invoicingPlanItemCode) {
		InvoicingPlanItemResponseDto result = new InvoicingPlanItemResponseDto();

		try {
			result.setInvoicingPlanItemDto(invoicingPlanItemApi.find(invoicingPlanItemCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ActionStatus update(InvoicingPlanItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanItemApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus remove(String invoicingPlanItemCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanItemApi.remove(invoicingPlanItemCode);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus createOrUpdate(InvoicingPlanItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoicingPlanItemApi.createOrUpdate(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public InvoicingPlanItemsResponseDto list(PagingAndFiltering pagingAndFiltering) {
		InvoicingPlanItemsResponseDto result = new InvoicingPlanItemsResponseDto();

		try {
			result = new InvoicingPlanItemsResponseDto(invoicingPlanItemApi.search(pagingAndFiltering));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

}
