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

package org.meveo.api.rest.catalog;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.OneShotChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Web service for managing {@link org.meveo.model.catalog.OneShotChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/oneShotChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OneShotChargeTemplateRs extends IBaseRs {

    /**
     * Create one shot charge template.
     * 
     * @param postData The one shot charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(OneShotChargeTemplateDto postData);

    /**
     * Update one shot charge template.
     * 
     * @param postData The one shot charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(OneShotChargeTemplateDto postData);

    /**
     * Search one shot charge templatewith a given code.
     * 
     * @param oneShotChargeTemplateCode The one shot charge template's code
     * @return one shot charge template
     */
    @GET
    @Path("/")
    GetOneShotChargeTemplateResponseDto find(@QueryParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * List one shot charge template with the following filters.
     * 
     * @param languageCode language's code
     * @param countryCode country's code
     * @param currencyCode currency's code
     * @param sellerCode seller's code
     * @param date application date
     * @return list of one shot charge template
     */
    @GET
    @Path("/list")
    OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(@QueryParam("languageCode") String languageCode, @QueryParam("countryCode") String countryCode,
            @QueryParam("currencyCode") String currencyCode, @QueryParam("sellerCode") String sellerCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Return the list of oneShotChargeTemplates.
     *
     * @return list of oneShotChargeTemplates
     */
    @GET
    @Path("/listAll")
    OneShotChargeTemplateResponseDto list();

    /**
     * Remove one shot charge tesmplate with a given code.
     * 
     * @param oneShotChargeTemplateCode The one shot charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{oneShotChargeTemplateCode}")
    ActionStatus remove(@PathParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * Create new or update an existing.
     * 
     * @param postData The exemple's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(OneShotChargeTemplateDto postData);

    /**
     * Enable a One shot charge template with a given code
     * 
     * @param code One shot charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a One shot charge template with a given code
     * 
     * @param code One shot charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}