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
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/notification/webhook")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface WebHookNotificationRs extends IBaseRs {

    /**
     * Create a new web hook notification
     * 
     * @param postData The web hook notification's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(WebHookDto postData);

    /**
     * Update an existing web hook notification
     * 
     * @param postData The web hook notification's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(WebHookDto postData);

    /**
     * Find a web hook notification with a given code 
     * 
     * @param notificationCode The web hook notification's code
     * @return
     */
    @GET
    @Path("/")
    GetWebHookNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing web hook notification with a given code 
     * 
     * @param notificationCode The web hook notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
    ActionStatus remove(@PathParam("notificationCode") String notificationCode);

    /**
     * Create new or update an existing web hook notification with a given code
     * 
     * @param postData The web hook notification's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(WebHookDto postData);
}
