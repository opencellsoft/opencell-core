package org.meveo.api.rest.job;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
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

	@Path("/")
	@PUT
	ActionStatus update(JobInstanceDto postData);

	@POST
	@Path("/createOrUpdate")
	ActionStatus createOrUpdate(JobInstanceDto postData);

	@GET
	@Path("/")
	JobInstanceResponseDto find(@QueryParam("jobInstanceCode") String jobInstanceCode);

	@DELETE
	@Path("/{jobInstanceCode}")
	ActionStatus remove(@PathParam("jobInstanceCode") String jobInstanceCode);

	// timer

	@Path("/createTimer")
	@POST
	ActionStatus createTimer(TimerEntityDto postData);

	@Path("/updateTimer")
	@PUT
	ActionStatus updateTimer(TimerEntityDto postData);

	@Path("/createOrUpdateTimer")
	@POST
	ActionStatus createOrUpdateTimer(TimerEntityDto postData);

}
