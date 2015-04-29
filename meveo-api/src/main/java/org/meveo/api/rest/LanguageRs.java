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
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.response.GetLanguageResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * * Web service for managing {@link org.meveo.model.billing.Language} and
 * {@link org.meveo.model.billing.TradingLanguage}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/language")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface LanguageRs extends IBaseRs {

	/**
	 * Create language.
	 * 
	 * @param postData
	 * @return
	 */
	@POST
	@Path("/")
	public ActionStatus create(LanguageDto postData);

	/**
	 * Search language given a code.
	 * 
	 * @param languageCode
	 * @return
	 */
	@GET
	@Path("/")
	public GetLanguageResponse find(
			@QueryParam("languageCode") String languageCode);

	/**
	 * Remove language with a given code.
	 * 
	 * @param languageCode
	 * @return
	 */
	@DELETE
	@Path("/{languageCode}")
	public ActionStatus remove(@PathParam("languageCode") String languageCode);

	/**
	 * Update language.
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	public ActionStatus update(LanguageDto postData);

}
