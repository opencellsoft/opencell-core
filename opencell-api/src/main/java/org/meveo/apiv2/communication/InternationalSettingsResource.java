package org.meveo.apiv2.communication;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatePatchDto;
import org.meveo.api.dto.communication.sms.SMSTemplateDto;
import org.meveo.api.rest.PATCH;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/setting/internationalSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface InternationalSettingsResource {

    @POST
    @Path("/EmailTemplate/{EmailTemplateCode}")
    @Operation(
            summary = "Create an EmailTemplate",
            tags = { "email", "email_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the email template successfully created"),
                    @ApiResponse(responseCode = "404", description = "The EmailTemplateCode already exists"),
                    @ApiResponse(responseCode = "400", description = "An error happened while creating EmailTemplate")
            }
    )
    EmailTemplateDto create(EmailTemplateDto emailTemplateDto);

    @GET
    @Path("/EmailTemplate/{EmailTemplateCode}")
    @Operation(
            summary = "Get An EmailTemplate",
            tags = { "email", "email_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the email template successfully returned"),
                    @ApiResponse(responseCode = "404", description = "The EmailTemplateCode does not exist"),
                    @ApiResponse(responseCode = "400", description = "An error happened while getting EmailTemplate")
            }
    )
    EmailTemplateDto getEmailTemplate(@PathParam("EmailTemplateCode") String emailTemplateCode);

    @DELETE
    @Path("/EmailTemplate/{EmailTemplateCode}")
    @Operation(
            summary = "Delete an EmailTemplate",
            tags = { "email", "email_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the EmailTemplate successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "The EmailTemplate does not exists"),
                    @ApiResponse(responseCode = "400", description = "An error happened while deleting EmailTemplate")
            }
    )
    ActionStatus deleteEmailTemplate(@PathParam("EmailTemplateCode") String emailTemplateCode);

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

    @PATCH
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
    EmailTemplateDto partialUpdate(@PathParam("EmailTemplateCode") String emailTemplateCode, EmailTemplatePatchDto emailTemplateDto);

    @POST
    @Path("/SMSTemplate")
    @Operation(
            summary = "Create an SMS Template",
            tags = { "sms", "sms_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the sms template successfully updated"),
                    @ApiResponse(responseCode = "400", description = "An error happened while creating SMS Template")
            }
    )
    SMSTemplateDto create(SMSTemplateDto smsTemplateDto);


    @PUT
    @Path("/SMSTemplate/{SMSTemplateCode}")
    @Operation(
            summary = "Update an SMSTemplate",
            tags = { "email", "sms_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the sms template successfully updated"),
                    @ApiResponse(responseCode = "404", description = "The SMS Template does not exists"),
                    @ApiResponse(responseCode = "400", description = "An error happened while updating SMSTemplate")
            }
    )
    SMSTemplateDto update(@PathParam("SMSTemplateCode") String smsTemplateCode, SMSTemplateDto smsTemplateDto);

    @DELETE
    @Path("/SMSTemplate/{SMSTemplateCode}")
    @Operation(
            summary = "Delete an SMSTemplate",
            tags = { "sms", "sms_template", "communication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "the sms template successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "The SMS Template does not exists"),
                    @ApiResponse(responseCode = "400", description = "An error happened while deleting SMSTemplate")
            }
    )
    ActionStatus delete(@PathParam("SMSTemplateCode") String smsTemplateCode);

    @GET
    @Path("/SMSTemplate/{SMSTemplateCode}")
    @Operation(
            summary = "Get an SMSTemplate",
            tags = {"sms", "sms_template", "communication"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "the sms template successfully returned"),
                    @ApiResponse(responseCode = "404", description = "The SMS Template does not exists"),
                    @ApiResponse(responseCode = "400", description = "An error happened while getting SMSTemplate")
            }
    )
    SMSTemplateDto get(@PathParam("SMSTemplateCode") String smsTemplateCode);
}
