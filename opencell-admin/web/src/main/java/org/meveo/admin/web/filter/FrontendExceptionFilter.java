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

package org.meveo.admin.web.filter;

import org.omnifaces.filter.HttpFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static org.omnifaces.util.Exceptions.unwrap;

/**
 * <p>
 * The {@link FrontendExceptionFilter} will solve 2 problems with exceptions thrown in JSF methods.
 * <ol>
 * <li>Frontend's <code>IOException</code> needs to be interpreted as 404.
 * <li>Root cause needs to be unwrapped from Exception  to utilize standard
 * Servlet API error page handling.
 * </ol>
 * <p>
 *
 * @author HORRI Khalid
 * @lastModifiedVersion 5.4
 */
@WebFilter("/frontend/*")
public class FrontendExceptionFilter extends HttpFilter {
    /**
     *
     */
    private static final Logger logger = Logger.getLogger(FrontendExceptionFilter.class.getName());

    /**
     *
     */
    private Class<? extends Throwable>[] exceptionTypesToUnwrap;

    /**
     * Filter the HTTP request. The session argument is <code>null</code> if there is no session.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param session  The HTTP session, if any, else <code>null</code>.
     * @param chain    The filter chain to continue.
     * @throws ServletException As wrapper exception when something fails in the request processing.
     * @throws IOException      Whenever something fails at I/O level.
     * @see Filter#doFilter (javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (IOException ignore) {
            logger.log(FINE, "Ignoring thrown exception; file not found should be interpreted as 404.", ignore);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
        } catch (ServletException ignore) {
            logger.log(FINE, "Ignoring thrown exception; this is a wrapper exception and only its root cause is of interest.", ignore);
            throw new ServletException(unwrap(ignore.getRootCause(), exceptionTypesToUnwrap));
        }
    }

}
