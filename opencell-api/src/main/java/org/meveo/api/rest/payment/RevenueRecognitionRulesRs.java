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
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/revenueRecognitionRule")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RevenueRecognitionRulesRs extends IBaseRs {

    /**
     * Create a new revenue recognition rule
     * 
     * @param postData The revenue recognition rule's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(RevenueRecognitionRuleDto postData);

    /**
     * Update an existing revenue recognition rule
     * 
     * @param postData The revenue recognition rule's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(RevenueRecognitionRuleDto postData);

    /**
     * Find a revenue recognition rule with a given code
     * 
     * @param revenueRecognitionRuleCode The revenue recognition rule's code
     * @return Revenue recognition rules results
     */
    @GET
    @Path("/")
    RevenueRecognitionRuleDtoResponse find(@QueryParam("revenueRecognitionRuleCode") String revenueRecognitionRuleCode);

    /**
     * Create new or update an existing revenue recognition rule with a given code
     * 
     * @param postData The revenue recognition rule's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(RevenueRecognitionRuleDto postData);

    /**
     * Remove an existing revenue recognition rule with a given code
     * 
     * @param revenueRecognitionRuleCode The revenue recognition rule's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{revenueRecognitionRuleCode}")
    ActionStatus remove(@PathParam("revenueRecognitionRuleCode") String revenueRecognitionRuleCode);

    /**
     * List of revenue recognition rules.
     * 
     * @return A list of revenue recognition rules
     */
    @POST
    @Path("/list")
    RevenueRecognitionRuleDtosResponse list();

    /**
     * Enable a Revenue recognition rule with a given code
     * 
     * @param code Revenue recognition rule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Revenue recognition rule with a given code
     * 
     * @param code Revenue recognition rule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}