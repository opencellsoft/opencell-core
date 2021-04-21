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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.dto.response.GetCurrenciesIsoResponse;
import org.meveo.api.dto.response.GetCurrencyIsoResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing Currency.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/currencyIso")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CurrencyIsoRs extends IBaseRs {

    /**
     * Creates tradingCurrency base on currency code. If the currency code does not exists, a currency record is created
     * 
     * @param currencyIsoDto currency iso
     * @return action status.
     */
    @POST
    @Path("/") 
    ActionStatus create(CurrencyIsoDto currencyIsoDto);

    /**
     * Search currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return currency iso if found.
     */
    @GET
    @Path("/") 
    GetCurrencyIsoResponse find(@QueryParam("currencyCode") String currencyCode);

    /**
     * Remove currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return action status.
     */
    @DELETE
    @Path("/{currencyCode}") 
    ActionStatus remove(@PathParam("currencyCode") String currencyCode);

    /**
     * Modify a tradingCurrency. Same input parameter as create. The currency and tradingCurrency are created if they don't exists. The operation fails if the tradingCurrency is
     * null
     * 
     * @param currencyIsoDto currency iso
     * @return action status.
     */
    @PUT
    @Path("/")
    ActionStatus update(CurrencyIsoDto currencyIsoDto);

    /**
     * Creates or modify a tradingCurrency base on currency code. 
     * @param currencyIsoDto currency iso to create or update
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate") 
    ActionStatus createOrUpdate(CurrencyIsoDto currencyIsoDto);

    /**
     * List all currencies.
     * @return list of all currency iso/
     */
    @GET
    @Path("/list")
    GetCurrenciesIsoResponse list();

    /**
     * List currencies ISO matching a given criteria
     *
     * @return List of currencies ISO
     */
    @GET
    @Path("/listGetAll")
    GetCurrenciesIsoResponse listGetAll();
    
}
