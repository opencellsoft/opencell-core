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

/**
 * Web service for managing {@link org.meveo.model.admin.Currency} and {@link org.meveo.model.billing.TradingCurrency}.
 * 
 * @author Edward P. Legaspi
 *  @deprecated will be renammed to  TradingCurrencyRs
 **/
@Path("/currency")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CurrencyRs extends IBaseRs {

    /**
     * Creates tradingCurrency base on currency code. If the currency code does not exists, a currency record is created
     * 
     * @param postData currency to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(CurrencyDto postData);

    /**
     * Search currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return currency if exists
     */
    @GET
    @Path("/")
    GetCurrencyResponse find(@QueryParam("currencyCode") String currencyCode);

    /**
     * Remove currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return action status
     */
    @DELETE
    @Path("/{currencyCode}")
    ActionStatus remove(@PathParam("currencyCode") String currencyCode);

    /**
     * Modify a tradingCurrency. Same input parameter as create. The currency and tradingCurrency are created if they don't exists. The operation fails if the tradingCurrency is
     * null
     * 
     * @param postData currency to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(CurrencyDto postData);

    /**
     * @param postData currency to be created or updated
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CurrencyDto postData);

}
