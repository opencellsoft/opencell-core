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

package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;

/**
 * @author anasseh
 * @since 03.07.2015
 **/
@WebService
@Deprecated
public interface InvoicingWs extends IBaseWs {


	@WebMethod
	ActionStatus createBillingRun(@WebParam(name = "createBillingRunRequest") CreateBillingRunDto createBillingRunDto);
	
	@WebMethod
	GetBillingRunInfoResponseDto getBillingRunInfo(@WebParam(name = "billingRunId") Long billingRunId);
	
	@WebMethod
	GetBillingAccountListInRunResponseDto getBillingAccountListInRun(@WebParam(name = "billingRunId") Long billingRunId);
	
    @WebMethod
    GetPreInvoicingReportsResponseDto getPreInvoicingReport(@WebParam(name = "billingRunId") Long billingRunId);
	
	@WebMethod
	GetPostInvoicingReportsResponseDto getPostInvoicingReport(@WebParam(name = "billingRunId") Long billingRunId);
	
//	@WebMethod
//	ActionStatus excludeBillingAccountListFromRun(@WebParam(name = "excludeBillingAccountListFromRunRequest") ExcludeBillingAccountListFromRunDto excludeBillingAccountListFromRunDto);
//
	@WebMethod
	ActionStatus validateBillingRun(@WebParam(name = "billingRunId") Long billingRunId);
	
	@WebMethod
	ActionStatus cancelBillingRun(@WebParam(name = "billingRunId") Long billingRunId);
 		
}
