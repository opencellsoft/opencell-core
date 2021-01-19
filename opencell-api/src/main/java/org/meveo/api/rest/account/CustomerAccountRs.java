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
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
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
 * Web service for managing customer account.
 * 
 * @author R.AITYAAZZA
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Path("/account/customerAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomerAccountRs extends IBaseRs {

    /**
     * Create a new customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(CustomerAccountDto postData);

    /**
     * Update an existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CustomerAccountDto postData);

    /**
     * Search for a customer account with a given code.
     *
     * @param customerAccountCode The customer account's code
     * @param calculateBalances True if needs to calculate balances
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param withAccountOperations True if needs to get account operations
     * @param includeBillingAccounts True to include billing accounts
     * @return customer account
     */
    @GET
    @Path("/")
    GetCustomerAccountResponseDto find(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("calculateBalances") boolean calculateBalances,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF, @QueryParam("withAccountOperations") boolean withAccountOperations,
            @QueryParam("includeBillingAccounts") boolean includeBillingAccounts);

    /**
     * Remove customerAccount with a given code.
     * 
     * @param customerAccountCode The customer account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customerAccountCode}")
    ActionStatus remove(@PathParam("customerAccountCode") String customerAccountCode);

    /**
     * List CustomerAccount filtered by customerCode.
     * 
     * @param customerCode The customer account's code
     * @return list of customer account by customer.
     */
    @GET
    @Path("/list")
    CustomerAccountsResponseDto listByCustomer(@QueryParam("customerCode") String customerCode);

    /**
     * Create a new credit category.
     * 
     * @param postData The credit category's data
     * @return Request processing status
     */
    @POST
    @Path("/creditCategory")
    ActionStatus createCreditCategory(CreditCategoryDto postData);

    /**
     * Remove credit category with a given code.
     * 
     * @param creditCategoryCode The credit category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/creditCategory/{creditCategoryCode}")
    ActionStatus removeCreditCategory(@PathParam("creditCategoryCode") String creditCategoryCode);

    /**
     * Create new or update existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomerAccountDto postData);

    /**
     * Transfer an amount from one customer to another.
     *
     * @param transferCustomerAccountDto the transfer Customer Account Dto
     * @return Request processing status
     */
    @POST
    @Path("/transferAccount")
    ActionStatus transferAccount(TransferCustomerAccountDto transferCustomerAccountDto);

    /**
     * Filter counters by period date.
     *
     * @param customerAccountCode The customer account's code
     * @param date The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
    GetCountersInstancesResponseDto filterCustomerAccountCountersByPeriod(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("date") @RestDateParam Date date);

}
