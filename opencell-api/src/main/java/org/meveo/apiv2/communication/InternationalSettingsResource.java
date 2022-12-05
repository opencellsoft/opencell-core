package org.meveo.apiv2.communication;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.dto.communication.EmailTemplateDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/setting/internationalSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface InternationalSettingsResource {

    @PUT
    @Path("/EmailTemplate/{EmailTemplateCode}")
    @Operation(
            summary = "Update an EmailTemplate",
            tags = { "email", "email_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the email template successfully updated"),
                    @ApiResponse(responseCode = "404", description = "The EmailTemplateCode does not exists"),
                    @ApiResponse(responseCode = "400", description = "An error happened while updating EmailTemplate")
            }
    )
    EmailTemplateDto update(@PathParam("EmailTemplateCode") String emailTemplateCode, EmailTemplateDto emailTemplateDto);
}
