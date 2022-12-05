/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.communication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.response.communication.EmailTemplateResponseDto;
import org.meveo.api.dto.response.communication.EmailTemplatesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 5:40:20 AM
 *
 */
@Path("/communication/emailTemplate")
@Tag(name = "EmailTemplate", description = "@%EmailTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface EmailTemplateRs extends IBaseRs {

	/**
	 * Create an email template by dto
     *
	 * @param emailTemplateDto The email template's data
	 * @return Request processing status
	 */
	@POST
    @Path("/")
	@Operation(
			summary="	  Create an email template by dto	  ",
			description="	  Create an email template by dto	  ",
			operationId="POST_EmailTemplate_create",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(EmailTemplateDto emailTemplateDto);

	/**
	 * update an emailTemplate by dto
     *
	 * @param emailTemplateDto The email template's data 
	 * @return Request processing status
	 */
    @PUT
    @Path("/")
	@Operation(
			summary="	  update an emailTemplate by dto	  ",
			description="	  update an emailTemplate by dto	  ",
			operationId="    PUT_EmailTemplate_update",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(EmailTemplateDto emailTemplateDto);

    /**
     * Find an email template with a given code
     * 
     * @param code The email template's code
     * @return Returns an email template
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find an email template with a given code  ",
			description=" Find an email template with a given code  ",
			operationId="    GET_EmailTemplate_search",
			responses= {
				@ApiResponse(description=" Returns an email template ",
						content=@Content(
									schema=@Schema(
											implementation= EmailTemplateResponseDto.class
											)
								)
				)}
	)
    EmailTemplateResponseDto find(@QueryParam("code") String code);

    /**
     * remove an emailTemplate by code
     * 
     * @param code The email template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" remove an emailTemplate by code  ",
			description=" remove an emailTemplate by code  ",
			operationId="    DELETE_EmailTemplate_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List email templates
     * 
     * @return List of email templates
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List email templates  ",
			description=" List email templates  ",
			operationId="    GET_EmailTemplate_list",
			responses= {
				@ApiResponse(description=" List of email templates ",
						content=@Content(
									schema=@Schema(
											implementation= EmailTemplatesResponseDto.class
											)
								)
				)}
	)
    EmailTemplatesResponseDto list();

    /**
     * Create new or update an existing email template by dto
     * 
     * @param emailTemplateDto The email template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing email template by dto  ",
			description=" Create new or update an existing email template by dto  ",
			operationId="    POST_EmailTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(EmailTemplateDto emailTemplateDto);
}

