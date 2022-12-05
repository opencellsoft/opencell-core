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

package org.meveo.api.rest.importExport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.meveo.api.dto.response.utilities.ImportExportRequestDto;
import org.meveo.api.dto.response.utilities.ImportExportResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for importing and exporting data to another instance of application.
 * 
 * @author Andrius Karpavicius
 **/
@Path("/importExport")
@Tag(name = "ImportExport", description = "@%ImportExport")
@Consumes({ MULTIPART_FORM_DATA, APPLICATION_JSON })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface ImportExportRs extends IBaseRs {

    /**
     * Send a file to be imported. ImportExportResponseDto.executionId contains
     * 
     * @param input file containing a list of object for import
     * @return As import is async process, ImportExportResponseDto.executionId contains and ID to be used to query for execution results via a call to
     *         /importExport/checkImportDataResult?id=..
     */
    @POST
    @Path("/importData")
	@Operation(
			summary=" Send a file to be imported. ImportExportResponseDto.executionId contains  ",
			description=" Send a file to be imported. ImportExportResponseDto.executionId contains  ",
			operationId="    POST_ImportExport_importData",
			responses= {
				@ApiResponse(description=" As import is async process, ImportExportResponseDto.executionId contains and ID to be used to query for execution results via a call to/importExport/checkImportDataResult?id=.. ",
						content=@Content(
									schema=@Schema(
											implementation= ImportExportResponseDto.class
											)
								)
				)}
	)
    @Consumes(MULTIPART_FORM_DATA)
    ImportExportResponseDto importData(MultipartFormDataInput input);

    /**
     * Check for execution results for a given execution identifier
     * 
     * @param executionId Returned in /importExport/importData call
     * @return the execution result
     */
    @GET
    @Path("/checkImportDataResult")
	@Operation(
			summary=" Check for execution results for a given execution identifier  ",
			description=" Check for execution results for a given execution identifier  ",
			operationId="    GET_ImportExport_checkImportDataResult",
			responses= {
				@ApiResponse(description=" the execution result ",
						content=@Content(
									schema=@Schema(
											implementation= ImportExportResponseDto.class
											)
								)
				)}
	)
    ImportExportResponseDto checkImportDataResult(@QueryParam("executionId") String executionId);

    /**
     * export data
     * 
     * @param importExportRequestDto
     * @return
     */
    @POST
    @Path("/exportData")
	@Operation(
			summary=" export data  ",
			description=" export data  ",
			operationId="    POST_ImportExport_exportData",
			responses= {
				@ApiResponse(description="ImportExportResponseDto response",
						content=@Content(
									schema=@Schema(
											implementation= ImportExportResponseDto.class
											)
								)
				)}
	)
    ImportExportResponseDto exportData(ImportExportRequestDto importExportRequestDto);

    /**
     * returns an entity list CSV
     */
    @POST
    @Path("/generateEntityList")
	@Operation(
			summary=" returns an entity list CSV ",
			description=" returns an entity list CSV ",
			operationId="    POST_ImportExport_generateEntityList",
			responses= {
				@ApiResponse(description="type ImportExportResponseDto.class s an entity list CSV ",
						content=@Content(
									schema=@Schema(
											implementation= ImportExportResponseDto.class
											)
								)
				)}
	)
    ImportExportResponseDto entityList(ImportExportRequestDto importExportRequestDto);

    /**
     * export Data From Entity List
     * 
     * @param input
     * @return
     */
    @POST
    @Path("/exportDataFromEntityList")
	@Operation(
			summary=" export Data From Entity List  ",
			description=" export Data From Entity List  ",
			operationId="    POST_ImportExport_exportDataFromEntityList",
			responses= {
				@ApiResponse(description="ImportExportResponseDto response",
						content=@Content(
									schema=@Schema(
											implementation= ImportExportResponseDto.class
											)
								)
				)}
	)
    @Consumes(MULTIPART_FORM_DATA)
    ImportExportResponseDto exportDataFromEntityList(MultipartFormDataInput input);
}
