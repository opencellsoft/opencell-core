package org.meveo.api.rest.communication;

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
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.response.communication.EmailTemplatesResponseDto;
import org.meveo.api.dto.response.communication.EmailTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 5:40:20 AM
 *
 */
@Path("/communication/emailTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface EmailTemplateRs extends IBaseRs {

	@POST
    @Path("/")
    ActionStatus create(EmailTemplateDto emailTemplateDto);

    @PUT
    @Path("/")
    ActionStatus update(EmailTemplateDto emailTemplateDto);

    @GET
    @Path("/")
    EmailTemplateResponseDto find(@QueryParam("code") String code);

    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    @GET
    @Path("/list")
    EmailTemplatesResponseDto list();

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(EmailTemplateDto emailTemplateDto);
}

