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

package org.meveo.api.rest.document;

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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
import org.meveo.api.dto.document.sign.SignProcedureResponseDto;
import org.meveo.api.dto.response.RawResponseDto;
import org.meveo.api.rest.IBaseRs;

/** 
 * Rest services to handle Document signature. 
 * 
 * @author Said Ramli 
 */ 
@Path("/document/sign") 
@Tag(name = "DocumentSign", description = "@%DocumentSign")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
public interface DocumentSignRs extends IBaseRs { 
    
    /**
     * Creates the procedure.
     *
     * @param postData the post data
     * @return the sign procedure response dto
     */
    @POST 
    @Path("/procedures")
	@Operation(
			summary=" Creates the procedure. ",
			description=" Creates the procedure. ",
			operationId="    POST _DocumentSign_procedures ",
			responses= {
				@ApiResponse(description=" the sign procedure response dto ",
						content=@Content(
									schema=@Schema(
											implementation= SignProcedureResponseDto.class
											)
								)
				)}
	)
    public SignProcedureResponseDto createProcedure(CreateProcedureRequestDto postData); 
    
    /**
     * Gets the procedure by id.
     *
     * @param id the id
     * @return the procedure by id
     */    
    @GET 
    @Path("/procedures/{id}")
	@Operation(
			summary=" Gets the procedure by id. ",
			description=" Gets the procedure by id. ",
			operationId="    GET _DocumentSign_procedures_{id} ",
			responses= {
				@ApiResponse(description=" the procedure by id ",
						content=@Content(
									schema=@Schema(
											implementation= SignProcedureResponseDto.class
											)
								)
				)}
	)
    public SignProcedureResponseDto getProcedureById(@PathParam("id") String id); 
    
    /**
     * Gets the procedure status by id.
     *
     * @param id the id
     * @return the procedure status by id
     */    
    @GET 
    @Path("/procedures/{id}/status")
	@Operation(
			summary=" Gets the procedure status by id. ",
			description=" Gets the procedure status by id. ",
			operationId="    GET _DocumentSign_procedures_{id}_status ",
			responses= {
				@ApiResponse(description=" the procedure status by id ",
						content=@Content(
									schema=@Schema(
											implementation= RawResponseDto.class
											)
								)
				)}
	)
    public RawResponseDto<String> getProcedureStatusById(@PathParam("id") String id); 
    
    /**
     * Download the files with the given id
     *
     * @param id The id
     * @return the file by id
     */    
    @GET 
    @Path("/files/{id}/download")
	@Operation(
			summary=" Download the files with the given id ",
			description=" Download the files with the given id ",
			operationId="    GET _DocumentSign_files_{id}_download ",
			responses= {
				@ApiResponse(description=" the file by id ",
						content=@Content(
									schema=@Schema(
											implementation= SignFileResponseDto.class
											)
								)
				)}
	)
    public SignFileResponseDto downloadFileById(@PathParam("id") String id); 

}
