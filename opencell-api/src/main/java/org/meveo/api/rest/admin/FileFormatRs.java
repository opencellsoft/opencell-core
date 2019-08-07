package org.meveo.api.rest.admin;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * File format resource
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@Path("/admin/fileFormat")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface FileFormatRs extends IBaseRs {

    /**
     * Create a new file format
     *
     * @param postData The file format's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(FileFormatDto postData);

    /**
     * Remove an existing file format with a given code
     *
     * @param code The web hook notification's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);
}
