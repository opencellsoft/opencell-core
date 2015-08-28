package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface FilteredListWs {

	@WebMethod
	FilteredListResponseDto list(@WebParam(name = "filter") String filter,
			@WebParam(name = "firstRow") Integer firstRow, @WebParam(name = "numberOfRows") Integer numberOfRows);

	@WebMethod
	FilteredListResponseDto listByXmlInput(@WebParam(name = "filter") FilteredListDto postData);

}
