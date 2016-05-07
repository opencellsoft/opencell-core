package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.account.AccountModelScriptResponseDto;
import org.meveo.api.dto.response.account.GetAccountModelScriptsResponseDto;
import org.meveo.api.dto.response.script.OfferModelScriptResponseDto;
import org.meveo.api.dto.response.script.RevenueRecognitionScriptResponseDto;
import org.meveo.api.dto.response.script.ServiceModelScriptResponseDto;
import org.meveo.api.dto.script.AccountModelScriptDto;
import org.meveo.api.dto.script.OfferModelScriptDto;
import org.meveo.api.dto.script.RevenueRecognitionScriptDto;
import org.meveo.api.dto.script.ServiceModelScriptDto;


@WebService
public interface ScriptWs extends IBaseWs {

	// Revenue Recognition Script

	@WebMethod
	ActionStatus createRevenueRecognitionScript(@WebParam(name = "revenueRecognitionScript") RevenueRecognitionScriptDto postData);

	@WebMethod
	ActionStatus updateRevenueRecognitionScript(@WebParam(name = "revenueRecognitionScript") RevenueRecognitionScriptDto postData);

	@WebMethod
	ActionStatus createOrUpdateRevenueRecognitionScript(@WebParam(name = "revenueRecognitionScript") RevenueRecognitionScriptDto postData);

	@WebMethod
	ActionStatus removeRevenueRecognitionScript(@WebParam(name = "revenueRecognitionScriptCode") String code);

	@WebMethod
	RevenueRecognitionScriptResponseDto findRevenueRecognitionScript(@WebParam(name = "revenueRecognitionScriptCode") String code);
	
	// Offer Model Script

	@WebMethod
	ActionStatus createOfferModelScript(@WebParam(name = "offerModelScript") OfferModelScriptDto postData);

	@WebMethod
	ActionStatus updateOfferModelScript(@WebParam(name = "offerModelScript") OfferModelScriptDto postData);

	@WebMethod
	ActionStatus createOrUpdateOfferModelScript(@WebParam(name = "offerModelScript") OfferModelScriptDto postData);

	@WebMethod
	ActionStatus removeOfferModelScript(@WebParam(name = "offerModelScriptCode") String code);

	@WebMethod
	OfferModelScriptResponseDto findOfferModelScript(@WebParam(name = "offerModelScriptCode") String code);

	// Service Model Script

	@WebMethod
	ActionStatus createServiceModelScript(@WebParam(name = "serviceModelScript") ServiceModelScriptDto postData);

	@WebMethod
	ActionStatus updateServiceModelScript(@WebParam(name = "serviceModelScript") ServiceModelScriptDto postData);

	@WebMethod
	ActionStatus createOrUpdateServiceModelScript(@WebParam(name = "serviceModelScript") ServiceModelScriptDto postData);

	@WebMethod
	ActionStatus removeServiceModelScript(@WebParam(name = "serviceModelScriptCode") String code);

	@WebMethod
	ServiceModelScriptResponseDto findServiceModelScript(@WebParam(name = "serviceModelScriptCode") String code);

	// Account Model Script

	@WebMethod
	ActionStatus createAccountModelScript(@WebParam(name = "accountModelScript") AccountModelScriptDto postData);

	@WebMethod
	ActionStatus updateAccountModelScript(@WebParam(name = "accountModelScript") AccountModelScriptDto postData);
	
	@WebMethod
	ActionStatus createOrUpdateAccountModelScript(@WebParam(name = "accountModelScript") AccountModelScriptDto postData);

	@WebMethod
	AccountModelScriptResponseDto findAccountModelScript(@WebParam(name = "accountModelScriptCode") String accountModelScriptCode);

	@WebMethod
	ActionStatus removeAccountModelScript(@WebParam(name = "accountModelScriptCode") String accountModelScriptCode);

	@WebMethod
	GetAccountModelScriptsResponseDto listAccountModelScript();

}
