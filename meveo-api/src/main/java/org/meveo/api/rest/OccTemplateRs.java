package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.rest.security.RSSecured;

@Path("/occTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface OccTemplateRs extends IBaseRs {

	/**
	 * Create  OccTemplate.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	ActionStatus create(OccTemplateDto postData);

	/**
	 * Update OccTemplate.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	ActionStatus update(OccTemplateDto postData);

	/**
	 * Search OccTemplate with a given code.
	 * @param OccTemplateCode
	 * @return
	 */
	@Path("/")
	@GET
	GetOccTemplateResponseDto find(
			@QueryParam("occTemplateCode") String OcctemplateCode);

	/**
	 * Remove OccTemplate with a given code.
	 * 
	 * @param OccTemplateCode
	 * @return
	 */
	@Path("/{occTemplateCode}")
	@DELETE
	ActionStatus remove(
			@PathParam("occTemplateCode") String occTemplateCode);
	
	
	/**
	 * Create or update OccTemplate
	 * @param postData
	 * @return
	 */
	@Path("/createOrUpdate")
	@POST
	ActionStatus createOrUpdate(OccTemplateDto postData);

}
