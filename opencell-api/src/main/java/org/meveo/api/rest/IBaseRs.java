package org.meveo.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.meveo.api.dto.ActionStatus;

/**
 * @author Edward P. Legaspi
 **/
public interface IBaseRs {

    /**
     * Get version of application
     * 
     * @return Action status with version number as a message
     */
    @GET
    @Path("/version")
    public ActionStatus index();

}
