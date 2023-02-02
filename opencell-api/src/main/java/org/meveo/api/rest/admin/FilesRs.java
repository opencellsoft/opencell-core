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

package org.meveo.api.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.admin.FileRequestDto;
import org.meveo.api.dto.response.admin.GetFilesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.admin.impl.FileUploadForm;

/**
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */
@Path("/admin/files")
@Tag(name = "Files", description = "@%Files")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface FilesRs extends IBaseRs {

    /**
     * Get the list of files 
     *
     * @return List of all files 
     */
    @GET
    @Path("/all")
	@Operation(
			summary=" Get the list of files  ",
			description=" Get the list of files  ",
			operationId="    GET_Files_all",
			responses= {
				@ApiResponse(description=" List of all files  ",
						content=@Content(
									schema=@Schema(
											implementation= GetFilesResponseDto.class
											)
								)
				)}
	)
    GetFilesResponseDto listFiles();

    /**
     * Get the list of files in a specific directory
     *
     * @param dir The directory name
     * @return Request processing status
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Get the list of files in a specific directory ",
			description=" Get the list of files in a specific directory ",
			operationId="    GET_Files_search",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= GetFilesResponseDto.class
											)
								)
				)}
	)
    GetFilesResponseDto listFiles(@QueryParam("dir") String dir);

    /**
     * Create a directory
     *
     * @param dir The directory name
     * @return Request processing status
     */
    @POST
    @Path("/createDir")
	@Operation(
			summary=" Create a directory ",
			description=" Create a directory ",
			operationId="    POST_Files_createDir",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createDir(String dir);

    /**
     * Will make a archive of a file
     *
     * @param file The name of the file 
     * @return Request processing status
     */
    @POST
    @Path("/zipFile")
	@Operation(
			summary=" Will make a archive of a file ",
			description=" Will make a archive of a file ",
			operationId="    POST_Files_zipFile",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus zipFile(String file);

    /**
     * Will make a archive of a directory 
     *
     * @param dir The name of the directory 
     * @return Request processing status
     */
    @POST
    @Path("/zipDirectory")
	@Operation(
			summary=" Will make a archive of a directory  ",
			description=" Will make a archive of a directory  ",
			operationId="    POST_Files_zipDirectory",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus zipDir(String dir);

    /**
     * Suppress the file 
     *
     * @param file The file name
     * @return Request processing status
     */
    @POST
    @Path("/suppressFile")
	@Operation(
			summary=" Suppress the file  ",
			description=" Suppress the file  ",
			operationId="    POST_Files_suppressFile",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus suppressFile(@QueryParam("filePath") String filePath, String file);

    /**
     * Suppress the directory 
     *
     * @param dir the directory name
     * @return Request processing status
     */
    @POST
    @Path("/suppressDirectory")
	@Operation(
			summary=" Suppress the directory  ",
			description=" Suppress the directory  ",
			operationId="    POST_Files_suppressDirectory",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus suppressDir(String dir);

    /**
     * Upload the file form.
     *
     * @param form The data to process
     * @return Request processing status
     */    
    @POST
    @Path("/upload")
	@Operation(
			summary=" Upload the file form. ",
			description=" Upload the file form. ",
			operationId="    POST_Files_upload",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    ActionStatus uploadFile(@MultipartForm FileUploadForm form);

    /**
     * Upload the file with the specific file data in 64 base.
     *
     * @param postData The data to process
     * @return Request processing status
     */
    @POST
    @Path("/uploadFileBase64")
	@Operation(
			summary=" Upload the file with the specific file data in 64 base. ",
			description=" Upload the file with the specific file data in 64 base. ",
			operationId="    POST_Files_uploadFileBase64",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus uploadFileBase64(FileRequestDto postData);

    /**
     * Upload the zipped file with the file data. 
     *
     * @param postData The data to process
     * @return Request processing status
     */
    @POST
    @Path("/uploadZippedFileBase64")
	@Operation(
			summary=" Upload the zipped file with the file data.  ",
			description=" Upload the zipped file with the file data.  ",
			operationId="    POST_Files_uploadZippedFileBase64",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus uploadZippedFileBase64(FileRequestDto postData);

    /**
     * Download file with a given file name.
     *
     * @param file file name
     * @return Request processing status
     */
    @GET
    @Path("/downloadFile")
	@Operation(
			summary=" Download file with a given file name. ",
			description=" Download file with a given file name. ",
			operationId="    GET_Files_downloadFile",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus downloadFile(@QueryParam("file") String file);



	/**
	 * Move a file or directory from a given source path to a given destination path.
	 *
	 * @param srcPath source path
	 * @param destPath destination path
	 * @return Request processing status
	 */
	@POST
	@Path("/moveFileOrDirectory")
	@Operation(
			summary = "Move file or directory with a given source path and a given destination path. ",
			description = "Given a source path and destination path, this API will move the content from source to destination",
			operationId = "GET_Files_moveFileOrDirectory",
			responses = {
					@ApiResponse(description = " Request processing status ",
							content=@Content(
									schema=@Schema(
											implementation = ActionStatus.class
									)
							)
					)}
	)
	ActionStatus moveFileOrDirectory(@QueryParam("sourcePath") String srcPath, @QueryParam("destinationPath") String destPath);
}
