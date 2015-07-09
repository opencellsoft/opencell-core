package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;

/**
 * @author anasseh
 * @created 03.07.2015
 **/
@WebService
public interface InvoicingWs extends IBaseWs {


	@WebMethod
	ActionStatus createBillingRun(@WebParam(name = "createBillingRunRequest") CreateBillingRunDto createBillingRunDto);
	
	@WebMethod
	GetBillingRunInfoResponseDto getBillingRunInfo(@WebParam(name = "getBillingRunInfoRequest") Long billingRunId);
	
	@WebMethod
	GetBillingAccountListInRunResponseDto getBillingAccountListInRun(@WebParam(name = "getBillingAccountListInRunRequest") Long billingRunId);
	
//	@WebMethod
//	ActionStatus getPreinvoicingReport(@WebParam(name = "getPreinvoicingReportRequest") long billingRunId);
//	
//	@WebMethod
//	ActionStatus getPostInvoicingReport(@WebParam(name = "getPostInvoicingReportRunRequest") long billingRunId);
//	
//	@WebMethod
//	ActionStatus excludeBillingAccountListFromRun(@WebParam(name = "excludeBillingAccountListFromRunRequest") ExcludeBillingAccountListFromRunDto excludeBillingAccountListFromRunDto);
//
//	@WebMethod
//	ActionStatus validateBillingRun(@WebParam(name = "validateBillingRunRequest") long billingRunId);
//	
//	@WebMethod
//	ActionStatus cancelBillingRun(@WebParam(name = "cancelBillingRunRequest") long billingRunId);
//		
}
