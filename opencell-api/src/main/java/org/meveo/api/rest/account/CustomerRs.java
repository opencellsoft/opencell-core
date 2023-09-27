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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

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
    @Operation(summary = "Create a new customer",
    tags = { "Customer management" })
    ActionStatus create(CustomerDto postData);

    /**
     * Update an existing customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "Update an existing customer",
    tags = { "Customer management" })
    ActionStatus update(CustomerDto postData);

    /**
     * Search for a customer with a given code
     *
     * @param customerCode The customer's code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param includeCustomerAccounts True to include customer accounts
     * @return The customer's data
     */
    @GET
    @Path("/")
    @Operation(summary = "Search for a customer with a given code", deprecated = true,
    tags = { "Deprecated" })
    GetCustomerResponseDto find(@Parameter(description = "The customer code") @QueryParam("customerCode") String customerCode, 
    							@Parameter(description = "The type of the customer ") @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
    							@Parameter(description = "Indicate if account of the customer will included") @QueryParam("includeCustomerAccounts") boolean includeCustomerAccounts);

    /**
     * Search for a customer with a given code
     *
     * @param customerCode The customer's code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param includeCustomerAccounts True to include customer accounts
     * @return The customer's data
     */
    @GET
    @Path("/{customerCode}")
    @Operation(summary = "Search for a customer with a given code",
            tags = { "Customer management" })
    GetCustomerResponseDto findV2(@Parameter(description = "The customer code") @PathParam("customerCode") String customerCode, 
    							  @Parameter(description = "The type of the customer ") @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
    							  @Parameter(description = "Indicate if account of the customer will included") @QueryParam("includeCustomerAccounts") boolean includeCustomerAccounts);

    /**
     * Remove customer with a given code
     *
     * @param customerCode The customer's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customerCode}")
    @Operation(summary = "Remove customer with a given code",
    tags = { "Customer management" })
    ActionStatus remove(@Parameter(description = "The customer code", required = true) @PathParam("customerCode") String customerCode);

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
    CustomersResponseDto list47(@Parameter(description = "The customer's data") @Deprecated CustomerDto postData, 
    							@Parameter(description = "from record number. Deprecated in v.4.7, use 'from' instead") @QueryParam("firstRow") @Deprecated Integer firstRow,
    							@Parameter(description = "Pagination - number of records to retrieve. Deprecated in v.4.7, use 'limit' instead")  @QueryParam("numberOfRows") @Deprecated Integer numberOfRows, 
    							@Parameter(description = "Pagination - from record number") @QueryParam("offset") Integer offset, 
    							@Parameter(description = "Pagination - number of records to retrieve") @QueryParam("limit") Integer limit,
    							@Parameter(description = "sort by field") @DefaultValue("c.code") @QueryParam("sortBy") String sortBy, 
    							@Parameter(description = "sort Order") @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

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
    @Operation(summary = "List customers matching a given criteria", deprecated = true,
    tags = { "Deprecated" })
    CustomersResponseDto listGet(@Parameter(description = "query Search criteria" ) @QueryParam("query") String query, 
    							@Parameter(description = "fields Data retrieval options/fieldnames separated by a comma" ) @QueryParam("fields") String fields, 
    							@Parameter(description = "offset Pagination - from record number" ) @QueryParam("offset") Integer offset,
    							@Parameter(description = "limit Pagination - number of records to retrieve" ) @QueryParam("limit") Integer limit, 
    							@Parameter(description = "Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields" ) @DefaultValue("code") @QueryParam("sortBy") String sortBy, 
    							@Parameter(description = "sort order" ) @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
    							@Parameter(description = "Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE" ) @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * List customers matching a given criteria
     *
     * @return List of customers
     */
    @GET
    @Path("/listGetAll")
    @Operation(summary = "List all customers.",
            tags = { "Customer management" })
    CustomersResponseDto list();

    /**
     * List customers matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of customers
     */
    @POST
    @Path("/list")
    @Operation(summary = "List customers matching a given criteria", deprecated = true,
    tags = { "Deprecated" })
    CustomersResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * List customers matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of customers
     */
    @POST
    @Path("/filtering")
    @Operation(summary = "List customers matching a given criteria",
            tags = { "Customer management" })
    CustomersResponseDto listPostV2(PagingAndFiltering pagingAndFiltering);

    /**
     * Create a new customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/createBrand")
    @Operation(summary = "Create a new customer brand", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus createBrand(CustomerBrandDto postData);

    /**
     * Create a new customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/brands")
    @Operation(summary = "Create a new customer brand",
            tags = { "Customer management" })
    ActionStatus createBrandV2(CustomerBrandDto postData);

    /**
     * Update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @PUT
    @Path("/updateBrand")
    @Operation(summary = "Update an existing customer brand", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus updateBrand(CustomerBrandDto postData);

    /**
     * Update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @PUT
    @Path("/brands")
    @Operation(summary = "Update an existing customer brand",
            tags = { "Customer management" })
    ActionStatus updateBrandV2(CustomerBrandDto postData);

    /**
     * Create new or update an existing customer brand
     *
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateBrand")
    @Operation(summary = " Create new or update an existing customer brand", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus createOrUpdateBrand(CustomerBrandDto postData);

    /**
     * Create a new customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/createCategory")
    @Operation(summary = "Create a new customer category", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus createCategory(CustomerCategoryDto postData);

    /**
     * Create a new customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/categories")
    @Operation(summary = "Create a new customer category",
            tags = { "Customer management" })
    ActionStatus createCategoryV2(CustomerCategoryDto postData);

    /**
     * Update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @PUT
    @Path("/updateCategory")
    @Operation(summary = "Update an existing customer category", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus updateCategory(CustomerCategoryDto postData);

    /**
     * Update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @PUT
    @Path("/categories")
    @Operation(summary = "Update an existing customer category",
            tags = { "Customer management" })
    ActionStatus updateCategoryV2(CustomerCategoryDto postData);
    
    /**
     * Search for a customer category with a given code
     * 
     * @param categoryCode The customer category's code
     * @return The customer category's data
     */
    @GET
    @Path("/category/{categoryCode}")
    @Operation(summary = "Search for a customer category with a given code", deprecated = true,
    tags = { "Deprecated" })
    GetCustomerCategoryResponseDto findCategory(@Parameter(description = "The category code") @PathParam("categoryCode") String categoryCode);

    /**
     * Search for a customer category with a given code
     *
     * @param categoryCode The customer category's code
     * @return The customer category's data
     */
    @GET
    @Path("/categories/{categoryCode}")
    @Operation(summary = "Search for a customer category with a given code",
            tags = { "Customer management" })
    GetCustomerCategoryResponseDto findCategoryV2(@Parameter(description = "The category code", required = true) @PathParam("categoryCode") String categoryCode);


    /**
     * Create new or update an existing customer category
     *
     * @param postData The customer category's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateCategory")
    @Operation(summary = "Create new or update an existing customer category", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus createOrUpdateCategory(CustomerCategoryDto postData);

    /**
     * Remove existing customer brand with a given brand code
     *
     * @param brandCode The brand's code
     * @return Request processing status
     */
    @DELETE
    @Path("/removeBrand/{brandCode}")
    @Operation(summary = "Remove existing customer brand with a given brand code", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus removeBrand(@Parameter(description = "The customer brand code", required = true) @PathParam("brandCode") String brandCode);

    /**
     * Remove an existing customer category with a given category code
     *
     * @param categoryCode The category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/removeCategory/{categoryCode}")
    @Operation(summary = "Remove an existing customer category with a given category code", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus removeCategory(@Parameter(description = "The category code", required = true) @PathParam("categoryCode") String categoryCode);

    /**
     * Remove existing customer brand with a given brand code
     *
     * @param brandCode The brand's code
     * @return Request processing status
     */
    @DELETE
    @Path("/brands/{brandCode}")
    @Operation(summary = "Remove existing customer brand with a given brand code",
            tags = { "Customer management" })
    ActionStatus removeBrandV2(@Parameter(description = "The customer brand code", required = true) @PathParam("brandCode") String brandCode);

    /**
     * Remove an existing customer category with a given category code
     *
     * @param categoryCode The category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/categories/{categoryCode}")
    @Operation(summary = "Remove an existing customer category with a given category code",
            tags = { "Customer management" })
    ActionStatus removeCategoryV2(@Parameter(description = "The category code", required = true) @PathParam("categoryCode") String categoryCode);

    /**
     * Create new or update existing customer
     *
     * @param postData The customer's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    @Operation(summary = " Create new or update existing customer",deprecated = true,
    tags = { "Deprecated" })
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
    @Operation(summary = " Exports an account hierarchy given a specific customer", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus exportCustomerHierarchy(@Parameter(description = "The customer code") @QueryParam("customerCode") String customerCode);

    /**
     * Exports an account hierarchy given a specific customer selected in the GUI.
     * It includes Subscription, AccountOperation and Invoice details. It packaged the json output
     * as a zipped file along with the pdf invoices.
     *
     * @param customerCode The customer's code
     * @return Request processing status
     */
    @GET
    @Path("/{customerCode}/exportation")
    @Operation(summary = " Exports an account hierarchy given a specific customer",
            tags = { "Customer management" })
    ActionStatus exportCustomerHierarchyV2(@Parameter(description = "The customer code") @PathParam("customerCode") String customerCode);
    
    /**
     * Right to be forgotten. This concerns listing of risky or grey/black listed customers and their data.
	 * Upon request, they can require their data to be erased.
	 * In such case, mandatory information (accounting, invoicing, payments) must be preserved but the data tables including the customer's data must be anonymize (firstname/name/emails/phones/addresses/etc) so if this person register back it will be treated as a new customer without history.
     * @param customerCode The code of the customer
     * @return Request processing status
     */
    @GET
    @Path("/anonymizeGdpr")
    @Operation(summary = "Anonymization of a specific customer", deprecated = true,
            tags = { "Deprecated" })
    ActionStatus anonymizeGdpr(@Parameter(description = "The customer code") @QueryParam("customerCode") String customerCode);
    
    /**
     * Right to be forgotten. This concerns listing of risky or grey/black listed customers and their data.
	 * Upon request, they can require their data to be erased.
	 * In such case, mandatory information (accounting, invoicing, payments) must be preserved but the data tables including the customer's data must be anonymize (firstname/name/emails/phones/addresses/etc) so if this person register back it will be treated as a new customer without history.
     * @param customerCode The code of the customer
     * @return Request processing status
     */
    @PUT
    @Path("/anonymizeGdpr")
    @Operation(summary = "Anonymization of a specific customer", tags = { "Customer management" })
    ActionStatus updateAnonymizeGdpr(@Parameter(description = "The customer code") @QueryParam("customerCode") String customerCode);

    /**
     * Right to be forgotten. This concerns listing of risky or grey/black listed customers and their data.
     * Upon request, they can require their data to be erased.
     * In such case, mandatory information (accounting, invoicing, payments) must be preserved but the data tables including the customer's data must be anonymize (firstname/name/emails/phones/addresses/etc) so if this person register back it will be treated as a new customer without history.
     * @param customerCode The code of the customer
     * @return Request processing status
     */
    @GET
    @Path("/{customerCode}/gdprAnonymization")
    ActionStatus anonymizeGdprV2(@Parameter(description = "The customer code") @PathParam("customerCode") String customerCode);

    /**
	 * Update the Provider's customer number sequence configuration.
	 * 
	 * @param postData
	 *            DTO
	 * @return status of the operation
	 */
	@PUT
	@Path("/customerNumberSequence")
	 @Operation(summary = " Update the Provider's customer number sequence configuration",
	    tags = { "Customer management" })
	ActionStatus updateCustomerNumberSequence(GenericSequenceDto postData);
	
    /**
	 * Calculates and returns the next value of the mandate number.
	 * 
	 * @return next customer no value
	 */
	@POST
	@Path("/customerNumberSequence")
	 @Operation(summary = " Calculates and returns the next value of the mandate number",
	    tags = { "Customer management" })
	GenericSequenceValueResponseDto getNextCustomerNumber();
	
	/**
	 * Creates a new customer sequence.
	 * @param postData customer sequence data
	 * @return request status
	 */
	@POST
	@Path("/sequence")
	 @Operation(summary = "Creates a new customer sequence",
	    tags = { "Customer management" })
	ActionStatus createCustomerSequence(CustomerSequenceDto postData);
	
	/**
	 * Updates a new customer sequence with a given code.
	 * @param postData customer sequence data
	 * @return request status
	 */
	@PUT
	@Path("/sequence")
	 @Operation(summary = "Updates a new customer sequence with a given code.",
	    tags = { "Customer management" })
	ActionStatus updateCustomerSequence(CustomerSequenceDto postData);
	
	/**
	 * Generates the next customer sequence number.
	 * @param code code of the sequence
	 * @return sequence value dto
	 */
	@POST
	@Path("/sequence/{code}/next")
	 @Operation(summary = "Generates the next customer sequence number",
	    tags = { "Customer management" })
	GenericSequenceValueResponseDto getNextCustomerSequenceNumber(@Parameter(description = "The code of the sequence", required = true) @PathParam("code") String code);

    /**
     * Filter counters by period date.
     *
     * @param customerCode The customer's code
     * @param date         The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
    @Operation(summary = "Filter counters by period date",
    tags = { "Deprecated" })
    GetCountersInstancesResponseDto filterCustomerCountersByPeriod(@Parameter(description = "The customer code") @QueryParam("customerCode") String customerCode, 
    															   @Parameter(description = "The date to compare ", required = true) @QueryParam("date") @RestDateParam Date date);

    /**
     * Filter counters by period date.
     *
     * @param customerCode The customer's code
     * @param date         The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("{customerCode}/filterCountersByPeriod")
    @Operation(summary = "Filter counters by period date",
            tags = { "Customer management" })
    GetCountersInstancesResponseDto filterCustomerCountersByPeriodV2(@Parameter(description = "The customer code") @PathParam("customerCode") String customerCode,
			   														@Parameter(description = "The date to compare ", required = true) @QueryParam("date") @RestDateParam Date date);
    
    /**
     * Get Customer root parent.
     *
     * @param customerCode The customer's code
     * @return The Root parent data
     */
    @GET
    @Path("/{customerCode}/rootParent")
    @Operation(summary = " Get Customer root parent",
            tags = { "Customer management" })
    GetCustomerResponseDto findRootParent(@Parameter(description = "The customer code") @PathParam("customerCode") String customerCode);

}
