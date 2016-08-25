package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.usage.UsageRequestDto;
import org.meveo.api.dto.usage.UsageResponseDto;

/**
 * @author Mbarek
 **/
@WebService
public interface UsageWs extends IBaseWs {


	@WebMethod
	UsageResponseDto findUsage(@WebParam(name = "usageRequestDto") UsageRequestDto usageRequestDto);  
		
}
