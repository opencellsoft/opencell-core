package org.meveo.api.rest.admin;

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
    GetFilesResponseDto listFiles();

    /**
     * Get the list of files in a specific directory
     *
     * @param dir The directory name
     * @return Request processing status
     */
    @GET
    @Path("/")
    GetFilesResponseDto listFiles(@QueryParam("dir") String dir);

    /**
     * Create a directory
     *
     * @param dir The directory name
     * @return Request processing status
     */
    @POST
    @Path("/createDir")
    ActionStatus createDir(String dir);

    /**
     * Will make a archive of a file
     *
     * @param file The name of the file 
     * @return Request processing status
     */
    @POST
    @Path("/zipFile")
    ActionStatus zipFile(String file);

    /**
     * Will make a archive of a directory 
     *
     * @param dir The name of the directory 
     * @return Request processing status
     */
    @POST
    @Path("/zipDirectory")
    ActionStatus zipDir(String dir);

    /**
     * Suppress the file 
     *
     * @param file The file name
     * @return Request processing status
     */
    @POST
    @Path("/suppressFile")
    ActionStatus suppressFile(String file);

    /**
     * Suppress the directory 
     *
     * @param dir the directory name
     * @return Request processing status
     */
    @POST
    @Path("/suppressDirectory")
    ActionStatus suppressDir(String dir);

    /**
     * Upload the file form.
     *
     * @param form The data to process
     * @return Request processing status
     */    
    @POST
    @Path("/upload")
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
    ActionStatus uploadFileBase64(FileRequestDto postData);

    /**
     * Upload the zipped file with the file data. 
     *
     * @param postData The data to process
     * @return Request processing status
     */
    @POST
    @Path("/uploadZippedFileBase64")
    ActionStatus uploadZippedFileBase64(FileRequestDto postData);

    /**
     * Download file with a given file name.
     *
     * @param file file name
     * @return Request processing status
     */
    @GET
    @Path("/downloadFile")
    ActionStatus downloadFile(@QueryParam("file") String file);

}
