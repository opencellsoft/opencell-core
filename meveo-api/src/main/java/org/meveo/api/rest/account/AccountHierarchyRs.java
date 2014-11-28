package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/account/accountHierarchy")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface AccountHierarchyRs extends IBaseRs {

	@POST
	@Path("/find")
	public CustomerListResponse find(AccountHierarchyDto customerDto);

	@POST
	@Path("/")
	public ActionStatus create(AccountHierarchyDto accountHierarchyDto);

	@PUT
	@Path("/")
	public ActionStatus update(AccountHierarchyDto accountHierarchyDto);

}