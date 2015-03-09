package org.meveo.api.ws.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.LoginException;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.commons.utils.StringUtils;
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

	@WebMethod
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "MEVEO API Rest Web Service V1.0");
		try {
			getCurrentUser();
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	protected User getCurrentUser() throws LoginException {
		MessageContext messageContext = webServiceContext.getMessageContext();

		// get request headers
		Map<?, ?> requestHeaders = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);

		List<Object> authorizationHeader = (List<Object>) requestHeaders.get("Authorization");
		if (authorizationHeader == null || authorizationHeader.size() == 0) {
			throw new LoginException("Authentication failed! This WS needs BASIC Authentication!");
		}

		String userpass = (String) authorizationHeader.get(0);
		userpass = userpass.substring(5);
		byte[] buf = Base64.decodeBase64(userpass.getBytes());
		String credentials = new String(buf);

		String username = null;
		String password = null;
		int p = credentials.indexOf(":");
		if (p > -1) {
			username = credentials.substring(0, p);
			password = credentials.substring(p + 1);
		} else {
			throw new RuntimeException("There was an error while decoding the Authentication!");
		}

		if (StringUtils.isBlank(username) | StringUtils.isBlank(password)) {
			throw new LoginException("Username and password are required.");
		}

		User user = userService.loginChecks(username, password);

		return user;
	}

}
