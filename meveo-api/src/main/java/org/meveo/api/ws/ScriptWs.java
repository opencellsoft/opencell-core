package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.script.OfferModelScriptResponseDto;
import org.meveo.api.dto.script.OfferModelScriptDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface ScriptWs extends IBaseWs {

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

}
