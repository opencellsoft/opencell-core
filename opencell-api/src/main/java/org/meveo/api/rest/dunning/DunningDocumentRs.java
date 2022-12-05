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

package org.meveo.api.rest.dunning;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dunning.DunningDocumentDto;
import org.meveo.api.dto.dunning.DunningDocumentResponseDto;
import org.meveo.api.dto.dunning.DunningDocumentsListResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

/**
 * @author abdelmounaim akadid
 **/
@Path("/dunning/dunningDocument")
@Tag(name = "DunningDocument", description = "@%DunningDocument")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DunningDocumentRs extends IBaseRs {

    /**
     * Create a dunningDocument.
     * 
     * @param postData DunningDocument data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a dunningDocument.  ",
			description=" Create a dunningDocument.  ",
			operationId="    POST_DunningDocument_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(DunningDocumentDto postData);

    /**
     * Add Payments to dunningDocument.
     * 
     * @param postData DunningDocument's data
     * @return Request processing status
     */
    @PUT
    @Path("/addPayments")
	@Operation(
			summary=" Add Payments to dunningDocument.  ",
			description=" Add Payments to dunningDocument.  ",
			operationId="    PUT_DunningDocument_addPayments",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus addPayments(DunningDocumentDto postData);
    
    /**
     * Search for a dunningDocument with a given code.
     * 
     * @param dunningDocumentCode The dunningDocument's code
     * @return customer account
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a dunningDocument with a given code.  ",
			description=" Search for a dunningDocument with a given code.  ",
			operationId="    GET_DunningDocument_search",
			responses= {
				@ApiResponse(description=" customer account ",
						content=@Content(
									schema=@Schema(
											implementation= DunningDocumentResponseDto.class
											)
								)
				)}
	)
    DunningDocumentResponseDto find(@QueryParam("dunningDocumentCode") String dunningDocumentCode);

    /**
     * List dunningDocuments matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of dunningDocuments
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List dunningDocuments matching a given criteria  ",
			description=" List dunningDocuments matching a given criteria  ",
			operationId="    POST_DunningDocument_list",
			responses= {
				@ApiResponse(description=" List of dunningDocuments ",
						content=@Content(
									schema=@Schema(
											implementation= DunningDocumentsListResponseDto.class
											)
								)
				)}
	)
    public DunningDocumentsListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
