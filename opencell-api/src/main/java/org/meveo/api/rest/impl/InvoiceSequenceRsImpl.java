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

import org.meveo.api.InvoiceSequenceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.dto.response.GetInvoiceSequenceResponse;
import org.meveo.api.dto.response.GetInvoiceSequencesResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.InvoiceSequenceRs;

/**
 * @author akadid abdelmounaim
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceSequenceRsImpl extends BaseRs implements InvoiceSequenceRs {

    @Inject
    private InvoiceSequenceApi invoiceSequenceApi;
    
	@Override
	public ActionStatus create(InvoiceSequenceDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceSequenceApi.create(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public ActionStatus update(InvoiceSequenceDto invoiceTypeDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceSequenceApi.update(invoiceTypeDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public GetInvoiceSequenceResponse find(String invoiceSequenceCode) {
	    GetInvoiceSequenceResponse result = new GetInvoiceSequenceResponse();
		result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
	    try {
	    	result.setInvoiceSequenceDto(invoiceSequenceApi.find(invoiceSequenceCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
	    return result;
	}

	@Override
	public ActionStatus createOrUpdate(InvoiceSequenceDto invoiceSequenceDto) {
	    ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
			invoiceSequenceApi.createOrUpdate(invoiceSequenceDto);
		} catch (Exception e) {
			processException(e, result);
		}
	    return result;
	}

	@Override
	public GetInvoiceSequencesResponse list() {
	    GetInvoiceSequencesResponse result = new GetInvoiceSequencesResponse();
		result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
	    try {
	    	result.setInvoiceSequencesDto(invoiceSequenceApi.list());
		} catch (Exception e) {
            processException(e, result.getActionStatus());
		}
	    return result;
	}
}