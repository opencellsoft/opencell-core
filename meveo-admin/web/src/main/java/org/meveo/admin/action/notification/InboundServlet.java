package org.meveo.admin.action.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.util.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.admin.exception.UnknownUserException;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.InboundRequest;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.notification.InboundRequestService;
import org.slf4j.Logger;

@WebServlet("/inbound/*")
public class InboundServlet extends HttpServlet {

	private static final long serialVersionUID = 1551787937225264581L;

	private static final String AUTHENTICATION_SCHEME = "Basic";

	@Inject
	InboundRequestService inboundRequestService;

	@Inject
	Logger log;

	@Inject
	ProviderService providerService;

	@Inject
	private UserService userService;

	@Inject
	@InboundRequestReceived
	protected Event<InboundRequest> eventProducer;

	private User currentUser;

	public static int readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis) throws IOException {
		int bufferOffset = 0;
		long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
		while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < b.length) {
			int readLength = java.lang.Math.min(is.available(), b.length - bufferOffset);
			// can alternatively use bufferedReader, guarded by isReady():
			int readResult = is.read(b, bufferOffset, readLength);
			if (readResult == -1)
				break;
			bufferOffset += readResult;
		}
		return bufferOffset;
	}

	private void doService(HttpServletRequest req, HttpServletResponse res) {
		final String authorization = req.getHeader("Authorization");

		// If no authorization information present; block access
		if (authorization == null || authorization.isEmpty()) {
			res.setStatus(401);
			log.error("Missing Authorization header");
			return;
		}

		final String encodedUserPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");

		// Decode username and password
		String usernameAndPassword = null;
		try {
			usernameAndPassword = new String(Base64.decode(encodedUserPassword));
		} catch (IOException e) {
			res.setStatus(401);
			return;
		}

		// Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		String username = tokenizer.nextToken();
		String password = tokenizer.nextToken();

		log.debug("InboundServlet call basic authentication. Username={}", username);

		try {
			currentUser = userService.loginChecks(username, password, false);
			log.debug("REST login successfull with username={}", username);

		} catch (LoginException e) {
			log.error("Login failed for the user {} for reason {} {}", new Object[] { username, e.getClass().getName(), e.getMessage() });
			if (e instanceof InactiveUserException) {
				log.error("login failed with username=" + username + " and password=" + password + " : cause Business Account or user is not active");
			} else if (e instanceof UnknownUserException) {
				log.error("login failed with username=" + username + " and password=" + password + " : cause unknown username/password");
			} else if (e instanceof PasswordExpiredException) {
				log.error("The password of user " + username + " has expired.");
			}

			currentUser = null;

			res.setStatus(401);
			return;
		}

		log.debug("received request for method {} ", req.getMethod());
		Provider provider = null;
		String path = req.getPathInfo();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String providerCode = null;
		try {
			providerCode = path.substring(0, path.indexOf('/'));
			provider = providerService.findByCode(providerCode);
		} catch (Exception e) {
		}

		if (provider == null) {
			log.debug("Request has invalid provider code {} ", providerCode);
			res.setStatus(404);
			return;
		}
		InboundRequest inReq = new InboundRequest();
		inReq.setProvider(provider);
		inReq.setCode(req.getRemoteAddr() + ":" + req.getRemotePort() + "_" + req.getMethod() + "_" + System.nanoTime());

		inReq.setContentLength(req.getContentLength());
		inReq.setContentType(req.getContentType());

		if (req.getParameterNames() != null) {
			Enumeration<String> parameterNames = req.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String parameterName = parameterNames.nextElement();
				String[] paramValues = req.getParameterValues(parameterName);
				String parameterValue = null;
				String sep = "";
				for (String paramValue : paramValues) {
					parameterValue = sep + paramValue;
					sep = "|";
				}
				inReq.getParameters().put(parameterName, parameterValue);
			}
		}
		inReq.setProtocol(req.getProtocol());
		inReq.setScheme(req.getScheme());
		inReq.setRemoteAddr(req.getRemoteAddr());
		inReq.setRemotePort(req.getRemotePort());
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader;
		try {
			reader = req.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		String body = buffer.toString();
		inReq.setBody(body);

		inReq.setMethod(req.getMethod());
		inReq.setAuthType(req.getAuthType());
		if (req.getCookies() != null) {
			for (Cookie cookie : req.getCookies()) {
				inReq.getCoockies().put(cookie.getName(), cookie.getValue());
			}
		}
		if (req.getHeaderNames() != null) {
			Enumeration<String> headerNames = req.getHeaderNames();

			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				inReq.getHeaders().put(headerName, req.getHeader(headerName));
			}
		}
		inReq.setPathInfo(path);
		inReq.setRequestURI(req.getRequestURI());

		// process the notifications
		eventProducer.fire(inReq);

		log.debug("triggered {} notification, resp body= {}", inReq.getNotificationHistories().size(), inReq.getResponseBody());
		// ONLY ScriptNotifications will produce notification history in
		// synchronous mode. Other type notifications will produce notification
		// history in asynchronous mode and thus
		// will not be related to inbound request.
		if (inReq.getNotificationHistories().size() == 0) {
			res.setStatus(404);
		} else {
			// produce the response
			res.setCharacterEncoding(inReq.getResponseEncoding() == null ? req.getCharacterEncoding() : inReq.getResponseEncoding());
			res.setContentType(inReq.getContentType());
			for (String cookieName : inReq.getResponseCoockies().keySet()) {
				res.addCookie(new Cookie(cookieName, inReq.getResponseCoockies().get(cookieName)));
			}
			
			for (String headerName : inReq.getResponseHeaders().keySet()) {
				res.addHeader(headerName, inReq.getResponseHeaders().get(headerName));
			}
			
			if (inReq.getResponseBody() != null) {
				try (PrintWriter out = res.getWriter()) {
					out.print(inReq.getResponseBody());
				} catch (IOException e) {
					log.error("Failed to produce the response", e);
					res.setStatus(500);
				}
			}
			res.setStatus(200);
		}

		try {
			inboundRequestService.create(inReq, currentUser);
		} catch (BusinessException e1) {
			log.error("Failed to create InboundRequest ", e1);
		}
		
		log.debug("exit with status {}", res.getStatus());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

	public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

	public void doHead(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

	public void doOption(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

	public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

	public void doTrace(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doService(req, res);
	}

}
