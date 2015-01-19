package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.CustomerAccountResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface AccountWs extends IBaseWs {

	@WebMethod
	public CustomerListResponse findAccountHierarchy(
			AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	public ActionStatus createAccountHierarchy(
			AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	public ActionStatus updateAccountHierarchy(
			AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	public CustomerAccountResponse getCustomerAccount(String customerAccountCode);

}
