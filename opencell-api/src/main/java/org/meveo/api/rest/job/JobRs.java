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
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.job.JobExecutionResultResponseDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.dto.response.job.TimerEntityResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Path("/job")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface JobRs extends IBaseRs {

    /**
     * Execute a given job instance info 
     * 
     * @param postData job instance info's data
     * @return request processing status
     */
    @POST
    @Path("/execute")
    JobExecutionResultResponseDto execute(JobInstanceInfoDto postData);
    
    /**
     * Stop a given job instance info 
     * 
     * @param jobInstanceCode job instance code
     * @return request processing status
     */
    @POST
    @Path("/stop")
    ActionStatus stop(@PathParam("jobInstanceCode") String jobInstanceCode);
    /**
     * Create a new job instance
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @Path("/create")
    @POST
    ActionStatus create(JobInstanceDto postData);

    /**
     * Update an existing job instance
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(JobInstanceDto postData);

    /**
     * Create new or update an existing job instance with a given code
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(JobInstanceDto postData);

    /**
     * Find a job instance with a given code 
     * 
     * @param jobInstanceCode string to match the code of JobInstance
     * @return object containing the matched JobInstance
     */
    @GET
    @Path("/")
    JobInstanceResponseDto find(@QueryParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Remove an existing job instance with a given code 
     * 
     * @param jobInstanceCode The job instance's code
     * @return request processing status
     */
    @DELETE
    @Path("/{jobInstanceCode}")
    ActionStatus remove(@PathParam("jobInstanceCode") String jobInstanceCode);

    // timer

    /**
     * Create a new timer entity
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @Path("/timer/")
    @POST
    ActionStatus createTimer(TimerEntityDto postData);

    /**
     * Update an existing timer entity
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @Path("/timer/")
    @PUT
    ActionStatus updateTimer(TimerEntityDto postData);

    /**
     * Create new or update an existing timer entity with a given code
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @Path("/timer/createOrUpdate/")
    @POST
    ActionStatus createOrUpdateTimer(TimerEntityDto postData);

    /**
     * Find a timer with a given code 
     * 
     * @param timerCode The timer's code
     * @return request processing status
     */
    @GET
    @Path("/timer/")
    TimerEntityResponseDto findTimer(@QueryParam("timerCode") String timerCode);

    /**
     * Remove an existing timer with a given code 
     * 
     * @param timerCode The timer's code
     * @return request processing status
     */
    @DELETE
    @Path("/timer/{timerCode}")
    ActionStatus removeTimer(@PathParam("timerCode") String timerCode);
    
    /**
     * Find a job execution result with a given id 
     * 
     * @param code string to match the code of the JobInstance
     * @param jobExecutionResultId A jobExecutionResultId
     * @return object containing the JobExecutionResultImpl
     */
    @GET
    @Path("/jobReport")
    JobExecutionResultResponseDto findJobExecutionResult(@QueryParam("code") String code, @QueryParam("id") Long jobExecutionResultId);
    
}
