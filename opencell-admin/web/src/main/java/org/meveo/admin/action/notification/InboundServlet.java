/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.action.notification;

import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.keycloak.KeycloakPrincipal;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.notification.InboundRequest;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.notification.InboundRequestService;
import org.slf4j.Logger;

/**
 * To call this servlet the url must be in this format: /inbound/&lt;provider.code&gt;
 *
 * @author Abdellatif BARI
 * @author Mohammed STITANE
 * @lastModifiedVersion 8.0.0
 */
@WebServlet("/inbound/*")
@ServletSecurity
public class InboundServlet extends HttpServlet {

    private static final long serialVersionUID = 1551787937225264581L;

    @Inject
    InboundRequestService inboundRequestService;
    
    @Inject
    ParamBeanFactory paramBeanFactory;

    @Inject
    Logger log;

    @Inject
    @InboundRequestReceived
    protected Event<InboundRequest> eventProducer;

    @Inject
    private AuditOrigin auditOrigin;

    private void doService(HttpServletRequest req, HttpServletResponse res) {
        
        try {

            ParamBean param = paramBeanFactory.getInstance();
            boolean authorizationDisabled = "true".equalsIgnoreCase(param.getProperty("inbound.authorization.disabled", "false"));

            boolean isUserInRole = isUserInRole(req);

            boolean processRequest = authorizationDisabled || isUserInRole;

            String path = req.getPathInfo();
            log.debug("received request for method {} , path={}", req.getMethod(), path);

            auditOrigin.setAuditOrigin(ChangeOriginEnum.INBOUND_REQUEST);
            auditOrigin.setAuditOriginName(path);

            InboundRequest inReq = new InboundRequest();
            inReq.setCode(req.getRemoteAddr() + "_" + req.getRemotePort() + "_" + req.getMethod() + "_" + System.nanoTime());

            inReq.setContentLength(req.getContentLength());
            inReq.setContentType(req.getContentType());

            addParams(req, inReq);
            inReq.setProtocol(req.getProtocol());
            inReq.setScheme(req.getScheme());
            inReq.setRemoteAddr(req.getRemoteAddr());
            inReq.setRemotePort(req.getRemotePort());
            String body;
            if (inReq.getParameters().containsKey("body")) {
                body = retrieveBodyFromParm(inReq);
            } else {
                body = getBodyString(req);
            }
            inReq.setBody(body);

            inReq.setMethod(req.getMethod());
            inReq.setAuthType(req.getAuthType());
            addCookies(req, inReq);
            addHeaders(req, inReq);
            inReq.setPathInfo(path);
            inReq.setRequestURI(req.getRequestURI());
            inboundRequestService.create(inReq);
            
            int status = HttpURLConnection.HTTP_OK;
            
            if(processRequest) {
                // process the notifications
                status = processNotificationAndReturnStatus(inReq, status);
    
                log.debug("triggered {} notification, resp body= {}", inReq.getNotificationHistories().size(), inReq.getResponseBody());
                // ONLY ScriptNotifications will produce notification history in
                // synchronous mode. Other type notifications will produce notification
                // history in asynchronous mode and thus
                // will not be related to inbound request.
    
                if (inReq.getResponseStatus() != null) {
                    status = inReq.getResponseStatus();
                }
            } else {
                status = HttpURLConnection.HTTP_UNAUTHORIZED;
            }
            
            res.setStatus(status);

            // produce the response
            res.setCharacterEncoding(inReq.getResponseEncoding() == null ? req.getCharacterEncoding() : inReq.getResponseEncoding());
            res.setContentType(inReq.getResponseContentType() == null ? inReq.getContentType() : inReq.getResponseContentType());

            produceResponse(res, inReq);

            inboundRequestService.update(inReq);

            log.debug("Inbound request finished with status {}", res.getStatus());

        } catch (BusinessException | IOException e) {
            log.error("Failed to process Inbound request ", e);
        }
    }

    private boolean isUserInRole(HttpServletRequest req) {
        if (req.isUserInRole("inboundAccess")) {
            return true;
        } else {
            return isUserInCustomRole(req);
        }

    }

    private boolean isUserInCustomRole(HttpServletRequest req) {

        String[] stringUri = req.getRequestURI().split("/");
        String inboundWsRole = "";
        if (stringUri.length > 0) {
            inboundWsRole = stringUri[stringUri.length - 1];
        }
        if (StringUtils.isBlank(inboundWsRole)) {
            return false;
        }
        return req.isUserInRole(inboundWsRole);
    }

    private Set<String> getCustomRoles(KeycloakPrincipal principal) {
        String client = principal.getKeycloakSecurityContext().getToken().getIssuedFor();

        return principal.getKeycloakSecurityContext().getToken().getResourceAccess(client).getRoles();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    @Override
    public void doHead(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doOption(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    @Override
    public void doTrace(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    private String retrieveBodyFromParm(InboundRequest inReq) {
        String body = ofNullable(inReq.getParameters().get("body")).orElse("");
        inReq.getParameters().remove("body");
        return body;
    }

    private String getBodyString(HttpServletRequest req) {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader;
        try {
            reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e2) {
            log.error("Error at getBodyString: ", e2);
        }
        String body = buffer.toString();
        body = body.trim();
        return body;
    }


    private void addHeaders(HttpServletRequest req, InboundRequest inReq) {
        if (req.getHeaderNames() != null) {
            Enumeration<String> headerNames = req.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (headerName != null && !headerName.equalsIgnoreCase("authorization")) {
                    inReq.getHeaders().put(headerName, req.getHeader(headerName));
                }
            }
        }
    }

    private void addCookies(HttpServletRequest req, InboundRequest inReq) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                inReq.getCoockies().put(cookie.getName(), cookie.getValue());
            }
        }
    }

    private void addParams(HttpServletRequest req, InboundRequest inReq) {
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
    }

    private void produceResponse(HttpServletResponse res, InboundRequest inReq) throws IOException {
        for (String cookieName : inReq.getResponseCoockies().keySet()) {
            res.addCookie(new Cookie(cookieName, inReq.getResponseCoockies().get(cookieName)));
        }

        for (String headerName : inReq.getResponseHeaders().keySet()) {
            res.addHeader(headerName, inReq.getResponseHeaders().get(headerName));
        }
        if (inReq.getBytes() != null) {
            IOUtils.copy(new ByteArrayInputStream(inReq.getBytes()), res.getOutputStream());
            res.flushBuffer();
        } else if (inReq.getResponseBody() != null) {
            try (PrintWriter out = res.getWriter()) {
                out.print(inReq.getResponseBody());
            } catch (IOException e) {
                log.error("Failed to produce the response", e);
                res.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        }
    }

    private int processNotificationAndReturnStatus(InboundRequest inReq, int status) {
        try {
            eventProducer.fire(inReq);
            if (inReq.getHeaders().get("fired").equals("false")) {
                status = HttpURLConnection.HTTP_NOT_FOUND;
            }
        } catch (BusinessException be) {
            log.error("Failed when processing the notifications", be);
            status = HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
        return status;
    }
}
