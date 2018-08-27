package org.meveo.api.pub.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.document.sign.SignCallbackDto;
import org.meveo.api.rest.IBaseRs;

/**
 * A public rest services to serve Yousign webhook callbacks .
 * 
 * @author Said Ramli
 */
@Path("/yousign")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface YousignCallbackRs extends IBaseRs {

    @PUT
    @Path("/callback")
    public ActionStatus youSignCallback(SignCallbackDto callbackDto);

}
