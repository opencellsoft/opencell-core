package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;

@WebService
public interface FilteredList4_3Ws extends IBaseWs {

	@WebMethod
	@Deprecated
	// since 4.4
	public FilteredListResponseDto list(@WebParam(name = "filter") String filter, @WebParam(name = "firstRow") Integer firstRow,
			@WebParam(name = "numberOfRows") Integer numberOfRows);

	@WebMethod
	@Deprecated
	// since 4.4
	public FilteredListResponseDto listByXmlInput(@WebParam(name = "filter") FilteredListDto postData);

}