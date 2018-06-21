package org.meveo.api.rest.admin;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Mounir Bahije
 */
@Path("/admin/audit")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface AuditRs extends IBaseRs {

	/**
	 * @param enableORdisable
	 * @return
	 */

	@PUT
	@Path("/{enableORdisable}")
	ActionStatus enableORdisableAudit(@PathParam("enableORdisable") String enableORdisable);


}
