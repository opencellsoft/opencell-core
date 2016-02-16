package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface ReportingWs extends IBaseWs {

	@WebMethod
	public ActionStatus createMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

}
