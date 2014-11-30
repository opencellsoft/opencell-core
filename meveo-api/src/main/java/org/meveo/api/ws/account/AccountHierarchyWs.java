package org.meveo.api.ws.account;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.ws.IBaseWs;

@WebService
public interface AccountHierarchyWs extends IBaseWs {

	@WebMethod
	public CustomerListResponse find(AccountHierarchyDto customerDto);

	@WebMethod
	public ActionStatus create(AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	public ActionStatus update(AccountHierarchyDto accountHierarchyDto);

}