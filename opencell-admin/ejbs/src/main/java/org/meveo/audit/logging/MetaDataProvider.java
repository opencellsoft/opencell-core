package org.meveo.audit.logging;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;

@Stateless
public class MetaDataProvider {

	@Context
	private HttpServletRequest httpRequest;

	@Inject
	@CurrentUser
	private MeveoUser currentUser;

	public String getOrigin() {
		try {
			return getClientIp(httpRequest);
		} catch (Exception e) {
			return AuditConstants.LOGGING_UNKNOWN_IP;
		}
	}

	public String getActor() {
		if (currentUser != null) {
			return currentUser.getUserName();
		}

		return AuditConstants.LOGGING_DEFAULT_USER;
	}

	private static String getClientIp(HttpServletRequest request) {
		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}

		return remoteAddr;
	}
}
