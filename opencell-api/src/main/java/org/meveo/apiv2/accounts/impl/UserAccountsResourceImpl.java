package org.meveo.apiv2.accounts.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounts.resource.UserAccountsResource;
import org.meveo.apiv2.accounts.service.UserAccountsApiService;

public class UserAccountsResourceImpl implements UserAccountsResource {

    @Inject
    private UserAccountsApiService userAccountsApiService;

	@Override
	public Response allowedUserAccountParents(String userAccountCode) {
        return Response
                .ok(userAccountsApiService.allowedUserAccountParents(userAccountCode))
                .build();
	}


}
