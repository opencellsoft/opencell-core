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

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 3:51:34 AM 
 */
package org.meveo.api.rest.account;

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
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;
import org.meveo.api.dto.response.account.ProviderContactsResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 3:51:34 AM
 *
 */

@Path("/account/providerContact")
@Tag(name = "ProviderContact", description = "@%ProviderContact")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ProviderContactRs extends IBaseRs {

	/**
	 * Create a provider contact
	 * @param providerContactDto The provider contact's data
	 * @return Request processing status
	 */
    @POST
    @Path("/")
	@Operation(
			summary="	  Create a provider contact	  ",
			description="	  Create a provider contact	  ",
			operationId="    POST_ProviderContact_create",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(ProviderContactDto providerContactDto);

    /**
     * Update an existing provider contact
     * 
     * @param providerContactDto The provider contact's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing provider contact  ",
			description=" Update an existing provider contact  ",
			operationId="    PUT_ProviderContact_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(ProviderContactDto providerContactDto);

    /**
     * Search for a provider contact with a given code 
     * @param providerContactCode The provider contact's code
     * @return A provider contact
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a provider contact with a given code  ",
			description=" Search for a provider contact with a given code  ",
			operationId="    GET_ProviderContact_search",
			responses= {
				@ApiResponse(description=" A provider contact ",
						content=@Content(
									schema=@Schema(
											implementation= ProviderContactResponseDto.class
											)
								)
				)}
	)
    ProviderContactResponseDto find(@QueryParam("providerContactCode") String providerContactCode);

    /**
     * Remove an existing provider contact with a given code 
     * 
     * @param providerContactCode The provider contact's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove an existing provider contact with a given code   ",
			description=" Remove an existing provider contact with a given code   ",
			operationId="    DELETE_ProviderContact_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("code") String providerContactCode);

    /**
     * List of provider contacts
     *
     * @return A list of provider contacts
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List of provider contacts ",
			description=" List of provider contacts ",
			operationId="    GET_ProviderContact_list",
			responses= {
				@ApiResponse(description=" A list of provider contacts ",
						content=@Content(
									schema=@Schema(
											implementation= ProviderContactsResponseDto.class
											)
								)
				)}
	)
    ProviderContactsResponseDto list();
    
    /**
     * Create new or update an existing provider contact
     * 
     * @param providerContactDto The provider contact's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing provider contact  ",
			description=" Create new or update an existing provider contact  ",
			operationId="    POST_ProviderContact_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(ProviderContactDto providerContactDto);
}

