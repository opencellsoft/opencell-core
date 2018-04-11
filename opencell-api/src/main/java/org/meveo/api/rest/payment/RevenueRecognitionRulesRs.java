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
     * @return
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