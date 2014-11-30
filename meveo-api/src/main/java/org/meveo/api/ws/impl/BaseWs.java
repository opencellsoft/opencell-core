package org.meveo.api.ws.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.meveo.admin.exception.LoginException;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseWs {

	@Resource
	private WebServiceContext webServiceContext;

	@Inject
	protected UserService userService;

	protected User getCurrentUser() throws LoginException {
		MessageContext messageContext = webServiceContext.getMessageContext();

		// get request headers
		Map<?, ?> requestHeaders = (Map<?, ?>) messageContext
				.get(MessageContext.HTTP_REQUEST_HEADERS);
		List<?> usernameList = (List<?>) requestHeaders.get("username");
		List<?> passwordList = (List<?>) requestHeaders.get("password");

		String username = "";
		String password = "";

		if (usernameList != null) {
			username = usernameList.get(0).toString();
		}

		if (passwordList != null) {
			password = passwordList.get(0).toString();
		}

		User user = userService.loginChecks(username, password);

		return user;
	}

}
