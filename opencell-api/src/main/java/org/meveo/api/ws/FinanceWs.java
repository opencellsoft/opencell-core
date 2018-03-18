package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;

@WebService
public interface FinanceWs extends IBaseWs {

	@WebMethod
	ActionStatus createRevenueRecognitionRule(@WebParam(name = "revenueRecognitionRule") RevenueRecognitionRuleDto moduleDto);

	@WebMethod
	ActionStatus updateRevenueRecognitionRule(@WebParam(name = "revenueRecognitionRule") RevenueRecognitionRuleDto moduleDto);

	@WebMethod
	ActionStatus deleteRevenueRecognitionRule(@WebParam(name = "code") String code);

	@WebMethod
	RevenueRecognitionRuleDtosResponse listRevenueRecognitionRules();

	@WebMethod
	RevenueRecognitionRuleDtoResponse getRevenueRecognitionRule(@WebParam(name = "code") String code);

	@WebMethod
	ActionStatus createOrUpdateRevenueRecognitionRule(@WebParam(name = "revenueRecognitionRule") RevenueRecognitionRuleDto moduleDto);

    @WebMethod
    ActionStatus createReportExtract(@WebParam(name = "reportExtract") ReportExtractDto postData);

    @WebMethod
    ActionStatus updateReportExtract(@WebParam(name = "reportExtract") ReportExtractDto postData);

    @WebMethod
    ActionStatus createOrUpdateReportExtract(@WebParam(name = "reportExtract") ReportExtractDto postData);

    @WebMethod
    ActionStatus removeReportExtract(@WebParam(name = "reportExtractCode") String reportExtractCode);

    @WebMethod
    ReportExtractsResponseDto listReportExtract();

    @WebMethod
    ReportExtractResponseDto findReportExtract(@WebParam(name = "reportExtractCode") String reportExtractCode);

    @WebMethod
    ActionStatus runReportExtract(@WebParam(name = "runReport") RunReportExtractDto postData);

}
