package org.meveo.api.rest.billing;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.billing.PdpStatusDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/billing/einvoicing")
@Tag(name = "E-Invoice", description = "@%E-Invoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface EinvoicingRs extends IBaseRs {
	
	@Path("pdp-status")
	@POST
	Response creatOrUpdatePdpStatus(PdpStatusDto pdpStatusDto);
}
