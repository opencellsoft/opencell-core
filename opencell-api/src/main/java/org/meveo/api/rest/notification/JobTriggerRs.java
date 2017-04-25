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

/**
 * @author Tyshan Shi
 **/
@Path("/notification/jobTrigger")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface JobTriggerRs extends IBaseRs {

    /**
     * Create a new job trigger
     * 
     * @param postData The job trigger's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(JobTriggerDto postData);

    /**
     * Update an existing job trigger
     * 
     * @param postData The job trigger's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(JobTriggerDto postData);

    /**
     * Find a job trigger with a given code 
     * 
     * @param notificationCode The job trigger's code
     * @return
     */
    @GET
    @Path("/")
    GetJobTriggerResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing job trigger with a given code 
     * 
     * @param notificationCode The job trigger's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
    ActionStatus remove(@PathParam("notificationCode") String notificationCode);

    /**
     * Create new or update an existing job trigger with a given code
     * 
     * @param postData The job trigger's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(JobTriggerDto postData);
}
