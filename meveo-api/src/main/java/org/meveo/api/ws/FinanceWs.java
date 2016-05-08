package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.RevenueRecognitionRuleDto;
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
	ActionStatus createOrUpdateRevenueRecognitionRule(@WebParam(name = "module") RevenueRecognitionRuleDto moduleDto);
}
