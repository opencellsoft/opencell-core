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
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.response.notification.GetNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/notification")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface NotificationRs extends IBaseRs {

    /**
     * Create a new notification
     * 
     * @param postData The notification's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(NotificationDto postData);

    /**
     * Update an existing notification
     * 
     * @param postData The notification's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(NotificationDto postData);

    /**
     * Find a notification with a given code 
     * 
     * @param notificationCode The notification's code
     * @return
     */
    @GET
    @Path("/")
    GetNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

    /**
     * Remove an existing notification with a given code 
     * 
     * @param notificationCode The notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{notificationCode}")
    ActionStatus remove(@PathParam("notificationCode") String notificationCode);
    
    /**
     * List the notification history
     * 
     * @return
     */
    @GET
    @Path("/listNotificationHistory")
    NotificationHistoriesResponseDto listNotificationHistory();

    /**
     * List inbound requests
     * 
     * @return
     */
    @GET
    @Path("/listInboundRequest")
    InboundRequestsResponseDto listInboundRequest();

    /**
     * Create new or update an existing notification with a given code
     * 
     * @param postData The notification's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(NotificationDto postData);
}
