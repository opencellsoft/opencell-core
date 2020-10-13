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
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to retrieve a document information by id document",
            tags = { "Document" },
            description ="retrieve and return an existing document",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document successfully retrieved",
                            content = @Content(schema = @Schema(implementation = Document.class))),
                    @ApiResponse(responseCode = "404", description = "the document with id in param does not exist")
            })
    Response getDocument(@Parameter(description = "The id here is the database primary key of the document to fetch", required = true)
                         @PathParam("id") @NotNull Long id);

    @POST
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
    @Path("/{id}")
    @Operation(summary = "Delete a document by providing it's Id as param",
            tags = { "Document" },
            description ="provide a document id for this endpoint, and it will delete the document along with it's related physical file",
            responses = {
                    @ApiResponse(responseCode="204", description = "document successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "the document with id in param does not exist")
            })
    Response deleteDocument(@Parameter(description = "The id here is the database primary key of the document to delete", required = true)
                            @PathParam("id") @NotNull Long id);

    @GET
    @Path("/{id}/file")
    @Operation(summary = "This endpoint allows to retrieve a document's file using the document id",
            tags = { "Document" },
            description ="retrieve and return an existing document file in base64 format",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file successfully retrieved",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "the document file with document id in param does not exist")
            })
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    Response getDocumentFile(@Parameter(description = "The id here is the database primary key of the document's file to fetch", required = true)
                             @PathParam("id") @NotNull Long id);

    @PUT
    @Path("/{id}/file")
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
    Response updateDocumentFile(@Parameter(description = "The id here is the database primary key of the document's file to fetch", required = true)
                                @PathParam("id") @NotNull Long id, @Parameter(description = "the document object", required = true) String encodedDocumentFile);

    @DELETE
    @Path("/{id}/file")
    @Operation(summary = "This endpoint allows to delete a document's file using the document id",
            tags = { "Document" },
            description ="delete an existing document file from disk",
            responses = {
                    @ApiResponse(responseCode="200", description = "the document file successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "the document file with document id in param does not exist")
            })
    Response deleteDocumentFile(@Parameter(description = "The id here is the database primary key of the document's file to delete", required = true) @PathParam("id") @NotNull Long id,
                                @Parameter(description = "a flag to include the document instance in the delete operation", required = true) @QueryParam("includingDocument") @DefaultValue("false") boolean includingDocument);
}
