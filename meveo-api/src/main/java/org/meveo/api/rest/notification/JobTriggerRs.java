package org.meveo.api.rest.notification;

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
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.response.notification.GetJobTriggerResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Tyshan Shi
 **/
@Path("/notification/jobTrigger")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface JobTriggerRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(JobTriggerDto postData);

	@PUT
	@Path("/")
	ActionStatus update(JobTriggerDto postData);

	@GET
	@Path("/")
	GetJobTriggerResponseDto find(@QueryParam("notificationCode") String notificationCode);

	@DELETE
	@Path("/{notificationCode}")
	ActionStatus remove(@PathParam("notificationCode") String notificationCode);

	@POST
	@Path("/createOrUpdate")
	ActionStatus createOrUpdate(JobTriggerDto postData);
}
