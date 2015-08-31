package org.meveo.api.rest.communication;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;


@Path("Communication")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CommunicationRs extends IBaseRs {
	
    @POST
    @Path("/inbound")
    public ActionStatus inboundCommunication(CommunicationRequestDto communicationRequestDto);
    
}
