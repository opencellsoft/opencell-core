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

package org.meveo.api.rest.account;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.custom.GenericCodeDto;
import org.meveo.api.dto.custom.GenericCodeResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.response.account.GetCustomerCategoryResponseDto;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 **/
@Path("/account/customer")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomerRs extends IBaseRs {

    /**
     * Create a new customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(CustomerDto postData);

    /**
     * Update an existing customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CustomerDto postData);

    /**
     * Search for a customer with a given code
     *
     * @param customerCode The customer's code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return The customer's data
     */
    @GET
    @Path("/")
    GetCustomerResponseDto find(@QueryParam("customerCode") String customerCode, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove customer with a given code
     *
     * @param customerCode The customer's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customerCode}")
    ActionStatus remove(@PathParam("customerCode") String customerCode);

    /**
     * Filters are: category, seller, brand and provider.
     *
     * @param postData The customer's data
     * @param firstRow Pagination - from record number. Deprecated in v.4.7, use "from" instead
     * @param numberOfRows Pagination - number of records to retrieve. Deprecated in v.4.7, use "limit" instead
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy sortBy field
     * @param sortOrder ASC/DESC
     * @return list of customers
     */
    @POST
    @Path("/list47")
    public CustomersResponseDto list47(@Deprecated CustomerDto postData, @QueryParam("firstRow") @Deprecated Integer firstRow,
            @QueryParam("numberOfRows") @Deprecated Integer numberOfRows, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("c.code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List customers matching a given criteria
     *
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE..
     * @return List of customers
     */
    @GET
    @Path("/list")
    public CustomersResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * List customers matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of customers
     */
    @POST
    @Path("/list")
    public CustomersResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Create a new customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/createBrand")
    ActionStatus createBrand(CustomerBrandDto postData);

    /**
     * Update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @PUT
    @Path("/updateBrand")
    ActionStatus updateBrand(CustomerBrandDto postData);

    /**
     * Create new or update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateBrand")
    ActionStatus createOrUpdateBrand(CustomerBrandDto postData);

    /**
     * Create a new customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/createCategory")
    ActionStatus createCategory(CustomerCategoryDto postData);

    /**
     * Update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @PUT
    @Path("/updateCategory")
    ActionStatus updateCategory(CustomerCategoryDto postData);
    
    /**
     * Search for a customer category with a given code
     * 
     * @param categoryCode The customer category's code
     * @return The customer category's data
     */
    @GET
    @Path("/category/{categoryCode}")
    GetCustomerCategoryResponseDto findCategory(@PathParam("categoryCode") String categoryCode);

    /**
     * Create new or update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateCategory")
    ActionStatus createOrUpdateCategory(CustomerCategoryDto postData);

    /**
     * Remove existing customer brand with a given brand code
     *
     * @param brandCode The brand's code
     * @return Request processing status
     */
    @DELETE
    @Path("/removeBrand/{brandCode}")
    ActionStatus removeBrand(@PathParam("brandCode") String brandCode);

    /**
     * Remove an existing customer category with a given category code
     *
     * @param categoryCode The category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/removeCategory/{categoryCode}")
    ActionStatus removeCategory(@PathParam("categoryCode") String categoryCode);

    /**
     * Create new or update existing customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomerDto postData);

    /**
	 * Exports an account hierarchy given a specific customer selected in the GUI.
	 * It includes Subscription, AccountOperation and Invoice details. It packaged the json output
	 * as a zipped file along with the pdf invoices.
	 * 
	 * @param customerCode The customer's code
     * @return Request processing status
	 */
    @GET
    @Path("/exportCustomerHierarchy")
    ActionStatus exportCustomerHierarchy(@QueryParam("customerCode") String customerCode);
    
    /**
     * Right to be forgotten. This concerns listing of risky or grey/black listed customers and their data.
	 * Upon request, they can require their data to be erased.
	 * In such case, mandatory information (accounting, invoicing, payments) must be preserved but the data tables including the customer's data must be anonymize (firstname/name/emails/phones/addresses/etc) so if this person register back it will be treated as a new customer without history.
     * @param customerCode The code of the customer
     * @return Request processing status
     */
    @GET
    @Path("/anonymizeGdpr")
    ActionStatus anonymizeGdpr(@QueryParam("customerCode") String customerCode);

    /**
	 * Update the Provider's customer number sequence configuration.
	 * 
	 * @param postData
	 *            DTO
	 * @return status of the operation
	 */
	@PUT
	@Path("/customerNumberSequence")
	ActionStatus updateCustomerNumberSequence(GenericSequenceDto postData);
	
    /**
	 * Calculates and returns the next value of the mandate number.
	 * 
	 * @return next customer no value
	 */
	@POST
	@Path("/customerNumberSequence")
	GenericSequenceValueResponseDto getNextCustomerNumber();
	
	/**
	 * Creates a new customer sequence.
	 * @param postData customer sequence data
	 * @return request status
	 */
	@POST
	@Path("/sequence")
	ActionStatus createCustomerSequence(GenericCodeDto postData);
	
	/**
	 * Updates a new customer sequence with a given code.
	 * @param postData customer sequence data
	 * @return request status
	 */
	@PUT
	@Path("/sequence")
	ActionStatus updateCustomerSequence(GenericCodeDto postData);
	
	/**
	 * Generates the next customer sequence number.
	 * @param code code of the sequence
	 * @return sequence value dto
	 */
	@POST
	@Path("/sequence/next")
    GenericCodeResponseDto getNextCustomerSequenceNumber(GenericCodeDto genericCodeDto);

    /**
     * Filter counters by period date.
     *
     * @param customerCode The customer's code
     * @param date         The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
    GetCountersInstancesResponseDto filterCustomerCountersByPeriod(@QueryParam("customerCode") String customerCode, @QueryParam("date") @RestDateParam Date date);


}
