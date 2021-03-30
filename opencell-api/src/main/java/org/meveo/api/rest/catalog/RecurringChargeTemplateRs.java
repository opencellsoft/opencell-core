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
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.RecurringChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.catalog.RecurringChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/recurringChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RecurringChargeTemplateRs extends IBaseRs {

    /**
     * Create a new recurring charge template.
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    public ActionStatus create(RecurringChargeTemplateDto postData);

    /**
     * Find a recurring charge template with a given code.
     * 
     * @param recurringChargeTemplateCode The recurring charge template's code
     * @return Return a recurringChargeTemplate
     */
    @GET
    @Path("/")
    public GetRecurringChargeTemplateResponseDto find(@QueryParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

    /**
     * Return the list of recurringChargeTemplates.
     *
     * @return list of recurringChargeTemplates
     */
    @GET
    @Path("/listGetAll")
    RecurringChargeTemplateResponseDto list();

    /**
     * Update an existing recurring charge template.
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    public ActionStatus update(RecurringChargeTemplateDto postData);

    /**
     * Remove an existing recurring charge template with a given code.
     * 
     * @param recurringChargeTemplateCode The recurring charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{recurringChargeTemplateCode}")
    public ActionStatus remove(@PathParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

    /**
     * Create new or update an existing recurring charge template
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(RecurringChargeTemplateDto postData);

    /**
     * Enable a Recurring charge template with a given code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Recurring charge template with a given code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}