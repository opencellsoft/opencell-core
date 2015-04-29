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
import org.meveo.api.dto.response.notification.ListInboundRequestResponseDto;
import org.meveo.api.dto.response.notification.ListNotificationHistoryResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/notification")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface NotificationRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(NotificationDto postData);

	@PUT
	@Path("/")
	ActionStatus update(NotificationDto postData);

	@GET
	@Path("/")
	GetNotificationResponseDto find(@QueryParam("notificationCode") String notificationCode);

	@DELETE
	@Path("/{notificationCode}")
	ActionStatus remove(@PathParam("notificationCode") String notificationCode);

	@GET
	@Path("/listNotificationHistory")
	ListNotificationHistoryResponseDto listNotificationHistory();
	
	@GET
	@Path("/listInboundRequest")
	ListInboundRequestResponseDto listInboundRequest();

}
