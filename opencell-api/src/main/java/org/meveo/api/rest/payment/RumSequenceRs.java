package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.RumSequenceDto;
import org.meveo.api.dto.response.payment.RumSequenceValueResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * API for managing RUM sequence use for SEPA direct debit.
 * 
 * @author Edward P. Legaspi
 */
@Path("/payment/rumSequences")
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
	ActionStatus update(RumSequenceDto postData);

	/**
	 * Calculates and returns but not save the next value of the mandate number.
	 * 
	 * @return next mandate value
	 */
	@POST
	@Path("nextMandateNumber")
	RumSequenceValueResponseDto getNextMandateNumber();

}
