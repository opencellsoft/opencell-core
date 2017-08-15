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
import org.meveo.api.dto.response.admin.GetFilesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.admin.impl.FileUploadForm;

/**
 * @author Edward P. Legaspi
 */
@Path("/admin/files")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface FilesRs extends IBaseRs {

	@GET
	@Path("/all")
	GetFilesResponseDto listFiles();

	@GET
	@Path("/")
	GetFilesResponseDto listFiles(@QueryParam("dir") String dir);

	@POST
	@Path("/createDir")
	ActionStatus createDir(String dir);

	@POST
	@Path("/zipFile")
	ActionStatus zipFile(String file);

	@POST
	@Path("/zipDirectory")
	ActionStatus zipDir(String dir);

	@POST
	@Path("/suppressFile")
	ActionStatus suppressFile(String file);

	@POST
	@Path("/suppressDirectory")
	ActionStatus suppressDir(String dir);
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	ActionStatus uploadFile(@MultipartForm FileUploadForm form);

}
