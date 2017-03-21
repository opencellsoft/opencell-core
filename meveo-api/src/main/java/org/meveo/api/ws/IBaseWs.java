package org.meveo.api.ws;

import javax.jws.WebMethod;

import org.meveo.api.dto.ActionStatus;

/**
 * @author Edward P. Legaspi
 **/
public interface IBaseWs {

    /**
     * Get version of application ws
     * 
     * @return Action status with version number as a message ws
     */
    @WebMethod
    public ActionStatus index();

}
