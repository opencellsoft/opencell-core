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
 * @created 03.07.2015
 **/
@WebService
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
