package org.meveo.apiv2.accounts.impl;

import static javax.ws.rs.core.Response.ok;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.meveo.api.dto.account.UserAccountCodeIdsDto;
import org.meveo.apiv2.accounts.resource.UserAccountsResource;
import org.meveo.apiv2.accounts.service.UserAccountsApiService;
import org.meveo.model.billing.UserAccount;

public class UserAccountsResourceImpl implements UserAccountsResource {

    @Inject
    private UserAccountsApiService userAccountsApiService;

	@Override
	public Response allowedUserAccountParents(String userAccountCode) {
		UserAccountCodeIdsDto userAccounts = userAccountsApiService.allowedUserAccountParents(userAccountCode);
		ResponseBuilder response = ok();
		if( userAccounts != null ) {
			response.entity(userAccounts);
		}
		return response.build();
		
	}


}
