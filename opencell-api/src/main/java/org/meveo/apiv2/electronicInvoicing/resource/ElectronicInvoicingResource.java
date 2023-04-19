package org.meveo.apiv2.electronicInvoicing.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/electronicInvoicing")
@Produces({APPLICATION_JSON})
@Consumes({APPLICATION_JSON})
public interface ElectronicInvoicingResource {
	 
    @Path("/isoIcd")
	@Operation(
		summary="Create new or update an existing isoIcd  ",
		description=" Create new or update an existing isoIcd  ",
		responses = {
            @ApiResponse(responseCode = "200", description = "IsoIcd successfully created"),
            @ApiResponse(responseCode = "404", description = "a related entity does not exist"),
            @ApiResponse(responseCode = "412", description = "Missing parameters"),
            @ApiResponse(responseCode = "400", description = "IsoIcd create failed")
        })
    @POST
    Response createIsoIcd(@Parameter(required = true, description = "isoIcd") IsoIcd pIsoIcd);
    
    @PUT
    @Path("/isoIcd/{isoIcdId}")
    @Operation(
		summary = "Update an IsoIcd",
        tags = {"IsoIcd"},
        description = "Update an IsoIcd",
        responses = {
            @ApiResponse(responseCode = "200", description = "IsoIcd successfully updated"),
            @ApiResponse(responseCode = "404", description = "a related entity does not exist"),
            @ApiResponse(responseCode = "412", description = "Missing parameters"),
            @ApiResponse(responseCode = "400", description = "IsoIcd update failed")
        })
    Response updateIsoIcd(@Parameter(required = true, description = "IsoIcd id") @PathParam("isoIcdId") Long pIsoIcdId,
    						@Parameter(required = true, description = "IsoIcd") IsoIcd pIsoIcd);
    
    @DELETE
    @Path("/isoIcd/{isoIcdId}")
    @Operation(summary = "Delete an IsoIcd",
        tags = {"IsoIcd"},
        description = "Delete an IsoIcd",
        responses = {
            @ApiResponse(responseCode = "200", description = "IsoIcd successfully updated"),
            @ApiResponse(responseCode = "404", description = "a related entity does not exist"),
            @ApiResponse(responseCode = "412", description = "Missing parameters"),
            @ApiResponse(responseCode = "400", description = "IsoIcd update failed")
        })
    Response deleteIsoIcd(@Parameter(required = true, description = "IsoIcd id") @PathParam("isoIcdId") Long pIsoIcdId);
    
    @Path("/untdidAllowanceCode")
	@Operation(
		summary="Create new or update an existing UntdidAllowanceCode",
		description=" Create new or update an existing UntdidAllowanceCode",
		responses = {
            @ApiResponse(responseCode = "200", description = "UntdidAllowanceCode successfully created"),
            @ApiResponse(responseCode = "404", description = "a related entity does not exist"),
            @ApiResponse(responseCode = "412", description = "Missing parameters"),
            @ApiResponse(responseCode = "400", description = "UntdidAllowanceCode create failed")
        })
    @POST
    Response createUntdidAllowanceCode(@Parameter(required = true, description = "untdidAllowanceCode") UntdidAllowanceCode untdidAllowanceCode);
    
    @PUT
    @Path("/untdidAllowanceCode/{untdidAllowanceCodeId}")
    @Operation(
		summary = "Update an UntdidAllowanceCode",
        tags = {"UntdidAllowanceCode"},
        description = "Update an UntdidAllowanceCode",
        responses = {
            @ApiResponse(responseCode = "200", description = "UntdidAllowanceCode successfully updated"),
            @ApiResponse(responseCode = "404", description = "a related entity does not exist"),
            @ApiResponse(responseCode = "412", description = "Missing parameters"),
            @ApiResponse(responseCode = "400", description = "UntdidAllowanceCode update failed")
        })
    Response updateUntdidAllowanceCode(@Parameter(required = true, description = "UntdidAllowanceCode id") @PathParam("untdidAllowanceCodeId") Long pUntdidAllowanceCodeId,
    						@Parameter(required = true, description = "UntdidAllowanceCode") UntdidAllowanceCode pUntdidAllowanceCode);
    
    @DELETE
    @Path("/untdidAllowanceCode/{untdidAllowanceCodeId}")
    @Operation(summary = "Delete an UntdidAllowanceCode",
        tags = {"UntdidAllowanceCode"},
        description = "Delete an UntdidAllowanceCode",
        responses = {
            @ApiResponse(responseCode = "200", description = "UntdidAllowanceCode successfully updated"),
            @ApiResponse(responseCode = "404", description = "a related entity does not exist"),
            @ApiResponse(responseCode = "412", description = "Missing parameters"),
            @ApiResponse(responseCode = "400", description = "UntdidAllowanceCode update failed")
        })
    Response deleteUntdidAllowanceCode(@Parameter(required = true, description = "UntdidAllowanceCode id") @PathParam("untdidAllowanceCodeId") Long pUntdidAllowanceCodeId);
    
    @Path("/untdidInvoiceCodeType")
    @Operation(
    	summary="Create new or update an existing UntdidInvoiceCodeType",
    	description=" Create new or update an existing UntdidInvoiceCodeType",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidInvoiceCodeType successfully created"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidInvoiceCodeType create failed")
    	})
    @POST
    Response createUntdidInvoiceCodeType(@Parameter(required = true, description = "untdidInvoiceCodeType") UntdidInvoiceCodeType untdidInvoiceCodeType);

    @PUT
    @Path("/untdidInvoiceCodeType/{untdidInvoiceCodeTypeId}")
    @Operation(
    	summary = "Update an UntdidInvoiceCodeType",
    	tags = {"UntdidInvoiceCodeType"},
    	description = "Update an UntdidInvoiceCodeType",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidInvoiceCodeType successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidInvoiceCodeType update failed")
    	})
    Response updateUntdidInvoiceCodeType(@Parameter(required = true, description = "UntdidInvoiceCodeType id") @PathParam("untdidInvoiceCodeTypeId") Long pUntdidInvoiceCodeTypeId,
    						@Parameter(required = true, description = "UntdidInvoiceCodeType") UntdidInvoiceCodeType pUntdidInvoiceCodeType);

    @DELETE
    @Path("/untdidInvoiceCodeType/{untdidInvoiceCodeTypeId}")
    @Operation(summary = "Delete an UntdidInvoiceCodeType",
    	tags = {"UntdidInvoiceCodeType"},
    	description = "Delete an UntdidInvoiceCodeType",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidInvoiceCodeType successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidInvoiceCodeType update failed")
    	})
    Response deleteUntdidInvoiceCodeType(@Parameter(required = true, description = "UntdidInvoiceCodeType id") @PathParam("untdidInvoiceCodeTypeId") Long pUntdidInvoiceCodeTypeId);

    @Path("/untdidInvoiceSubjectCode")
    @Operation(
    	summary="Create new or update an existing UntdidInvoiceSubjectCode",
    	description=" Create new or update an existing UntdidInvoiceSubjectCode",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidInvoiceSubjectCode successfully created"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidInvoiceSubjectCode create failed")
    	})
    @POST
    Response createUntdidInvoiceSubjectCode(@Parameter(required = true, description = "untdidInvoiceSubjectCode") UntdidInvoiceSubjectCode untdidInvoiceSubjectCode);

    @PUT
    @Path("/untdidInvoiceSubjectCode/{untdidInvoiceSubjectCodeId}")
    @Operation(
    	summary = "Update an UntdidInvoiceSubjectCode",
    	tags = {"UntdidInvoiceSubjectCode"},
    	description = "Update an UntdidInvoiceSubjectCode",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidInvoiceSubjectCode successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidInvoiceSubjectCode update failed")
    	})
    Response updateUntdidInvoiceSubjectCode(@Parameter(required = true, description = "UntdidInvoiceSubjectCode id") @PathParam("untdidInvoiceSubjectCodeId") Long pUntdidInvoiceSubjectCodeId,
    						@Parameter(required = true, description = "UntdidInvoiceSubjectCode") UntdidInvoiceSubjectCode pUntdidInvoiceSubjectCode);

    @DELETE
    @Path("/untdidInvoiceSubjectCode/{untdidInvoiceSubjectCodeId}")
    @Operation(summary = "Delete an UntdidInvoiceSubjectCode",
    	tags = {"UntdidInvoiceSubjectCode"},
    	description = "Delete an UntdidInvoiceSubjectCode",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidInvoiceSubjectCode successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidInvoiceSubjectCode update failed")
    	})
    Response deleteUntdidInvoiceSubjectCode(@Parameter(required = true, description = "UntdidInvoiceSubjectCode id") @PathParam("untdidInvoiceSubjectCodeId") Long pUntdidInvoiceSubjectCodeId);

    @Path("/untdidPaymentMeans")
    @Operation(
    	summary="Create new or update an existing UntdidPaymentMeans",
    	description=" Create new or update an existing UntdidPaymentMeans",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidPaymentMeans successfully created"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidPaymentMeans create failed")
    	})
    @POST
    Response createUntdidPaymentMeans(@Parameter(required = true, description = "untdidPaymentMeans") UntdidPaymentMeans untdidPaymentMeans);

    @PUT
    @Path("/untdidPaymentMeans/{untdidPaymentMeansId}")
    @Operation(
    	summary = "Update an UntdidPaymentMeans",
    	tags = {"UntdidPaymentMeans"},
    	description = "Update an UntdidPaymentMeans",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidPaymentMeans successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidPaymentMeans update failed")
    	})
    Response updateUntdidPaymentMeans(@Parameter(required = true, description = "UntdidPaymentMeans id") @PathParam("untdidPaymentMeansId") Long pUntdidPaymentMeansId,
    						@Parameter(required = true, description = "UntdidPaymentMeans") UntdidPaymentMeans pUntdidPaymentMeans);

    @DELETE
    @Path("/untdidPaymentMeans/{untdidPaymentMeansId}")
    @Operation(summary = "Delete an UntdidPaymentMeans",
    	tags = {"UntdidPaymentMeans"},
    	description = "Delete an UntdidPaymentMeans",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidPaymentMeans successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidPaymentMeans update failed")
    	})
    Response deleteUntdidPaymentMeans(@Parameter(required = true, description = "UntdidPaymentMeans id") @PathParam("untdidPaymentMeansId") Long pUntdidPaymentMeansId);

    @Path("/untdidTaxationCategory")
    @Operation(
    	summary="Create new or update an existing UntdidTaxationCategory",
    	description=" Create new or update an existing UntdidTaxationCategory",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidTaxationCategory successfully created"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidTaxationCategory create failed")
    	})
    @POST
    Response createUntdidTaxationCategory(@Parameter(required = true, description = "untdidTaxationCategory") UntdidTaxationCategory untdidTaxationCategory);

    @PUT
    @Path("/untdidTaxationCategory/{untdidTaxationCategoryId}")
    @Operation(
    	summary = "Update an UntdidTaxationCategory",
    	tags = {"UntdidTaxationCategory"},
    	description = "Update an UntdidTaxationCategory",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidTaxationCategory successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidTaxationCategory update failed")
    	})
    Response updateUntdidTaxationCategory(@Parameter(required = true, description = "UntdidTaxationCategory id") @PathParam("untdidTaxationCategoryId") Long pUntdidTaxationCategoryId,
    						@Parameter(required = true, description = "UntdidTaxationCategory") UntdidTaxationCategory pUntdidTaxationCategory);

    @DELETE
    @Path("/untdidTaxationCategory/{untdidTaxationCategoryId}")
    @Operation(summary = "Delete an UntdidTaxationCategory",
    	tags = {"UntdidTaxationCategory"},
    	description = "Delete an UntdidTaxationCategory",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidTaxationCategory successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidTaxationCategory update failed")
    	})
    Response deleteUntdidTaxationCategory(@Parameter(required = true, description = "UntdidTaxationCategory id") @PathParam("untdidTaxationCategoryId") Long pUntdidTaxationCategoryId);

    @Path("/untdidVatex")
    @Operation(
    	summary="Create new or update an existing UntdidVatex",
    	description=" Create new or update an existing UntdidVatex",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidVatex successfully created"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidVatex create failed")
    	})
    @POST
    Response createUntdidVatex(@Parameter(required = true, description = "untdidVatex") UntdidVatex untdidVatex);

    @PUT
    @Path("/untdidVatex/{untdidVatexId}")
    @Operation(
    	summary = "Update an UntdidVatex",
    	tags = {"UntdidVatex"},
    	description = "Update an UntdidVatex",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidVatex successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidVatex update failed")
    	})
    Response updateUntdidVatex(@Parameter(required = true, description = "UntdidVatex id") @PathParam("untdidVatexId") Long pUntdidVatexId,
    						@Parameter(required = true, description = "UntdidVatex") UntdidVatex pUntdidVatex);

    @DELETE
    @Path("/untdidVatex/{untdidVatexId}")
    @Operation(summary = "Delete an UntdidVatex",
    	tags = {"UntdidVatex"},
    	description = "Delete an UntdidVatex",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidVatex successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidVatex update failed")
    	})
    Response deleteUntdidVatex(@Parameter(required = true, description = "UntdidVatex id") @PathParam("untdidVatexId") Long pUntdidVatexId);

    @Path("/untdidVatPaymentOption")
    @Operation(
    	summary="Create new or update an existing UntdidVatPaymentOption",
    	description=" Create new or update an existing UntdidVatPaymentOption",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidVatPaymentOption successfully created"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidVatPaymentOption create failed")
    	})
    @POST
    Response createUntdidVatPaymentOption(@Parameter(required = true, description = "untdidVatPaymentOption") UntdidVatPaymentOption untdidVatPaymentOption);

    @PUT
    @Path("/untdidVatPaymentOption/{untdidVatPaymentOptionId}")
    @Operation(
    	summary = "Update an UntdidVatPaymentOption",
    	tags = {"UntdidVatPaymentOption"},
    	description = "Update an UntdidVatPaymentOption",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidVatPaymentOption successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidVatPaymentOption update failed")
    	})
    Response updateUntdidVatPaymentOption(@Parameter(required = true, description = "UntdidVatPaymentOption id") @PathParam("untdidVatPaymentOptionId") Long pUntdidVatPaymentOptionId,
    						@Parameter(required = true, description = "UntdidVatPaymentOption") UntdidVatPaymentOption pUntdidVatPaymentOption);

    @DELETE
    @Path("/untdidVatPaymentOption/{untdidVatPaymentOptionId}")
    @Operation(summary = "Delete an UntdidVatPaymentOption",
    	tags = {"UntdidVatPaymentOption"},
    	description = "Delete an UntdidVatPaymentOption",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "UntdidVatPaymentOption successfully updated"),
    		@ApiResponse(responseCode = "404", description = "a related entity does not exist"),
    		@ApiResponse(responseCode = "412", description = "Missing parameters"),
    		@ApiResponse(responseCode = "400", description = "UntdidVatPaymentOption update failed")
    	})
    Response deleteUntdidVatPaymentOption(@Parameter(required = true, description = "UntdidVatPaymentOption id") @PathParam("untdidVatPaymentOptionId") Long pUntdidVatPaymentOptionId);
}
