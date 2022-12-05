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

package org.meveo.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.LanguageIsoDto;
import org.meveo.api.dto.response.GetLanguageIsoResponse;
import org.meveo.api.dto.response.GetLanguagesIsoResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * * Web service for managing {@link org.meveo.model.billing.Language}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/languageIso")
@Tag(name = "LanguageIso", description = "@%LanguageIso")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface LanguageIsoRs extends IBaseRs {

    /**
     * Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.
     * 
     * @param languageIsoDto language iso.
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.  ",
			description=" Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.  ",
			operationId="    POST_LanguageIso_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(LanguageIsoDto languageIsoDto);

    /**
     * Search language given a code.
     * 
     * @param languageCode code of language
     * @return language iso for given code
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search language given a code.  ",
			description=" Search language given a code.  ",
			operationId="    GET_LanguageIso_search",
			responses= {
				@ApiResponse(description=" language iso for given code ",
						content=@Content(
									schema=@Schema(
											implementation= GetLanguageIsoResponse.class
											)
								)
				)}
	)
    GetLanguageIsoResponse find(@QueryParam("languageCode") String languageCode);

    /**
     * Does not delete a language but the tradingLanguage associated to it.
     * 
     * @param languageCode code of language.
     * @return action status
     */
    @DELETE
    @Path("/{languageCode}")
	@Operation(
			summary=" Does not delete a language but the tradingLanguage associated to it.  ",
			description=" Does not delete a language but the tradingLanguage associated to it.  ",
			operationId="    DELETE_LanguageIso_{languageCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("languageCode") String languageCode);

    /**
     * modify a language. Same input parameter as create. The language and trading Language are created if they don't exists. The operation fails if the tradingLanguage is null.
     * 
     * @param languageIsoDto language iso
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" modify a language",
			description=" modify a language. Same input parameter as create. The language and trading Language are created if they don't exists. The operation fails if the tradingLanguage is null.  ",
			operationId="    PUT_LanguageIso_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(LanguageIsoDto languageIsoDto);

    /**
     * Create or update a language if it doesn't exists.
     * 
     * @param languageIsoDto langauge iso
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a language if it doesn't exists.  ",
			description=" Create or update a language if it doesn't exists.  ",
			operationId="    POST_LanguageIso_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(LanguageIsoDto languageIsoDto);

    /**
     * List all languages.
     * 
     * @return all languages
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List all languages.  ",
			description=" List all languages.  ",
			operationId="    GET_LanguageIso_list",
			responses= {
				@ApiResponse(description=" all languages ",
						content=@Content(
									schema=@Schema(
											implementation= GetLanguagesIsoResponse.class
											)
								)
				)}
	)
    GetLanguagesIsoResponse list();

    /**
     * List languages ISO matching a given criteria
     *
     * @return List of languages ISO
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List languages ISO matching a given criteria ",
			description=" List languages ISO matching a given criteria ",
			operationId="    GET_LanguageIso_listGetAll",
			responses= {
				@ApiResponse(description=" List of languages ISO ",
						content=@Content(
									schema=@Schema(
											implementation= GetLanguagesIsoResponse.class
											)
								)
				)}
	)
    GetLanguagesIsoResponse listGetAll();
}
