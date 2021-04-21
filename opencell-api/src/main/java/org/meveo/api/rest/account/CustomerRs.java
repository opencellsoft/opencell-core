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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.response.account.GetCustomerCategoryResponseDto;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.dto.sequence.CustomerSequenceDto;
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
@Tag(name = "Customer", description = "@%Customer")
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
	@Operation(
			summary=" Create a new customer ",
			description=" Create a new customer ",
			operationId="    POST_Customer_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CustomerDto postData);

    /**
     * Update an existing customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing customer ",
			description=" Update an existing customer ",
			operationId="    PUT_Customer_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Search for a customer with a given code ",
			description=" Search for a customer with a given code ",
			operationId="    GET_Customer_search",
			responses= {
				@ApiResponse(description=" The customer's data ",
						content=@Content(
									schema=@Schema(
											implementation= GetCustomerResponseDto.class
											)
								)
				)}
	)
    GetCustomerResponseDto find(@QueryParam("customerCode") String customerCode, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove customer with a given code
     *
     * @param customerCode The customer's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customerCode}")
	@Operation(
			summary=" Remove customer with a given code ",
			description=" Remove customer with a given code ",
			operationId="    DELETE_Customer_{customerCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Filters are: category, seller, brand and provider. ",
			description=" Filters are: category, seller, brand and provider. ",
			operationId="    POST_Customer_list47",
			responses= {
				@ApiResponse(description=" list of customers ",
						content=@Content(
									schema=@Schema(
											implementation= CustomersResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List customers matching a given criteria ",
			description=" List customers matching a given criteria ",
			operationId="    GET_Customer_list",
			responses= {
				@ApiResponse(description=" List of customers ",
						content=@Content(
									schema=@Schema(
											implementation= CustomersResponseDto.class
											)
								)
				)}
	)
    public CustomersResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * List customers matching a given criteria
     *
     * @return List of customers
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List customers matching a given criteria ",
			description=" List customers matching a given criteria ",
			operationId="    GET_Customer_listGetAll",
			responses= {
				@ApiResponse(description=" List of customers ",
						content=@Content(
									schema=@Schema(
											implementation= CustomersResponseDto.class
											)
								)
				)}
	)
    CustomersResponseDto list();

    /**
     * List customers matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of customers
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List customers matching a given criteria ",
			description=" List customers matching a given criteria ",
			operationId="    POST_Customer_list",
			responses= {
				@ApiResponse(description=" List of customers ",
						content=@Content(
									schema=@Schema(
											implementation= CustomersResponseDto.class
											)
								)
				)}
	)
    public CustomersResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Create a new customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/createBrand")
	@Operation(
			summary=" Create a new customer brand ",
			description=" Create a new customer brand ",
			operationId="    POST_Customer_createBrand",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createBrand(CustomerBrandDto postData);

    /**
     * Update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @PUT
    @Path("/updateBrand")
	@Operation(
			summary=" Update an existing customer brand ",
			description=" Update an existing customer brand ",
			operationId="    PUT_Customer_updateBrand",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateBrand(CustomerBrandDto postData);

    /**
     * Create new or update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateBrand")
	@Operation(
			summary=" Create new or update an existing customer brand ",
			description=" Create new or update an existing customer brand ",
			operationId="    POST_Customer_createOrUpdateBrand",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdateBrand(CustomerBrandDto postData);

    /**
     * Create a new customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/createCategory")
	@Operation(
			summary=" Create a new customer category ",
			description=" Create a new customer category ",
			operationId="    POST_Customer_createCategory",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createCategory(CustomerCategoryDto postData);

    /**
     * Update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @PUT
    @Path("/updateCategory")
	@Operation(
			summary=" Update an existing customer category ",
			description=" Update an existing customer category ",
			operationId="    PUT_Customer_updateCategory",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateCategory(CustomerCategoryDto postData);
    
    /**
     * Search for a customer category with a given code
     * 
     * @param categoryCode The customer category's code
     * @return The customer category's data
     */
    @GET
    @Path("/category/{categoryCode}")
	@Operation(
			summary=" Search for a customer category with a given code  ",
			description=" Search for a customer category with a given code  ",
			operationId="    GET_Customer_category_{categoryCode}",
			responses= {
				@ApiResponse(description=" The customer category's data ",
						content=@Content(
									schema=@Schema(
											implementation= GetCustomerCategoryResponseDto.class
											)
								)
				)}
	)
    GetCustomerCategoryResponseDto findCategory(@PathParam("categoryCode") String categoryCode);

    /**
     * Create new or update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateCategory")
	@Operation(
			summary=" Create new or update an existing customer category ",
			description=" Create new or update an existing customer category ",
			operationId="    POST_Customer_createOrUpdateCategory",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdateCategory(CustomerCategoryDto postData);

    /**
     * Remove existing customer brand with a given brand code
     *
     * @param brandCode The brand's code
     * @return Request processing status
     */
    @DELETE
    @Path("/removeBrand/{brandCode}")
	@Operation(
			summary=" Remove existing customer brand with a given brand code ",
			description=" Remove existing customer brand with a given brand code ",
			operationId="    DELETE_Customer_removeBrand_{brandCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus removeBrand(@PathParam("brandCode") String brandCode);

    /**
     * Remove an existing customer category with a given category code
     *
     * @param categoryCode The category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/removeCategory/{categoryCode}")
	@Operation(
			summary=" Remove an existing customer category with a given category code ",
			description=" Remove an existing customer category with a given category code ",
			operationId="    DELETE_Customer_removeCategory_{categoryCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus removeCategory(@PathParam("categoryCode") String categoryCode);

    /**
     * Create new or update existing customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update existing customer ",
			description=" Create new or update existing customer ",
			operationId="    POST_Customer_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary="	  Exports an account hierarchy given a specific customer selected in the GUI",
			description="	  Exports an account hierarchy given a specific customer selected in the GUI.	  It includes Subscription, AccountOperation and Invoice details. It packaged the json output	  as a zipped file along with the pdf invoices.	  	  ",
			operationId="    GET_Customer_exportCustomerHierarchy",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Right to be forgotten",
			description=" Right to be forgotten. This concerns listing of risky or grey/black listed customers and their data.	  Upon request, they can require their data to be erased.	  In such case, mandatory information (accounting, invoicing, payments) must be preserved but the data tables including the customer's data must be anonymize (firstname/name/emails/phones/addresses/etc) so if this person register back it will be treated as a new customer without history. ",
			operationId="    GET_Customer_anonymizeGdpr",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary="	  Update the Provider's customer number sequence configuration.	  	  ",
			description="	  Update the Provider's customer number sequence configuration.	  	  ",
			operationId="PUT_Customer_customerNumberSequence",
			responses= {
				@ApiResponse(description=" status of the operation	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus updateCustomerNumberSequence(GenericSequenceDto postData);
	
    /**
	 * Calculates and returns the next value of the mandate number.
	 * 
	 * @return next customer no value
	 */
	@POST
	@Path("/customerNumberSequence")
	@Operation(
			summary="	  Calculates and returns the next value of the mandate number.	  	  ",
			description="	  Calculates and returns the next value of the mandate number.	  	  ",
			operationId="POST_Customer_customerNumberSequence",
			responses= {
				@ApiResponse(description=" next customer no value	  ",
						content=@Content(
									schema=@Schema(
											implementation= GenericSequenceValueResponseDto.class
											)
								)
				)}
	)
	GenericSequenceValueResponseDto getNextCustomerNumber();
	
	/**
	 * Creates a new customer sequence.
	 * @param postData customer sequence data
	 * @return request status
	 */
	@POST
	@Path("/sequence")
	@Operation(
			summary="	  Creates a new customer sequence.	  ",
			description="	  Creates a new customer sequence.	  ",
			operationId="POST_Customer_sequence",
			responses= {
				@ApiResponse(description=" request status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus createCustomerSequence(CustomerSequenceDto postData);
	
	/**
	 * Updates a new customer sequence with a given code.
	 * @param postData customer sequence data
	 * @return request status
	 */
	@PUT
	@Path("/sequence")
	@Operation(
			summary="	  Updates a new customer sequence with a given code.	  ",
			description="	  Updates a new customer sequence with a given code.	  ",
			operationId="PUT_Customer_sequence",
			responses= {
				@ApiResponse(description=" request status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus updateCustomerSequence(CustomerSequenceDto postData);
	
	/**
	 * Generates the next customer sequence number.
	 * @param code code of the sequence
	 * @return sequence value dto
	 */
	@POST
	@Path("/sequence/{code}/next")
	@Operation(
			summary="	  Generates the next customer sequence number.	  ",
			description="	  Generates the next customer sequence number.	  ",
			operationId="POST_Customer_sequence_{code}_next",
			responses= {
				@ApiResponse(description=" sequence value dto	  ",
						content=@Content(
									schema=@Schema(
											implementation= GenericSequenceValueResponseDto.class
											)
								)
				)}
	)
	GenericSequenceValueResponseDto getNextCustomerSequenceNumber(@PathParam("code") String code);

    /**
     * Filter counters by period date.
     *
     * @param customerCode The customer's code
     * @param date         The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
	@Operation(
			summary=" Filter counters by period date. ",
			description=" Filter counters by period date. ",
			operationId="    GET_Customer_filterCountersByPeriod",
			responses= {
				@ApiResponse(description=" counter instances. ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountersInstancesResponseDto.class
											)
								)
				)}
	)
    GetCountersInstancesResponseDto filterCustomerCountersByPeriod(@QueryParam("customerCode") String customerCode, @QueryParam("date") @RestDateParam Date date);


}
