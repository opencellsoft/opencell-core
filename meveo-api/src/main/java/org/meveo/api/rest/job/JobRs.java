package org.meveo.api.rest.job;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;
import org.meveo.model.jobs.JobInstanceInfoDto;

/**
 * @author Edward P. Legaspi
 **/
@Path("/job")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface JobRs extends IBaseRs {

	@POST
	@Path("/execute")
	ActionStatus execute(JobInstanceInfoDto postData);
	
	@Path("/create")
	@POST
	ActionStatus create(JobInstanceDto postData);
	
	@Path("/createTimer")
	@POST
	ActionStatus createTimer(TimerEntityDto postData);


}
