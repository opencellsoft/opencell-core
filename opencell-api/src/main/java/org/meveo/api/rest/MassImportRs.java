package org.meveo.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ImportFileTypeDto;
import org.meveo.api.rest.admin.impl.FileImportForm;
import org.meveo.api.rest.admin.impl.FileUploadForm;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Ilham Chafik
 **/

@Path("/massImport")
@Tag(name = "MassImport", description = "@%MassImport")
@Consumes({MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MassImportRs {

    /**
     * Upload file and detect its type.
     *
     * @param form file to import
     * @return action status
     */
    @POST
    @Path("/uploadAndImport")
    @Operation(
            summary=" Upload file and detect its type.  ",
            description=" Upload file and detect its type.  ",
            operationId="    POST_UploadMass",
            responses= {
                    @ApiResponse(description=" action status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ImportFileTypeDto.class
                                    )
                            )
                    )}
    )
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    List<ImportFileTypeDto> uploadAndImport(@MultipartForm FileImportForm form);



    /**
     * Upload file and detect its type.
     *
     * @param form file to import
     * @return action status
     */
    @POST
    @Path("/upload")
    @Operation(
            summary=" Upload file and detect its type.  ",
            description=" Upload file and detect its type.  ",
            operationId="    POST_UploadMass",
            responses= {
                    @ApiResponse(description=" action status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ImportFileTypeDto.class
                                    )
                            )
                    )}
    )
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    List<ImportFileTypeDto> uploadMassFile(@MultipartForm FileUploadForm form);

    /**
     * Upload and put file in corresponding folder.
     *
     * @param filesType files to import
     * @return action status
     */
    @POST
    @Path("/import")
    @Operation(
            summary=" Upload and put file in corresponding folder.  ",
            description=" Upload and put file in corresponding folder.  ",
            operationId="    POST_UploadMass",
            responses= {
                    @ApiResponse(description=" action status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    @Consumes({MediaType.APPLICATION_JSON })
    ActionStatus importMassFile(List<ImportFileTypeDto> filesType);
}
