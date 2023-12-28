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

package org.meveo.api.rest.crm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.crm.GetAddressBookResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/addressbook")
@Tag(name = "AddressBook", description = "@%AddressBook")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Deprecated
public interface AddressBookRs extends IBaseRs {

    /**
     * Create All addressbook
     *
     * @return Request processing status
     */   
	@GET
    @Path("/createAll")
	@Operation(
			summary=" Create All addressbook ",
			description=" Create All addressbook ",
			operationId="GET_AddressBook_createAll",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createAll();

    /**
     * Create a new contact address for a contact code
     *
     * @param addrCode The address book code
     * @param ctCode The contact code 
     * @return Request processing status
     */
    @GET
    @Path("/addContact")
	@Operation(
			summary=" Create a new contact address for a contact code ",
			description=" Create a new contact address for a contact code ",
			operationId="    GET_AddressBook_addContact",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus addContact(@QueryParam("addressbookCode") String addrCode, @QueryParam("contactCode") String ctCode);
    
    /**
     * Find a AddressBook with a given code and from
     *
     * @param code The address book code
     * @param from The from information
     * @return GetAddressBookResponse data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a AddressBook with a given code and from ",
			description=" Find a AddressBook with a given code and from ",
			operationId="    GET_AddressBook_search",
			responses= {
				@ApiResponse(description=" GetAddressBookResponse data ",
						content=@Content(
									schema=@Schema(
											implementation= GetAddressBookResponseDto.class
											)
								)
				)}
	)
    GetAddressBookResponseDto find(@QueryParam("code") String code, @QueryParam("from") String from);
        
    /**
     * List of address
     *
     * @return Request processing status
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List of address ",
			description=" List of address ",
			operationId="    GET_AddressBook_list",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus list();
}
