package org.meveo.api.rest.job;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * 
 * @author Manu Liwanag
 *
 */
@Path("/timerEntity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface TimerEntityRs extends IBaseRs{
	
	@Path("/create")
	@POST
	ActionStatus create(TimerEntityDto postData);
	
	@Path("/update")
	@POST
	ActionStatus update(TimerEntityDto postData);
	
	@Path("/createOrUpdate")
	@POST
	ActionStatus createOrUpdate(TimerEntityDto postData);
}
