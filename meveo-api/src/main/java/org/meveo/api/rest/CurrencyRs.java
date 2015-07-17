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
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.response.GetCurrencyResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.Currency} and
 * {@link org.meveo.model.billing.TradingCurrency}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/currency")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CurrencyRs extends IBaseRs {

	/**
	 * Create {@link org.meveo.model.billing.Currency} and
	 * {@link org.meveo.model.billing.TradingCurrency}.
	 * 
	 * @param postData
	 * @return
	 */
	@POST
	@Path("/")
	public ActionStatus create(CurrencyDto postData);

	/**
	 * Search currency with a given currency code.
	 * 
	 * @param currencyCode
	 * @return
	 */
	@GET
	@Path("/")
	public GetCurrencyResponse find(
			@QueryParam("currencyCode") String currencyCode);

	/**
	 * Remove currency with a given currency code.
	 * 
	 * @param currencyCode
	 * @return
	 */
	@DELETE
	@Path("/{currencyCode}")
	public ActionStatus remove(@PathParam("currencyCode") String currencyCode);

	/**
	 * Update {@link org.meveo.model.billing.Currency} and
	 * {@link org.meveo.model.billing.TradingCurrency}.
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	public ActionStatus update(CurrencyDto postData);

}
