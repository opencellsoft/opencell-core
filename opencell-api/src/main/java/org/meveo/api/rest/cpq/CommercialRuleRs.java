package org.meveo.api.rest.cpq;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialRuleDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialRulesResponseDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * @author Rachid.AITYAAZZA
 * @version 11.0
 *
 */
@Path("/commercialRules")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CommercialRuleRs extends IBaseRs {

	@POST
	@Path("/")
	@Operation(summary = "This endpoint allows to create new commercialRule", tags = {
			"CommercialRules" }, description = "Creating a new commercialRule", responses = {
					@ApiResponse(responseCode = "200", description = "the CommercialRule successfully added", content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "missing required paramter for CommercialRuleDto.The required params are : code", content = @Content(schema = @Schema(implementation = MissingParameterException.class)))

	})

	Response create(
			@Parameter(name = "commercialRuleDto", description = "CommercialRule dto for a new insertion") CommercialRuleHeaderDTO commercialRuleDTO);

	@PUT
	@Path("/")
	@Operation(summary = "This endpoint allows to update an existing CommercialRule", description = "Updating an existing CommercialRule", tags = {
			"CommercialRules" }, responses = {
					@ApiResponse(responseCode = "200", description = "the CommercialRule successfully updated", content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "missing required paramter for CommercialRuleDTO.The required params are : code", content = @Content(schema = @Schema(implementation = MissingParameterException.class))) })
	Response update(
			@Parameter(description = "commercialRule dto for updating an existing commercialRule", required = true) CommercialRuleHeaderDTO commercialRuleDTO);

	@DELETE
	@Path("/{code}")
	@Operation(summary = "This endpoint allows to  delete an existing CommercialRule", description = "Deleting an existing CommercialRule with its code", tags = {
			"CommercialRules" }, responses = {
					@ApiResponse(responseCode = "200", description = "The CommercialRule successfully deleted", content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
					@ApiResponse(responseCode = "400", description = "No CommercialRule found for the code parameter", content = @Content(schema = @Schema(implementation = BusinessException.class))) })
	Response delete(
			@Parameter(description = "contain the code of CommercialRule te be deleted by its code", required = true) @PathParam("code") String code);

	@GET
	@Path("/{code}")
	@Operation(summary = "This endpoint allows to find a commercialRule by its code", description = "Finding a commercialRule by its code ", tags = {
			"CommercialRules" }, responses = {
					@ApiResponse(responseCode = "200", description = "The CommercialRule successfully retrieved", content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))), })
	Response findByCode(
			@Parameter(description = "retrieving a commercialRule with its code") @PathParam("code") String code);

	@GET
	@Path("/")
	@Operation(summary = "Get commercial rules matching the given criteria", tags = {
			"CommercialRules" }, description = "Get commercial rules matching the given criteria", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class))) })
	public Response list(PagingAndFiltering pagingAndFiltering);

	@GET
	@Path("/productRules")
	@Operation(summary = "Get product commercial rules", tags = {
			"CommercialRules" }, description = "Get product commercial rules", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class))) })
	public Response findProductRules(
			@Parameter(description = "offer code", required = false) @QueryParam("offerCode") String offerCode,
			@Parameter(description = "product code", required = true) @QueryParam("productCode") String productCode,
			@Parameter(description = "product version", required = false) @QueryParam("productVersion") Integer productVersion);

	@GET
	@Path("/attributeRules")
	@Operation(summary = "Get attribute commercial rules", tags = {
			"CommercialRules" }, description = "Get attribute commercial rules", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class))) })
	public Response findAttributeRules(
			@Parameter(description = "attribute code", required = false) @QueryParam("attributeCode") String attributeCode,
			@Parameter(description = "product code", required = true) @QueryParam("productCode") String productCode);

	@GET
	@Path("/tagRules/{tagCode}")
	@Operation(summary = "Get tag commercial rules", tags = {
			"CommercialRules" }, description = "Get tag commercial rules", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class))) })
	public Response findTagRules(
			@Parameter(description = "tag code", required = true) @PathParam("tagCode") String tagCode);

	@GET
	@Path("/offerRules/{offerCode}")
	@Operation(summary = "Get offer commercial rules", tags = {
			"CommercialRules" }, description = "Get offer commercial rules", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class))) })
	public Response findOfferRules(
			@Parameter(description = "offer code", required = true) @PathParam("offerCode") String offerCode);

	@GET
	@Path("/groupedAttributeRules")
	@Operation(summary = "Get grouped attribute commercial rules", tags = {
			"CommercialRules" }, description = "Get grouped attribute commercial rules", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class))) })
	public Response findGroupedAttributeRules(
			@Parameter(description = "grouped attribute code", required = false) @QueryParam("groupedAttributeCode") String grouppedAttributeCode,
			@Parameter(description = "product code", required = true) @QueryParam("productCode") String productCode);

}
