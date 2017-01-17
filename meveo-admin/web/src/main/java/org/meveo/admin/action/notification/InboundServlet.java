package org.meveo.admin.action.notification;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.notification.InboundRequest;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.notification.InboundRequestService;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.slf4j.Logger;

/**
 * To call this servlet the url must be in this format: /inbound/<provider.code>
 */
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
	@InboundRequestReceived
	protected Event<InboundRequest> eventProducer;
	
	@Inject
	private Identity identity;
	
	@Inject
	private Credentials credentials;
	
	private User currentUser;

	private void authenticateRequest(HttpServletRequest req, HttpServletResponse res) {
        final String authorization = req.getHeader("Authorization");
        
        // If no authorization information present; block access        
        if (authorization == null || authorization.isEmpty()) {
            log.error("Missing Authorization header");
        } else {
            final String encodedUserPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");

            // Decode username and password
            String usernameAndPassword = null;
            try {
                usernameAndPassword = new String(Base64.decode(encodedUserPassword));
            } catch (IOException e) {
                log.error("Failed to decode authorization string.");
            }
            if (usernameAndPassword != null) {
                // Split username and password tokens
                final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
                String username = tokenizer.nextToken();
                String password = tokenizer.nextToken();

                log.debug("InboundServlet call basic authentication. Username={}", username);
                
                credentials.setUsername(username);
				credentials.setCredential(new PasswordCredential(password));

				String result = identity.login();

				if (result.equals(Identity.RESPONSE_LOGIN_SUCCESS)) {
					res.setStatus(200);
					return;
				}
            }
        }
        
        res.setStatus(401);
	}

	private void doService(HttpServletRequest req, HttpServletResponse res) {
		log.debug("doService.....");
        authenticateRequest(req, res);
        
        if (identity != null && identity.isLoggedIn()) {
            currentUser = ((MeveoUser) identity.getUser()).getUser();
        }
        
        if(currentUser == null){
            res.addHeader("WWW-Authenticate",  "Basic");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String path = req.getPathInfo();
        log.debug("received request for method {} , path={}", req.getMethod(),path);

		InboundRequest inReq = new InboundRequest();
		inReq.setAuditable(new Auditable(currentUser));
		inReq.setProvider(currentUser.getProvider());
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
		if ((!inReq.getHeaders().containsKey("fired"))||inReq.getHeaders().get("fired").equals("false")) {
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
