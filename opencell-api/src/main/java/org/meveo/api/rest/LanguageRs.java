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
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.response.GetTradingLanguageResponse;
import org.meveo.api.dto.response.TradingLanguagesResponseDto;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * * Web service for managing {@link org.meveo.model.billing.Language} and {@link org.meveo.model.billing.TradingLanguage}.
 * 
 * @author Edward P. Legaspi
 * 
 **/
@Path("/language")
@Tag(name = "Language", description = "@%Language")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface LanguageRs extends IBaseRs {

    /**
     * Search for list of trading languages.
     *
     * @return list of trading languages
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Search for list of trading languages. ",
			description=" Search for list of trading languages. ",
			operationId="    GET_Language_list",
			responses= {
				@ApiResponse(description=" list of trading languages ",
						content=@Content(
									schema=@Schema(
											implementation= TradingLanguagesResponseDto.class
											)
								)
				)}
	)
    TradingLanguagesResponseDto list();

    /**
     * Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.
     * 
     * @param postData language to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.  ",
			description=" Creates tradingLanguage base on language code. If the language code does not exists, a language record is created.  ",
			operationId="    POST_Language_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(LanguageDto postData);

    /**
     * Search language given a code.
     * 
     * @param languageCode language's code
     * @return language
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search language given a code.  ",
			description=" Search language given a code.  ",
			operationId="    GET_Language_search",
			responses= {
				@ApiResponse(description=" language ",
						content=@Content(
									schema=@Schema(
											implementation= GetTradingLanguageResponse.class
											)
								)
				)}
	)
    GetTradingLanguageResponse find(@QueryParam("languageCode") String languageCode);

    /**
     * Does not delete a language but the tradingLanguage associated to it.
     * 
     * @param languageCode language's code
     * @return action satus
     */
    @DELETE
    @Path("/{languageCode}")
	@Operation(
			summary=" Does not delete a language but the tradingLanguage associated to it.  ",
			description=" Does not delete a language but the tradingLanguage associated to it.  ",
			operationId="    DELETE_Language_{languageCode}",
			responses= {
				@ApiResponse(description=" action satus ",
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
     * @param postData language to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" modify a language",
			description=" modify a language. Same input parameter as create. The language and trading Language are created if they don't exists. The operation fails if the tradingLanguage is null.  ",
			operationId="    PUT_Language_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(LanguageDto postData);

    /**
     * Create or update a language if it doesn't exists.
     * 
     * @param postData language to be created or updated
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a language if it doesn't exists.  ",
			description=" Create or update a language if it doesn't exists.  ",
			operationId="    POST_Language_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(LanguageDto postData);

    /**
     * Enable a Trading language with a given language code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Trading language with a given language code  ",
			description=" Enable a Trading language with a given language code  ",
			operationId="    POST_Language_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Trading language with a given language code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Trading language with a given language code  ",
			description=" Disable a Trading language with a given language code  ",
			operationId="    POST_Language_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);
}
