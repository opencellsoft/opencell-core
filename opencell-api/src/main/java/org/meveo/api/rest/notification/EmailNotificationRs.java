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
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/notification/email")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface EmailNotificationRs extends IBaseRs {

    /**
     * Create a new email notification
     * 
     * @param postData The email notification's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(EmailNotificationDto postData);

    /**
     * Update an existing email notification
     * 
     * @param postData The email notification's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(EmailNotificationDto postData);

    /**
     * Find a email notification with a given code 
     * 
     * @param notificationCode The email notification's code
     * @return
     */
    @GET
    @Path("/")
    GetEmailNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing email notification with a given code 
     * 
     * @param notificationCode The email notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
    ActionStatus remove(@PathParam("notificationCode") String notificationCode);

    /**
     * Create new or update an existing email notification with a given code
     * 
     * @param postData The email notification's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(EmailNotificationDto postData);

}
