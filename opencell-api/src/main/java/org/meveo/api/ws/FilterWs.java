package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.GetFilterResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface FilterWs extends IBaseWs {

    @WebMethod
    ActionStatus createOrUpdateFilter(@WebParam(name = "filter") FilterDto postData);

    /**
     * Find a Filter by its code
     * 
     * @param code Filter code
     * @return Request processing status and filter information
     */
    @WebMethod
    GetFilterResponseDto findFilter(@WebParam(name = "code") String code);

    /**
     * Enable a Filter by its code
     * 
     * @param code Filter code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableFilter(@WebParam(name = "code") String code);

    /**
     * Disable a Workflow by its code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableFilter(@WebParam(name = "code") String code);
}