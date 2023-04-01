package org.meveo.apiv2.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.models.Document;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/document")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DocumentResource {
    
    @GET
    @Hidden
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to retrieve a document information by id document",
            tags = { "Document" },
            description ="retrieve and return an existing document",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document successfully retrieved",
                            content = @Content(schema = @Schema(implementation = Document.class))),
                    @ApiResponse(responseCode = "404", description = "the document with code in param does not exist")
            })
    Response getDocument(@Parameter(description = "The id here is the database primary key of the document to fetch", required = true)
                         @PathParam("id") @NotNull Long id);
    
    @GET
    @Path("/{code}/{version}")
    @Operation(summary = "This endpoint allows to retrieve a document information by code and version document",
            tags = { "Document" },
            description ="retrieve and return an existing document",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document successfully retrieved",
                            content = @Content(schema = @Schema(implementation = Document.class))),
                    @ApiResponse(responseCode = "404", description = "the document with code and version in param does not exist")
            })
    Response getDocument(@Parameter(description = "Get object using code for the last version of the Document", required = true)
                         @PathParam("code") @NotNull String code,
                         @Parameter(description = "The version of the document to delete") @PathParam("version") Integer version);

    @POST
    @Hidden
    @Path("/")
    @Operation(summary = "This endpoint allows to create a document and store the associated physical file in disk",
            tags = { "Document" },
            description ="create a new document",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when document information contains an error")
            })
    Response createDocument(@Parameter(description = "the document object", required = true) Document document);

    @DELETE
    @Path("/{code}/{version}")
    @Operation(summary = "Delete a document by providing it's code and version as param",
            tags = { "Document" },
            description ="provide a document code and version for this endpoint, and it will delete the document along with it's related physical file",
            responses = {
                    @ApiResponse(responseCode="204", description = "document successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "the document with code and version in param does not exist")
            })
    Response deleteDocument(@Parameter(description = "The code of the document to delete", required = true) @PathParam("code") @NotNull String code,
                            @Parameter(description = "The version of the document to delete", required = true) @PathParam("version") @NotNull Integer version);

    @GET
    @Path("/{code}/file")
    @Operation(summary = "This endpoint allows to retrieve a document's file using the document code",
            tags = { "Document" },
            description ="retrieve and return an existing document file in base64 format",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file successfully retrieved",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "the document file with document code in param does not exist")
            })
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    Response getDocumentFile(@Parameter(description = "The code of the document's last version file to fetch", required = true)
                             @PathParam("code") @NotNull String code);
    
    @GET
    @Path("/{code}/{version}/file")
    @Operation(summary = "This endpoint allows to retrieve a document's file using the document code and version",
            tags = { "Document" },
            description ="retrieve and return an existing document file in base64 format",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file successfully retrieved",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "the document file with document code in param does not exist")
            })
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    Response getDocumentFile(@Parameter(description = "The code of the document's last version file to fetch", required = true)
                             @PathParam("code") @NotNull String code,
                             @Parameter(description = "The version of the document to fetch") @PathParam("version") Integer version);

    @PUT
    @Path("/{code}/file")
    @Operation(summary = "This endpoint allows to update the document file content",
            tags = { "Document" },
            description ="update an existing document file content",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file content successfully updated"),
                    @ApiResponse(responseCode = "404", description = "the document file with document id in param does not exist"),
                    @ApiResponse(responseCode = "400", description = "bad request when provided file content contains an error")
            })
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    Response updateDocumentFile(@Parameter(description = "The code of the document's file to update", required = true)
                                @PathParam("code") @NotNull String code, @Parameter(description = "the document object", required = true) String encodedFile);

    
    @PUT
    @Path("/{code}/{version}/file")
    @Operation(summary = "This endpoint allows to update the document file content",
            tags = { "Document" },
            description ="update an existing document file content",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file content successfully updated"),
                    @ApiResponse(responseCode = "404", description = "the document file with document id in param does not exist"),
                    @ApiResponse(responseCode = "400", description = "bad request when provided file content contains an error")
            })
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    Response updateDocumentFile(@Parameter(description = "The code of the document's file to update", required = true)
                                @PathParam("code") @NotNull String code, @Parameter(description = "The version of the document to update") @PathParam("version") @NotNull Integer version ,@Parameter(description = "the document object", required = true) String encodedFile);

    @DELETE
    @Path("/{code}/{version}/file")
    @Operation(summary = "This endpoint allows to delete a document's file using the document id",
            tags = { "Document" },
            description ="delete an existing document file from disk",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "the document file with document id in param does not exist")
            })
    Response deleteDocumentFile(@Parameter(description = "The code of the document's file to delete", required = true) @PathParam("code") @NotNull String code,
    							@Parameter(description = "The version of the document to delete") @PathParam("version") @NotNull Integer version,
                                @Parameter(description = "a flag to include the document instance in the delete operation", required = true) @QueryParam("includingDocument") @DefaultValue("false") boolean includingDocument);
}
