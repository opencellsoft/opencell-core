package org.meveo.api.rest.filter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/filteredList")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface FilteredListRs extends IBaseRs {

	@Path("/")
	@GET
	Response list(@QueryParam("filter") String filter, @QueryParam("firstRow") Integer firstRow,
			@QueryParam("numberOfRows") Integer numberOfRows);

	@Path("/xmlInput")
	@POST
	Response listByXmlInput(FilteredListDto postData);

}
