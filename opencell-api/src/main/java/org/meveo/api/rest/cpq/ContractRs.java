package org.meveo.api.rest.cpq;

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
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.cpq.ContractItemDto;
import org.meveo.api.dto.cpq.ContractListResponsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetContractDtoResponse;
import org.meveo.api.dto.response.cpq.GetListContractDtoResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.cpq.enums.ContractAccountLevel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 * @author Tarik F.
 * @version 10.0
 *
 */
@Path("/cpq/contract")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ContractRs extends IBaseRs {

	/**
	 * @param contractDto
	 * @return
	 */
	@POST
	@Path("/")
    @Operation(summary = "This endpoint allows to create new contract",
    tags = { "Contract" },
    description ="Creating a new contract",
    responses = {
            @ApiResponse(responseCode="200", description = "the contract successfully added",
            		content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for contractDto.The required params are : code, sellerCode, contractDate, beginDate, endDate",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "date start is great than date end ", 
    				content = @Content(schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "302", description = "code of the contract already exist", 
    				content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
            @ApiResponse(responseCode = "404", description = "code of the seller doesn't exist", 
    				content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
	Response createContract(@Parameter(description = "contract dto for a new insertion", required = true) ContractDto contractDto);
	
	@PUT
	@Path("/")
    @Operation(summary = "This endpoint allows to update an existing Contract",
    description ="Updating an existing Contract",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "the Contract successfully updated",
            		content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for contractDto.The required params are : code, sellerCode, contractDate, beginDate, endDate",
    				content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
		    @ApiResponse(responseCode = "400", description = "date start is great than date end ", 
					content = @Content(schema = @Schema(implementation = BusinessException.class))),
		    @ApiResponse(responseCode = "302", description = "code of the contract already exist", 
					content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
		    @ApiResponse(responseCode = "404", description = "code of the seller doesn't exist", 
					content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
	Response updateContract(@Parameter(description = "contract dto for an updating an existing contract", required = true) ContractDto contractDto);
	
	@DELETE
	@Path("/{contractCode}")
    @Operation(summary = "This endpoint allows to  delete an existing Contract",
    description ="Deleting an existing Contract with its code",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Contract successfully deleted",
            		content = @Content(schema = @Schema(implementation = ActionStatusEnum.class))),
            @ApiResponse(responseCode = "404", description = "No Contract found", 
    		content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))),
            @ApiResponse(responseCode = "400", description = "Status of the contract is Active", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response deleteContract(@Parameter(description = "contain the code of contract te be deleted by its code", required = true) @PathParam("contractCode") String contractCode);
	

	@GET
	@Path("/{contractCode}")
    @Operation(summary = "This endpoint allows to find a Contract by ContractCode parameter",
    description ="Find a Contract type by contractCode parameter",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Contract successfully retrieved",
            		content = @Content(schema = @Schema(implementation = GetContractDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "The parameter contractCode is missing",
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
    })
	Response findByCode(@Parameter(description = "retrieving a Contract with its code") @PathParam("contractCode") String contractCode);
	

	@GET
	@Path("/")
    @Operation(summary = "This endpoint allows to find a Contract by ContractCode and contractAccountLevel parameters",
    description ="Find a Contract type by contractCode and contractAccountLevel parameters",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Contract successfully retrieved",
            		content = @Content(schema = @Schema(implementation = GetListContractDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "One of the parameters is missing",
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
    })
	Response findByCode(@Parameter(description = "retrieving a Contract with its code") @QueryParam("contractAccountLevel") ContractAccountLevel contractAccountLevel,
								@Parameter(description = "retrieving a Contract with its code") @QueryParam("accountCode") String accountCode);
	@POST
	@Path("/list")
	@Operation(summary = "This endpoint allows to find list of contract with filters and paging",
    description ="Find a list of contract by filtrering on its property",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Contract successfully retrieved",
            		content = @Content(schema = @Schema(implementation = ContractListResponsDto.class))),
            @ApiResponse(responseCode = "412", description = "One of the parameters is missing",
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
    })
	Response ListPost(PagingAndFiltering pagingAndFiltering);
	

	@POST
	@Path("/contractLine")
    @Operation(summary = "This endpoint allows to create new contract Line",
    tags = { "Contract" },
    description ="Creating a new contract Line",
    responses = {
            @ApiResponse(responseCode="200", description = "the contract Line successfully added",
            		content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for contractDto.The required params are : contractCode, code, serviceTemplateCode",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "302", description = "code of the contract line already exist", 
    				content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
            @ApiResponse(responseCode = "404", description = "one of these parameters contractCode, serviceTemplateCode doesn't exist", 
    				content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
	Response createContractLine(@Parameter(description = "contract Line dto for a new insertion", required = true) ContractItemDto contractItemDto);
	

	@PUT
	@Path("/contractLine")
    @Operation(summary = "This endpoint allows to create new contract Line",
    tags = { "Contract" },
    description ="Creating a new contract Line",
    responses = {
            @ApiResponse(responseCode="200", description = "the contract Line successfully added",
            		content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for contractDto.The required params are : contractCode, code, serviceTemplateCode",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "404", description = "one of these parameters contractCode, serviceTemplateCode doesn't exist", 
    				content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
    })
	Response updateContractLine(@Parameter(description = "contract Line dto for a new insertion", required = true) ContractItemDto contractItemDto);
	
	@DELETE
	@Path("/contractLine/{contractCode}")
    @Operation(summary = "This endpoint allows to  delete an existing Contract item",
    description ="Deleting an existing Contract with its code",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Contract item successfully deleted",
            		content = @Content(schema = @Schema(implementation = ActionStatusEnum.class))),
            @ApiResponse(responseCode = "404", description = "No Contract item found", 
    		content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))),
            @ApiResponse(responseCode = "400", description = "Status of the contract is Active", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response deleteContractLine(@Parameter(description = "contract item code to be deleted", required = true) @PathParam("contractCode") String contractItemCode);
	
	@GET
	@Path("/contractLine")
    @Operation(summary = "This endpoint allows to find a Contract item by contractItemCode parameters",
    description ="Find a Contract item type by contractItemCode parameters",
    tags = { "Contract" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Contract item successfully retrieved",
            		content = @Content(schema = @Schema(implementation = GetListContractDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "contractItemCode is missing",
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
    })
	Response getContractLines(@Parameter(description = "contract item code", required = true) @QueryParam("contractItemCode")  String contractItemCode);
	
}
