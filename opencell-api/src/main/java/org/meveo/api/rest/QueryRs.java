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

import org.meveo.api.dto.response.QueryResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

/**
 * Allows HQL queries to be called directly.
 *
 * @author Tony Alejandro
 * @version %I%, %G%
 * @since 5.1
 * @lastModifiedVersion 5.1
 */
@Path("/query")
@Tag(name = "Query", description = "@%Query")
@Consumes({ MediaType.APPLICATION_JSON})
@Produces({ MediaType.APPLICATION_JSON})
public interface QueryRs {

    /**
     * THIS IS A TEMPORARY API FOR DYNAMIC PORTAL USE ONLY.  IT MAY BE REMOVED AT ANY TIME.
     *
     * @param params Contains all query parameters passed. Will be parsed for the following parameters:<br>
     *        query - Search criteria. An HQL query that retrieves the list of entities. It only allows HQL queries<br>
     *        that start with "from" and does not contain the keyword "into", otherwise, will throw an error.<br>
     *        alias - alias name for the main entity that was used in the query.<br>
     *        e.g. if the query is "FROM Customer cust", then the alias should be "cust"<br>
     *        fields - comma delimited fields. allows nested field names.<br>
     *        offset - Pagination - from record number<br>
     *        limit - Pagination - number of records to retrieve<br>
     *        orderBy - Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.<br>
     *        sortOrder - Sorting - sort order.<br>
     *        groupBy - Grouping - group by clause, allow to use aggregation funciton like sum, avg, count.<br>
     *
     *        all other parameters will be used as query parameters to the HQL
     *
     * @return QueryResponse object that contains the status, pagination, and the result in json string form.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" THIS IS A TEMPORARY API FOR DYNAMIC PORTAL USE ONLY.  IT MAY BE REMOVED AT ANY TIME. ",
			description=" THIS IS A TEMPORARY API FOR DYNAMIC PORTAL USE ONLY.  IT MAY BE REMOVED AT ANY TIME. ",
			operationId="    GET_Query_search",
			responses= {
				@ApiResponse(description=" QueryResponse object that contains the status, pagination, and the result in json string form. ",
						content=@Content(
									schema=@Schema(
											implementation= QueryResponse.class
											)
								)
				)}
	)
    QueryResponse list(@Context UriInfo params);

}
