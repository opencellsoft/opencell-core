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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 * @since 22 Aug 2017
 */
@Path("/payment/creditCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CreditCategoryRs extends IBaseRs {

    /**
     * Create a new credit category 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@POST
	@Path("/")
	ActionStatus create(CreditCategoryDto postData);

    /**
     * Update a credit category payment 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@PUT
	@Path("/")
	ActionStatus update(CreditCategoryDto postData);

    /**
     * Create or update a credit category payment 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@POST
	@Path("/createOrUpdate")
	ActionStatus createOrUpdate(CreditCategoryDto postData);

    /**
     * Get a credit category payment with a credit category code
     *
     * @param creditCategoryCode The creditCategory code 
     * @return Credit Category Response data
     */
	@GET
	@Path("/")
	CreditCategoryResponseDto find(@QueryParam("creditCategoryCode") String creditCategoryCode);

    /**
     * Retrieve the list of credit category paiement 
     *
     * @param postData The contact data 
     * @return List of Credit Categories
     */
	@GET
	@Path("/list")
	CreditCategoriesResponseDto list();

	/**
	 * List creditCategories matching a given criteria
	 *
	 * @return List of creditCategories
	 */
	@GET
	@Path("/listGetAll")
	CreditCategoriesResponseDto listGetAll();

    /**
     * Delete a credit category with his given code 
     *
     * @param creditCategoryCode The creditCategory code 
     * @return Request processing status
     */
	@DELETE
	@Path("/{creditCategoryCode}")
	ActionStatus remove(@PathParam("creditCategoryCode") String creditCategoryCode);

}
