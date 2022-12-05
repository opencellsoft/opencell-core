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

package org.meveo.api.rest.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * API for managing RUM sequence use for SEPA direct debit.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Path("/payment/rumSequences")
@Tag(name = "RumSequence", description = "@%RumSequence")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface RumSequenceRs extends IBaseRs {

	/**
	 * Update the Provider's RUM sequence configuration.
	 * 
	 * @param postData
	 *            DTO
	 * @return status of the operation
	 */
	@PUT
	@Path("/")
	@Operation(
			summary="	  Update the Provider's RUM sequence configuration.	  	  ",
			description="	  Update the Provider's RUM sequence configuration.	  	  ",
			operationId="PUT_RumSequence_update",
			responses= {
				@ApiResponse(description=" status of the operation	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus update(GenericSequenceDto postData);

	/**
	 * Calculates and returns the next value of the mandate number.
	 * 
	 * @return next mandate value
	 */
	@POST
	@Path("nextMandateNumber")
	@Operation(
			summary="	  Calculates and returns the next value of the mandate number.	  	  ",
			description="	  Calculates and returns the next value of the mandate number.	  	  ",
			operationId="POST_RumSequencenextMandateNumber",
			responses= {
				@ApiResponse(description=" next mandate value	  ",
						content=@Content(
									schema=@Schema(
											implementation= GenericSequenceValueResponseDto.class
											)
								)
				)}
	)
	GenericSequenceValueResponseDto getNextMandateNumber();

}
