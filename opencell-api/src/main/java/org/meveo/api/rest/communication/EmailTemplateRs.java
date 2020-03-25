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
    ActionStatus create(EmailTemplateDto emailTemplateDto);

	/**
	 * update an emailTemplate by dto
     *
	 * @param emailTemplateDto The email template's data 
	 * @return Request processing status
	 */
    @PUT
    @Path("/")
    ActionStatus update(EmailTemplateDto emailTemplateDto);

    /**
     * Find an email template with a given code
     * 
     * @param code The email template's code
     * @return Returns an email template
     */
    @GET
    @Path("/")
    EmailTemplateResponseDto find(@QueryParam("code") String code);

    /**
     * remove an emailTemplate by code
     * 
     * @param code The email template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List email templates
     * 
     * @return List of email templates
     */
    @GET
    @Path("/list")
    EmailTemplatesResponseDto list();

    /**
     * Create new or update an existing email template by dto
     * 
     * @param emailTemplateDto The email template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(EmailTemplateDto emailTemplateDto);
}

